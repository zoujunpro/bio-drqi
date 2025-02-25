package com.bio.drqi.manage.dto.seed;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SeedQualityCheckDTO {
    @NotBlank(message = "文件不能为空")
    private String excelUrl;
}
