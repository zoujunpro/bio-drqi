package com.bio.drqi.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.client.LlmClient;
import com.bio.drqi.ai.dto.llm.LlmChatMessageDTO;
import com.bio.drqi.ai.dto.llm.LlmCallOptionsDTO;
import com.bio.drqi.ai.config.AiProperties;
import com.bio.drqi.ai.dto.plan.AiQueryFilterDTO;
import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.dto.plan.AiReportPlanDTO;
import com.bio.drqi.ai.dto.plan.AiReportStepDTO;
import com.bio.drqi.ai.schema.AiDomainPromptDTO;
import com.bio.drqi.ai.schema.AiDomainSchema;
import com.bio.drqi.ai.schema.AiDomainSummaryDTO;
import com.bio.drqi.ai.schema.AiFieldPromptDTO;
import com.bio.drqi.ai.schema.AiFieldSchema;
import com.bio.drqi.ai.schema.AiMetricPromptDTO;
import com.bio.drqi.ai.prompt.SqlPrompt;
import com.bio.drqi.ai.registry.AiDomainRegistry;
import com.bio.drqi.ai.service.AiReportPlanService;
import com.bio.drqi.ai.util.AiJsonExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class AiReportPlanServiceImpl implements AiReportPlanService {

    /**
     * 大模型客户端：只负责把“自然语言报表需求”转换成结构化 ReportPlan。
     * 真正查库/调接口不在这个类里做，避免模型规划和数据执行职责混在一起。
     */
    @Resource
    private LlmClient llmClient;

    /**
     * 业务域注册中心：提供所有可查询业务域、字段、指标。
     * 这里不会把全部 schema 直接塞给模型，而是先筛选，再压缩，降低误选和 token 消耗。
     */
    @Resource
    private AiDomainRegistry aiDomainRegistry;

    /**
     * AI 配置项：读取模型温度、最多提供多少业务域给 Prompt 等运行参数。
     */
    @Resource
    private AiProperties aiProperties;

    @Override
    public AiReportPlanDTO generate(String question) {
        // 先尝试命中固定模板。原因：有些业务问题非常明确，用代码规则更稳定，不必每次都让模型规划。
        AiReportPlanDTO templatePlan = tryBuildSchemeObjectReport(question);
        if (templatePlan != null) {
            log.info("AI多步骤报表命中固定模板，reportCode={}，stepCount={}，question={}",
                    templatePlan.getReportCode(), templatePlan.getSteps().size(), question);
            return templatePlan;
        }

        // 固定模板未命中时，才进入通用 LLM 规划流程。
        long startTime = System.currentTimeMillis();
        // 先本地筛选候选业务域，避免把几十上百个 domain 全丢给模型导致选择混乱。
        List<String> domains = preselectDomains(question);
        log.info("AI多步骤报表本地候选业务域完成，cost={}ms，domains={}", System.currentTimeMillis() - startTime, domains);
        // system prompt 放规则和候选 schema，user prompt 放用户原始问题。
        String content = llmClient.chat(Arrays.asList(
                new LlmChatMessageDTO("system", buildSystemPrompt(question, domains)),
                new LlmChatMessageDTO("user", question)
        ), LlmCallOptionsDTO.of("report", aiProperties.getLlm().getReportTemperature()));
        // 模型可能输出 Markdown 或解释文本，所以先抽取 JSON；如果抽取失败，会自动二次纠偏。
        String json = extractReportJson(content, question, domains);
        return JSONUtil.toBean(json, AiReportPlanDTO.class);
    }

    /**
     * 固定模板：处理“实施方案 + 转化/取样/种植/种子”这类复合报表问题。
     * 设计原因：
     * 1. 这类问题在业务上很常见。
     * 2. 它不是一个单表查询，而是多个对象分别统计。
     * 3. 用规则生成步骤比让模型自由拆分更稳定。
     */
    private AiReportPlanDTO tryBuildSchemeObjectReport(String question) {
        // 从自然语言里提取“实施方案编号”，例如“实施方案 XS1-01 的转化和种植数据”。
        String schemeCode = extractSchemeCode(question);
        // 没有方案编号，或者问题里没有相关业务对象关键词，就不走这个模板。
        if (StrUtil.isBlank(schemeCode) || !containsAny(question, "转化", "取样", "种植", "大田", "种子")) {
            return null;
        }

        // key 是内部步骤对象名，value 是用于匹配自然语言和业务域的关键词。
        Map<String, String[]> objectKeywords = new LinkedHashMap<>();
        objectKeywords.put("transform", new String[]{"转化", "transform"});
        objectKeywords.put("sample", new String[]{"取样", "样品", "样本", "sample"});
        objectKeywords.put("field", new String[]{"种植", "大田", "田间", "plant", "field"});
        objectKeywords.put("seed", new String[]{"种子", "库存", "seed"});

        // 报表计划只描述“要执行哪些步骤”，不直接执行查询。
        AiReportPlanDTO plan = new AiReportPlanDTO();
        plan.setReportCode("scheme_related_data_report");
        plan.setReportName("实施方案" + schemeCode + "相关数据统计");

        for (Map.Entry<String, String[]> entry : objectKeywords.entrySet()) {
            // 用户没提到的对象不生成步骤，例如只问“转化和种植”，就不生成取样/种子步骤。
            if (!containsAny(question, entry.getValue())) {
                continue;
            }
            // 在注册的业务域中找最像“转化/取样/种植/种子”的 domain。
            AiDomainSchema schema = findBestDomain(entry.getValue(), schemeCode);
            if (schema == null) {
                continue;
            }
            // 找到这个 domain 里可用于按实施方案编号过滤的字段。
            String filterField = resolveSchemeFilterField(schema);
            if (StrUtil.isBlank(filterField)) {
                continue;
            }
            // 一个对象对应一个报表步骤，每个步骤内部是一份 QueryPlan。
            AiReportStepDTO step = new AiReportStepDTO();
            step.setStepCode(entry.getKey() + "_count");
            step.setSheetName(labelFor(entry.getKey()) + "统计");
            step.setQueryPlan(buildCountPlan(schema, filterField, schemeCode));
            plan.getSteps().add(step);
        }

        // 至少两个步骤才认为是“复合报表”。只有一个步骤时交给普通查询链路更合适。
        return plan.getSteps().size() >= 2 ? plan : null;
    }

    /**
     * 构造单个统计步骤的 QueryPlan。
     * 这里统一做 aggregate，是因为固定模板解决的是“相关数据统计”，不是明细列表。
     */
    private AiQueryPlanDTO buildCountPlan(AiDomainSchema schema, String filterField, String schemeCode) {
        AiQueryPlanDTO queryPlan = new AiQueryPlanDTO();
        // domain 决定后续执行时使用哪个业务域 schema。
        queryPlan.setDomain(schema.getDomain());
        // aggregate 表示聚合统计查询。
        queryPlan.setQueryType("aggregate");
        // 优先用 totalCount；如果该业务域没有 totalCount，就取第一个已配置指标兜底。
        queryPlan.getMetrics().add(schema.getMetrics().containsKey("totalCount")
                ? "totalCount"
                : schema.getMetrics().keySet().iterator().next());
        // 报表导出/多步骤结果默认用表格表达，避免强行生成图表。
        queryPlan.setChartType("table");
        // 聚合查询理论上结果很少，这里仍设置 limit，保持 QueryPlan 结构完整。
        queryPlan.setLimit(500);

        // 按实施方案编号过滤，只查询当前方案相关数据。
        AiQueryFilterDTO filter = new AiQueryFilterDTO();
        filter.setField(filterField);
        filter.setOp("eq");
        filter.setValue(schemeCode);
        queryPlan.getFilters().add(filter);
        return queryPlan;
    }

    /**
     * 根据关键词在所有业务域中找最匹配的 domain。
     * 这里不用模型选 domain，是为了让固定模板完全可控。
     */
    private AiDomainSchema findBestDomain(String[] objectKeywords, String schemeCode) {
        AiDomainSchema best = null;
        int bestScore = 0;
        for (AiDomainSummaryDTO summary : aiDomainRegistry.listSummaryForPrompt(resolveMaxDomainPromptSize())) {
            // 取完整 schema 用于检查字段和指标。
            AiDomainSchema schema = aiDomainRegistry.getRequired(summary.getDomain());
            String filterField = resolveSchemeFilterField(schema);
            // 没有方案过滤字段或没有指标的业务域，不能用于这个统计模板。
            if (StrUtil.isBlank(filterField) || schema.getMetrics().isEmpty()) {
                continue;
            }
            // 对 domain 名、字段名、字段中文名做关键词打分。
            int objectScore = scoreSchema(schema, objectKeywords);
            if (containsAny(schema.getDomain(), objectKeywords) || containsAny(schema.getName(), objectKeywords)) {
                objectScore += 10;
            }
            if (objectScore <= 0) {
                continue;
            }
            int score = objectScore;
            // schema 明确包含过滤字段时加分，避免选到“看起来像但无法按方案过滤”的 domain。
            if (schema.getFields().containsKey(filterField)) {
                score += 8;
            }
            if (score > bestScore) {
                bestScore = score;
                best = schema;
            }
        }
        log.debug("AI实施方案复合报表业务域匹配，keywords={}，schemeCode={}，domain={}，score={}",
                Arrays.toString(objectKeywords), schemeCode, best == null ? null : best.getDomain(), bestScore);
        return bestScore > 0 ? best : null;
    }

    /**
     * 计算业务域和关键词的相关性。
     * domain/name 权重更高，字段命中也加分。
     */
    private int scoreSchema(AiDomainSchema schema, String[] objectKeywords) {
        int score = 0;
        score += scoreText(Arrays.asList(objectKeywords), schema.getDomain(), schema.getName()) * 2;
        for (AiFieldSchema field : schema.getFields().values()) {
            score += scoreText(Arrays.asList(objectKeywords), field.getField(), field.getLabel());
        }
        return score;
    }

    /**
     * 找到业务域中代表“实施方案/任务编号”的过滤字段。
     * 先按确定字段名匹配，再按字段名/中文标签模糊兜底。
     */
    private String resolveSchemeFilterField(AiDomainSchema schema) {
        String[] preferredFields = {"vectorTaskCode", "vectorTaskId", "taskCode", "taskNum", "subProjectCode"};
        for (String field : preferredFields) {
            if (schema.getFields().containsKey(field)) {
                return field;
            }
        }
        for (Map.Entry<String, AiFieldSchema> entry : schema.getFields().entrySet()) {
            AiFieldSchema field = entry.getValue();
            String name = entry.getKey();
            String label = field == null ? null : field.getLabel();
            if (containsAny(name, "vector", "task", "scheme", "plan")
                    || containsAny(label, "实施方案", "试验方案", "方案编号", "任务编号")) {
                return name;
            }
        }
        return null;
    }

    /**
     * 从用户问题中提取实施方案编号。
     * 当前只提取“实施方案/试验方案/方案 + 编号”的显式表达，避免误把普通数字识别成方案号。
     */
    private String extractSchemeCode(String question) {
        if (StrUtil.isBlank(question)) {
            return null;
        }
        Matcher matcher = Pattern.compile("(?:实施方案|试验方案|方案)(?:编号|编码)?\\s*[:：为是]?\\s*([A-Za-z0-9_-]{2,})").matcher(question);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * 把内部对象编码转成用户能看懂的 sheet 名称。
     */
    private String labelFor(String objectCode) {
        if ("transform".equals(objectCode)) {
            return "转化";
        }
        if ("sample".equals(objectCode)) {
            return "取样";
        }
        if ("field".equals(objectCode)) {
            return "种植";
        }
        if ("seed".equals(objectCode)) {
            return "种子";
        }
        return objectCode;
    }

    /**
     * 判断一个字符串是否包含任意关键词。
     * 统一忽略大小写，方便同时匹配中文和英文 code。
     */
    private boolean containsAny(String value, String... keywords) {
        if (StrUtil.isBlank(value) || keywords == null) {
            return false;
        }
        for (String keyword : keywords) {
            if (StrUtil.isNotBlank(keyword) && value.toLowerCase().contains(keyword.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 通用 LLM 规划前的业务域预筛选。
     * 目的：减少 prompt 中业务域数量，让模型只在最相关的几个 domain 里规划。
     */
    private List<String> preselectDomains(String question) {
        List<String> tokens = tokenize(question);
        List<DomainScore> scores = new ArrayList<>();
        for (AiDomainSummaryDTO summary : aiDomainRegistry.listSummaryForPrompt(resolveMaxDomainPromptSize())) {
            AiDomainPromptDTO promptDTO = aiDomainRegistry.getForPrompt(summary.getDomain());
            int score = scoreDomain(question, tokens, promptDTO);
            if (score > 0) {
                scores.add(new DomainScore(summary.getDomain(), score));
            }
        }
        scores.sort(Comparator.comparing(DomainScore::getScore).reversed());
        List<String> domains = new ArrayList<>();
        for (DomainScore score : scores) {
            domains.add(score.getDomain());
            // 最多给 6 个候选 domain，减少模型误选和上下文长度。
            if (domains.size() >= 6) {
                break;
            }
        }
        if (domains.isEmpty()) {
            // 完全没有本地命中时，给模型少量默认候选，避免直接失败。
            for (AiDomainSummaryDTO summary : aiDomainRegistry.listSummaryForPrompt(Math.min(resolveMaxDomainPromptSize(), 8))) {
                domains.add(summary.getDomain());
            }
        }
        return domains;
    }

    /**
     * 构建报表规划 prompt。
     */
    private String buildSystemPrompt(String question, List<String> domains) {
        return SqlPrompt.reportPlanPrompt(listSelectedDomains(question, domains));
    }

    /**
     * 构建 JSON 纠偏 prompt。
     * 用于模型第一次输出不是合法 JSON 时，要求模型只修格式，不重新发挥。
     */
    private String buildRepairPrompt(String question, List<String> domains, String badContent) {
        return SqlPrompt.reportRepairPrompt(question, listSelectedDomains(question, domains), badContent);
    }

    /**
     * 获取候选 domain 的 prompt schema，并做字段/指标裁剪。
     */
    private List<AiDomainPromptDTO> listSelectedDomains(String question, List<String> domains) {
        List<AiDomainPromptDTO> result = new ArrayList<>();
        List<String> tokens = tokenize(question);
        for (String domain : domains) {
            // 每个 domain 再做一次压缩，避免一个业务域字段太多导致 prompt 过长。
            result.add(compactDomainPrompt(aiDomainRegistry.getForPrompt(domain), tokens));
        }
        return result;
    }

    /**
     * 压缩单个业务域暴露给模型的 schema。
     * 模型只需要知道“最可能相关”的字段、维度和指标，不需要看到完整数据库映射。
     */
    private AiDomainPromptDTO compactDomainPrompt(AiDomainPromptDTO source, List<String> tokens) {
        AiDomainPromptDTO target = new AiDomainPromptDTO();
        target.setDomain(source.getDomain());
        target.setName(source.getName());
        // 字段、维度、指标分别设置上限，避免 prompt 膨胀。
        target.setFields(limitFields(source.getFields(), tokens, 30));
        target.setDimensions(limitFields(source.getDimensions(), tokens, 20));
        target.setMetrics(limitMetrics(source.getMetrics(), tokens, 12));
        return target;
    }

    /**
     * 按用户问题关键词给字段排序，只保留前 limit 个。
     * 这样模型优先看到和问题相关的字段。
     */
    private List<AiFieldPromptDTO> limitFields(List<AiFieldPromptDTO> fields, List<String> tokens, int limit) {
        List<FieldScore> scores = new ArrayList<>();
        for (AiFieldPromptDTO field : fields) {
            scores.add(new FieldScore(field, scoreText(tokens, field.getField(), field.getLabel())));
        }
        scores.sort(Comparator.comparing(FieldScore::getScore).reversed());
        List<AiFieldPromptDTO> result = new ArrayList<>();
        for (FieldScore score : scores) {
            result.add(score.getField());
            if (result.size() >= limit) {
                break;
            }
        }
        return result;
    }

    /**
     * 按用户问题关键词给指标排序，只保留前 limit 个。
     */
    private List<AiMetricPromptDTO> limitMetrics(List<AiMetricPromptDTO> metrics, List<String> tokens, int limit) {
        List<MetricScore> scores = new ArrayList<>();
        for (AiMetricPromptDTO metric : metrics) {
            scores.add(new MetricScore(metric, scoreText(tokens, metric.getMetric(), metric.getLabel())));
        }
        scores.sort(Comparator.comparing(MetricScore::getScore).reversed());
        List<AiMetricPromptDTO> result = new ArrayList<>();
        for (MetricScore score : scores) {
            result.add(score.getMetric());
            if (result.size() >= limit) {
                break;
            }
        }
        return result;
    }

    /**
     * 计算业务域和用户问题的相关性。
     * domain/name 命中加分，字段命中加分，指标命中加倍，因为报表问题通常更依赖指标。
     */
    private int scoreDomain(String question, List<String> tokens, AiDomainPromptDTO domain) {
        int score = scoreText(tokens, domain.getDomain(), domain.getName());
        if (question.contains(domain.getDomain()) || question.contains(domain.getName())) {
            score += 10;
        }
        for (AiFieldPromptDTO field : domain.getFields()) {
            score += scoreText(tokens, field.getField(), field.getLabel());
        }
        for (AiMetricPromptDTO metric : domain.getMetrics()) {
            score += scoreText(tokens, metric.getMetric(), metric.getLabel()) * 2;
        }
        return score;
    }

    /**
     * 通用文本打分：用户问题 token 出现在目标文本里就加分。
     * 长 token 区分度更高，所以长度 >= 3 的 token 权重更大。
     */
    private int scoreText(List<String> tokens, String... texts) {
        int score = 0;
        for (String text : texts) {
            if (StrUtil.isBlank(text)) {
                continue;
            }
            for (String token : tokens) {
                if (text.contains(token)) {
                    score += token.length() >= 3 ? 3 : 1;
                }
            }
        }
        return score;
    }

    /**
     * 简单分词：
     * 1. 英文/数字/下划线/中划线按连续片段保留，适合编号和字段名。
     * 2. 中文按 2~4 字滑窗切分，避免没有分词库时完全匹配不到业务词。
     */
    private List<String> tokenize(String question) {
        List<String> tokens = new ArrayList<>();
        if (StrUtil.isBlank(question)) {
            return tokens;
        }
        Matcher matcher = Pattern.compile("[A-Za-z0-9_-]{2,}|[\\u4e00-\\u9fa5]{2,}").matcher(question);
        while (matcher.find()) {
            String word = matcher.group();
            if (word.matches("[A-Za-z0-9_-]+")) {
                tokens.add(word);
                continue;
            }
            for (int size = 2; size <= Math.min(4, word.length()); size++) {
                for (int i = 0; i + size <= word.length(); i++) {
                    tokens.add(word.substring(i, i + size));
                }
            }
        }
        return tokens;
    }

    /**
     * 读取最大业务域 prompt 数量。
     * 未配置时默认 80，避免因为配置缺失导致候选为空。
     */
    private int resolveMaxDomainPromptSize() {
        return aiProperties.getMaxDomainPromptSize() == null ? 80 : aiProperties.getMaxDomainPromptSize();
    }

    /**
     * 从模型输出中抽取 JSON。
     * 模型有时会输出说明文字或 Markdown 代码块，所以不能直接 JSONUtil.toBean(content)。
     * 第一次抽取失败后，会再让模型“只修 JSON 格式”重试一次。
     */
    private String extractReportJson(String content, String question, List<String> domains) {
        try {
            return AiJsonExtractor.extractObject(content, "AI报表计划为空", "AI报表计划不是合法JSON");
        } catch (BusinessException e) {
            log.warn("AI报表计划原始返回无法解析，准备纠偏重试，content={}", limitText(content, 2000));
            String repairedContent = llmClient.chat(Arrays.asList(
                    new LlmChatMessageDTO("system", buildRepairPrompt(question, domains, content)),
                    new LlmChatMessageDTO("user", "请把上一次输出修正为合法JSON。只输出JSON对象。")
            ), LlmCallOptionsDTO.of("report", aiProperties.getLlm().getReportTemperature()));
            try {
                return AiJsonExtractor.extractObject(repairedContent, "AI报表计划为空", "AI报表计划不是合法JSON");
            } catch (BusinessException retryException) {
                log.warn("AI报表计划纠偏返回仍无法解析，content={}", limitText(repairedContent, 2000));
                throw retryException;
            }
        }
    }

    /**
     * 限制日志长度。
     * 失败日志需要保留模型原始输出用于排查，但不能把超长内容完整打进日志。
     */
    private String limitText(String text, int maxLength) {
        if (text == null) {
            return null;
        }
        String normalized = text.replaceAll("[\\r\\n\\t]+", " ");
        if (normalized.length() <= maxLength) {
            return normalized;
        }
        return normalized.substring(0, maxLength) + "...";
    }

    /**
     * domain 打分结果对象。
     * 使用小对象而不是 Map.Entry，代码读起来更明确。
     */
    private static class DomainScore {
        private final String domain;
        private final Integer score;

        private DomainScore(String domain, Integer score) {
            this.domain = domain;
            this.score = score;
        }

        private String getDomain() {
            return domain;
        }

        private Integer getScore() {
            return score;
        }
    }

    /**
     * 字段打分结果对象。
     */
    private static class FieldScore {
        private final AiFieldPromptDTO field;
        private final Integer score;

        private FieldScore(AiFieldPromptDTO field, Integer score) {
            this.field = field;
            this.score = score;
        }

        private AiFieldPromptDTO getField() {
            return field;
        }

        private Integer getScore() {
            return score;
        }
    }

    /**
     * 指标打分结果对象。
     */
    private static class MetricScore {
        private final AiMetricPromptDTO metric;
        private final Integer score;

        private MetricScore(AiMetricPromptDTO metric, Integer score) {
            this.metric = metric;
            this.score = score;
        }

        private AiMetricPromptDTO getMetric() {
            return metric;
        }

        private Integer getScore() {
            return score;
        }
    }
}
