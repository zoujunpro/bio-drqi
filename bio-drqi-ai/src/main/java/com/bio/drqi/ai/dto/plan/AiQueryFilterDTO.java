package com.bio.drqi.ai.dto.plan;

import lombok.Data;

@Data
public class AiQueryFilterDTO {

    /**
     * 过滤字段，必须是业务域 schema.fields 中声明过的字段名。
     */
    private String field;

    /**
     * eq/in/like/between/gte/lte/last_days/last_months
     */
    private String op;

    /**
     * 过滤值，格式由 op 决定：eq 是单值，in/between 通常是数组。
     */
    private Object value;
}
