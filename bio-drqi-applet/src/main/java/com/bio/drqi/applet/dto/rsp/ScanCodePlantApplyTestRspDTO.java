package com.bio.drqi.applet.dto.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class ScanCodePlantApplyTestRspDTO {


    /**
     * PD号
     */
    private String pdImplementCode;

    /**
     * 种植申请编号
     */
    private String plantApplyNum;

    /**
     * 区域
     */
    private String regionNum;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;

    /**
     * 种子编号
     */
    private String seedNum;

    /**
     * 品种
     */
    private String breedCode;

    /**
     * 种植编号
     */
    private String plantCode;

    /**
     * 代次编号
     */
    private String generationCode;

    /**
     * 物种
     */
    private String speciesCode;

    /**
     * 播种时间
     */
    private String plantTime;

    /**
     * 播种数量
     */
    private Integer plantNumber;


    /**
     * 播种单位
     */
    private String plantUnit;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 基因型
     */
    private String geneType;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 创建时间
     */
    private Date createTime;


}
