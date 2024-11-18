package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 载体信息主表
 * @TableName cer_vector_tb
 */
@TableName(value ="cer_vector_tb")
@Data
public class CerVectorTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 载体构建任务
     */
    private Integer vectorTaskId;

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



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}