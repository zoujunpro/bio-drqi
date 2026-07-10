package com.bio.drqi.ai.dto.chat;

import lombok.Data;

/**
 * AI 聊天响应。
 */
@Data
public class AiChatRspDTO {

    /**
     * 当前会话 ID。
     */
    private String sessionId;

    /**
     * AI 回复内容。
     */
    private String answer;

    /**
     * 本次回复是否成功生成。
     */
    private Boolean success;

    /**
     * 错误码。成功时为空。
     */
    private String errorCode;

    /**
     * 错误消息。成功时为空。
     */
    private String errorMessage;
}
