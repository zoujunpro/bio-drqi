package com.bio.drqi.plant.dto.task;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ExperimentTaskDTO {


    @NotBlank(message = "物种必填")
    private String speciesCode;

    @NotBlank(message = "试验类型必填")
    private String experimentType;

    private String experimentTarget;

    private String fileUrl;

    @NotBlank(message = "试验方案设计必填")
    private String designUrl;
}
