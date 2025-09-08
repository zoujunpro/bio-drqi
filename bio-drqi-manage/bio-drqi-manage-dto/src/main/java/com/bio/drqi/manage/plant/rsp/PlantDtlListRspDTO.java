package com.bio.drqi.manage.plant.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class PlantDtlListRspDTO {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 所属项目ID
     */
    private Integer projectId;

    /**
     * 所属项目编码
     */
    private String projectCode;

    /**
     * 种子编号
     */
    private String plantCode;

    /**
     * 子项目编号
     */
    private String subProjectCode;

    /**
     * 子项目ID
     */
    private Integer subProjectId;

    /**
     * 转化组合名称
     */
    private String transformGroupName;

    /**
     * 任务ID
     */
    private Integer vectorTaskId;

    /**
     * 任务编码
     */
    private String vectorTaskCode;

    /**
     * 质粒信息
     */
    private String plasmidName;

    /**
     * 转化编号
     */
    private String transformCode;

    /**
     * 取样编号
     */
    private String sampleCode;

    /**
     * 代次
     */
    private String generation;

    /**
     * 株树
     */
    private Integer plantNumber;

    /**
     * 播种/移苗日期
     */
    private String plantDate;

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
     * 植株状态 1正常，2异常, 3已剔除
     */
    private String plantStatus;

    /**
     * 父本信息
     */
    private String fatherInfo;

    /**
     * 母本信息
     */
    private String motherInfo;

    /**
     * 授粉时间
     */
    private String pollinationDate;

    /**
     * 收获日期
     */
    private String harvestDate;

    /**
     * 其他字段
     */
    private Object otherField;

    /**
     * 编辑类型
     */
    private String editType;

    /**
     * 项目物种
     */
    private String speciesCode;

    /**
     * 项目物种
     */
    private String speciesName;

    /**
     * 受体材料
     */
    private String acceptorMaterial;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 更新日期
     */
    private Date updateTime;

}
