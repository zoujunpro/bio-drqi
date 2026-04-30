package com.bio.drqi.manage.plant.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class PlantSingleStockListPageRspDTO {

    private Integer id;

    /**
     * 种植编号
     */
    private String plantCode;

    /**
     * 代次
     */
    private String generation;

    /**
     * 株树
     */
    private Integer plantNumber;

    /**
     * 播种日期
     */
    private String plantDate;

    /**
     * 取样编号
     */
    private String sampleCode;

    /**
     * 移栽日期
     */
    private String transplantDate;

    /**
     * 春化开始日期
     */
    private String vernalizationBeginDate;

    /**
     * 春化结束日期
     */
    private String vernalizationEndDate;

    /**
     * 授粉方式
     */
    private String pollinationMethod;

    /**
     * 授粉方式
     */
    private String pollinationMethodName;

    /**
     * 植株状态 1正常，2异常, 3已剔除，4已收获
     */
    private String plantStatus;

    /**
     * 授粉时间
     */
    private String pollinationDate;

    /**
     * 收获日期
     */
    private String harvestDate;

    /**
     * 收获方式
     */
    private String harvestType;

    /**
     * 授粉方式
     */
    private String harvestTypeName;

    /**
     * 其他字段
     */
    private Object otherField;

    /**
     * 编辑类型
     */
    private String editType;

    /**
     * 物种
     */
    private String speciesCode;

    /**
     * 物种
     */
    private String speciesName;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 更新日期
     */
    private Date updateTime;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 品种
     */
    private String breedCode;

    /**
     * 品种
     */
    private String breedName;

    /**
     * 来源渠道 1项目，4种子库
     */
    private String sourceCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;


    private String pdImplementCode;

    private String seedNums;
}
