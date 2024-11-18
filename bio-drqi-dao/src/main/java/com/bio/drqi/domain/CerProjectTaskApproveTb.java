package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName cer_project_task_approve_tb
 */
@TableName(value ="cer_project_task_approve_tb")
public class CerProjectTaskApproveTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 任务ID
     */
    private Integer taskId;

    /**
     * 审批人
     */
    private Integer approveUserId;

    /**
     * 审批人
     */
    private String approveUserName;

    /**
     * 审批时间
     */
    private Date approveTime;

    /**
     * 审批结果
     */
    private String approveResult;

    /**
     * 拒绝原因
     */
    private String rejectReason;

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
     * 任务ID
     */
    public Integer getTaskId() {
        return taskId;
    }

    /**
     * 任务ID
     */
    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    /**
     * 审批人
     */
    public Integer getApproveUserId() {
        return approveUserId;
    }

    /**
     * 审批人
     */
    public void setApproveUserId(Integer approveUserId) {
        this.approveUserId = approveUserId;
    }

    /**
     * 审批人
     */
    public String getApproveUserName() {
        return approveUserName;
    }

    /**
     * 审批人
     */
    public void setApproveUserName(String approveUserName) {
        this.approveUserName = approveUserName;
    }

    /**
     * 审批时间
     */
    public Date getApproveTime() {
        return approveTime;
    }

    /**
     * 审批时间
     */
    public void setApproveTime(Date approveTime) {
        this.approveTime = approveTime;
    }

    /**
     * 审批结果
     */
    public String getApproveResult() {
        return approveResult;
    }

    /**
     * 审批结果
     */
    public void setApproveResult(String approveResult) {
        this.approveResult = approveResult;
    }

    /**
     * 拒绝原因
     */
    public String getRejectReason() {
        return rejectReason;
    }

    /**
     * 拒绝原因
     */
    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
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
        CerProjectTaskApproveTb other = (CerProjectTaskApproveTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTaskId() == null ? other.getTaskId() == null : this.getTaskId().equals(other.getTaskId()))
            && (this.getApproveUserId() == null ? other.getApproveUserId() == null : this.getApproveUserId().equals(other.getApproveUserId()))
            && (this.getApproveUserName() == null ? other.getApproveUserName() == null : this.getApproveUserName().equals(other.getApproveUserName()))
            && (this.getApproveTime() == null ? other.getApproveTime() == null : this.getApproveTime().equals(other.getApproveTime()))
            && (this.getApproveResult() == null ? other.getApproveResult() == null : this.getApproveResult().equals(other.getApproveResult()))
            && (this.getRejectReason() == null ? other.getRejectReason() == null : this.getRejectReason().equals(other.getRejectReason()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTaskId() == null) ? 0 : getTaskId().hashCode());
        result = prime * result + ((getApproveUserId() == null) ? 0 : getApproveUserId().hashCode());
        result = prime * result + ((getApproveUserName() == null) ? 0 : getApproveUserName().hashCode());
        result = prime * result + ((getApproveTime() == null) ? 0 : getApproveTime().hashCode());
        result = prime * result + ((getApproveResult() == null) ? 0 : getApproveResult().hashCode());
        result = prime * result + ((getRejectReason() == null) ? 0 : getRejectReason().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", taskId=").append(taskId);
        sb.append(", approveUserId=").append(approveUserId);
        sb.append(", approveUserName=").append(approveUserName);
        sb.append(", approveTime=").append(approveTime);
        sb.append(", approveResult=").append(approveResult);
        sb.append(", rejectReason=").append(rejectReason);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}