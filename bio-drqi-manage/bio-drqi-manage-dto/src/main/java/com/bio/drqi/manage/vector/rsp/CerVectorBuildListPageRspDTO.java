package com.bio.drqi.manage.vector.rsp;


import lombok.Data;

@Data
public class CerVectorBuildListPageRspDTO {

    private Integer id;

    /**
     * 载体构建任务
     */
    private Integer vectorTaskId;

    /**
     * 载体构建任务
     */
    private String vectorTaskCode;

    /**
     * 质粒名称
     */
    private String plasmidName;

    /**
     * 靶位点
     */
    private String targetSite;

    /**
     * 细菌抗性
     */
    private String bacterialResistance;

    /**
     * 质粒特异性引物
     */
    private String plasmidSpecificPrimers;

    /**
     * 细菌复制子
     */
    private String bacterialReplicon;

    /**
     * 拷贝数
     */
    private String copyNumber;

    /**
     * 农杆菌信息
     */
    private String agrobacteriumInformation;

    /**
     * 植物筛选标记
     */
    private String  selectionMarker;

    /**
     * 外源基因
     */
    private String foreignGene;

    /**
     * 目标特性
     */
    private String geneCharacter;

    /**
     * 靶基因
     */
    private String targetGene;

    /**
     * PAM
     */
    private String pam;

    /**
     * 备注
     */
    private String remark;

    private String fileUrls;

    /**
     * 期望阳性苗
     */
    private Integer expectedPositiveVaccine;

    /**
     * 质质检结果  空未质检 pass已通过 refuse未通过
     */
    private String qualityInspectionResult;

    /**
     * 目的条带大小
     */
    private String destinationStripeSize;

    /**
     * 载体大小
     */
    private String vectorSize;


    private String taskNum;


}
