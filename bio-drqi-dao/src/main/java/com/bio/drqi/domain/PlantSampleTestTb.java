package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bio.common.core.util.StringUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * 取样检测信息表
 *
 * @TableName plant_sample_test_tb
 */
@TableName(value = "plant_sample_test_tb")
@Data
public class PlantSampleTestTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 载体任务编码
     */
    private String vectorTaskCode;

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

    /**
     * 受体材料
     */
    private String acceptorMaterial;

    /**
     * 取样申请编号
     */
    private String applyNo;

    /**
     * 鉴定引物
     */
    private String identifyPrimer;

    /**
     * 唯一约束
     */
    private String uniqueCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 克隆苗
     */
    private String cloneSampleCode;

    /**
     * 来源
     */
    private String sourceCode;

    /**
     * 检测原始结果
     */
    private String testOrgResult;

    private String generation;

    private String speciesCode;

    private String breedCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    public static PlantSampleTestTb of(String vectorTaskCode,String generation, String breedCode,String speciesCode,String sampleCode, BioTaskDtlTb bioTaskDtlTb, String sourceCode,String uniqueCode) {
        PlantSampleTestTb plantSampleTestTb = new PlantSampleTestTb();
        plantSampleTestTb.setVectorTaskCode(vectorTaskCode);
        plantSampleTestTb.setSampleCode(sampleCode);
        plantSampleTestTb.setGeneration(generation);
        plantSampleTestTb.setBreedCode(breedCode);
        plantSampleTestTb.setSpeciesCode(speciesCode);
        plantSampleTestTb.setApplyTime(bioTaskDtlTb.getApplyTime());
        plantSampleTestTb.setApplyUserId(bioTaskDtlTb.getApplyUserId());
        plantSampleTestTb.setApplyUserName(bioTaskDtlTb.getApplyUserName());
        plantSampleTestTb.setSourceCode(sourceCode);
        plantSampleTestTb.setTestIdentifyPrimer(null);
        plantSampleTestTb.setTestMethod(null);
        plantSampleTestTb.setTestEditType(null);
        plantSampleTestTb.setTestNoTransIdentityPrimer(null);
        plantSampleTestTb.setTestIsGeneModifyPositive(null);
        plantSampleTestTb.setTestIfFixedPoint(null);
        plantSampleTestTb.setTestIfCopyInsert(null);
        plantSampleTestTb.setTestFixedPointType(null);
        plantSampleTestTb.setTestDonorResidueInfo(null);
        plantSampleTestTb.setTestInsertionSite(null);
        plantSampleTestTb.setTestElisaResult(null);
        plantSampleTestTb.setTestQbzrSeq(null);
        plantSampleTestTb.setTestEditResidueInfo(null);
        plantSampleTestTb.setTestUserId(null);
        plantSampleTestTb.setTestUserName(null);
        plantSampleTestTb.setTestTime(null);
        plantSampleTestTb.setCheckUserName(null);
        plantSampleTestTb.setCheckUserId(null);
        plantSampleTestTb.setCheckResult(null);
        plantSampleTestTb.setCreateTime(new Date());
        plantSampleTestTb.setUpdateTime(new Date());
        plantSampleTestTb.setApplyNo(bioTaskDtlTb.getTaskNum());
        plantSampleTestTb.setIdentifyPrimer(null);
        if(StringUtils.isNotEmpty(uniqueCode)){
            plantSampleTestTb.setUniqueCode(uniqueCode);
        }
        plantSampleTestTb.setRemark(null);
        plantSampleTestTb.setCloneSampleCode(null);
        plantSampleTestTb.setTestOrgResult(null);
        return plantSampleTestTb;
    }

}