package com.bio.drqi.ai.provider.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 大模型配置。
 */
@Component
@ConfigurationProperties(prefix = "bio.ai.llm")
public class AiLlmProperties {

    /**
     * OpenAI 兼容接口地址，例如 https://dashscope.aliyuncs.com/compatible-mode/v1。
     */
    private String baseUrl;

    /**
     * API Key。
     */
    private String apiKey;

    /**
     * 默认模型。
     */
    private String model = "qwen-plus";

    /**
     * 默认温度。
     */
    private BigDecimal temperature = new BigDecimal("0.1");

    /**
     * 默认超时时间，单位毫秒。
     */
    private Integer timeout = 180000;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
