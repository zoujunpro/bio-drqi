package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.Date;

/**
 * 打印信息表
 * @TableName bio_print_label_info_tb
 */
public class BioPrintLabelInfoTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 打印类型
     */
    private String labelType;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 更新日期
     */
    private Date updateTime;

    /**
     * 打印唯一编号
     */
    private String uniqueCode;

    /**
     * 扫码编号
     */
    private String printCode;

    /**
     * 标签内容
     */
    private String labelText;

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 主键ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 打印类型
     */
    public String getLabelType() {
        return labelType;
    }

    /**
     * 打印类型
     */
    public void setLabelType(String labelType) {
        this.labelType = labelType;
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
     * 更新日期
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 更新日期
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 打印唯一编号
     */
    public String getUniqueCode() {
        return uniqueCode;
    }

    /**
     * 打印唯一编号
     */
    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }

    /**
     * 扫码编号
     */
    public String getPrintCode() {
        return printCode;
    }

    /**
     * 扫码编号
     */
    public void setPrintCode(String printCode) {
        this.printCode = printCode;
    }

    /**
     * 标签内容
     */
    public String getLabelText() {
        return labelText;
    }

    /**
     * 标签内容
     */
    public void setLabelText(String labelText) {
        this.labelText = labelText;
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
        BioPrintLabelInfoTb other = (BioPrintLabelInfoTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getLabelType() == null ? other.getLabelType() == null : this.getLabelType().equals(other.getLabelType()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getUniqueCode() == null ? other.getUniqueCode() == null : this.getUniqueCode().equals(other.getUniqueCode()))
            && (this.getPrintCode() == null ? other.getPrintCode() == null : this.getPrintCode().equals(other.getPrintCode()))
            && (this.getLabelText() == null ? other.getLabelText() == null : this.getLabelText().equals(other.getLabelText()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getLabelType() == null) ? 0 : getLabelType().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getUniqueCode() == null) ? 0 : getUniqueCode().hashCode());
        result = prime * result + ((getPrintCode() == null) ? 0 : getPrintCode().hashCode());
        result = prime * result + ((getLabelText() == null) ? 0 : getLabelText().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", labelType=").append(labelType);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", uniqueCode=").append(uniqueCode);
        sb.append(", printCode=").append(printCode);
        sb.append(", labelText=").append(labelText);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}