package com.bio.drqi.ai.dto.memory;

import lombok.Data;

/**
 * 短期对话消息。
 */
@Data
public class AiMemoryMessageDTO {

    private String role;

    private String content;
}
