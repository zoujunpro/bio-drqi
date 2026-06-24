package com.bio.drqi.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.client.LlmClient;
import com.bio.drqi.ai.dto.llm.LlmChatMessageDTO;
import com.bio.drqi.ai.config.AiProperties;
import com.bio.drqi.ai.dto.plan.AiReportPlanDTO;
import com.bio.drqi.ai.schema.AiDomainPromptDTO;
import com.bio.drqi.ai.schema.AiDomainSummaryDTO;
import com.bio.drqi.ai.schema.AiFieldPromptDTO;
import com.bio.drqi.ai.schema.AiMetricPromptDTO;
import com.bio.drqi.ai.registry.AiDomainRegistry;
import com.bio.drqi.ai.service.AiReportPlanService;
import com.bio.drqi.ai.util.AiJsonExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class AiReportPlanServiceImpl implements AiReportPlanService {

    @Resource
    private LlmClient llmClient;

    @Resource
    private AiDomainRegistry aiDomainRegistry;

    @Resource
    private AiProperties aiProperties;

    @Override
    public AiReportPlanDTO generate(String question) {
        long startTime = System.currentTimeMillis();
        List<String> domains = preselectDomains(question);
        log.info("AI多步骤报表本地候选业务域完成，cost={}ms，domains={}", System.currentTimeMillis() - startTime, domains);
        String content = llmClient.chat(Arrays.asList(
                new LlmChatMessageDTO("system", buildSystemPrompt(question, domains)),
                new LlmChatMessageDTO("user", question)
        ));
        String json = extractReportJson(content, question, domains);
        return JSONUtil.toBean(json, AiReportPlanDTO.class);
    }

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
            if (domains.size() >= 6) {
                break;
            }
        }
        if (domains.isEmpty()) {
            for (AiDomainSummaryDTO summary : aiDomainRegistry.listSummaryForPrompt(Math.min(resolveMaxDomainPromptSize(), 8))) {
                domains.add(summary.getDomain());
            }
        }
        return domains;
    }

    private String buildSystemPrompt(String question, List<String> domains) {
        return "你是业务报表计划生成器。必须直接输出一个JSON对象，回复第一个字符必须是{，最后一个字符必须是}。"
                + "禁止输出Markdown，禁止输出```代码块，禁止输出解释说明，禁止输出SQL文本，禁止输出Python，禁止输出操作建议。"
                + "你只能生成后端可执行的结构化查询计划，不能回答用户如何手工查询。"
                + "报表由多个steps组成，每个step必须包含stepCode、sheetName、queryPlan。"
                + "一个用户问题里如果同时要求多个统计、明细、Excel或表格，要拆成多个steps。"
                + "同一张表的多个统计指标可以放在一个aggregate step；不同表要拆成不同step。"
                + "如果用户要求明细，必须额外生成detail step。"
                + "queryPlan只能使用给定的domain、fields、metrics、dimensions。"
                + "queryType只能是aggregate/detail；统计汇总用aggregate，明细列表用detail。"
                + "aggregate必须返回metrics，可选dimensions；detail必须返回selectFields，metrics和dimensions返回空数组。"
                + "过滤条件要尽量从用户问题中提取，例如方案编号、项目编号、种子编号、时间范围。"
                + "用户说方案、实施方案、试验方案时，优先匹配字段标签为实施方案编号或字段名类似vectorTaskCode/vector_task_code的字段。"
                + "用户说成功/失败时，优先匹配检测结果、审核结果等枚举字段；不确定时仍按最相关字段生成计划。"
                + "每个step的limit默认500，最大500。"
                + "输出格式：{\"reportCode\":\"plasmid_quality_report\",\"reportName\":\"质粒质检报表\","
                + "\"steps\":[{\"stepCode\":\"summary\",\"sheetName\":\"项目汇总\",\"queryPlan\":"
                + "{\"domain\":\"plasmid_quality\",\"queryType\":\"aggregate\",\"selectFields\":[],"
                + "\"metrics\":[\"totalCount\",\"passCount\",\"failCount\",\"passRate\"],\"dimensions\":[\"projectCode\"],"
                + "\"filters\":[],\"orderBy\":[],\"chartType\":\"table\",\"limit\":500}},"
                + "{\"stepCode\":\"detail\",\"sheetName\":\"gRNA明细\",\"queryPlan\":"
                + "{\"domain\":\"plasmid_quality\",\"queryType\":\"detail\","
                + "\"selectFields\":[\"projectCode\",\"plasmidName\",\"qualityInspectionType\",\"grnaSequence\",\"createTime\"],"
                + "\"metrics\":[],\"dimensions\":[],\"filters\":[{\"field\":\"qualityInspectionType\",\"op\":\"eq\",\"value\":\"3\"}],"
                + "\"orderBy\":[{\"field\":\"createTime\",\"direction\":\"desc\"}],\"chartType\":\"table\",\"limit\":500}}],"
                + "\"aggregations\":[]}。"
                + "当前支持的业务域：" + JSONUtil.toJsonStr(listSelectedDomains(question, domains));
    }

    private String buildRepairPrompt(String question, List<String> domains, String badContent) {
        return "你刚才的输出不是合法JSON。现在必须把它修正为一个后端可解析的JSON对象。"
                + "只输出JSON，不要输出解释、SQL、Markdown、代码块或自然语言。"
                + "JSON格式必须是：{\"reportCode\":\"...\",\"reportName\":\"...\",\"steps\":[{\"stepCode\":\"...\",\"sheetName\":\"...\",\"queryPlan\":{...}}],\"aggregations\":[]}。"
                + "queryPlan只能使用给定的domain、fields、metrics、dimensions，不能输出SQL。"
                + "queryType只能是aggregate/detail；aggregate必须返回metrics；detail必须返回selectFields。"
                + "用户问题：" + question
                + "。允许的业务域：" + JSONUtil.toJsonStr(listSelectedDomains(question, domains))
                + "。你刚才的错误输出：" + limitText(badContent, 3000);
    }

    private List<AiDomainPromptDTO> listSelectedDomains(String question, List<String> domains) {
        List<AiDomainPromptDTO> result = new ArrayList<>();
        List<String> tokens = tokenize(question);
        for (String domain : domains) {
            result.add(compactDomainPrompt(aiDomainRegistry.getForPrompt(domain), tokens));
        }
        return result;
    }

    private AiDomainPromptDTO compactDomainPrompt(AiDomainPromptDTO source, List<String> tokens) {
        AiDomainPromptDTO target = new AiDomainPromptDTO();
        target.setDomain(source.getDomain());
        target.setName(source.getName());
        target.setFields(limitFields(source.getFields(), tokens, 30));
        target.setDimensions(limitFields(source.getDimensions(), tokens, 20));
        target.setMetrics(limitMetrics(source.getMetrics(), tokens, 12));
        return target;
    }

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

    private int resolveMaxDomainPromptSize() {
        return aiProperties.getMaxDomainPromptSize() == null ? 80 : aiProperties.getMaxDomainPromptSize();
    }

    private String extractReportJson(String content, String question, List<String> domains) {
        try {
            return AiJsonExtractor.extractObject(content, "AI报表计划为空", "AI报表计划不是合法JSON");
        } catch (BusinessException e) {
            log.warn("AI报表计划原始返回无法解析，准备纠偏重试，content={}", limitText(content, 2000));
            String repairedContent = llmClient.chat(Arrays.asList(
                    new LlmChatMessageDTO("system", buildRepairPrompt(question, domains, content)),
                    new LlmChatMessageDTO("user", "请把上一次输出修正为合法JSON。只输出JSON对象。")
            ));
            try {
                return AiJsonExtractor.extractObject(repairedContent, "AI报表计划为空", "AI报表计划不是合法JSON");
            } catch (BusinessException retryException) {
                log.warn("AI报表计划纠偏返回仍无法解析，content={}", limitText(repairedContent, 2000));
                throw retryException;
            }
        }
    }

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
