package com.bio.drqi.ai.provider.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 向量模型配置。
 */
@Component
@ConfigurationProperties(prefix = "bio.ai.embedding")
public class AiEmbeddingProperties {

    /**
     * OpenAI 兼容接口地址，例如 https://dashscope.aliyuncs.com/compatible-mode/v1。
     */
    private String baseUrl;

    /**
     * API Key。
     */
    private String apiKey;

    /**
     * 默认向量模型。
     */
    private String model = "text-embedding-v4";

    /**
     * 默认向量维度。
     */
    private Integer dim = 1536;

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

    public Integer getDim() {
        return dim;
    }

    public void setDim(Integer dim) {
        this.dim = dim;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
