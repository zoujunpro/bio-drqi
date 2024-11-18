package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * 字典信息表
 * @TableName bio_dict
 */
@TableName(value ="bio_dict")
public class BioDict implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 字典值名称
     */
    private String dictValueName;

    /**
     * 字典值编码
     */
    private String dictValueCode;

    /**
     * 状态 1启用 2禁用
     */
    private String dictStatus;

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
     * 字典名称
     */
    public String getDictName() {
        return dictName;
    }

    /**
     * 字典名称
     */
    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    /**
     * 字典类型
     */
    public String getDictType() {
        return dictType;
    }

    /**
     * 字典类型
     */
    public void setDictType(String dictType) {
        this.dictType = dictType;
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
     * 字典值编码
     */
    public String getDictValueCode() {
        return dictValueCode;
    }

    /**
     * 字典值编码
     */
    public void setDictValueCode(String dictValueCode) {
        this.dictValueCode = dictValueCode;
    }

    /**
     * 状态 1启用 2禁用
     */
    public String getDictStatus() {
        return dictStatus;
    }

    /**
     * 状态 1启用 2禁用
     */
    public void setDictStatus(String dictStatus) {
        this.dictStatus = dictStatus;
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
        BioDict other = (BioDict) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getDictName() == null ? other.getDictName() == null : this.getDictName().equals(other.getDictName()))
            && (this.getDictType() == null ? other.getDictType() == null : this.getDictType().equals(other.getDictType()))
            && (this.getDictValueName() == null ? other.getDictValueName() == null : this.getDictValueName().equals(other.getDictValueName()))
            && (this.getDictValueCode() == null ? other.getDictValueCode() == null : this.getDictValueCode().equals(other.getDictValueCode()))
            && (this.getDictStatus() == null ? other.getDictStatus() == null : this.getDictStatus().equals(other.getDictStatus()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getDictName() == null) ? 0 : getDictName().hashCode());
        result = prime * result + ((getDictType() == null) ? 0 : getDictType().hashCode());
        result = prime * result + ((getDictValueName() == null) ? 0 : getDictValueName().hashCode());
        result = prime * result + ((getDictValueCode() == null) ? 0 : getDictValueCode().hashCode());
        result = prime * result + ((getDictStatus() == null) ? 0 : getDictStatus().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", dictName=").append(dictName);
        sb.append(", dictType=").append(dictType);
        sb.append(", dictValueName=").append(dictValueName);
        sb.append(", dictValueCode=").append(dictValueCode);
        sb.append(", dictStatus=").append(dictStatus);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}