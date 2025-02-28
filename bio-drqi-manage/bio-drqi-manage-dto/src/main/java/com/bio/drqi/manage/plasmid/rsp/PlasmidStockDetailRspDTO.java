package com.bio.drqi.manage.plasmid.rsp;


import lombok.Data;

@Data
public class PlasmidStockDetailRspDTO {

    /**主键ID*/
    private Integer projectId;

    /**项目名称*/
    private String projectName;

    /**项目编码*/
    private String projectCode;

    /**载体主键ID*/
    private Integer vectorId;

    /**子项目编号*/
    private String vectorCode;

    /**质粒名称*/
    private String plasmidName;

    /**编辑工具*/
    private String editTools;

    /**递送方式*/
    private String deliveryMethod;

    /**质检编号*/
    private String qualityInspectionNumber;

    /**质检结果*/
    private String qualityInspectionResult;


    /**质检日期*/
    private String qualityInspectionDate;
    /**弄杆菌信息*/
    private String agrobacteriumInformation;

    /**质检操作人*/
    private String  qualityInspectionUserName;





}
