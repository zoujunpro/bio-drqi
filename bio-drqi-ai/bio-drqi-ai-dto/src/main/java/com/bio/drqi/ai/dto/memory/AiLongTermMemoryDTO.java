package com.bio.drqi.ai.dto.memory;

import lombok.Data;

/**
 * 长期记忆项。
 */
@Data
public class AiLongTermMemoryDTO {

    private String key;

    private String value;
}
