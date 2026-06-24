package com.bio.drqi.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.client.LlmClient;
import com.bio.drqi.ai.config.AiProperties;
import com.bio.drqi.ai.dto.plan.AiDomainSelectDTO;
import com.bio.drqi.ai.dto.llm.LlmChatMessageDTO;
import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.exception.AiGeneralChatException;
import com.bio.drqi.ai.registry.AiDomainRegistry;
import com.bio.drqi.ai.service.AiQueryPlanService;
import com.bio.drqi.ai.util.AiJsonExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;

@Service
@Slf4j
public class AiQueryPlanServiceImpl implements AiQueryPlanService {

    private static final String GENERAL_CHAT_DOMAIN = "general_chat";

    @Resource
    private LlmClient llmClient;

    @Resource
    private AiDomainRegistry aiDomainRegistry;

    @Resource
    private AiProperties aiProperties;

    @Override
    public AiQueryPlanDTO generate(String question, String preferredChartType) {
        long startTime = System.currentTimeMillis();
        String domain = selectDomain(question);
        if (GENERAL_CHAT_DOMAIN.equals(domain)) {
            throw new AiGeneralChatException();
        }
        log.info("AI业务域识别完成，cost={}ms，domain={}，question={}", System.currentTimeMillis() - startTime, domain, question);

        // system prompt 告诉模型只能按白名单生成 JSON；user prompt 只放用户原始问题。
        long planStartTime = System.currentTimeMillis();
        String content = llmClient.chat(Arrays.asList(
                new LlmChatMessageDTO("system", buildSystemPrompt(preferredChartType, domain)),
                new LlmChatMessageDTO("user", question)
        ));
        log.info("AI查询计划模型返回完成，cost={}ms，domain={}", System.currentTimeMillis() - planStartTime, domain);

        // 模型有时会包一层 ```json，这里先提取纯 JSON，再转成后端 DTO。
        String json = extractJson(content);
        AiQueryPlanDTO plan = JSONUtil.toBean(json, AiQueryPlanDTO.class);
        if (StrUtil.isBlank(plan.getDomain())) {
            plan.setDomain(domain);
        }
        if (!domain.equals(plan.getDomain())) {
            throw new BusinessException("AI选择业务域和查询计划业务域不一致");
        }
        log.info("AI查询计划解析完成，domain={}，queryType={}，selectFields={}，metrics={}，dimensions={}，filters={}，orderBy={}，limit={}",
                plan.getDomain(), plan.getQueryType(), plan.getSelectFields(), plan.getMetrics(), plan.getDimensions(),
                plan.getFilters(), plan.getOrderBy(), plan.getLimit());
        return plan;
    }

    private String selectDomain(String question) {
        long startTime = System.currentTimeMillis();
        String content = llmClient.chat(Arrays.asList(
                new LlmChatMessageDTO("system", buildDomainSelectPrompt()),
                new LlmChatMessageDTO("user", question)
        ));
        log.info("AI业务域模型返回完成，cost={}ms", System.currentTimeMillis() - startTime);
        String json = extractJson(content);
        AiDomainSelectDTO dto = JSONUtil.toBean(json, AiDomainSelectDTO.class);
        if (dto == null || StrUtil.isBlank(dto.getDomain())) {
            throw new BusinessException("AI未能识别查询业务域");
        }
        if (GENERAL_CHAT_DOMAIN.equals(dto.getDomain())) {
            return GENERAL_CHAT_DOMAIN;
        }
        aiDomainRegistry.getRequired(dto.getDomain());
        return dto.getDomain();
    }

    private String buildDomainSelectPrompt() {
        int maxSize = aiProperties.getMaxDomainPromptSize() == null ? 80 : aiProperties.getMaxDomainPromptSize();
        return "你是业务域选择器，只能输出JSON，不要输出解释。"
                + "只能从给定domains中选择一个最匹配用户问题的domain。"
                + "如果用户问的是闲聊、知识问答、系统无关问题，或者不是查询本系统业务数据，返回{\"domain\":\"general_chat\"}。"
                + "如果用户问题可能是查询本系统业务数据，优先选择最匹配的业务域，不要返回general_chat。"
                + "输出格式：{\"domain\":\"plasmid_quality\"}。"
                + "当前可选domains：" + JSONUtil.toJsonStr(aiDomainRegistry.listSummaryForPrompt(maxSize))
                + "，额外可选domain：general_chat";
    }

    private String buildSystemPrompt(String preferredChartType, String domain) {
        String chartType = StrUtil.blankToDefault(preferredChartType, "auto");
        // 注意：这里暴露给模型的是业务字段和指标名称，不暴露完整 SQL 执行能力。
        // 真正的表名、列名、表达式保存在后端 schema 中，后续执行时按白名单拼装。
        return "你是业务查询计划生成器，只能输出JSON，不要输出解释，不要输出SQL。"
                + "只能使用给定的domain、fields、metrics、dimensions。"
                + "queryType只能是aggregate/detail；统计数量、比例、趋势时用aggregate；查询列表、明细、最近几条记录时用detail。"
                + "queryType=aggregate时必须返回metrics，可选dimensions；queryType=detail时必须返回selectFields，metrics和dimensions返回空数组。"
                + "chartType只能是table/bar/line/pie/auto，用户偏好的chartType=" + chartType + "。"
                + "如果用户没有指定limit，默认100，最大500。"
                + "统计输出格式：{\"domain\":\"plasmid_quality\",\"queryType\":\"aggregate\",\"selectFields\":[],"
                + "\"metrics\":[\"totalCount\"],\"dimensions\":[\"projectCode\"],"
                + "\"filters\":[{\"field\":\"qualityInspectionType\",\"op\":\"eq\",\"value\":\"3\"}],"
                + "\"orderBy\":[],\"chartType\":\"bar\",\"limit\":100}。"
                + "明细输出格式：{\"domain\":\"plasmid_quality\",\"queryType\":\"detail\","
                + "\"selectFields\":[\"projectCode\",\"plasmidName\",\"qualityInspectionType\",\"createTime\"],"
                + "\"metrics\":[],\"dimensions\":[],\"filters\":[],\"orderBy\":[{\"field\":\"createTime\",\"direction\":\"desc\"}],"
                + "\"chartType\":\"table\",\"limit\":10}。"
                + "当前支持的业务域：" + JSONUtil.toJsonStr(aiDomainRegistry.getForPrompt(domain));
    }

    private String extractJson(String content) {
        try {
            return AiJsonExtractor.extractObject(content, "AI查询计划为空", "AI查询计划不是合法JSON");
        } catch (BusinessException e) {
            log.warn("AI查询计划原始返回无法解析，content={}", limitText(content, 2000));
            throw e;
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

}
