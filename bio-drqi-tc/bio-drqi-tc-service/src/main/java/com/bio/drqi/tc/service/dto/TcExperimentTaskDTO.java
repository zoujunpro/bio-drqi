package com.bio.drqi.tc.service.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class TcExperimentTaskDTO {


    @NotEmpty(message = "参数缺失：实施方案")
    private List<String> vectorTaskCodeList;

    private String experimentGoal;

    @NotEmpty(message = "参数缺失：试验地点")
    private String experimentAddressCode;

    private String experimentAddressName;

    private String experimentType;
    /**
     * 实验方案附件
     */
    @NotBlank(message = "参数缺失：实验方案附件")
    private String fileUrl;
    /**
     * 田间设计excel地址
     */
    private String experimentDesignUrl;


    private String speciesCode;


    private String speciesName;


    private String sampleCodePrefix;

    private String breedingFlag;


}
