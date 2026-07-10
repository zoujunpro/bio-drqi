package com.bio.drqi.ai.dto.chat;

import lombok.Data;

/**
 * AI 聊天文件上传响应。
 */
@Data
public class AiChatFileUploadRspDTO {

    private String sessionId;

    private String fileId;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private String objectKey;

    private String fileUrl;

    private String parseStatus;

    private String summary;

    private String errorMessage;
}
