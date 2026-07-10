package com.bio.drqi.ai.dto.memory;

import lombok.Data;

/**
 * 保存 AI 会话文件请求。
 */
@Data
public class AiMemoryFileSaveReqDTO {

    private String sessionId;

    private Long messageId;

    private String userId;

    private String fileId;

    private String fileName;

    private String fileType;

    private String mimeType;

    private Long fileSize;

    private String bucketName;

    private String objectKey;

    private String fileUrl;

    private String parseStatus;
}
