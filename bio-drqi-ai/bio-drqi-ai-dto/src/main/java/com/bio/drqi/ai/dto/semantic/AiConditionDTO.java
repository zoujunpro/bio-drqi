package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询条件。
 */
@Data
public class AiConditionDTO implements Serializable {

    /**
     * 条件字段，例如 status、number、date。
     */
    private String field;

    /**
     * 操作符，例如 EQ、GT、GTE、LT、LTE、BETWEEN、LIKE。
     */
    private String operator;

    /**
     * 条件值。
     */
    private String value;

    /**
     * 第二个条件值，范围表达时使用。
     */
    private String secondValue;

    /**
     * 值类型，例如 STRING/NUMBER/DATE。
     */
    private String valueType;

    /**
     * 来源：RULE/DICTIONARY/LLM。
     */
    private String source;

    private static final long serialVersionUID = 1L;
}
