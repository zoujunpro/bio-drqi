package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName tc_sample_test_bio_result_ref
 */
@TableName(value ="tc_sample_test_bio_result_ref")
public class TcSampleTestBioResultRef implements Serializable {
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
     * 实施方案编号
     */
    private String sampleCode;

    /**
     * 取样编号
     */
    private String vectorTaskCode;

    /**
     * 材料名称
     */
    private String sampleId;

    /**
     * 测序编号
     */
    private String runId;

    /**
     * 创建时间
     */
    private Date createTime;

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
     * 实施方案编号
     */
    public String getSampleCode() {
        return sampleCode;
    }

    /**
     * 实施方案编号
     */
    public void setSampleCode(String sampleCode) {
        this.sampleCode = sampleCode;
    }

    /**
     * 取样编号
     */
    public String getVectorTaskCode() {
        return vectorTaskCode;
    }

    /**
     * 取样编号
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
        TcSampleTestBioResultRef other = (TcSampleTestBioResultRef) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getApplyNo() == null ? other.getApplyNo() == null : this.getApplyNo().equals(other.getApplyNo()))
            && (this.getSampleCode() == null ? other.getSampleCode() == null : this.getSampleCode().equals(other.getSampleCode()))
            && (this.getVectorTaskCode() == null ? other.getVectorTaskCode() == null : this.getVectorTaskCode().equals(other.getVectorTaskCode()))
            && (this.getSampleId() == null ? other.getSampleId() == null : this.getSampleId().equals(other.getSampleId()))
            && (this.getRunId() == null ? other.getRunId() == null : this.getRunId().equals(other.getRunId()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
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
        result = prime * result + ((getRunId() == null) ? 0 : getRunId().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
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
        sb.append(", runId=").append(runId);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}