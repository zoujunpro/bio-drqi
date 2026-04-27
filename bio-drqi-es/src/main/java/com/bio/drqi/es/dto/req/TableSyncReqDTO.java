package com.bio.drqi.es.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TableSyncReqDTO {

    /**
     * 表名，支持 table 或 database.table
     */
    @NotBlank(message = "参数缺少：表名")
    private String tableName;

}
