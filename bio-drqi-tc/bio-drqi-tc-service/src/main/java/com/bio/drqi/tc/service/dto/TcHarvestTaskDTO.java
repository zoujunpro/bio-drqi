package com.bio.drqi.tc.service.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class TcHarvestTaskDTO {
    /**
     * 实验编号
     */
    @NotBlank(message = "参数缺失：请选择试验")
    private String experimentNum;


    private String harvestTime;

    /**
     *收获excel地址
     */
    @NotBlank(message = "参数缺失：收获excel地址")
    private String harvestFileUrl;


    private List<TcHarvestExcelDTO>  tcHarvestExcelDTOList;



}
