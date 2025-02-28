package com.bio.drqi.manage.vector.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class VectorDetailRspDTO {
    /**
     * 主键ID
     */
    private Integer id;


    /**
     * 载体编号（子项目编号）
     */
    private String vectorCode;

    /**
     * 工号
     */
    private String jobNum;

    /**
     * 基因编辑类型 1转基因 2基因编辑
     */
    private String geneEditType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 质粒名称
     */
    private String plasmidName;

    /**
     * 编辑工具
     */
    private String editTools;

    /**
     * 递送方式
     */
    private String deliveryMethod;

    /**
     * 目标特性
     */
    private String geneCharacter;

    /**
     * 细菌抗性
     */
    private String bacterialResistance;

    /**
     * 靶基因
     */
    private String targetGene;

    /**
     * 靶位点
     */
    private String targetSite;

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
     * 受体材料
     */
    private String acceptorMaterial;


    /**
     * 项目物种
     */
    private String species;


    /**
     * 农杆菌信息
     */
    private String agrobacteriumInformation;

    /**
     * 植物筛选标记
     */
    private String selectionMarker;

    /**
     * 备注
     */
    private String remark;

    /**
     * 提交日期
     */
    private String submitDate;

    /**
     * 外源基因
     */
    private String foreignGene;

    /**
     * 载体构建日期
     */
    private String structureDate;

    /**
     * PAM
     */
    private String pam;




}
