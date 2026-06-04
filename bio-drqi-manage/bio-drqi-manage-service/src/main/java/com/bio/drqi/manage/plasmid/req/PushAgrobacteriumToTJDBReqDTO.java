package com.bio.drqi.manage.plasmid.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PushAgrobacteriumToTJDBReqDTO {

    /**
     * 质粒名称
     */
    @NotBlank(message = "参数缺失：质粒名称")
    private String plasmidName;

    /**
     * 农杆菌储存位置
     */
    @NotBlank(message = "参数缺失：农杆菌储存位置")
    private String agrobacteriumLocation;

    /**
     * 农杆菌抗性
     */
    @NotBlank(message = "参数缺失：农杆菌抗性")
    private String agrobacteriumResistance;

    /**
     * 农杆菌信息
     */
    @NotBlank(message = "参数缺失：农杆菌信息")
    private String agrobacteriumInformation;

    /**
     * 农杆菌制备时间
     */
    @NotBlank(message = "参数缺失：农杆菌制备时间")
    private String makingDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * 如果要覆盖，传入 T，会根据冰箱位置进行信息覆盖。
     */
    private String updateFlag;
}
