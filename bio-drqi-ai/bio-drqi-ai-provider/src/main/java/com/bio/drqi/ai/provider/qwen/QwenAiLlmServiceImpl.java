package com.bio.drqi.ai.provider.qwen;

import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.api.llm.AiLlmService;
import com.bio.drqi.ai.api.llm.dto.AiLlmChatReqDTO;
import com.bio.drqi.ai.api.llm.dto.AiLlmChatRspDTO;
import com.bio.drqi.ai.api.llm.dto.AiLlmMessageDTO;
import com.bio.drqi.ai.provider.config.AiLlmProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * 阿里千问 OpenAI 兼容接口实现。
 */
@Service
public class QwenAiLlmServiceImpl implements AiLlmService {

    private static final String CHAT_COMPLETIONS_PATH = "/chat/completions";

    @Resource
    private AiLlmProperties aiLlmProperties;

    @Override
    public AiLlmChatRspDTO chat(AiLlmChatReqDTO reqDTO) {
        validate(reqDTO);

        JSONObject body = buildRequestBody(reqDTO);
        String url = buildChatCompletionsUrl(aiLlmProperties.getBaseUrl());
        int timeout = reqDTO.getTimeoutMs() == null ? aiLlmProperties.getTimeout() : reqDTO.getTimeoutMs();

        HttpResponse response = HttpRequest.post(url)
                .header(Header.AUTHORIZATION, "Bearer " + aiLlmProperties.getApiKey())
                .contentType(ContentType.JSON.getValue())
                .timeout(timeout)
                .body(body.toJSONString())
                .execute();

        String responseBody = response.body();
        if (!response.isOk()) {
            throw new BusinessException("千问模型调用失败，HTTP状态：" + response.getStatus() + "，响应：" + responseBody);
        }

        return parseResponse(responseBody);
    }

    private JSONObject buildRequestBody(AiLlmChatReqDTO reqDTO) {
        JSONObject body = new JSONObject();
        body.put("model", firstText(reqDTO.getModel(), aiLlmProperties.getModel()));
        body.put("temperature", firstDecimal(reqDTO.getTemperature(), aiLlmProperties.getTemperature()));
        body.put("stream", Boolean.FALSE);
        if (reqDTO.getMaxTokens() != null) {
            body.put("max_tokens", reqDTO.getMaxTokens());
        }

        JSONArray messages = new JSONArray();
        for (AiLlmMessageDTO messageDTO : reqDTO.getMessages()) {
            if (messageDTO == null || !hasText(messageDTO.getRole()) || !hasText(messageDTO.getContent())) {
                continue;
            }
            JSONObject message = new JSONObject();
            message.put("role", messageDTO.getRole());
            message.put("content", messageDTO.getContent());
            messages.add(message);
        }
        body.put("messages", messages);
        return body;
    }

    private AiLlmChatRspDTO parseResponse(String responseBody) {
        JSONObject jsonObject = JSONObject.parseObject(responseBody);
        JSONArray choices = jsonObject.getJSONArray("choices");
        if (choices == null || choices.isEmpty()) {
            throw new BusinessException("千问模型响应缺少 choices：" + responseBody);
        }

        JSONObject firstChoice = choices.getJSONObject(0);
        JSONObject message = firstChoice.getJSONObject("message");
        if (message == null) {
            throw new BusinessException("千问模型响应缺少 message：" + responseBody);
        }

        AiLlmChatRspDTO rspDTO = new AiLlmChatRspDTO();
        rspDTO.setContent(message.getString("content"));
        rspDTO.setModel(jsonObject.getString("model"));
        rspDTO.setResponseId(jsonObject.getString("id"));
        rspDTO.setRawResponse(responseBody);

        JSONObject usage = jsonObject.getJSONObject("usage");
        if (usage != null) {
            rspDTO.setPromptTokens(usage.getInteger("prompt_tokens"));
            rspDTO.setCompletionTokens(usage.getInteger("completion_tokens"));
            rspDTO.setTotalTokens(usage.getInteger("total_tokens"));
        }
        return rspDTO;
    }

    private void validate(AiLlmChatReqDTO reqDTO) {
        if (reqDTO == null || reqDTO.getMessages() == null || reqDTO.getMessages().isEmpty()) {
            throw new BusinessException("模型消息不能为空");
        }
        if (!hasText(aiLlmProperties.getBaseUrl())) {
            throw new BusinessException("模型 base-url 未配置");
        }
        if (!hasText(aiLlmProperties.getApiKey())) {
            throw new BusinessException("模型 api-key 未配置");
        }
    }

    private String buildChatCompletionsUrl(String baseUrl) {
        String trimmed = baseUrl.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        if (trimmed.endsWith(CHAT_COMPLETIONS_PATH)) {
            return trimmed;
        }
        return trimmed + CHAT_COMPLETIONS_PATH;
    }

    private String firstText(String first, String second) {
        return hasText(first) ? first : second;
    }

    private BigDecimal firstDecimal(BigDecimal first, BigDecimal second) {
        return first == null ? second : first;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
