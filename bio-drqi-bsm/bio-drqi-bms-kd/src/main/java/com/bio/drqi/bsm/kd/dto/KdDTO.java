package com.bio.drqi.bsm.kd.dto;

import lombok.Data;

import java.util.List;

@Data
public class KdDTO {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 修改更新字段
     */
    private List<Field> fieldList;

    @Data
    public class Field{
        private String fieldName;
        private String fieldValue;
    }
}
