package com.bio.drqi.manage.seed;

import lombok.Data;

@Data
public class SeedQualityCheckEditReqDTO {
    private Integer id;
    private String fieldName;
    private String fieldCode;
}
