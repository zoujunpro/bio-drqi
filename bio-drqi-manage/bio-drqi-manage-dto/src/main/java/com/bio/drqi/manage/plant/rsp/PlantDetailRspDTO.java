package com.bio.drqi.manage.plant.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class PlantDetailRspDTO {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 编辑类型
     */
    private String editType;

    /**
     * 品种
     */
    private String breed;

    /**
     * 种子编号/取样编号
     */
    private String cerNumber;

    /**
     * 代次
     */
    private String generation;

    /**
     * 温室编号
     */
    private String greenhouseNumber;

    /**
     * 株树
     */
    private Integer plantNumber;

    /**
     * 播种/移苗日期
     */
    private String firstPlantDate;

    /**
     * 移栽日期
     */
    private String transplantDate;

    /**
     * 植株状态
     */
    private String plantStatus;

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
     * 父本信息
     */
    private String fatherInfo;

    /**
     * 母本信息
     */
    private String motherInfo;

    /**
     * 散粉期
     */
    private String powderDispersionDate;

    /**
     * 吐丝期
     */
    private String spinningDate;

    /**
     * 抽穗期
     */
    private String headingDate;

    /**
     * 开花期
     */
    private String floweringDate;

    /**
     * 鼓粒期
     */
    private String podFillDate;

    /**
     * 授粉时间
     */
    private String pollinationDate;

    /**
     * 收获日期
     */
    private String harvestDate;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 更新日期
     */
    private Date updateTime;

    /**
     * 所属项目ID
     */
    private Integer projectId;

    /**
     * 所属项目编码
     */
    private String projectCode;

    /**
     * 所属项目名称
     */
    private String projectName;

    /**
     * 其他字段
     */
    private String otherField;
}
