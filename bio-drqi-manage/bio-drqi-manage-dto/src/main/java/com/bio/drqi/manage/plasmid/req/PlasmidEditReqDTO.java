package com.bio.drqi.manage.plasmid.req;

import com.bio.drqi.manage.validator.EnumValue;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PlasmidEditReqDTO {
    /**主键ID*/
    @NotNull(message = "主键ID缺失")
    private Integer vectorId;

    /**质检结果*/
    @EnumValue(strValues = {"合格","不合格"},message = "质检结果数据非法")
    private String qualityInspectionResult;

    /**弄杆菌信息*/
    private String agrobacteriumInformation;


    /**质检日期*/
    @NotBlank(message = "质检日期缺失")
    private String qualityInspectionDate;
}
