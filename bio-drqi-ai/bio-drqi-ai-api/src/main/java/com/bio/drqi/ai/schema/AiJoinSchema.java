package com.bio.drqi.ai.schema;

import lombok.Data;

@Data
public class AiJoinSchema {

    /**
     * join 表名。
     */
    private String tableName;

    /**
     * join 表别名。
     */
    private String alias;

    /**
     * join 类型，例如 left join / inner join。
     */
    private String joinType;

    /**
     * 固定 on 条件，只能由后端配置，不能由模型生成。
     */
    private String onExpression;
}
