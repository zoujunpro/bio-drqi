package com.bio.drqi.ai.dto.memory;

import lombok.Data;

/**
 * 保存消息请求。
 */
@Data
public class AiMemoryMessageReqDTO {

    private String sessionId;

    private String userId;

    private String role;

    private String content;

    /**
     * 消息来源，例如 conversation/dify/tool/system/memory。
     */
    private String source;
}
