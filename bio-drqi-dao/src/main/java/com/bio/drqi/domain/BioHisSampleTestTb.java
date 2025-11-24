package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * 取样检测信息表
 * @TableName bio_his_sample_test_tb
 */
@TableName(value ="bio_his_sample_test_tb")
public class BioHisSampleTestTb implements Serializable {
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
     * 物种
     */
    private String speciesCode;

    /**
     * 品种
     */
    private String breedCode;

    /**
     * 代次
     */
    private String generation;

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
     * 试验编号
     */
    private String experimentNum;

    /**
     * 区域
     */
    private String regionNum;

    /**
     * 种子编号
     */
    private String seedNum;

    /**
     * 检测原始结果
     */
    private byte[] testOrgResult;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 主键ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 载体任务编码
     */
    public String getVectorTaskCode() {
        return vectorTaskCode;
    }

    /**
     * 载体任务编码
     */
    public void setVectorTaskCode(String vectorTaskCode) {
        this.vectorTaskCode = vectorTaskCode;
    }

    /**
     * 取样编号
     */
    public String getSampleCode() {
        return sampleCode;
    }

    /**
     * 取样编号
     */
    public void setSampleCode(String sampleCode) {
        this.sampleCode = sampleCode;
    }

    /**
     * 取样申请时间
     */
    public Date getApplyTime() {
        return applyTime;
    }

    /**
     * 取样申请时间
     */
    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    /**
     * 取样申请人ID
     */
    public Integer getApplyUserId() {
        return applyUserId;
    }

    /**
     * 取样申请人ID
     */
    public void setApplyUserId(Integer applyUserId) {
        this.applyUserId = applyUserId;
    }

    /**
     * 取样申请人姓名
     */
    public String getApplyUserName() {
        return applyUserName;
    }

    /**
     * 取样申请人姓名
     */
    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    /**
     * 物种
     */
    public String getSpeciesCode() {
        return speciesCode;
    }

    /**
     * 物种
     */
    public void setSpeciesCode(String speciesCode) {
        this.speciesCode = speciesCode;
    }

    /**
     * 品种
     */
    public String getBreedCode() {
        return breedCode;
    }

    /**
     * 品种
     */
    public void setBreedCode(String breedCode) {
        this.breedCode = breedCode;
    }

    /**
     * 代次
     */
    public String getGeneration() {
        return generation;
    }

    /**
     * 代次
     */
    public void setGeneration(String generation) {
        this.generation = generation;
    }

    /**
     * 鉴定引物
     */
    public String getTestIdentifyPrimer() {
        return testIdentifyPrimer;
    }

    /**
     * 鉴定引物
     */
    public void setTestIdentifyPrimer(String testIdentifyPrimer) {
        this.testIdentifyPrimer = testIdentifyPrimer;
    }

    /**
     * 检测方法
     */
    public String getTestMethod() {
        return testMethod;
    }

    /**
     * 检测方法
     */
    public void setTestMethod(String testMethod) {
        this.testMethod = testMethod;
    }

    /**
     * 编辑类型
     */
    public String getTestEditType() {
        return testEditType;
    }

    /**
     * 编辑类型
     */
    public void setTestEditType(String testEditType) {
        this.testEditType = testEditType;
    }

    /**
     * 非转鉴定引物
     */
    public String getTestNoTransIdentityPrimer() {
        return testNoTransIdentityPrimer;
    }

    /**
     * 非转鉴定引物
     */
    public void setTestNoTransIdentityPrimer(String testNoTransIdentityPrimer) {
        this.testNoTransIdentityPrimer = testNoTransIdentityPrimer;
    }

    /**
     * 是否为转基因阳性
     */
    public String getTestIsGeneModifyPositive() {
        return testIsGeneModifyPositive;
    }

    /**
     * 是否为转基因阳性
     */
    public void setTestIsGeneModifyPositive(String testIsGeneModifyPositive) {
        this.testIsGeneModifyPositive = testIsGeneModifyPositive;
    }

    /**
     * 是否为定点插入
     */
    public String getTestIfFixedPoint() {
        return testIfFixedPoint;
    }

    /**
     * 是否为定点插入
     */
    public void setTestIfFixedPoint(String testIfFixedPoint) {
        this.testIfFixedPoint = testIfFixedPoint;
    }

    /**
     * 是否为单拷贝插入
     */
    public String getTestIfCopyInsert() {
        return testIfCopyInsert;
    }

    /**
     * 是否为单拷贝插入
     */
    public void setTestIfCopyInsert(String testIfCopyInsert) {
        this.testIfCopyInsert = testIfCopyInsert;
    }

    /**
     * 定点插入方式（定点纯合/定点杂合）
     */
    public String getTestFixedPointType() {
        return testFixedPointType;
    }

    /**
     * 定点插入方式（定点纯合/定点杂合）
     */
    public void setTestFixedPointType(String testFixedPointType) {
        this.testFixedPointType = testFixedPointType;
    }

    /**
     * donor载体残留情况
     */
    public String getTestDonorResidueInfo() {
        return testDonorResidueInfo;
    }

    /**
     * donor载体残留情况
     */
    public void setTestDonorResidueInfo(String testDonorResidueInfo) {
        this.testDonorResidueInfo = testDonorResidueInfo;
    }

    /**
     * 插入位点
     */
    public String getTestInsertionSite() {
        return testInsertionSite;
    }

    /**
     * 插入位点
     */
    public void setTestInsertionSite(String testInsertionSite) {
        this.testInsertionSite = testInsertionSite;
    }

    /**
     * ELISA结果（蛋白表达量）
     */
    public String getTestElisaResult() {
        return testElisaResult;
    }

    /**
     * ELISA结果（蛋白表达量）
     */
    public void setTestElisaResult(String testElisaResult) {
        this.testElisaResult = testElisaResult;
    }

    /**
     * qbzr表达量
     */
    public String getTestQbzrSeq() {
        return testQbzrSeq;
    }

    /**
     * qbzr表达量
     */
    public void setTestQbzrSeq(String testQbzrSeq) {
        this.testQbzrSeq = testQbzrSeq;
    }

    /**
     * 编辑工具残留情况
     */
    public String getTestEditResidueInfo() {
        return testEditResidueInfo;
    }

    /**
     * 编辑工具残留情况
     */
    public void setTestEditResidueInfo(String testEditResidueInfo) {
        this.testEditResidueInfo = testEditResidueInfo;
    }

    /**
     * 检测数据递送人ID
     */
    public Integer getTestUserId() {
        return testUserId;
    }

    /**
     * 检测数据递送人ID
     */
    public void setTestUserId(Integer testUserId) {
        this.testUserId = testUserId;
    }

    /**
     * 检测人
     */
    public String getTestUserName() {
        return testUserName;
    }

    /**
     * 检测人
     */
    public void setTestUserName(String testUserName) {
        this.testUserName = testUserName;
    }

    /**
     * 检测时间
     */
    public String getTestTime() {
        return testTime;
    }

    /**
     * 检测时间
     */
    public void setTestTime(String testTime) {
        this.testTime = testTime;
    }

    /**
     * 审核人姓名
     */
    public String getCheckUserName() {
        return checkUserName;
    }

    /**
     * 审核人姓名
     */
    public void setCheckUserName(String checkUserName) {
        this.checkUserName = checkUserName;
    }

    /**
     * 审核人ID
     */
    public Integer getCheckUserId() {
        return checkUserId;
    }

    /**
     * 审核人ID
     */
    public void setCheckUserId(Integer checkUserId) {
        this.checkUserId = checkUserId;
    }

    /**
     * 审查结果
     */
    public String getCheckResult() {
        return checkResult;
    }

    /**
     * 审查结果
     */
    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult;
    }

    /**
     * 创建日期
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建日期
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 更新日期
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 更新日期
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 取样申请编号
     */
    public String getApplyNo() {
        return applyNo;
    }

    /**
     * 取样申请编号
     */
    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
    }

    /**
     * 鉴定引物
     */
    public String getIdentifyPrimer() {
        return identifyPrimer;
    }

    /**
     * 鉴定引物
     */
    public void setIdentifyPrimer(String identifyPrimer) {
        this.identifyPrimer = identifyPrimer;
    }

    /**
     * 唯一约束
     */
    public String getUniqueCode() {
        return uniqueCode;
    }

    /**
     * 唯一约束
     */
    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    /**
     * 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 克隆苗
     */
    public String getCloneSampleCode() {
        return cloneSampleCode;
    }

    /**
     * 克隆苗
     */
    public void setCloneSampleCode(String cloneSampleCode) {
        this.cloneSampleCode = cloneSampleCode;
    }

    /**
     * 来源
     */
    public String getSourceCode() {
        return sourceCode;
    }

    /**
     * 来源
     */
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    /**
     * 试验编号
     */
    public String getExperimentNum() {
        return experimentNum;
    }

    /**
     * 试验编号
     */
    public void setExperimentNum(String experimentNum) {
        this.experimentNum = experimentNum;
    }

    /**
     * 区域
     */
    public String getRegionNum() {
        return regionNum;
    }

    /**
     * 区域
     */
    public void setRegionNum(String regionNum) {
        this.regionNum = regionNum;
    }

    /**
     * 种子编号
     */
    public String getSeedNum() {
        return seedNum;
    }

    /**
     * 种子编号
     */
    public void setSeedNum(String seedNum) {
        this.seedNum = seedNum;
    }

    /**
     * 检测原始结果
     */
    public byte[] getTestOrgResult() {
        return testOrgResult;
    }

    /**
     * 检测原始结果
     */
    public void setTestOrgResult(byte[] testOrgResult) {
        this.testOrgResult = testOrgResult;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        BioHisSampleTestTb other = (BioHisSampleTestTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getVectorTaskCode() == null ? other.getVectorTaskCode() == null : this.getVectorTaskCode().equals(other.getVectorTaskCode()))
            && (this.getSampleCode() == null ? other.getSampleCode() == null : this.getSampleCode().equals(other.getSampleCode()))
            && (this.getApplyTime() == null ? other.getApplyTime() == null : this.getApplyTime().equals(other.getApplyTime()))
            && (this.getApplyUserId() == null ? other.getApplyUserId() == null : this.getApplyUserId().equals(other.getApplyUserId()))
            && (this.getApplyUserName() == null ? other.getApplyUserName() == null : this.getApplyUserName().equals(other.getApplyUserName()))
            && (this.getSpeciesCode() == null ? other.getSpeciesCode() == null : this.getSpeciesCode().equals(other.getSpeciesCode()))
            && (this.getBreedCode() == null ? other.getBreedCode() == null : this.getBreedCode().equals(other.getBreedCode()))
            && (this.getGeneration() == null ? other.getGeneration() == null : this.getGeneration().equals(other.getGeneration()))
            && (this.getTestIdentifyPrimer() == null ? other.getTestIdentifyPrimer() == null : this.getTestIdentifyPrimer().equals(other.getTestIdentifyPrimer()))
            && (this.getTestMethod() == null ? other.getTestMethod() == null : this.getTestMethod().equals(other.getTestMethod()))
            && (this.getTestEditType() == null ? other.getTestEditType() == null : this.getTestEditType().equals(other.getTestEditType()))
            && (this.getTestNoTransIdentityPrimer() == null ? other.getTestNoTransIdentityPrimer() == null : this.getTestNoTransIdentityPrimer().equals(other.getTestNoTransIdentityPrimer()))
            && (this.getTestIsGeneModifyPositive() == null ? other.getTestIsGeneModifyPositive() == null : this.getTestIsGeneModifyPositive().equals(other.getTestIsGeneModifyPositive()))
            && (this.getTestIfFixedPoint() == null ? other.getTestIfFixedPoint() == null : this.getTestIfFixedPoint().equals(other.getTestIfFixedPoint()))
            && (this.getTestIfCopyInsert() == null ? other.getTestIfCopyInsert() == null : this.getTestIfCopyInsert().equals(other.getTestIfCopyInsert()))
            && (this.getTestFixedPointType() == null ? other.getTestFixedPointType() == null : this.getTestFixedPointType().equals(other.getTestFixedPointType()))
            && (this.getTestDonorResidueInfo() == null ? other.getTestDonorResidueInfo() == null : this.getTestDonorResidueInfo().equals(other.getTestDonorResidueInfo()))
            && (this.getTestInsertionSite() == null ? other.getTestInsertionSite() == null : this.getTestInsertionSite().equals(other.getTestInsertionSite()))
            && (this.getTestElisaResult() == null ? other.getTestElisaResult() == null : this.getTestElisaResult().equals(other.getTestElisaResult()))
            && (this.getTestQbzrSeq() == null ? other.getTestQbzrSeq() == null : this.getTestQbzrSeq().equals(other.getTestQbzrSeq()))
            && (this.getTestEditResidueInfo() == null ? other.getTestEditResidueInfo() == null : this.getTestEditResidueInfo().equals(other.getTestEditResidueInfo()))
            && (this.getTestUserId() == null ? other.getTestUserId() == null : this.getTestUserId().equals(other.getTestUserId()))
            && (this.getTestUserName() == null ? other.getTestUserName() == null : this.getTestUserName().equals(other.getTestUserName()))
            && (this.getTestTime() == null ? other.getTestTime() == null : this.getTestTime().equals(other.getTestTime()))
            && (this.getCheckUserName() == null ? other.getCheckUserName() == null : this.getCheckUserName().equals(other.getCheckUserName()))
            && (this.getCheckUserId() == null ? other.getCheckUserId() == null : this.getCheckUserId().equals(other.getCheckUserId()))
            && (this.getCheckResult() == null ? other.getCheckResult() == null : this.getCheckResult().equals(other.getCheckResult()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getApplyNo() == null ? other.getApplyNo() == null : this.getApplyNo().equals(other.getApplyNo()))
            && (this.getIdentifyPrimer() == null ? other.getIdentifyPrimer() == null : this.getIdentifyPrimer().equals(other.getIdentifyPrimer()))
            && (this.getUniqueCode() == null ? other.getUniqueCode() == null : this.getUniqueCode().equals(other.getUniqueCode()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
            && (this.getCloneSampleCode() == null ? other.getCloneSampleCode() == null : this.getCloneSampleCode().equals(other.getCloneSampleCode()))
            && (this.getSourceCode() == null ? other.getSourceCode() == null : this.getSourceCode().equals(other.getSourceCode()))
            && (this.getExperimentNum() == null ? other.getExperimentNum() == null : this.getExperimentNum().equals(other.getExperimentNum()))
            && (this.getRegionNum() == null ? other.getRegionNum() == null : this.getRegionNum().equals(other.getRegionNum()))
            && (this.getSeedNum() == null ? other.getSeedNum() == null : this.getSeedNum().equals(other.getSeedNum()))
            && (Arrays.equals(this.getTestOrgResult(), other.getTestOrgResult()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getVectorTaskCode() == null) ? 0 : getVectorTaskCode().hashCode());
        result = prime * result + ((getSampleCode() == null) ? 0 : getSampleCode().hashCode());
        result = prime * result + ((getApplyTime() == null) ? 0 : getApplyTime().hashCode());
        result = prime * result + ((getApplyUserId() == null) ? 0 : getApplyUserId().hashCode());
        result = prime * result + ((getApplyUserName() == null) ? 0 : getApplyUserName().hashCode());
        result = prime * result + ((getSpeciesCode() == null) ? 0 : getSpeciesCode().hashCode());
        result = prime * result + ((getBreedCode() == null) ? 0 : getBreedCode().hashCode());
        result = prime * result + ((getGeneration() == null) ? 0 : getGeneration().hashCode());
        result = prime * result + ((getTestIdentifyPrimer() == null) ? 0 : getTestIdentifyPrimer().hashCode());
        result = prime * result + ((getTestMethod() == null) ? 0 : getTestMethod().hashCode());
        result = prime * result + ((getTestEditType() == null) ? 0 : getTestEditType().hashCode());
        result = prime * result + ((getTestNoTransIdentityPrimer() == null) ? 0 : getTestNoTransIdentityPrimer().hashCode());
        result = prime * result + ((getTestIsGeneModifyPositive() == null) ? 0 : getTestIsGeneModifyPositive().hashCode());
        result = prime * result + ((getTestIfFixedPoint() == null) ? 0 : getTestIfFixedPoint().hashCode());
        result = prime * result + ((getTestIfCopyInsert() == null) ? 0 : getTestIfCopyInsert().hashCode());
        result = prime * result + ((getTestFixedPointType() == null) ? 0 : getTestFixedPointType().hashCode());
        result = prime * result + ((getTestDonorResidueInfo() == null) ? 0 : getTestDonorResidueInfo().hashCode());
        result = prime * result + ((getTestInsertionSite() == null) ? 0 : getTestInsertionSite().hashCode());
        result = prime * result + ((getTestElisaResult() == null) ? 0 : getTestElisaResult().hashCode());
        result = prime * result + ((getTestQbzrSeq() == null) ? 0 : getTestQbzrSeq().hashCode());
        result = prime * result + ((getTestEditResidueInfo() == null) ? 0 : getTestEditResidueInfo().hashCode());
        result = prime * result + ((getTestUserId() == null) ? 0 : getTestUserId().hashCode());
        result = prime * result + ((getTestUserName() == null) ? 0 : getTestUserName().hashCode());
        result = prime * result + ((getTestTime() == null) ? 0 : getTestTime().hashCode());
        result = prime * result + ((getCheckUserName() == null) ? 0 : getCheckUserName().hashCode());
        result = prime * result + ((getCheckUserId() == null) ? 0 : getCheckUserId().hashCode());
        result = prime * result + ((getCheckResult() == null) ? 0 : getCheckResult().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getApplyNo() == null) ? 0 : getApplyNo().hashCode());
        result = prime * result + ((getIdentifyPrimer() == null) ? 0 : getIdentifyPrimer().hashCode());
        result = prime * result + ((getUniqueCode() == null) ? 0 : getUniqueCode().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getCloneSampleCode() == null) ? 0 : getCloneSampleCode().hashCode());
        result = prime * result + ((getSourceCode() == null) ? 0 : getSourceCode().hashCode());
        result = prime * result + ((getExperimentNum() == null) ? 0 : getExperimentNum().hashCode());
        result = prime * result + ((getRegionNum() == null) ? 0 : getRegionNum().hashCode());
        result = prime * result + ((getSeedNum() == null) ? 0 : getSeedNum().hashCode());
        result = prime * result + (Arrays.hashCode(getTestOrgResult()));
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", vectorTaskCode=").append(vectorTaskCode);
        sb.append(", sampleCode=").append(sampleCode);
        sb.append(", applyTime=").append(applyTime);
        sb.append(", applyUserId=").append(applyUserId);
        sb.append(", applyUserName=").append(applyUserName);
        sb.append(", speciesCode=").append(speciesCode);
        sb.append(", breedCode=").append(breedCode);
        sb.append(", generation=").append(generation);
        sb.append(", testIdentifyPrimer=").append(testIdentifyPrimer);
        sb.append(", testMethod=").append(testMethod);
        sb.append(", testEditType=").append(testEditType);
        sb.append(", testNoTransIdentityPrimer=").append(testNoTransIdentityPrimer);
        sb.append(", testIsGeneModifyPositive=").append(testIsGeneModifyPositive);
        sb.append(", testIfFixedPoint=").append(testIfFixedPoint);
        sb.append(", testIfCopyInsert=").append(testIfCopyInsert);
        sb.append(", testFixedPointType=").append(testFixedPointType);
        sb.append(", testDonorResidueInfo=").append(testDonorResidueInfo);
        sb.append(", testInsertionSite=").append(testInsertionSite);
        sb.append(", testElisaResult=").append(testElisaResult);
        sb.append(", testQbzrSeq=").append(testQbzrSeq);
        sb.append(", testEditResidueInfo=").append(testEditResidueInfo);
        sb.append(", testUserId=").append(testUserId);
        sb.append(", testUserName=").append(testUserName);
        sb.append(", testTime=").append(testTime);
        sb.append(", checkUserName=").append(checkUserName);
        sb.append(", checkUserId=").append(checkUserId);
        sb.append(", checkResult=").append(checkResult);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", applyNo=").append(applyNo);
        sb.append(", identifyPrimer=").append(identifyPrimer);
        sb.append(", uniqueCode=").append(uniqueCode);
        sb.append(", remark=").append(remark);
        sb.append(", cloneSampleCode=").append(cloneSampleCode);
        sb.append(", sourceCode=").append(sourceCode);
        sb.append(", experimentNum=").append(experimentNum);
        sb.append(", regionNum=").append(regionNum);
        sb.append(", seedNum=").append(seedNum);
        sb.append(", testOrgResult=").append(testOrgResult);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}