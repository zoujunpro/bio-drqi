package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName cer_sample_test_bio_info_result_tb
 */
@TableName(value ="cer_sample_test_bio_info_result_tb")
public class CerSampleTestBioInfoResultTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 申请编号
     */
    private String applyNo;

    /**
     * 取样编号
     */
    private String sampleCode;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;

    /**
     * 材料名称
     */
    private String sampleId;

    /**
     * 生信系统唯一编号
     */
    private String uniqueDbCode;

    /**
     * 测序编号
     */
    private String runId;

    /**
     * HapID
     */
    private String hapId;

    /**
     * 变异类型合计
     */
    private String varType;

    /**
     * 突变方向合计
     */
    private String mutate;

    /**
     * 变异类型占比(%)
     */
    private String ratio;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 生信分析结果确认状态 checked  none 
     */
    private String confirmStatus;

    /**
     * 分析编号
     */
    private String resultKey;

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
     * 申请编号
     */
    public String getApplyNo() {
        return applyNo;
    }

    /**
     * 申请编号
     */
    public void setApplyNo(String applyNo) {
        this.applyNo = applyNo;
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
     * 实施方案编号
     */
    public String getVectorTaskCode() {
        return vectorTaskCode;
    }

    /**
     * 实施方案编号
     */
    public void setVectorTaskCode(String vectorTaskCode) {
        this.vectorTaskCode = vectorTaskCode;
    }

    /**
     * 材料名称
     */
    public String getSampleId() {
        return sampleId;
    }

    /**
     * 材料名称
     */
    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    /**
     * 生信系统唯一编号
     */
    public String getUniqueDbCode() {
        return uniqueDbCode;
    }

    /**
     * 生信系统唯一编号
     */
    public void setUniqueDbCode(String uniqueDbCode) {
        this.uniqueDbCode = uniqueDbCode;
    }

    /**
     * 测序编号
     */
    public String getRunId() {
        return runId;
    }

    /**
     * 测序编号
     */
    public void setRunId(String runId) {
        this.runId = runId;
    }

    /**
     * HapID
     */
    public String getHapId() {
        return hapId;
    }

    /**
     * HapID
     */
    public void setHapId(String hapId) {
        this.hapId = hapId;
    }

    /**
     * 变异类型合计
     */
    public String getVarType() {
        return varType;
    }

    /**
     * 变异类型合计
     */
    public void setVarType(String varType) {
        this.varType = varType;
    }

    /**
     * 突变方向合计
     */
    public String getMutate() {
        return mutate;
    }

    /**
     * 突变方向合计
     */
    public void setMutate(String mutate) {
        this.mutate = mutate;
    }

    /**
     * 变异类型占比(%)
     */
    public String getRatio() {
        return ratio;
    }

    /**
     * 变异类型占比(%)
     */
    public void setRatio(String ratio) {
        this.ratio = ratio;
    }

    /**
     * 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 生信分析结果确认状态 checked  none 
     */
    public String getConfirmStatus() {
        return confirmStatus;
    }

    /**
     * 生信分析结果确认状态 checked  none 
     */
    public void setConfirmStatus(String confirmStatus) {
        this.confirmStatus = confirmStatus;
    }

    /**
     * 分析编号
     */
    public String getResultKey() {
        return resultKey;
    }

    /**
     * 分析编号
     */
    public void setResultKey(String resultKey) {
        this.resultKey = resultKey;
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
        CerSampleTestBioInfoResultTb other = (CerSampleTestBioInfoResultTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getApplyNo() == null ? other.getApplyNo() == null : this.getApplyNo().equals(other.getApplyNo()))
            && (this.getSampleCode() == null ? other.getSampleCode() == null : this.getSampleCode().equals(other.getSampleCode()))
            && (this.getVectorTaskCode() == null ? other.getVectorTaskCode() == null : this.getVectorTaskCode().equals(other.getVectorTaskCode()))
            && (this.getSampleId() == null ? other.getSampleId() == null : this.getSampleId().equals(other.getSampleId()))
            && (this.getUniqueDbCode() == null ? other.getUniqueDbCode() == null : this.getUniqueDbCode().equals(other.getUniqueDbCode()))
            && (this.getRunId() == null ? other.getRunId() == null : this.getRunId().equals(other.getRunId()))
            && (this.getHapId() == null ? other.getHapId() == null : this.getHapId().equals(other.getHapId()))
            && (this.getVarType() == null ? other.getVarType() == null : this.getVarType().equals(other.getVarType()))
            && (this.getMutate() == null ? other.getMutate() == null : this.getMutate().equals(other.getMutate()))
            && (this.getRatio() == null ? other.getRatio() == null : this.getRatio().equals(other.getRatio()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getConfirmStatus() == null ? other.getConfirmStatus() == null : this.getConfirmStatus().equals(other.getConfirmStatus()))
            && (this.getResultKey() == null ? other.getResultKey() == null : this.getResultKey().equals(other.getResultKey()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getApplyNo() == null) ? 0 : getApplyNo().hashCode());
        result = prime * result + ((getSampleCode() == null) ? 0 : getSampleCode().hashCode());
        result = prime * result + ((getVectorTaskCode() == null) ? 0 : getVectorTaskCode().hashCode());
        result = prime * result + ((getSampleId() == null) ? 0 : getSampleId().hashCode());
        result = prime * result + ((getUniqueDbCode() == null) ? 0 : getUniqueDbCode().hashCode());
        result = prime * result + ((getRunId() == null) ? 0 : getRunId().hashCode());
        result = prime * result + ((getHapId() == null) ? 0 : getHapId().hashCode());
        result = prime * result + ((getVarType() == null) ? 0 : getVarType().hashCode());
        result = prime * result + ((getMutate() == null) ? 0 : getMutate().hashCode());
        result = prime * result + ((getRatio() == null) ? 0 : getRatio().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getConfirmStatus() == null) ? 0 : getConfirmStatus().hashCode());
        result = prime * result + ((getResultKey() == null) ? 0 : getResultKey().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", applyNo=").append(applyNo);
        sb.append(", sampleCode=").append(sampleCode);
        sb.append(", vectorTaskCode=").append(vectorTaskCode);
        sb.append(", sampleId=").append(sampleId);
        sb.append(", uniqueDbCode=").append(uniqueDbCode);
        sb.append(", runId=").append(runId);
        sb.append(", hapId=").append(hapId);
        sb.append(", varType=").append(varType);
        sb.append(", mutate=").append(mutate);
        sb.append(", ratio=").append(ratio);
        sb.append(", createTime=").append(createTime);
        sb.append(", confirmStatus=").append(confirmStatus);
        sb.append(", resultKey=").append(resultKey);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}