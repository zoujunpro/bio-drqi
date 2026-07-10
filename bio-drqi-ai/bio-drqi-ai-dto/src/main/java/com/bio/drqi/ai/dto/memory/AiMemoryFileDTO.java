package com.bio.drqi.ai.dto.memory;

import lombok.Data;

/**
 * AI 会话文件上下文。
 */
@Data
public class AiMemoryFileDTO {

    private String fileId;

    private String fileName;

    private String fileType;

    private String mimeType;

    private Long fileSize;

    private String bucketName;

    private String objectKey;

    private String fileUrl;

    private String parseStatus;

    private String parsedText;

    private String summary;
}
