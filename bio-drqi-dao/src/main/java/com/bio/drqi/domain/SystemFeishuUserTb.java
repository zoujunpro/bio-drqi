package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 飞书用户同步表
 * @TableName system_feishu_user_tb
 */
@TableName(value ="system_feishu_user_tb")
@Data
public class SystemFeishuUserTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 飞书用户ID
     */
    private String feishuUserId;

    /**
     * 本地用户ID
     */
    private Integer localUserId;

    /**
     * 部门ID
     */
    private String departmentId;

    /**
     * 状态
     */
    private String feishuStatus;

    /**
     * 上级领导ID
     */
    private String managerId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

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
        SystemFeishuUserTb other = (SystemFeishuUserTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getFeishuUserId() == null ? other.getFeishuUserId() == null : this.getFeishuUserId().equals(other.getFeishuUserId()))
            && (this.getLocalUserId() == null ? other.getLocalUserId() == null : this.getLocalUserId().equals(other.getLocalUserId()))
            && (this.getDepartmentId() == null ? other.getDepartmentId() == null : this.getDepartmentId().equals(other.getDepartmentId()))
            && (this.getFeishuStatus() == null ? other.getFeishuStatus() == null : this.getFeishuStatus().equals(other.getFeishuStatus()))
            && (this.getManagerId() == null ? other.getManagerId() == null : this.getManagerId().equals(other.getManagerId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getFeishuUserId() == null) ? 0 : getFeishuUserId().hashCode());
        result = prime * result + ((getLocalUserId() == null) ? 0 : getLocalUserId().hashCode());
        result = prime * result + ((getDepartmentId() == null) ? 0 : getDepartmentId().hashCode());
        result = prime * result + ((getFeishuStatus() == null) ? 0 : getFeishuStatus().hashCode());
        result = prime * result + ((getManagerId() == null) ? 0 : getManagerId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", feishuUserId=").append(feishuUserId);
        sb.append(", localUserId=").append(localUserId);
        sb.append(", departmentId=").append(departmentId);
        sb.append(", feishuStatus=").append(feishuStatus);
        sb.append(", managerId=").append(managerId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}