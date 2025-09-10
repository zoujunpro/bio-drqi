package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * 取样检测信息表
 * @TableName cer_sample_test_tb
 */
@TableName(value ="cer_sample_test_tb")
@Data
public class CerSampleTestTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 项目ID
     */
    private Integer projectId;

    /**
     * 子项目ID
     */
    private Integer subProjectId;

    /**
     * 载体任务ID
     */
    private Integer vectorTaskId;

    /**
     * 项目编码
     */
    private String projectCode;
    /**
     * 受体材料
     */
    private String acceptorMaterial;

    /**
     * 子项目编码
     */
    private String subProjectCode;

    /**
     * 载体任务编码
     */
    private String vectorTaskCode;

    /**
     * 质粒名称
     */
    private String plasmidName;

    /**
     * 转化编号/种子编号
     */
    private String transformCode;

    /**
     * 取样编号
     */
    private String sampleCode;

    /**
     * 取样申请时间
     */
    private Date applyTime;

    /**
     * 取样申请人ID
     */
    private Integer applyUserId;

    /**
     * 取样申请人姓名
     */
    private String applyUserName;

    /**
     * 取样时间
     */
    private String sampleTime;

    /**
     * 取样备注
     */
    private String sampleRemark;

    /**
     * 代次
     */
    private String sampleGeneration;


    /**
     * 取样数据递送人姓名
     */
    private String sampleUserName;

    /**
     * 取样数据递送人ID
     */
    private Integer sampleUserId;

    /**
     * 鉴定引物
     */
    private String testIdentifyPrimer;

    /**
     * 检测方法
     */
    private String testMethod;

    /**
     * 编辑类型
     */
    private String testEditType;

    /**
     * 非转鉴定引物
     */
    private String testNoTransIdentityPrimer;

    /**
     * 是否为转基因阳性
     */
    private String testIsGeneModifyPositive;

    /**
     * 是否为定点插入
     */
    private String testIfFixedPoint;

    /**
     * 是否为单拷贝插入
     */
    private String testIfCopyInsert;

    /**
     * 定点插入方式（定点纯合/定点杂合）
     */
    private String testFixedPointType;

    /**
     * donor载体残留情况
     */
    private String testDonorResidueInfo;

    /**
     * 插入位点
     */
    private String testInsertionSite;

    /**
     * ELISA结果（蛋白表达量）
     */
    private String testElisaResult;

    /**
     * qbzr表达量
     */
    private String testQbzrSeq;

    /**
     * 编辑工具残留情况
     */
    private String testEditResidueInfo;

    /**
     * 检测数据递送人ID
     */
    private Integer testUserId;

    /**
     * 检测人
     */
    private String testUserName;

    /**
     * 检测时间
     */
    private String testTime;

    /**
     * 审核人姓名
     */
    private String checkUserName;

    /**
     * 审核人ID
     */
    private Integer checkUserId;

    /**
     * 审查结果
     */
    private String checkResult;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 更新日期
     */
    private Date updateTime;

    private String applyNo;

    private String identifyPrimer;


    private String remark;

    private String targetFlag;

    @TableField(exist = false)
    private Integer countNum;

    @TableField(exist = false)
    private String sampleMonth;


    @TableField(exist = false)
    private String cloneSampleFlag;

    private String uniqueCode;

    private String cloneSampleCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}