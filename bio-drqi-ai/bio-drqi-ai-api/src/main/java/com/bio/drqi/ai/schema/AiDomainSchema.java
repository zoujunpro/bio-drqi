package com.bio.drqi.ai.schema;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class AiDomainSchema {

    /**
     * 业务域编码，模型输出的 domain 必须匹配这里的值。
     */
    private String domain;

    /**
     * 业务域中文名称，用于提示模型理解业务含义。
     */
    private String name;

    /**
     * 后端执行查询时使用的主表名，不直接交给模型生成 SQL。
     */
    private String tableName;

    /**
     * 主表别名。为空时不使用别名。
     */
    private String tableAlias;

    /**
     * 当前业务域允许使用的 join 白名单。
     */
    private Map<String, AiJoinSchema> joins = new LinkedHashMap<>();

    /**
     * 可过滤、可排序的字段白名单。
     */
    private Map<String, AiFieldSchema> fields = new LinkedHashMap<>();

    /**
     * 可分组统计的维度白名单。
     */
    private Map<String, AiFieldSchema> dimensions = new LinkedHashMap<>();

    /**
     * 可统计的指标白名单。
     */
    private Map<String, AiMetricSchema> metrics = new LinkedHashMap<>();
}
