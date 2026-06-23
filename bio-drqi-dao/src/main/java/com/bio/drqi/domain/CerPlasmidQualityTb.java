package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 质粒质检表
 * @TableName cer_plasmid_quality_tb
 */
@TableName(value ="cer_plasmid_quality_tb")
@Data
public class CerPlasmidQualityTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 子项目ID
     */
    private Integer subProjectId;

    /**
     * 项目ID
     */
    private Integer projectId;

    /**
     * 载体任务ID
     */
    private Integer vectorTaskId;

    /**
     * 质粒名称
     */
    private String plasmidName;

    /**
     * 质检编号
     */
    private String qualityInspectionNumber;

    /**
     * 质检结果
     */
    private String qualityInspectionResult;

    /**
     * 质检农杆菌信息
     */
    private String agrobacteriumInformation;

    /**
     * 质检人名称
     */
    private String createUserName;

    /**
     * 质检人ID
     */
    private Integer createUserId;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 质检类型（下一步安排）1质粒制备 2农杆菌转化 3gRNA合成
     */
    private String qualityInspectionType;

    /**
     * 农杆菌抗性
     */
    private String agrobacteriumResistance;

    /**
     * 质粒浓度
     */
    private String plasmidConcentration;

    /**
     * 提取试剂盒
     */
    private String extractionKit;

    /**
     * gRNA序列
     */
    private String grnaSequence;

    /**
     * 附件地址集合
     */
    private String fileUrls;

    /**
     * 任务状态 1审批中 2审批通过，3审批拒绝
     */
    private String taskStatus;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 子项目编号
     */
    private String subProjectCode;

    /**
     * 载体任务编号
     */
    private String vectorTaskCode;

    private String remark;

    /**
     * 农杆菌储存位置
     */
    private String agrobacteriumLocation;

    /**
     * 农杆菌制备时间
     */
    private String makingDate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}
