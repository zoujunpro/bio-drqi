package com.bio.drqi.es.dto.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class TablesSyncReqDTO {

    /**
     * 表名列表，支持 table 或 database.table
     */
    @NotEmpty(message = "参数缺少：表名列表")
    private List<String> tableNames;

    public List<String> getTableNames() {
        return tableNames;
    }

    public void setTableNames(List<String> tableNames) {
        this.tableNames = tableNames;
    }
}
