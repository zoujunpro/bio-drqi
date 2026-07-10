package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;

/**
 * 语义实体。
 */
@Data
public class AiEntityDTO implements Serializable {

    /**
     * 实体名称，例如 projectCode、sampleCode。
     */
    private String name;

    /**
     * 实体类型，例如 PROJECT、DATE、CER_SAMPLE。
     */
    private String type;

    /**
     * 实体值。
     */
    private String value;

    /**
     * 来源：RULE/LLM/MEMORY。
     */
    private String source;

    private static final long serialVersionUID = 1L;
}
