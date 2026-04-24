package com.bio.drqi.es.api;

import lombok.Data;

@Data
public class TableSyncRequest {

    /**
     * 表名，支持 table 或 database.table
     */
    private String tableName;

    /**
     * ES 文档主键字段，默认 id
     */
    private String idField = "id";
}
