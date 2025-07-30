package com.bio.drqi.manage.seed;

import lombok.Data;

@Data
public class SeedQualityCheckRspDTO {
    private Integer id;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段编码
     */
    private String fieldCode;
}
