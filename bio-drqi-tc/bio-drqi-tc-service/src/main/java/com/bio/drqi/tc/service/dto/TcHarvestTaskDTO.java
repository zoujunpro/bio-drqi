package com.bio.drqi.tc.service.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TcHarvestTaskDTO {

    /**
     * 授粉批次号
     */
    @NotBlank(message = "参数缺失：授粉批次号")
    private String pollinationApplyNum;


    /**
     *收获excel地址
     */
    @NotBlank(message = "参数缺失：收获excel地址")
    private String harvestFileUrl;


}
