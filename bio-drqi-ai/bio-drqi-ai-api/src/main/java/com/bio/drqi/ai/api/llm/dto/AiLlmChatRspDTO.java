package com.bio.drqi.ai.api.llm.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 大模型聊天响应。
 */
@Data
public class AiLlmChatRspDTO implements Serializable {

    /**
     * 模型回复内容。
     */
    private String content;

    /**
     * 模型名称。
     */
    private String model;

    /**
     * 原始响应 ID。
     */
    private String responseId;

    /**
     * 输入 token 数。
     */
    private Integer promptTokens;

    /**
     * 输出 token 数。
     */
    private Integer completionTokens;

    /**
     * 总 token 数。
     */
    private Integer totalTokens;

    /**
     * 原始响应 JSON，便于排查。
     */
    private String rawResponse;

    private static final long serialVersionUID = 1L;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public Integer getPromptTokens() {
        return promptTokens;
    }

    public void setPromptTokens(Integer promptTokens) {
        this.promptTokens = promptTokens;
    }

    public Integer getCompletionTokens() {
        return completionTokens;
    }

    public void setCompletionTokens(Integer completionTokens) {
        this.completionTokens = completionTokens;
    }

    public Integer getTotalTokens() {
        return totalTokens;
    }

    public void setTotalTokens(Integer totalTokens) {
        this.totalTokens = totalTokens;
    }

    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }
}
