package com.bio.drqi.manage.plant.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class PlantApplyListPageRspDTO {
    private Integer id;

    /**
     * 物种
     */
    private String speciesCode;

    /**
     * 试验类型 1供试  2分离提存 3扩繁  4法规测试
     */
    private String experimentType;
    /**
     * 种植目标
     */
    private String plantTarget;

    /**
     * 种植明细
     */
    private String plantDetailUrl;

    /**
     * 附件地址
     */
    private String fileUrl;

    /**
     * 种植申请编号
     */
    private String plantApplyNum;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private Integer createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 试验方案
     */
    private String vectorTaskCodes;


    private String pdImplementCodeList;

    private String sampleCodePrefix;
}
