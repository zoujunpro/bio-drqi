package com.bio.drqi.ai.tool.adapter.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.bio.drqi.ai.common.enums.AiToolTypeEnum;
import com.bio.drqi.ai.dao.domain.AiToolDefinition;
import com.bio.drqi.ai.dto.tool.AiToolExecuteReqDTO;
import com.bio.drqi.ai.dto.tool.AiToolExecuteRspDTO;
import com.bio.drqi.ai.tool.adapter.AiToolAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * HTTP API 工具适配器。
 */
@Component
public class ApiToolAdapter implements AiToolAdapter {

    @Value("${ai.tool.api-base-url:}")
    private String apiBaseUrl;

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Override
    public String supportToolType() {
        return AiToolTypeEnum.API.getCode();
    }

    @Override
    public AiToolExecuteRspDTO execute(AiToolDefinition tool, AiToolExecuteReqDTO reqDTO) {
        long start = System.currentTimeMillis();
        try {
            HttpRequest request = buildRequest(tool, reqDTO);
            try (HttpResponse response = request.execute()) {
                return AiToolExecuteRspDTO.success(
                        tool.getToolCode(),
                        tool.getToolType(),
                        tool.getTargetCode(),
                        response.getStatus(),
                        response.body(),
                        System.currentTimeMillis() - start
                );
            }
        } catch (Exception e) {
            return AiToolExecuteRspDTO.fail(
                    tool.getToolCode(),
                    tool.getToolType(),
                    tool.getTargetCode(),
                    e.getMessage(),
                    System.currentTimeMillis() - start
            );
        }
    }

    private HttpRequest buildRequest(AiToolDefinition tool, AiToolExecuteReqDTO reqDTO) {
        String method = tool.getHttpMethod() == null ? "POST" : tool.getHttpMethod().trim().toUpperCase();
        String url = buildUrl(tool.getServiceUrl());
        HttpRequest request;
        if ("GET".equals(method)) {
            request = HttpRequest.get(url);
        } else if ("PUT".equals(method)) {
            request = HttpRequest.put(url);
        } else if ("DELETE".equals(method)) {
            request = HttpRequest.delete(url);
        } else {
            request = HttpRequest.post(url);
        }
        request.header("Content-Type", "application/json;charset=UTF-8");
        request.header("x-ai-tool-code", tool.getToolCode());
        request.header("x-traceId", hasText(reqDTO.getSessionId()) ? reqDTO.getSessionId() : "ai-tool");
        request.header("userId", hasText(reqDTO.getUserId()) ? reqDTO.getUserId() : "0");
        request.header("username", hasText(reqDTO.getUsername()) ? reqDTO.getUsername() : "ai-tool");
        request.header("nickname", hasText(reqDTO.getNickname()) ? reqDTO.getNickname() : "AI助手");
        request.header("jobNum", hasText(reqDTO.getUsername()) ? reqDTO.getUsername() : "ai-tool");
        if (hasText(reqDTO.getUserId())) {
            request.header("x-ai-user-id", reqDTO.getUserId());
        }
        if (!"GET".equals(method) && hasText(reqDTO.getInputJson())) {
            request.body(reqDTO.getInputJson());
        }
        return request.timeout(30000);
    }

    private String buildUrl(String serviceUrl) {
        if (!hasText(serviceUrl)) {
            return serviceUrl;
        }
        String url = serviceUrl.trim();
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        String baseUrl = hasText(apiBaseUrl) ? apiBaseUrl.trim() : "http://127.0.0.1:" + serverPort;
        String normalizedContextPath = normalizePath(contextPath);
        String normalizedUrl = normalizePath(url);
        if (hasText(normalizedContextPath) && !normalizedUrl.startsWith(normalizedContextPath + "/")) {
            return trimEnd(baseUrl) + normalizedContextPath + normalizedUrl;
        }
        return trimEnd(baseUrl) + normalizedUrl;
    }

    private String normalizePath(String path) {
        if (!hasText(path)) {
            return "";
        }
        String normalized = path.trim();
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        while (normalized.endsWith("/") && normalized.length() > 1) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        return normalized;
    }

    private String trimEnd(String value) {
        String result = value;
        while (result.endsWith("/") && result.length() > 1) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
