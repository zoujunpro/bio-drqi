package com.bio.drqi.manage.dto.plant.task;

import com.bio.drqi.common.validator.EnumValue;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class PlantExperimentTaskDTO {


    private String sampleCodePrefix;

    @NotBlank(message = "物种必填")
    private String speciesCode;

    @NotEmpty(message = "试验类型必填")
    private List<String> experimentType;

    private String experimentTarget;

    private String fileUrl;

    @NotBlank(message = "试验方案设计必填")
    private String designUrl;

}
