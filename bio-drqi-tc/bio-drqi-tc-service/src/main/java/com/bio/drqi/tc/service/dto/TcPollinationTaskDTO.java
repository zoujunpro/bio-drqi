package com.bio.drqi.tc.service.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class TcPollinationTaskDTO {

    @NotBlank(message = "参数缺失：试验编号")
    private String experimentNum;

    private String sampleApplyNum;

    @NotBlank(message = "参数缺失：授粉方式")
    private String pollinationType;

    private String pollinationTypeName;

    @NotBlank(message = "参数缺失：授粉表单")
    private String pollinationExcelUrl;

    @Valid
    @NotEmpty(message = "数据缺失")
    private  List<TcPollinationExcelDTO> tcPollinationExcelDTOList;
}
