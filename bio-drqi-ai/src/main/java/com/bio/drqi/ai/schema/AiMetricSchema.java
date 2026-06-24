package com.bio.drqi.ai.schema;

import lombok.Data;

@Data
public class AiMetricSchema {

    /**
     * 给模型和前端使用的指标名，例如 totalCount。
     */
    private String metric;

    /**
     * 指标中文名。
     */
    private String label;

    /**
     * 指标类型：sql 表示可直接聚合，derived 表示由其他指标计算。
     */
    private String type;

    /**
     * 后端白名单表达式，禁止模型生成。
     */
    private String expression;
}
