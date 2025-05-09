package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName tc_sample_test_apply_tb
 */
@TableName(value ="tc_sample_test_apply_tb")
public class TcSampleTestApplyTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId
    private Integer id;

    /**
     * 申请编号
     */
    private String sampleApplyNum;

    /**
     * 实验编号
     */
    private String experimentNum;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 取样组织
     */
    private String sampleOrganize;

    /**
     * 取样类型 F首次取样   R重复取样
     */
    private String applyType;

    /**
     * 预计取样时间
     */
    private String expectedSampleTime;

    /**
     * 预计检测结果返回时间
     */
    private String expectedResultTime;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人名字
     */
    private String createUserName;

    /**
     * 创建日期
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
    public String getSampleApplyNum() {
        return sampleApplyNum;
    }

    /**
     * 申请编号
     */
    public void setSampleApplyNum(String sampleApplyNum) {
        this.sampleApplyNum = sampleApplyNum;
    }

    /**
     * 实验编号
     */
    public String getExperimentNum() {
        return experimentNum;
    }

    /**
     * 实验编号
     */
    public void setExperimentNum(String experimentNum) {
        this.experimentNum = experimentNum;
    }

    /**
     * 任务编号
     */
    public String getTaskNum() {
        return taskNum;
    }

    /**
     * 任务编号
     */
    public void setTaskNum(String taskNum) {
        this.taskNum = taskNum;
    }

    /**
     * 取样组织
     */
    public String getSampleOrganize() {
        return sampleOrganize;
    }

    /**
     * 取样组织
     */
    public void setSampleOrganize(String sampleOrganize) {
        this.sampleOrganize = sampleOrganize;
    }

    /**
     * 取样类型 F首次取样   R重复取样
     */
    public String getApplyType() {
        return applyType;
    }

    /**
     * 取样类型 F首次取样   R重复取样
     */
    public void setApplyType(String applyType) {
        this.applyType = applyType;
    }

    /**
     * 预计取样时间
     */
    public String getExpectedSampleTime() {
        return expectedSampleTime;
    }

    /**
     * 预计取样时间
     */
    public void setExpectedSampleTime(String expectedSampleTime) {
        this.expectedSampleTime = expectedSampleTime;
    }

    /**
     * 预计检测结果返回时间
     */
    public String getExpectedResultTime() {
        return expectedResultTime;
    }

    /**
     * 预计检测结果返回时间
     */
    public void setExpectedResultTime(String expectedResultTime) {
        this.expectedResultTime = expectedResultTime;
    }

    /**
     * 创建人ID
     */
    public Integer getCreateUserId() {
        return createUserId;
    }

    /**
     * 创建人ID
     */
    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    /**
     * 创建人名字
     */
    public String getCreateUserName() {
        return createUserName;
    }

    /**
     * 创建人名字
     */
    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
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
        TcSampleTestApplyTb other = (TcSampleTestApplyTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSampleApplyNum() == null ? other.getSampleApplyNum() == null : this.getSampleApplyNum().equals(other.getSampleApplyNum()))
            && (this.getExperimentNum() == null ? other.getExperimentNum() == null : this.getExperimentNum().equals(other.getExperimentNum()))
            && (this.getTaskNum() == null ? other.getTaskNum() == null : this.getTaskNum().equals(other.getTaskNum()))
            && (this.getSampleOrganize() == null ? other.getSampleOrganize() == null : this.getSampleOrganize().equals(other.getSampleOrganize()))
            && (this.getApplyType() == null ? other.getApplyType() == null : this.getApplyType().equals(other.getApplyType()))
            && (this.getExpectedSampleTime() == null ? other.getExpectedSampleTime() == null : this.getExpectedSampleTime().equals(other.getExpectedSampleTime()))
            && (this.getExpectedResultTime() == null ? other.getExpectedResultTime() == null : this.getExpectedResultTime().equals(other.getExpectedResultTime()))
            && (this.getCreateUserId() == null ? other.getCreateUserId() == null : this.getCreateUserId().equals(other.getCreateUserId()))
            && (this.getCreateUserName() == null ? other.getCreateUserName() == null : this.getCreateUserName().equals(other.getCreateUserName()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSampleApplyNum() == null) ? 0 : getSampleApplyNum().hashCode());
        result = prime * result + ((getExperimentNum() == null) ? 0 : getExperimentNum().hashCode());
        result = prime * result + ((getTaskNum() == null) ? 0 : getTaskNum().hashCode());
        result = prime * result + ((getSampleOrganize() == null) ? 0 : getSampleOrganize().hashCode());
        result = prime * result + ((getApplyType() == null) ? 0 : getApplyType().hashCode());
        result = prime * result + ((getExpectedSampleTime() == null) ? 0 : getExpectedSampleTime().hashCode());
        result = prime * result + ((getExpectedResultTime() == null) ? 0 : getExpectedResultTime().hashCode());
        result = prime * result + ((getCreateUserId() == null) ? 0 : getCreateUserId().hashCode());
        result = prime * result + ((getCreateUserName() == null) ? 0 : getCreateUserName().hashCode());
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
        sb.append(", sampleApplyNum=").append(sampleApplyNum);
        sb.append(", experimentNum=").append(experimentNum);
        sb.append(", taskNum=").append(taskNum);
        sb.append(", sampleOrganize=").append(sampleOrganize);
        sb.append(", applyType=").append(applyType);
        sb.append(", expectedSampleTime=").append(expectedSampleTime);
        sb.append(", expectedResultTime=").append(expectedResultTime);
        sb.append(", createUserId=").append(createUserId);
        sb.append(", createUserName=").append(createUserName);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}