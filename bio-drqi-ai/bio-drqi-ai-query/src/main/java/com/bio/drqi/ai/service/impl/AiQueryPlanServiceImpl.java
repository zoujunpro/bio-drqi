package com.bio.drqi.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.client.LlmClient;
import com.bio.drqi.ai.config.AiProperties;
import com.bio.drqi.ai.dto.llm.LlmCallOptionsDTO;
import com.bio.drqi.ai.dto.plan.AiDomainSelectDTO;
import com.bio.drqi.ai.dto.llm.LlmChatMessageDTO;
import com.bio.drqi.ai.dto.memory.AiConversationContextDTO;
import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.exception.AiGeneralChatException;
import com.bio.drqi.ai.prompt.RouterPrompt;
import com.bio.drqi.ai.prompt.SqlPrompt;
import com.bio.drqi.ai.registry.AiDomainRegistry;
import com.bio.drqi.ai.service.AiLlmCacheService;
import com.bio.drqi.ai.service.AiTermService;
import com.bio.drqi.ai.service.AiQueryPlanService;
import com.bio.drqi.ai.util.AiJsonExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Resource
    private AiTermService aiTermService;

    @Resource
    private AiLlmCacheService aiLlmCacheService;

    @Override
    public AiQueryPlanDTO generate(String question, String preferredChartType) {
        return generate(question, preferredChartType, null);
    }

    @Override
    public AiQueryPlanDTO generate(String question, String preferredChartType, AiConversationContextDTO context) {
        long startTime = System.currentTimeMillis();
        List<AiProperties.Term> terms = aiTermService.recall(question);
        String domain = selectDomain(question, terms);
        if (GENERAL_CHAT_DOMAIN.equals(domain)) {
            throw new AiGeneralChatException();
        }
        log.info("AI业务域识别完成，cost={}ms，domain={}，question={}", System.currentTimeMillis() - startTime, domain, question);

        String contextPrompt = context == null ? null : context.toPromptText();
        String planCacheKey = buildQueryPlanCacheKey(question, preferredChartType, domain, terms, contextPrompt);
        AiQueryPlanDTO cachedPlan = aiLlmCacheService.get("query-plan", planCacheKey, AiQueryPlanDTO.class);
        if (cachedPlan != null) {
            log.info("AI查询计划命中缓存，domain={}，question={}", cachedPlan.getDomain(), question);
            return cachedPlan;
        }

        // system prompt 告诉模型只能按白名单生成 JSON；user prompt 只放用户原始问题。
        long planStartTime = System.currentTimeMillis();
        AiQueryPlanDTO plan;
        try {
            String content = llmClient.chat(Arrays.asList(
                    new LlmChatMessageDTO("system", buildSystemPrompt(preferredChartType, domain, terms, contextPrompt)),
                    new LlmChatMessageDTO("user", question)
            ), LlmCallOptionsDTO.of("query", aiProperties.getLlm().getQueryTemperature()));
            log.info("AI查询计划模型返回完成，cost={}ms，domain={}", System.currentTimeMillis() - planStartTime, domain);

            // 模型有时会包一层 ```json，这里先提取纯 JSON，再转成后端 DTO。
            String json = extractJson(content);
            plan = JSONUtil.toBean(json, AiQueryPlanDTO.class);
        } catch (BusinessException e) {
            log.warn("AI查询计划生成失败，启用保守兜底计划，domain={}，question={}，message={}", domain, question, e.getMessage());
            plan = fallbackPlan(question, preferredChartType, domain, terms);
        }
        if (StrUtil.isBlank(plan.getDomain())) {
            plan.setDomain(domain);
        }
        if (!domain.equals(plan.getDomain())) {
            throw new BusinessException("AI选择业务域和查询计划业务域不一致");
        }
        log.info("AI查询计划解析完成，domain={}，queryType={}，selectFields={}，metrics={}，dimensions={}，filters={}，orderBy={}，limit={}",
                plan.getDomain(), plan.getQueryType(), plan.getSelectFields(), plan.getMetrics(), plan.getDimensions(),
                plan.getFilters(), plan.getOrderBy(), plan.getLimit());
        aiLlmCacheService.set("query-plan", planCacheKey, plan, Duration.ofSeconds(cacheTtl(aiProperties.getCache().getQueryPlanTtlSeconds())));
        return plan;
    }

    private String selectDomain(String question, List<AiProperties.Term> terms) {
        long startTime = System.currentTimeMillis();
        String cacheKey = buildDomainSelectCacheKey(question, terms);
        AiDomainSelectDTO cachedDto = aiLlmCacheService.get("domain-select", cacheKey, AiDomainSelectDTO.class);
        if (cachedDto != null && StrUtil.isNotBlank(cachedDto.getDomain())) {
            if (!GENERAL_CHAT_DOMAIN.equals(cachedDto.getDomain())) {
                aiDomainRegistry.getRequired(cachedDto.getDomain());
            }
            log.info("AI业务域命中缓存，domain={}，question={}", cachedDto.getDomain(), question);
            return cachedDto.getDomain();
        }
        AiDomainSelectDTO dto;
        try {
            String content = llmClient.chat(Arrays.asList(
                    new LlmChatMessageDTO("system", buildDomainSelectPrompt(question, terms)),
                    new LlmChatMessageDTO("user", question)
            ), LlmCallOptionsDTO.of("router", aiProperties.getLlm().getRouterTemperature()));
            log.info("AI业务域模型返回完成，cost={}ms", System.currentTimeMillis() - startTime);
            String json = extractJson(content);
            dto = JSONUtil.toBean(json, AiDomainSelectDTO.class);
        } catch (BusinessException e) {
            String fallbackDomain = fallbackDomainByTerm(terms);
            if (StrUtil.isBlank(fallbackDomain)) {
                throw e;
            }
            log.warn("AI业务域识别失败，使用术语兜底业务域，domain={}，question={}，message={}", fallbackDomain, question, e.getMessage());
            dto = new AiDomainSelectDTO();
            dto.setDomain(fallbackDomain);
        }
        if (dto == null || StrUtil.isBlank(dto.getDomain())) {
            throw new BusinessException("AI未能识别查询业务域");
        }
        if (GENERAL_CHAT_DOMAIN.equals(dto.getDomain())) {
            aiLlmCacheService.set("domain-select", cacheKey, dto, Duration.ofSeconds(cacheTtl(aiProperties.getCache().getDomainSelectTtlSeconds())));
            return GENERAL_CHAT_DOMAIN;
        }
        aiDomainRegistry.getRequired(dto.getDomain());
        aiLlmCacheService.set("domain-select", cacheKey, dto, Duration.ofSeconds(cacheTtl(aiProperties.getCache().getDomainSelectTtlSeconds())));
        return dto.getDomain();
    }

    private String buildDomainSelectPrompt(String question, List<AiProperties.Term> terms) {
        int maxSize = aiProperties.getMaxDomainPromptSize() == null ? 80 : aiProperties.getMaxDomainPromptSize();
        return RouterPrompt.domainSelectPrompt(aiDomainRegistry.listSummaryForPrompt(question, maxSize), terms);
    }

    private String buildSystemPrompt(String preferredChartType, String domain, List<AiProperties.Term> terms, String contextPrompt) {
        // 注意：这里暴露给模型的是业务字段和指标名称，不暴露完整 SQL 执行能力。
        // 真正的表名、列名、表达式保存在后端 schema 中，后续执行时按白名单拼装。
        return SqlPrompt.queryPlanPrompt(preferredChartType, aiDomainRegistry.getForPrompt(domain),
                terms, contextPrompt);
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

    private String buildDomainSelectCacheKey(String question, List<AiProperties.Term> terms) {
        return JSONUtil.toJsonStr(Arrays.asList(
                safeText(question),
                JSONUtil.toJsonStr(terms),
                aiDomainRegistry.listSummaryForPrompt(question, aiProperties.getMaxDomainPromptSize() == null ? 80 : aiProperties.getMaxDomainPromptSize())
        ));
    }

    private String buildQueryPlanCacheKey(String question, String preferredChartType, String domain, List<AiProperties.Term> terms, String contextPrompt) {
        return JSONUtil.toJsonStr(Arrays.asList(
                safeText(question),
                StrUtil.blankToDefault(preferredChartType, ""),
                domain,
                JSONUtil.toJsonStr(terms),
                StrUtil.blankToDefault(contextPrompt, "")
        ));
    }

    private String safeText(String text) {
        return StrUtil.blankToDefault(text, "").trim();
    }

    private String fallbackDomainByTerm(List<AiProperties.Term> terms) {
        if (terms == null) {
            return null;
        }
        for (AiProperties.Term term : terms) {
            if (term != null && StrUtil.isNotBlank(term.getDomain()) && aiDomainRegistry.contains(term.getDomain())) {
                return term.getDomain();
            }
        }
        return null;
    }

    private AiQueryPlanDTO fallbackPlan(String question, String preferredChartType, String domain, List<AiProperties.Term> terms) {
        AiQueryPlanDTO plan = new AiQueryPlanDTO();
        plan.setDomain(domain);
        plan.setChartType(StrUtil.blankToDefault(preferredChartType, "table"));
        plan.setLimit(50);

        String metric = firstUsableMetric(domain, terms);
        if (StrUtil.isNotBlank(metric) || looksLikeAggregate(question)) {
            plan.setQueryType("aggregate");
            plan.setMetrics(new ArrayList<String>());
            plan.getMetrics().add(StrUtil.blankToDefault(metric, "totalCount"));
            return plan;
        }
        plan.setQueryType("detail");
        return plan;
    }

    private String firstUsableMetric(String domain, List<AiProperties.Term> terms) {
        if (terms != null) {
            for (AiProperties.Term term : terms) {
                if (term == null || StrUtil.isBlank(term.getMetric())) {
                    continue;
                }
                if (StrUtil.isBlank(term.getDomain()) || domain.equals(term.getDomain())) {
                    if (aiDomainRegistry.getRequired(domain).getMetrics().containsKey(term.getMetric())) {
                        return term.getMetric();
                    }
                }
            }
        }
        return aiDomainRegistry.getRequired(domain).getMetrics().containsKey("totalCount") ? "totalCount" : null;
    }

    private boolean looksLikeAggregate(String question) {
        String text = StrUtil.blankToDefault(question, "");
        return text.contains("统计") || text.contains("数量") || text.contains("多少") || text.contains("占比") || text.contains("趋势");
    }

    private long cacheTtl(Long ttlSeconds) {
        if (ttlSeconds == null || ttlSeconds <= 0) {
            return 1L;
        }
        return ttlSeconds;
    }

}
