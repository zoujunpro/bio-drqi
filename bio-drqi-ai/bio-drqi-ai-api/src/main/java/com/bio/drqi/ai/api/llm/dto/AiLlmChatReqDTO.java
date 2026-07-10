package com.bio.drqi.ai.api.llm.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 大模型聊天请求。
 */
@Data
public class AiLlmChatReqDTO implements Serializable {

    /**
     * 模型名称；为空时使用配置默认模型。
     */
    private String model;

    /**
     * 消息列表。
     */
    private List<AiLlmMessageDTO> messages = new ArrayList<AiLlmMessageDTO>();

    /**
     * 温度；为空时使用配置默认值。
     */
    private BigDecimal temperature;

    /**
     * 最大输出 token 数。
     */
    private Integer maxTokens;

    /**
     * 请求超时时间，单位毫秒；为空时使用配置默认值。
     */
    private Integer timeoutMs;

    private static final long serialVersionUID = 1L;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<AiLlmMessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<AiLlmMessageDTO> messages) {
        this.messages = messages;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public Integer getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(Integer timeoutMs) {
        this.timeoutMs = timeoutMs;
    }
}
