package com.bio.drqi.ai.client.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.client.LlmClient;
import com.bio.drqi.ai.config.AiProperties;
import com.bio.drqi.ai.dto.llm.LlmChatMessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class OpenAiCompatibleLlmClient implements LlmClient {

    @Resource
    private AiProperties aiProperties;

    @Override
    public String chat(List<LlmChatMessageDTO> messages) {
        AiProperties.Llm llm = aiProperties.getLlm();
        if (StrUtil.isBlank(llm.getBaseUrl())) {
            throw new BusinessException("AI模型地址未配置：bio.ai.llm.base-url");
        }

        // 使用 OpenAI 兼容协议，方便同时兼容 Ollama、vLLM 等本地部署方案。
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("model", llm.getModel());
        request.put("messages", messages);
        request.put("temperature", llm.getTemperature());
        request.put("stream", false);

        long startTime = System.currentTimeMillis();
        log.info("AI模型调用开始，baseUrl={}，model={}，messageCount={}", llm.getBaseUrl(), llm.getModel(), messages.size());
        HttpResponse response;
        try {
            response = HttpRequest.post(trimEnd(llm.getBaseUrl()) + "/chat/completions")
                    .header("Content-Type", "application/json")
                    .body(JSONUtil.toJsonStr(request))
                    .timeout(llm.getTimeout())
                    .execute();
        } catch (HttpException e) {
            log.error("AI模型调用异常，cost={}ms，baseUrl={}，model={}",
                    System.currentTimeMillis() - startTime, llm.getBaseUrl(), llm.getModel(), e);
            throw new BusinessException("AI模型响应超时，请缩小查询范围或稍后重试");
        }
        log.info("AI模型调用结束，cost={}ms，status={}", System.currentTimeMillis() - startTime, response.getStatus());
        if (!response.isOk()) {
            throw new BusinessException("AI模型调用失败，HTTP状态码：" + response.getStatus());
        }

        // 只取非流式响应的第一条 choice，模型输出内容由上层再做 JSON 提取和校验。
        JSONObject responseJson = JSONUtil.parseObj(response.body());
        JSONArray choices = responseJson.getJSONArray("choices");
        if (choices == null || choices.isEmpty()) {
            throw new BusinessException("AI模型返回为空");
        }
        JSONObject message = choices.getJSONObject(0).getJSONObject("message");
        if (message == null || StrUtil.isBlank(message.getStr("content"))) {
            throw new BusinessException("AI模型返回内容为空");
        }
        return message.getStr("content");
    }

    private String trimEnd(String baseUrl) {
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }
}
