package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bio.common.core.util.StringUtils;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 取样检测信息表
 *
 * @TableName bio_sample_test_tb
 */
@TableName(value = "bio_sample_test_tb")
@Data
public class BioSampleTestTb implements Serializable {
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

    private String experimentNum;

    private String regionNum;

    private String seedNum;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


    public static BioSampleTestTb of(String seedNum,String regionNum,String experimentNum,String vectorTaskCode, String generation, String breedCode, String speciesCode, String sampleCode, BioTaskDtlTb bioTaskDtlTb, String sourceCode, String uniqueCode) {
        BioSampleTestTb bioSampleTestTb = new BioSampleTestTb();
        bioSampleTestTb.setVectorTaskCode(vectorTaskCode);
        bioSampleTestTb.setSampleCode(sampleCode);
        bioSampleTestTb.setGeneration(generation);
        bioSampleTestTb.setBreedCode(breedCode);
        bioSampleTestTb.setSpeciesCode(speciesCode);
        bioSampleTestTb.setSeedNum(seedNum);
        bioSampleTestTb.setExperimentNum(regionNum);
        bioSampleTestTb.setRegionNum(experimentNum);
        bioSampleTestTb.setApplyTime(bioTaskDtlTb.getApplyTime());
        bioSampleTestTb.setApplyUserId(bioTaskDtlTb.getApplyUserId());
        bioSampleTestTb.setApplyUserName(bioTaskDtlTb.getApplyUserName());
        bioSampleTestTb.setSourceCode(sourceCode);
        bioSampleTestTb.setTestIdentifyPrimer(null);
        bioSampleTestTb.setTestMethod(null);
        bioSampleTestTb.setTestEditType(null);
        bioSampleTestTb.setTestNoTransIdentityPrimer(null);
        bioSampleTestTb.setTestIsGeneModifyPositive(null);
        bioSampleTestTb.setTestIfFixedPoint(null);
        bioSampleTestTb.setTestIfCopyInsert(null);
        bioSampleTestTb.setTestFixedPointType(null);
        bioSampleTestTb.setTestDonorResidueInfo(null);
        bioSampleTestTb.setTestInsertionSite(null);
        bioSampleTestTb.setTestElisaResult(null);
        bioSampleTestTb.setTestQbzrSeq(null);
        bioSampleTestTb.setTestEditResidueInfo(null);
        bioSampleTestTb.setTestUserId(null);
        bioSampleTestTb.setTestUserName(null);
        bioSampleTestTb.setTestTime(null);
        bioSampleTestTb.setCheckUserName(null);
        bioSampleTestTb.setCheckUserId(null);
        bioSampleTestTb.setCheckResult(null);
        bioSampleTestTb.setCreateTime(new Date());
        bioSampleTestTb.setUpdateTime(new Date());
        bioSampleTestTb.setApplyNo(bioTaskDtlTb.getTaskNum());
        bioSampleTestTb.setIdentifyPrimer(null);
        if(StringUtils.isNotEmpty(uniqueCode)){
            bioSampleTestTb.setUniqueCode(uniqueCode);
        }
        bioSampleTestTb.setRemark(null);
        bioSampleTestTb.setCloneSampleCode(null);
        bioSampleTestTb.setTestOrgResult(null);
        return bioSampleTestTb;
    }

}