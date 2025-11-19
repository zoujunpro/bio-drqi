package com.bio.drqi.plant.dto.task;

import cn.hutool.json.JSONUtil;
import com.bio.drqi.common.validator.EnumValue;
import lombok.Data;
import org.apache.tomcat.Jar;

import javax.validation.constraints.NotBlank;

@Data
public class PlantExperimentTaskDTO {


    private String sampleCodePrefix;

    @NotBlank(message = "物种必填")
    private String speciesCode;

    @EnumValue(message = "试验类型必填",strValues = {"1","2","3","4","5"})
    private String experimentType;

    private String experimentTarget;

    private String fileUrl;

    @NotBlank(message = "试验方案设计必填")
    private String designUrl;

}
