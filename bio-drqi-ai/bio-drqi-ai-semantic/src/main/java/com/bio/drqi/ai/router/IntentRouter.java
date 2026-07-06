package com.bio.drqi.ai.router;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.client.LlmClient;
import com.bio.drqi.ai.config.AiProperties;
import com.bio.drqi.ai.dto.llm.LlmCallOptionsDTO;
import com.bio.drqi.ai.dto.llm.LlmChatMessageDTO;
import com.bio.drqi.ai.dto.memory.AiConversationContextDTO;
import com.bio.drqi.ai.dto.router.AiIntentDTO;
import com.bio.drqi.ai.entity.AiIntentKeyword;
import com.bio.drqi.ai.prompt.RouterPrompt;
import com.bio.drqi.ai.service.AiIntentKeywordService;
import com.bio.drqi.ai.service.AiTermService;
import com.bio.drqi.ai.util.AiJsonExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AI请求入口路由。
 * 先用规则处理明显场景；规则无法确认时，再让模型按 prompt 输出 intent JSON。
 *
 * 这个类只负责判断“用户这句话想做什么”，不负责查数据、不负责生成 SQL。
 * 当前识别流程：
 * 1. 关键词权重打分。
 * 2. 多轮上下文加分。
 * 3. 业务术语命中加分。
 * 4. 规则分数明显领先时直接返回意图。
 * 5. 规则不确定时调用模型兜底。
 */
@Component
@Slf4j
public class IntentRouter {

    /**
     * 普通聊天，例如“你好”“你是谁”“怎么使用”。
     */
    public static final String INTENT_CHAT = "chat";

    /**
     * 业务数据查询，例如“统计项目数量”“查看种子库存”。
     */
    public static final String INTENT_BUSINESS_QUERY = "business_query";

    /**
     * 报表导出，例如“导出刚才的数据”“下载 Excel”。
     */
    public static final String INTENT_REPORT_EXPORT = "report_export";

    /**
     * 工单/审批/流程，例如“我的待办”“查看审批流程”。
     */
    public static final String INTENT_WORKFLOW = "workflow";

    /**
     * 无法确定意图，主流程会提示用户补充说明。
     */
    public static final String INTENT_UNKNOWN = "unknown";

    /**
     * 规则判定的最低分。低于这个分数，即使是最高分，也认为证据不足。
     */
    private static final int MIN_RULE_SCORE = 5;

    /**
     * 第一名和第二名至少要相差 3 分，才认为“明显领先”。
     * 例如 business_query=8、workflow=7 时差距太小，需要交给模型或追问。
     */
    private static final int MIN_LEAD_SCORE = 3;

    /**
     * 命中“刚才/这些 + 数据/项目”等上下文追问时，给业务查询加的分。
     */
    private static final int CONTEXT_BUSINESS_SCORE = 4;

    /**
     * 命中一条业务术语时，给业务查询加的分。
     * 例如“取样数量”没有“查询/统计”，但命中术语后仍能识别为业务查询。
     */
    private static final int TERM_BUSINESS_SCORE = 10;

    /**
     * 表示用户在引用上一次对话结果的词。
     */
    private static final List<String> CONTEXT_REFERENCE_WORDS = Arrays.asList(
            "这些", "上述", "上面", "刚才", "刚刚", "前面", "它们", "他们", "该项目", "这些项目", "这些数据"
    );

    /**
     * 表示用户引用的是业务数据，而不是普通聊天内容的词。
     */
    private static final List<String> CONTEXT_BUSINESS_WORDS = Arrays.asList(
            "项目", "实施方案", "方案", "种子", "样品", "取样", "数据", "信息", "明细", "统计", "整理", "查看", "查询"
    );

    /**
     * 大模型客户端。只有规则无法明确判断时，才调用模型识别意图。
     */
    @Resource
    private LlmClient llmClient;

    /**
     * AI配置，主要读取模型温度、意图置信度等配置。
     */
    @Resource
    private AiProperties aiProperties;

    /**
     * 意图关键词服务。负责从数据库或配置文件读取关键词及权重。
     */
    @Resource
    private AiIntentKeywordService aiIntentKeywordService;

    /**
     * 业务术语服务。用于判断用户输入是否命中了业务术语。
     */
    @Resource
    private AiTermService aiTermService;

    /**
     * 没有会话上下文时的入口，直接复用带 context 的方法。
     */
    public String route(String question) {
        return route(question, null);
    }

    /**
     * 判断用户问题所属意图。
     *
     * @param question 用户输入的自然语言
     * @param context  当前会话上下文，可用于判断“刚才/这些”等追问
     * @return 意图编码
     */
    public String route(String question, AiConversationContextDTO context) {
        // 空问题没有业务含义，按普通聊天处理，避免后续模型或规则空指针。
        if (StrUtil.isBlank(question)) {
            return INTENT_CHAT;
        }

        // 先使用规则打分，规则能解释、成本低、稳定性比模型更好。
        RuleRouteResult ruleResult = routeByRuleScore(question, context);

        // 如果规则分数明显领先，直接采用规则结果，不再调用模型。
        if (ruleResult.isDecisive()) {
            // 记录分数和命中原因，方便后续排查为什么判断成该意图。
            log.info("AI意图规则识别命中，intent={}，bestScore={}，secondScore={}，scores={}，reasons={}，question={}",
                    ruleResult.getBestIntent(), ruleResult.getBestScore(), ruleResult.getSecondScore(),
                    ruleResult.getScores(), ruleResult.getReasons(), question);
            // 返回规则判定出的第一名意图。
            return ruleResult.getBestIntent();
        }

        // 规则不够确定时记录现场，后续如果模型误判，可以根据这些分数调整关键词权重。
        log.info("AI意图规则识别不确定，bestIntent={}，bestScore={}，secondScore={}，scores={}，reasons={}，question={}",
                ruleResult.getBestIntent(), ruleResult.getBestScore(), ruleResult.getSecondScore(),
                ruleResult.getScores(), ruleResult.getReasons(), question);

        // 规则不确定时交给模型判断；模型失败时会再用规则兜底。
        return routeByModel(question, ruleResult);
    }

    /**
     * 根据关键词、上下文和业务术语计算各意图分数。
     */
    private RuleRouteResult routeByRuleScore(String question, AiConversationContextDTO context) {
        // 创建本次规则路由结果对象，用于记录每个意图的分数和命中原因。
        RuleRouteResult result = new RuleRouteResult();

        // 初始化所有常用意图分数为 0，保证日志里每个意图都有值。
        for (String intent : Arrays.asList(INTENT_REPORT_EXPORT, INTENT_WORKFLOW, INTENT_BUSINESS_QUERY, INTENT_CHAT)) {
            result.getScores().put(intent, 0);
        }

        // 读取关键词规则。优先数据库，数据库不可用时走配置文件兜底。
        List<AiIntentKeyword> rules = aiIntentKeywordService.listKeywordRules();

        // 遍历每条关键词规则，判断用户问题是否命中。
        for (AiIntentKeyword rule : rules) {
            // 跳过无效规则，避免脏数据影响意图识别。
            if (rule == null || StrUtil.isBlank(rule.getIntent()) || StrUtil.isBlank(rule.getKeyword())) {
                continue;
            }

            // 把数据库里的 intent 归一化，只允许系统支持的几种意图。
            String intent = normalizeIntent(rule.getIntent());

            // 如果 intent 不在白名单里，归一化后会变成 unknown，这类规则不参与打分。
            if (INTENT_UNKNOWN.equals(intent)) {
                continue;
            }

            // 去掉关键词首尾空格，避免后台配置多了空格导致无法命中。
            String keyword = rule.getKeyword().trim();

            // 当前是简单包含匹配：用户问题包含关键词，就认为命中。
            if (question.contains(keyword)) {
                // 没配权重或权重非法时默认 1 分，避免配置错误导致异常。
                int weight = rule.getWeight() == null || rule.getWeight() <= 0 ? 1 : rule.getWeight();
                // 给命中的意图加分，同时记录命中原因。
                addScore(result, intent, weight, "关键词[" + keyword + "]+" + weight);
            }
        }

        // 如果用户是在追问“刚才这些数据/项目”，且上文有结果快照，则给业务查询加分。
        if (isContextBusinessQuery(question, context)) {
            addScore(result, INTENT_BUSINESS_QUERY, CONTEXT_BUSINESS_SCORE, "上下文追问+" + CONTEXT_BUSINESS_SCORE);
        }

        // 召回业务术语。比如“取样数量”命中术语后，说明它更像业务查询。
        int termCount = aiTermService.recall(question).size();

        // 命中业务术语时给 business_query 加分；命中越多，业务查询证据越强。
        if (termCount > 0) {
            addScore(result, INTENT_BUSINESS_QUERY, TERM_BUSINESS_SCORE * termCount,
                    "业务术语命中" + termCount + "个+" + (TERM_BUSINESS_SCORE * termCount));
        }

        // 根据分数计算第一名、第二名，供后面判断是否明显领先。
        fillBestScore(result);

        // 返回完整规则结果，而不是只返回 intent，方便日志和模型失败兜底使用。
        return result;
    }

    /**
     * 判断用户是否在基于上一次查询结果继续追问业务数据。
     */
    private boolean isContextBusinessQuery(String question, AiConversationContextDTO context) {
        // 没有上下文，或者上一次没有结果快照，就不能判断为上下文追问。
        if (context == null || StrUtil.isBlank(context.getLastResultSnapshot())) {
            return false;
        }

        // 既要命中“刚才/这些”等引用词，也要命中“项目/数据/统计”等业务词，才算业务上下文追问。
        return containsAny(question, CONTEXT_REFERENCE_WORDS) && containsAny(question, CONTEXT_BUSINESS_WORDS);
    }

    /**
     * 调用模型识别意图。
     * 只有规则不够确定时才走这里，降低模型成本并减少模型随机性。
     */
    private String routeByModel(String question, RuleRouteResult ruleResult) {
        // 记录模型调用开始时间，用于日志统计耗时。
        long startTime = System.currentTimeMillis();
        try {
            // 按 OpenAI 兼容格式组织消息：system 放规则提示词，user 放用户原始问题。
            String content = llmClient.chat(Arrays.asList(
                    new LlmChatMessageDTO("system", RouterPrompt.intentPrompt()),
                    new LlmChatMessageDTO("user", question)
            ), LlmCallOptionsDTO.of("router", aiProperties.getLlm().getRouterTemperature()));

            // 从模型返回中提取 JSON 对象，兼容模型返回 ```json 包裹的情况。
            String json = AiJsonExtractor.extractObject(content, "AI意图识别为空", "AI意图识别不是合法JSON");

            // 把模型 JSON 转成后端 DTO，里面包含 intent、confidence、reason。
            AiIntentDTO intentDTO = JSONUtil.toBean(json, AiIntentDTO.class);

            // 模型输出的 intent 也必须归一化，防止模型返回未定义枚举。
            String intent = normalizeIntent(intentDTO == null ? null : intentDTO.getIntent());

            // 模型置信度，低于阈值时不采纳。
            Double confidence = intentDTO == null ? null : intentDTO.getConfidence();

            // 记录模型识别结果和原因，方便排查模型为什么这么判断。
            log.info("AI意图识别完成，cost={}ms，intent={}，confidence={}，reason={}，question={}",
                    System.currentTimeMillis() - startTime, intent, confidence,
                    intentDTO == null ? null : intentDTO.getReason(), question);

            // 读取配置的最低置信度；没配时默认 0.6。
            double minConfidence = aiProperties.getIntent().getConfidenceThreshold() == null ? 0.6D : aiProperties.getIntent().getConfidenceThreshold();

            // 模型返回 unknown、没返回置信度、或置信度过低时，都认为无法确定。
            if (INTENT_UNKNOWN.equals(intent) || confidence == null || confidence < minConfidence) {
                return INTENT_UNKNOWN;
            }

            // 模型结果合法且置信度达标，采用模型意图。
            return intent;
        } catch (BusinessException e) {
            // 模型返回格式不合法、超时等业务异常时，不直接失败，尝试使用规则分数兜底。
            log.warn("AI意图识别失败，尝试使用规则兜底，question={}，message={}", question, e.getMessage());
            return fallbackIntent(ruleResult);
        } catch (Exception e) {
            // 非预期异常也不影响主流程，尽量用规则兜底，避免 AI 查询入口不可用。
            log.warn("AI意图识别异常，尝试使用规则兜底，question={}", question, e);
            return fallbackIntent(ruleResult);
        }
    }

    /**
     * 规范化意图，只允许返回系统支持的枚举。
     */
    private String normalizeIntent(String intent) {
        // 如果传入的是系统支持的意图，直接返回。
        if (INTENT_CHAT.equals(intent)
                || INTENT_BUSINESS_QUERY.equals(intent)
                || INTENT_REPORT_EXPORT.equals(intent)
                || INTENT_WORKFLOW.equals(intent)
                || INTENT_UNKNOWN.equals(intent)) {
            return intent;
        }

        // 未知值统一归为 unknown，避免模型或数据库配置返回乱值。
        return INTENT_UNKNOWN;
    }

    /**
     * 给某个意图加分，并记录加分原因。
     */
    private void addScore(RuleRouteResult result, String intent, int score, String reason) {
        // 原分数 + 本次命中分数。
        result.getScores().put(intent, result.getScores().getOrDefault(intent, 0) + score);
        // 保存命中原因，例如 business_query:关键词[统计]+10。
        result.getReasons().add(intent + ":" + reason);
    }

    /**
     * 从分数表里计算最高分和第二高分。
     */
    private void fillBestScore(RuleRouteResult result) {
        // 把 Map 转成 List，方便按分数排序。
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(result.getScores().entrySet());

        // 按分数倒序排序，第一条就是最高分意图。
        entries.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        // 没有任何分数时保持默认 unknown。
        if (entries.isEmpty()) {
            return;
        }

        // 保存最高分意图。
        result.setBestIntent(entries.get(0).getKey());

        // 保存最高分。
        result.setBestScore(entries.get(0).getValue());

        // 保存第二高分；只有一个意图时第二高分按 0 处理。
        result.setSecondScore(entries.size() > 1 ? entries.get(1).getValue() : 0);
    }

    /**
     * 模型识别失败时，用规则结果兜底。
     */
    private String fallbackIntent(RuleRouteResult ruleResult) {
        // 只要规则最高分达到最低分，就采用规则第一名；这里不要求明显领先，因为模型已经不可用。
        if (ruleResult != null && ruleResult.getBestScore() >= MIN_RULE_SCORE) {
            return ruleResult.getBestIntent();
        }

        // 规则也没有足够证据时，退回普通聊天，避免误查业务数据。
        return INTENT_CHAT;
    }

    /**
     * 判断文本是否包含词列表中的任意一个词。
     */
    private boolean containsAny(String text, java.util.List<String> words) {
        // 词列表为空时直接返回 false。
        if (words == null || words.isEmpty()) {
            return false;
        }

        // 逐个词判断是否被用户问题包含。
        for (String word : words) {
            // 命中一个就返回 true，不需要继续判断。
            if (text.contains(word)) {
                return true;
            }
        }

        // 所有词都没命中。
        return false;
    }

    /**
     * 规则路由结果对象。
     * 用来保存各意图分数、命中原因、最高分和第二高分。
     */
    private static class RuleRouteResult {
        /**
         * 各意图得分。LinkedHashMap 用于保持日志输出顺序稳定。
         */
        private final Map<String, Integer> scores = new LinkedHashMap<>();

        /**
         * 命中原因列表，用于排查为什么某个意图得分。
         */
        private final List<String> reasons = new ArrayList<>();

        /**
         * 当前最高分意图，默认 unknown。
         */
        private String bestIntent = INTENT_UNKNOWN;

        /**
         * 当前最高分。
         */
        private int bestScore;

        /**
         * 当前第二高分。
         */
        private int secondScore;

        /**
         * 判断规则结果是否足够明确。
         */
        private boolean isDecisive() {
            // 最高分要达到最低阈值，并且领先第二名足够多，才直接采纳规则结果。
            return bestScore >= MIN_RULE_SCORE && bestScore - secondScore >= MIN_LEAD_SCORE;
        }

        /**
         * 返回各意图得分。
         */
        public Map<String, Integer> getScores() {
            return scores;
        }

        /**
         * 返回命中原因。
         */
        public List<String> getReasons() {
            return reasons;
        }

        /**
         * 返回最高分意图。
         */
        public String getBestIntent() {
            return bestIntent;
        }

        /**
         * 设置最高分意图。
         */
        public void setBestIntent(String bestIntent) {
            this.bestIntent = bestIntent;
        }

        /**
         * 返回最高分。
         */
        public int getBestScore() {
            return bestScore;
        }

        /**
         * 设置最高分。
         */
        public void setBestScore(int bestScore) {
            this.bestScore = bestScore;
        }

        /**
         * 返回第二高分。
         */
        public int getSecondScore() {
            return secondScore;
        }

        /**
         * 设置第二高分。
         */
        public void setSecondScore(int secondScore) {
            this.secondScore = secondScore;
        }
    }
}
