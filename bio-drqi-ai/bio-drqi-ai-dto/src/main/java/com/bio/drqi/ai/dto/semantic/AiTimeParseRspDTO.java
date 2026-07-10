package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;

/**
 * 时间解析结果。
 */
@Data
public class AiTimeParseRspDTO implements Serializable {

    /**
     * 是否识别到时间表达。
     */
    private Boolean matched;

    /**
     * 原始时间表达。
     */
    private String expression;

    /**
     * 开始日期，格式 yyyy-MM-dd。
     */
    private String startDate;

    /**
     * 结束日期，格式 yyyy-MM-dd。
     */
    private String endDate;

    private static final long serialVersionUID = 1L;
}
