package com.bio.drqi.manage.dto.project;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SampleDataDTO {

    @NotBlank(message = "文件不能为空")
    private String excelUrl;

}
