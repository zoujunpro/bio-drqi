package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * 取样编号前缀表
 * @TableName cer_sample_code_prefix_tb
 */
@TableName(value ="cer_sample_code_prefix_tb")
public class CerSampleCodePrefixTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 取样编号前缀
     */
    private String sampleCodePrefix;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;

    /**
     * 当前索引
     */
    private Integer currentIndex;

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
     * 取样编号前缀
     */
    public String getSampleCodePrefix() {
        return sampleCodePrefix;
    }

    /**
     * 取样编号前缀
     */
    public void setSampleCodePrefix(String sampleCodePrefix) {
        this.sampleCodePrefix = sampleCodePrefix;
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
     * 当前索引
     */
    public Integer getCurrentIndex() {
        return currentIndex;
    }

    /**
     * 当前索引
     */
    public void setCurrentIndex(Integer currentIndex) {
        this.currentIndex = currentIndex;
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
        CerSampleCodePrefixTb other = (CerSampleCodePrefixTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSampleCodePrefix() == null ? other.getSampleCodePrefix() == null : this.getSampleCodePrefix().equals(other.getSampleCodePrefix()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getVectorTaskCode() == null ? other.getVectorTaskCode() == null : this.getVectorTaskCode().equals(other.getVectorTaskCode()))
            && (this.getCurrentIndex() == null ? other.getCurrentIndex() == null : this.getCurrentIndex().equals(other.getCurrentIndex()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSampleCodePrefix() == null) ? 0 : getSampleCodePrefix().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getVectorTaskCode() == null) ? 0 : getVectorTaskCode().hashCode());
        result = prime * result + ((getCurrentIndex() == null) ? 0 : getCurrentIndex().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", sampleCodePrefix=").append(sampleCodePrefix);
        sb.append(", createTime=").append(createTime);
        sb.append(", vectorTaskCode=").append(vectorTaskCode);
        sb.append(", currentIndex=").append(currentIndex);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}