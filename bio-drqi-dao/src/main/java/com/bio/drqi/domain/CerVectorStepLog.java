package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName cer_vector_step_log
 */
@TableName(value ="cer_vector_step_log")
public class CerVectorStepLog implements Serializable {
    /**
     * 主键ID
     */
    @TableId
    private Integer id;

    /**
     * 项目ID
     */
    private Integer projectId;

    /**
     * 实施方案ID
     */
    private Integer vectorTaskId;

    /**
     * 步骤
     */
    private String stepCode;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 任务编号
     */
    private String taskNum;

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
     * 项目ID
     */
    public Integer getProjectId() {
        return projectId;
    }

    /**
     * 项目ID
     */
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    /**
     * 实施方案ID
     */
    public Integer getVectorTaskId() {
        return vectorTaskId;
    }

    /**
     * 实施方案ID
     */
    public void setVectorTaskId(Integer vectorTaskId) {
        this.vectorTaskId = vectorTaskId;
    }

    /**
     * 步骤
     */
    public String getStepCode() {
        return stepCode;
    }

    /**
     * 步骤
     */
    public void setStepCode(String stepCode) {
        this.stepCode = stepCode;
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
        CerVectorStepLog other = (CerVectorStepLog) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getProjectId() == null ? other.getProjectId() == null : this.getProjectId().equals(other.getProjectId()))
            && (this.getVectorTaskId() == null ? other.getVectorTaskId() == null : this.getVectorTaskId().equals(other.getVectorTaskId()))
            && (this.getStepCode() == null ? other.getStepCode() == null : this.getStepCode().equals(other.getStepCode()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getTaskNum() == null ? other.getTaskNum() == null : this.getTaskNum().equals(other.getTaskNum()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getProjectId() == null) ? 0 : getProjectId().hashCode());
        result = prime * result + ((getVectorTaskId() == null) ? 0 : getVectorTaskId().hashCode());
        result = prime * result + ((getStepCode() == null) ? 0 : getStepCode().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getTaskNum() == null) ? 0 : getTaskNum().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", projectId=").append(projectId);
        sb.append(", vectorTaskId=").append(vectorTaskId);
        sb.append(", stepCode=").append(stepCode);
        sb.append(", createTime=").append(createTime);
        sb.append(", taskNum=").append(taskNum);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}