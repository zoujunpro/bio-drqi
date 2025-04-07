package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName system_dept_tb
 */
@TableName(value ="system_dept_tb")
public class SystemDeptTb implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 部门父ID
     */
    private Integer parentId;

    /**
     * 排序id
     */
    private String orderNun;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 状态 Y启用 N禁用
     */
    private String status;

    /**
     * 级别
     */
    private Integer deptLevel;

    /**
     * LDAP地址
     */
    private String ldapDn;

    /**
     * ou,cn
     */
    private String ldapType;

    /**
     * 部门负责人ID
     */
    private Integer leaderId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 部门名称
     */
    public String getDeptName() {
        return deptName;
    }

    /**
     * 部门名称
     */
    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    /**
     * 部门父ID
     */
    public Integer getParentId() {
        return parentId;
    }

    /**
     * 部门父ID
     */
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    /**
     * 排序id
     */
    public String getOrderNun() {
        return orderNun;
    }

    /**
     * 排序id
     */
    public void setOrderNun(String orderNun) {
        this.orderNun = orderNun;
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
     * 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 状态 Y启用 N禁用
     */
    public String getStatus() {
        return status;
    }

    /**
     * 状态 Y启用 N禁用
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 级别
     */
    public Integer getDeptLevel() {
        return deptLevel;
    }

    /**
     * 级别
     */
    public void setDeptLevel(Integer deptLevel) {
        this.deptLevel = deptLevel;
    }

    /**
     * LDAP地址
     */
    public String getLdapDn() {
        return ldapDn;
    }

    /**
     * LDAP地址
     */
    public void setLdapDn(String ldapDn) {
        this.ldapDn = ldapDn;
    }

    /**
     * ou,cn
     */
    public String getLdapType() {
        return ldapType;
    }

    /**
     * ou,cn
     */
    public void setLdapType(String ldapType) {
        this.ldapType = ldapType;
    }

    /**
     * 部门负责人ID
     */
    public Integer getLeaderId() {
        return leaderId;
    }

    /**
     * 部门负责人ID
     */
    public void setLeaderId(Integer leaderId) {
        this.leaderId = leaderId;
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
        SystemDeptTb other = (SystemDeptTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getDeptName() == null ? other.getDeptName() == null : this.getDeptName().equals(other.getDeptName()))
            && (this.getParentId() == null ? other.getParentId() == null : this.getParentId().equals(other.getParentId()))
            && (this.getOrderNun() == null ? other.getOrderNun() == null : this.getOrderNun().equals(other.getOrderNun()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getStatus() == null ? other.getStatus() == null : this.getStatus().equals(other.getStatus()))
            && (this.getDeptLevel() == null ? other.getDeptLevel() == null : this.getDeptLevel().equals(other.getDeptLevel()))
            && (this.getLdapDn() == null ? other.getLdapDn() == null : this.getLdapDn().equals(other.getLdapDn()))
            && (this.getLdapType() == null ? other.getLdapType() == null : this.getLdapType().equals(other.getLdapType()))
            && (this.getLeaderId() == null ? other.getLeaderId() == null : this.getLeaderId().equals(other.getLeaderId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getDeptName() == null) ? 0 : getDeptName().hashCode());
        result = prime * result + ((getParentId() == null) ? 0 : getParentId().hashCode());
        result = prime * result + ((getOrderNun() == null) ? 0 : getOrderNun().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getStatus() == null) ? 0 : getStatus().hashCode());
        result = prime * result + ((getDeptLevel() == null) ? 0 : getDeptLevel().hashCode());
        result = prime * result + ((getLdapDn() == null) ? 0 : getLdapDn().hashCode());
        result = prime * result + ((getLdapType() == null) ? 0 : getLdapType().hashCode());
        result = prime * result + ((getLeaderId() == null) ? 0 : getLeaderId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", deptName=").append(deptName);
        sb.append(", parentId=").append(parentId);
        sb.append(", orderNun=").append(orderNun);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", status=").append(status);
        sb.append(", deptLevel=").append(deptLevel);
        sb.append(", ldapDn=").append(ldapDn);
        sb.append(", ldapType=").append(ldapType);
        sb.append(", leaderId=").append(leaderId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}