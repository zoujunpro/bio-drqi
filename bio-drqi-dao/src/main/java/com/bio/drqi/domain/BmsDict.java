package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * 字典表
 * @TableName bms_dict
 */
@TableName(value ="bms_dict")
public class BmsDict implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 字典类型编号
     */
    private String dictTypeCode;

    /**
     * 字典类型名称
     */
    private String dictTypeName;

    /**
     * 字典值名称
     */
    private String dictValueName;

    /**
     * 字典值编号
     */
    private String dictValueCode;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

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
     * 字典类型编号
     */
    public String getDictTypeCode() {
        return dictTypeCode;
    }

    /**
     * 字典类型编号
     */
    public void setDictTypeCode(String dictTypeCode) {
        this.dictTypeCode = dictTypeCode;
    }

    /**
     * 字典类型名称
     */
    public String getDictTypeName() {
        return dictTypeName;
    }

    /**
     * 字典类型名称
     */
    public void setDictTypeName(String dictTypeName) {
        this.dictTypeName = dictTypeName;
    }

    /**
     * 字典值名称
     */
    public String getDictValueName() {
        return dictValueName;
    }

    /**
     * 字典值名称
     */
    public void setDictValueName(String dictValueName) {
        this.dictValueName = dictValueName;
    }

    /**
     * 字典值编号
     */
    public String getDictValueCode() {
        return dictValueCode;
    }

    /**
     * 字典值编号
     */
    public void setDictValueCode(String dictValueCode) {
        this.dictValueCode = dictValueCode;
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
     * 创建人名称
     */
    public String getCreateUserName() {
        return createUserName;
    }

    /**
     * 创建人名称
     */
    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
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
        BmsDict other = (BmsDict) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getDictTypeCode() == null ? other.getDictTypeCode() == null : this.getDictTypeCode().equals(other.getDictTypeCode()))
            && (this.getDictTypeName() == null ? other.getDictTypeName() == null : this.getDictTypeName().equals(other.getDictTypeName()))
            && (this.getDictValueName() == null ? other.getDictValueName() == null : this.getDictValueName().equals(other.getDictValueName()))
            && (this.getDictValueCode() == null ? other.getDictValueCode() == null : this.getDictValueCode().equals(other.getDictValueCode()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getCreateUserId() == null ? other.getCreateUserId() == null : this.getCreateUserId().equals(other.getCreateUserId()))
            && (this.getCreateUserName() == null ? other.getCreateUserName() == null : this.getCreateUserName().equals(other.getCreateUserName()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getDictTypeCode() == null) ? 0 : getDictTypeCode().hashCode());
        result = prime * result + ((getDictTypeName() == null) ? 0 : getDictTypeName().hashCode());
        result = prime * result + ((getDictValueName() == null) ? 0 : getDictValueName().hashCode());
        result = prime * result + ((getDictValueCode() == null) ? 0 : getDictValueCode().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getCreateUserId() == null) ? 0 : getCreateUserId().hashCode());
        result = prime * result + ((getCreateUserName() == null) ? 0 : getCreateUserName().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", dictTypeCode=").append(dictTypeCode);
        sb.append(", dictTypeName=").append(dictTypeName);
        sb.append(", dictValueName=").append(dictValueName);
        sb.append(", dictValueCode=").append(dictValueCode);
        sb.append(", createTime=").append(createTime);
        sb.append(", createUserId=").append(createUserId);
        sb.append(", createUserName=").append(createUserName);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}