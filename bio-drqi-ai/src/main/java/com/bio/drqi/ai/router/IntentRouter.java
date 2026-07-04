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
import com.bio.drqi.ai.prompt.RouterPrompt;
import com.bio.drqi.ai.service.AiIntentKeywordService;
import com.bio.drqi.ai.util.AiJsonExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * AI请求入口路由。
 * 先用规则处理明显场景；规则无法确认时，再让模型按 prompt 输出 intent JSON。
 */
@Component
@Slf4j
public class IntentRouter {

    public static final String INTENT_CHAT = "chat";
    public static final String INTENT_BUSINESS_QUERY = "business_query";
    public static final String INTENT_REPORT_EXPORT = "report_export";
    public static final String INTENT_WORKFLOW = "workflow";
    public static final String INTENT_UNKNOWN = "unknown";
    private static final List<String> CONTEXT_REFERENCE_WORDS = Arrays.asList(
            "这些", "上述", "上面", "刚才", "刚刚", "前面", "它们", "他们", "该项目", "这些项目", "这些数据"
    );
    private static final List<String> CONTEXT_BUSINESS_WORDS = Arrays.asList(
            "项目", "实施方案", "方案", "种子", "样品", "取样", "数据", "信息", "明细", "统计", "整理", "查看", "查询"
    );

    @Resource
    private LlmClient llmClient;

    @Resource
    private AiProperties aiProperties;

    @Resource
    private AiIntentKeywordService aiIntentKeywordService;

    public String route(String question) {
        return route(question, null);
    }

    public String route(String question, AiConversationContextDTO context) {
        if (StrUtil.isBlank(question)) {
            return INTENT_CHAT;
        }
        if (isContextBusinessQuery(question, context)) {
            return INTENT_BUSINESS_QUERY;
        }
        if (containsAny(question, aiIntentKeywordService.listKeywords(INTENT_REPORT_EXPORT))) {
            return INTENT_REPORT_EXPORT;
        }
        if (containsAny(question, aiIntentKeywordService.listKeywords(INTENT_WORKFLOW))) {
            return INTENT_WORKFLOW;
        }
        if (containsAny(question, aiIntentKeywordService.listKeywords(INTENT_BUSINESS_QUERY))) {
            return INTENT_BUSINESS_QUERY;
        }
        if (containsAny(question, aiIntentKeywordService.listKeywords(INTENT_CHAT))) {
            return INTENT_CHAT;
        }
        return routeByModel(question);
    }

    private boolean isContextBusinessQuery(String question, AiConversationContextDTO context) {
        if (context == null || StrUtil.isBlank(context.getLastResultSnapshot())) {
            return false;
        }
        return containsAny(question, CONTEXT_REFERENCE_WORDS) && containsAny(question, CONTEXT_BUSINESS_WORDS);
    }

    private String routeByModel(String question) {
        long startTime = System.currentTimeMillis();
        try {
            String content = llmClient.chat(Arrays.asList(
                    new LlmChatMessageDTO("system", RouterPrompt.intentPrompt()),
                    new LlmChatMessageDTO("user", question)
            ), LlmCallOptionsDTO.of("router", aiProperties.getLlm().getRouterTemperature()));
            String json = AiJsonExtractor.extractObject(content, "AI意图识别为空", "AI意图识别不是合法JSON");
            AiIntentDTO intentDTO = JSONUtil.toBean(json, AiIntentDTO.class);
            String intent = normalizeIntent(intentDTO == null ? null : intentDTO.getIntent());
            Double confidence = intentDTO == null ? null : intentDTO.getConfidence();
            log.info("AI意图识别完成，cost={}ms，intent={}，confidence={}，reason={}，question={}",
                    System.currentTimeMillis() - startTime, intent, confidence,
                    intentDTO == null ? null : intentDTO.getReason(), question);
            double minConfidence = aiProperties.getIntent().getConfidenceThreshold() == null ? 0.6D : aiProperties.getIntent().getConfidenceThreshold();
            if (INTENT_UNKNOWN.equals(intent) || confidence == null || confidence < minConfidence) {
                return INTENT_UNKNOWN;
            }
            return intent;
        } catch (BusinessException e) {
            log.warn("AI意图识别失败，降级为普通问答，question={}，message={}", question, e.getMessage());
            return INTENT_CHAT;
        } catch (Exception e) {
            log.warn("AI意图识别异常，降级为普通问答，question={}", question, e);
            return INTENT_CHAT;
        }
    }

    private String normalizeIntent(String intent) {
        if (INTENT_CHAT.equals(intent)
                || INTENT_BUSINESS_QUERY.equals(intent)
                || INTENT_REPORT_EXPORT.equals(intent)
                || INTENT_WORKFLOW.equals(intent)
                || INTENT_UNKNOWN.equals(intent)) {
            return intent;
        }
        return INTENT_UNKNOWN;
    }

    private boolean containsAny(String text, java.util.List<String> words) {
        if (words == null || words.isEmpty()) {
            return false;
        }
        for (String word : words) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;
    }
}
