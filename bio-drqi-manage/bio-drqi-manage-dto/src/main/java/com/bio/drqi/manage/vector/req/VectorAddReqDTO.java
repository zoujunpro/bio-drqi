package com.bio.drqi.manage.vector.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class VectorAddReqDTO {

    /**
     * 项目ID
     */
    @NotNull(message = "缺失项目ID")
    private Integer projectId;


    /**
     * 载体编号（子项目编号）
     */
    @NotBlank(message = "子项目编号缺失")
    @Size(max = 32, message = "子项目编号最大长度32")
    private String vectorCode;


    /**
     * 质粒名称
     */
    @NotBlank(message = "质粒名称缺失")
    @Size(max = 255, message = "质粒名称最大长度255")
    private String plasmidName;

    /**
     * 编辑工具
     */
    @NotBlank(message = "编辑工具缺失")
    @Size(max = 32, message = "编辑工具最大长度32")
    private String editTools;

    /**
     * 递送方式
     */
    @NotBlank(message = "递送方式缺失")
    @Size(max = 8, message = "递送方式最大长度8")
    private String deliveryMethod;

    /**
     * 靶位点
     */
    @NotBlank(message = "靶位点缺失")
    @Size(max = 255, message = "靶位点最大长度255")
    private String targetSite;

    /**
     * 细菌抗性
     */
    @Size(max = 255, message = "细菌抗性最大长度255")
    private String bacterialResistance;

    /**
     * 质粒特异性引物
     */
    @NotBlank(message = "质粒特异性引物缺失")
    @Size(max = 255, message = "质粒特异性引物最大长度255")
    private String plasmidSpecificPrimers;

    /**
     * 细菌复制子
     */
    @Size(max = 255, message = "细菌抗性最大长度255")
    private String bacterialReplicon;

    /**
     * 拷贝数
     */
    @Size(max = 32, message = "细菌抗性最大长度32")
    private String copyNumber;

    /**
     * 建议受体材料
     */
    @Size(max = 255, message = "细菌抗性最大长度255")
    private String acceptorMaterial;

    /**
     * 农杆菌信息
     */
    private String agrobacteriumInformation;

    /**
     * 植物筛选标记
     */
    private String selectionMarker;

    /**
     * 外源基因
     */
    private String foreignGene;

    /**
     * 目标特性
     */
    @Size(max = 255, message = "性状最大长度255")
    private String geneCharacter;

    /**
     * 靶基因
     */
    @Size(max = 255, message = "靶基因最大长度255")
    private String targetGene;

    /**
     * 备注
     */
    @Size(max = 255, message = "备注最大长度255")
    private String remark;


    /**
     * 载体构建时间
     */
    private String structureDate;

    /**
     * PAM
     */
    private String pam;


}
