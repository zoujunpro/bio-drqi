package com.bio.drqi.ai.api.llm.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 大模型消息。
 */
@Data
public class AiLlmMessageDTO implements Serializable {

    /**
     * 角色：system/user/assistant/tool。
     */
    private String role;

    /**
     * 消息内容。
     */
    private String content;

    private static final long serialVersionUID = 1L;

    public AiLlmMessageDTO() {
    }

    public AiLlmMessageDTO(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
