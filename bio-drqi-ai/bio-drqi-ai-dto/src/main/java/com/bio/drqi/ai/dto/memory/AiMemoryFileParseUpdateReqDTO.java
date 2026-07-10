package com.bio.drqi.ai.dto.memory;

import lombok.Data;

/**
 * 更新 AI 会话文件解析结果请求。
 */
@Data
public class AiMemoryFileParseUpdateReqDTO {

    private String fileId;

    private String parseStatus;

    private String parsedText;

    private String summary;

    private String errorMessage;
}
