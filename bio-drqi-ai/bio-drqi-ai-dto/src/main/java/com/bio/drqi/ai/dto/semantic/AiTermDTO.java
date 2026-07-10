package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;

/**
 * 业务术语。
 */
@Data
public class AiTermDTO implements Serializable {

    /**
     * 用户命中的词。
     */
    private String term;

    /**
     * 标准业务编码。
     */
    private String mappedCode;

    /**
     * 标准业务名称。
     */
    private String mappedName;

    /**
     * 术语类型，例如 PROJECT、CROP、BASE、DEPARTMENT。
     */
    private String termType;

    /**
     * 来源：DICTIONARY/RULE/LLM。
     */
    private String source;

    private static final long serialVersionUID = 1L;
}
