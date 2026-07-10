package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 数量表达。
 */
@Data
public class AiNumberItemDTO implements Serializable {

    /**
     * 原始数量表达，例如 大于10个、最近3个月。
     */
    private String expression;

    /**
     * 比较符：GT/GTE/LT/LTE/EQ/BETWEEN。
     */
    private String operator;

    /**
     * 数值。
     */
    private BigDecimal value;

    /**
     * 第二个数值，范围表达时使用。
     */
    private BigDecimal secondValue;

    /**
     * 单位，例如 个、条、亩、kg、天、月。
     */
    private String unit;

    private static final long serialVersionUID = 1L;
}
