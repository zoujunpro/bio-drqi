package com.bio.drqi.ai.clarify;

import cn.hutool.core.util.StrUtil;
import com.bio.drqi.ai.config.AiProperties;
import com.bio.drqi.ai.dto.memory.AiConversationContextDTO;
import com.bio.drqi.ai.registry.AiDomainRegistry;
import com.bio.drqi.ai.router.IntentRouter;
import com.bio.drqi.ai.schema.AiDomainSummaryDTO;
import com.bio.drqi.ai.service.AiTermService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 意图澄清引擎。
 *
 * 当前实现不依赖向量库：
 * 1. 高风险写操作直接拒绝。
 * 2. 上下文代词缺失时要求用户补充对象。
 * 3. 通过业务关键词、业务术语、已注册业务域、会话上下文生成候选项。
 * 4. 对候选项排序，返回追问模板。
 *
 * 后续接向量库或 LLM rerank 时，只需要在 buildDomainCandidates 中追加分数来源。
 */
@Component
@Slf4j
public class ClarificationEngine {

    /**
     * 单个候选项达到该分数，才可能被认为“比较确定”。
     * 这里不是最终放行条件，还要看它和第二名的分差。
     */
    private static final double STRONG_SCORE = 0.75D;

    /**
     * 第一名比第二名至少领先该分值，才认为用户意图明显。
     * 例如 0.82 vs 0.78 不能直接猜，要追问；0.85 vs 0.50 可以认为比较明确。
     */
    private static final double LEAD_SCORE = 0.18D;

    /**
     * 智能查询暂不允许执行的写操作/高风险操作。
     * 命中这些词时直接拒绝，不进入 SQL 或工具调用链路。
     */
    private static final List<String> UNSAFE_WORDS = Arrays.asList(
            "删除", "清空", "修改", "更新", "新增", "添加", "上传", "导入", "提交", "审批", "同意", "驳回", "作废"
    );

    /**
     * 代表用户在引用上文的词。
     * 如果命中这些词，但会话里没有上一轮结果，就需要先让用户补充对象。
     */
    private static final List<String> CONTEXT_WORDS = Arrays.asList(
            "刚才", "刚刚", "上面", "上述", "这些", "这个", "它们", "他们", "整理下", "导出刚才"
    );

    /**
     * 内置常见业务域候选。
     * 这不是最终业务白名单，只是用户意图不明确时给出的候选方向和推荐问法。
     */
    private static final Map<String, DomainDefinition> BUILTIN_DOMAINS = new LinkedHashMap<>();

    static {
        // 每个 domain 包含：编码、展示名、候选类型、触发关键词、推荐追问模板。
        BUILTIN_DOMAINS.put("project", domain("project", "项目信息", "domain",
                Arrays.asList("项目", "课题", "项目编号"), "查询项目列表", "统计项目数量"));
        BUILTIN_DOMAINS.put("vector_task", domain("vector_task", "实施方案", "domain",
                Arrays.asList("实施方案", "方案", "载体构建", "转化方案"), "查询实施方案列表", "统计实施方案数量"));
        BUILTIN_DOMAINS.put("sample", domain("sample", "取样检测", "domain",
                Arrays.asList("取样", "样品", "检测", "取样编号", "样品编号"), "查询取样信息", "统计取样编号数量"));
        BUILTIN_DOMAINS.put("seed", domain("seed", "种子库存", "domain",
                Arrays.asList("种子", "种子库", "库存", "入库", "出库"), "查询种子库存", "统计种子库存数量"));
        BUILTIN_DOMAINS.put("material", domain("material", "耗材管理", "domain",
                Arrays.asList("耗材", "试剂", "采购", "领用"), "查询耗材库存", "查询耗材采购申请"));
        BUILTIN_DOMAINS.put(IntentRouter.INTENT_WORKFLOW, domain(IntentRouter.INTENT_WORKFLOW, "工单审批", "intent",
                Arrays.asList("工单", "待办", "已办", "审批", "流程"), "查看我的待办", "查询审批工单"));
    }

    @Resource
    private AiTermService aiTermService;

    @Resource
    private AiDomainRegistry aiDomainRegistry;

    /**
     * 澄清决策入口。
     * 主流程在意图不确定时调用这里，由该方法决定：拒绝、追问，还是放行。
     */
    public ClarificationDecision decide(ClarificationRequest request) {
        // 兼容空请求，避免空指针；空问题后面会得到默认候选。
        String question = request == null || request.getQuestion() == null ? "" : request.getQuestion().trim();
        // 当前短期会话上下文，用于判断“刚才/这些”等代词是否有可引用结果。
        AiConversationContextDTO context = request == null ? null : request.getContext();

        // 第一层安全兜底：写操作不让 AI 查询入口执行，避免误删、误改、误审批。
        if (containsAny(question, UNSAFE_WORDS)) {
            return ClarificationDecision.rejected(
                    "unsafe_action",
                    "智能查询当前只支持查询、统计、查看和结果整理，不直接执行删除、修改、上传、审批等写操作。",
                    "请改成查询、统计、查看或整理类问题。"
            );
        }

        // 第二层上下文兜底：用户说“刚才/这些”，但当前没有上一轮结果时，不能靠猜。
        if (hasMissingContext(question, context)) {
            return ClarificationDecision.clarify(
                    ClarificationState.MISSING_CONTEXT,
                    "missing_context",
                    "当前会话里没有可引用的上一轮查询结果。",
                    "请补充你要处理的是哪一类数据，或者先发起一次明确查询。",
                    defaultCandidates()
            );
        }

        // 第三层候选生成：根据关键词、术语、已注册业务域、会话上下文生成候选项。
        List<ClarificationCandidate> candidates = buildCandidates(question, context);
        // 如果外层意图已经不是 unknown，且候选分数明显领先，就认为不需要追问。
        if (!IntentRouter.INTENT_UNKNOWN.equals(request == null ? null : request.getIntent())
                && isClearlyResolved(candidates)) {
            return ClarificationDecision.resolved();
        }

        // 候选大于 1 个通常说明业务方向不唯一；没有候选则按未知意图处理。
        String type = candidates.size() > 1 ? "ambiguous_or_missing_domain" : "unknown_intent";
        // 返回追问决策，前端可以展示 message、question，并把 candidates 渲染成按钮。
        return ClarificationDecision.clarify(
                ClarificationState.NEED_CLARIFY,
                type,
                "我还不能确定你的查询目标。",
                "请说明你要查哪个业务对象，以及要看明细还是统计数量。",
                candidates.isEmpty() ? defaultCandidates() : candidates
        );
    }

    /**
     * 判断候选是否已经足够明确。
     * 既要求第一名分数够高，也要求它明显领先第二名。
     */
    private boolean isClearlyResolved(List<ClarificationCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return false;
        }
        // 候选列表在 buildCandidates 里已经按 score 倒序排好。
        ClarificationCandidate first = candidates.get(0);
        ClarificationCandidate second = candidates.size() > 1 ? candidates.get(1) : null;
        double firstScore = first.getScore() == null ? 0D : first.getScore();
        double secondScore = second == null || second.getScore() == null ? 0D : second.getScore();
        return firstScore >= STRONG_SCORE && firstScore - secondScore >= LEAD_SCORE;
    }

    /**
     * 生成并排序所有候选项。
     * 当前候选来自两类：
     * 1. 内置常见业务域。
     * 2. AiDomainRegistry 中已经注册的真实可查询业务域。
     */
    private List<ClarificationCandidate> buildCandidates(String question, AiConversationContextDTO context) {
        List<ClarificationCandidate> candidates = new ArrayList<>();
        // 基于固定业务方向和关键词生成候选。
        candidates.addAll(buildBuiltinCandidates(question, context));
        // 基于系统已注册业务域和字段名称生成候选。
        candidates.addAll(buildRegisteredDomainCandidates(question));
        // 同一个 code 可能同时被内置规则和注册业务域命中，这里合并分数和原因。
        candidates = mergeCandidates(candidates);
        // 分数越高越靠前，前端展示时也优先展示更可能的选项。
        candidates.sort(Comparator.comparing(ClarificationCandidate::getScore, Comparator.nullsLast(Comparator.reverseOrder())));
        // 候选太多会增加用户选择成本，最多保留前 6 个。
        if (candidates.size() > 6) {
            return new ArrayList<>(candidates.subList(0, 6));
        }
        return candidates;
    }

    /**
     * 根据内置业务域计算候选分。
     * 分数来源包括：关键词命中、当前会话业务域、业务术语命中。
     */
    private List<ClarificationCandidate> buildBuiltinCandidates(String question, AiConversationContextDTO context) {
        List<ClarificationCandidate> candidates = new ArrayList<>();
        for (DomainDefinition definition : BUILTIN_DOMAINS.values()) {
            // rawScore 是未归一化的原始分，后面会转成 0-1 的 score。
            double rawScore = 0D;
            // reasons 用于记录命中原因，方便日志排查或前端展示解释。
            List<String> reasons = new ArrayList<>();
            for (String keyword : definition.getKeywords()) {
                if (StrUtil.isNotBlank(keyword) && question.contains(keyword)) {
                    // 关键词是最直接的证据，比如“种子”命中种子库存。
                    rawScore += 30D;
                    reasons.add("命中关键词[" + keyword + "]");
                }
            }
            if (context != null && definition.getCode().equals(context.getCurrentDomain())) {
                // 用户连续追问时，上一轮业务域可以作为轻量加权，但不能权重过高。
                rawScore += 12D;
                reasons.add("当前会话业务域");
            }
            int termCount = termCount(question, definition.getCode());
            if (termCount > 0) {
                // 业务术语比普通关键词更可靠，例如“取样数量”可映射到 sample。
                rawScore += termCount * 25D;
                reasons.add("业务术语命中" + termCount + "个");
            }
            if (rawScore > 0D) {
                // 只有有证据的业务域才作为候选返回。
                ClarificationCandidate candidate = ClarificationCandidate.of(
                        definition.getCode(), definition.getName(), definition.getType(),
                        normalizeScore(rawScore), String.join("，", reasons)
                );
                // templates 是推荐问法，前端可以渲染成快捷按钮。
                candidate.getPayload().put("templates", definition.getTemplates());
                candidates.add(candidate);
            }
        }
        return candidates;
    }

    /**
     * 根据系统已注册的业务域生成候选。
     * 这一步让后续动态注册的表/业务域也能参与澄清，而不是只能依赖内置 domain。
     */
    private List<ClarificationCandidate> buildRegisteredDomainCandidates(String question) {
        if (StrUtil.isBlank(question)) {
            return new ArrayList<>();
        }
        // 先取和问题最相关的前 5 个业务域摘要，避免候选过多。
        List<AiDomainSummaryDTO> summaries = aiDomainRegistry.listSummaryForPrompt(question, 5);
        List<ClarificationCandidate> candidates = new ArrayList<>();
        for (AiDomainSummaryDTO summary : summaries) {
            // 对业务域编码、业务域名称、字段标签做简单匹配评分。
            double score = registeredDomainScore(question, summary);
            if (score <= 0D) {
                continue;
            }
            ClarificationCandidate candidate = ClarificationCandidate.of(
                    summary.getDomain(), summary.getName(), "domain",
                    normalizeScore(score), "命中已注册业务域/字段"
            );
            // 把真实 domain 和字段摘要放进 payload，后续可用于调试或二次确认。
            candidate.getPayload().put("domain", summary.getDomain());
            candidate.getPayload().put("fields", summary.getFields());
            candidates.add(candidate);
        }
        return candidates;
    }

    /**
     * 已注册业务域的简单规则评分。
     * 后续接向量库时，可以在这里或 buildRegisteredDomainCandidates 里追加 vectorScore。
     */
    private double registeredDomainScore(String question, AiDomainSummaryDTO summary) {
        double score = 0D;
        if (summary == null) {
            return score;
        }
        if (StrUtil.isNotBlank(summary.getDomain()) && question.toLowerCase().contains(summary.getDomain().toLowerCase())) {
            // 命中 domain 编码，例如 plasmid_quality。
            score += 25D;
        }
        if (StrUtil.isNotBlank(summary.getName()) && question.contains(summary.getName())) {
            // 命中业务域中文名，通常比编码更符合用户表达。
            score += 35D;
        }
        if (summary.getFields() != null) {
            for (String field : summary.getFields()) {
                if (StrUtil.isBlank(field)) {
                    continue;
                }
                // fields 格式通常是“字段中文名/fieldCode”，这里优先拿中文名参与匹配。
                String label = field.contains("/") ? field.substring(0, field.indexOf('/')) : field;
                if (StrUtil.isNotBlank(label) && question.contains(label)) {
                    // 命中字段名说明用户很可能在问该业务域里的具体数据。
                    score += 10D;
                }
            }
        }
        return score;
    }

    /**
     * 合并相同 code 的候选项。
     * 例如内置规则和已注册业务域都命中 sample 时，保留一个 sample 候选并叠加部分分数。
     */
    private List<ClarificationCandidate> mergeCandidates(List<ClarificationCandidate> candidates) {
        Map<String, ClarificationCandidate> merged = new LinkedHashMap<>();
        for (ClarificationCandidate candidate : candidates) {
            if (candidate == null || StrUtil.isBlank(candidate.getCode())) {
                continue;
            }
            ClarificationCandidate exists = merged.get(candidate.getCode());
            if (exists == null) {
                merged.put(candidate.getCode(), candidate);
                continue;
            }
            double existsScore = exists.getScore() == null ? 0D : exists.getScore();
            double newScore = candidate.getScore() == null ? 0D : candidate.getScore();
            // 重复命中说明证据更强，但只加一半新分，避免重复来源把分数顶满。
            exists.setScore(Math.min(1D, existsScore + newScore * 0.5D));
            if (StrUtil.isNotBlank(candidate.getReason())) {
                // 合并命中原因，方便排查候选为什么排在前面。
                exists.setReason(StrUtil.blankToDefault(exists.getReason(), "") + "；" + candidate.getReason());
            }
            // payload 后者覆盖前者，保证动态注册业务域的信息可以补充进去。
            exists.getPayload().putAll(candidate.getPayload());
        }
        return new ArrayList<>(merged.values());
    }

    /**
     * 统计用户问题命中了多少业务术语。
     * domain 为空时统计所有术语；domain 不为空时优先统计同业务域术语。
     */
    private int termCount(String question, String domain) {
        int count = 0;
        List<AiProperties.Term> terms = aiTermService.recall(question);
        for (AiProperties.Term term : terms) {
            if (term == null) {
                continue;
            }
            if (StrUtil.isBlank(domain) || StrUtil.isBlank(term.getDomain()) || domain.equals(term.getDomain())) {
                count++;
            }
        }
        return count;
    }

    /**
     * 判断是否缺少可引用的上下文。
     * 用户说“刚才/这些”，但会话里没有结果摘要和结果快照时，就不能继续执行。
     */
    private boolean hasMissingContext(String question, AiConversationContextDTO context) {
        return containsAny(question, CONTEXT_WORDS)
                && (context == null || (StrUtil.isBlank(context.getLastResultSnapshot()) && StrUtil.isBlank(context.getLastResultSummary())));
    }

    /**
     * 默认候选。
     * 当用户输入过于模糊、完全没有命中任何业务域时，给用户几个常用方向选择。
     */
    private List<ClarificationCandidate> defaultCandidates() {
        return BUILTIN_DOMAINS.values().stream()
                .map(definition -> {
                    ClarificationCandidate candidate = ClarificationCandidate.of(
                            definition.getCode(), definition.getName(), definition.getType(), 0.5D, "默认候选"
                    );
                    candidate.getPayload().put("templates", definition.getTemplates());
                    return candidate;
                })
                .collect(Collectors.toList());
    }

    /**
     * 判断文本是否包含任意关键词。
     */
    private boolean containsAny(String text, List<String> keywords) {
        if (StrUtil.isBlank(text) || keywords == null || keywords.isEmpty()) {
            return false;
        }
        for (String keyword : keywords) {
            if (StrUtil.isNotBlank(keyword) && text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 把原始分归一化成 0-1。
     * 这样前端、日志、后续融合向量分/LLM 分时更容易统一处理。
     */
    private double normalizeScore(double rawScore) {
        return Math.min(1D, rawScore / 100D);
    }

    /**
     * 创建内置业务域定义。
     */
    private static DomainDefinition domain(String code, String name, String type, List<String> keywords, String... templates) {
        DomainDefinition definition = new DomainDefinition();
        definition.setCode(code);
        definition.setName(name);
        definition.setType(type);
        definition.setKeywords(keywords);
        definition.setTemplates(Arrays.asList(templates));
        return definition;
    }

    /**
     * 内置业务域定义。
     * 只用于澄清候选，不代表最终 SQL 可查询范围；最终查询仍由 AiDomainRegistry 和校验器决定。
     */
    private static class DomainDefinition {
        /**
         * 业务域或意图编码。
         */
        private String code;
        /**
         * 前端展示名称。
         */
        private String name;
        /**
         * 候选类型：domain 或 intent。
         */
        private String type;
        /**
         * 触发该候选的关键词。
         */
        private List<String> keywords = new ArrayList<>();
        /**
         * 推荐给用户的追问模板。
         */
        private List<String> templates = new ArrayList<>();

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<String> getKeywords() {
            return keywords;
        }

        public void setKeywords(List<String> keywords) {
            this.keywords = keywords;
        }

        public List<String> getTemplates() {
            return templates;
        }

        public void setTemplates(List<String> templates) {
            this.templates = templates;
        }
    }
}
