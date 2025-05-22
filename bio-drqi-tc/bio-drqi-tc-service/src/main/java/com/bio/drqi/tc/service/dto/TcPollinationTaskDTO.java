package com.bio.drqi.tc.service.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TcPollinationTaskDTO {

    @NotBlank(message = "参数缺失：试验编号")
    private String experimentNum;

    private String sampleApplyNum;

    @NotBlank(message = "参数缺失：授粉方式")
    private String pollinationType;

    private String pollinationName;

    @NotBlank(message = "参数缺失：授粉表单")
    private String pollinationExcelUrl;
}
