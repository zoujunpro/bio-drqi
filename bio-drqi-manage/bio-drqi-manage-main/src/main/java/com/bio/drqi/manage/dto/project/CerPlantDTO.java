package com.bio.drqi.manage.dto.project;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CerPlantDTO {
    @NotNull(message = "参数缺失：projectId")
    private Integer projectId;

    @NotNull(message = "参数缺失：subProjectId")
    private Integer subProjectId;

    @NotNull(message = "参数缺失：vectorTaskId")
    private String vectorTaskId;

    @NotBlank(message = "参数缺失：excelUrl")
    private String excelUrl;

    private String projectCode;

    private String projectName;

}
