package com.bio.drqi.ai.schema;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class AiFieldSchema {

    /**
     * 给模型和前端使用的字段名，例如 projectCode。
     */
    private String field;

    /**
     * 字段中文名，用于让模型理解问题。
     */
    private String label;

    /**
     * 后端查询执行时映射的数据库列或白名单表达式。
     */
    private String column;

    /**
     * 当前字段依赖的 join 别名。执行器会根据实际使用字段自动追加这些 join。
     */
    private List<String> requiredJoinAliases = new ArrayList<>();

    /**
     * 字段类型：string/date/enum 等。
     */
    private String type;

    /**
     * 枚举字段的值映射，例如 3 -> gRNA合成。
     */
    private Map<String, String> enumValues = new LinkedHashMap<>();
}
