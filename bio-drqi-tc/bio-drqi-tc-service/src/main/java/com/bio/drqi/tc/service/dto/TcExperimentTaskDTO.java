package com.bio.drqi.tc.service.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TcExperimentTaskDTO {

    @NotBlank(message = "参数缺失：项目信息")
    private String projectCode;
    @NotBlank(message = "参数缺失：项目信息")
    private String projectName;
    @NotBlank(message = "参数缺失：实施方案")
    private String vectorTaskName;
    @NotBlank(message = "参数缺失：实施方案")
    private String vectorTaskCode;

    private String experimentGoal;

    private String experimentAddress;
    /**
     * 实验方案附件
     */
    @NotBlank(message = "参数缺失：实验方案附件")
    private String fileUrl;
    /**
     * 田间设计excel地址
     */
    private String experimentDesignUrl;


}
