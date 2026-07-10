package com.bio.drqi.ai.dto.memory;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 保存长期记忆请求。
 */
@Data
public class AiLongTermMemorySaveReqDTO {

    private String userId;

    private String memoryType;

    private String memoryKey;

    private String memoryValue;

    private String source;

    private BigDecimal confidence;
}
