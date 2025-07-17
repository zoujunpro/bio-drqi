package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

/**
 * 
 * @TableName bio_print_sample_tb
 */
public class BioPrintSampleTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 打印ID(编号)
     */
    private Long printId;

    /**
     * 载体任务编号
     */
    private String vectorTaskCode;

    /**
     * 取样编号
     */
    private String sampleCode;

    /**
     * 转化编号
     */
    private String transformCode;

    /**
     * 质粒名称
     */
    private String plasmidName;

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
     * 打印ID(编号)
     */
    public Long getPrintId() {
        return printId;
    }

    /**
     * 打印ID(编号)
     */
    public void setPrintId(Long printId) {
        this.printId = printId;
    }

    /**
     * 载体任务编号
     */
    public String getVectorTaskCode() {
        return vectorTaskCode;
    }

    /**
     * 载体任务编号
     */
    public void setVectorTaskCode(String vectorTaskCode) {
        this.vectorTaskCode = vectorTaskCode;
    }

    /**
     * 取样编号
     */
    public String getSampleCode() {
        return sampleCode;
    }

    /**
     * 取样编号
     */
    public void setSampleCode(String sampleCode) {
        this.sampleCode = sampleCode;
    }

    /**
     * 转化编号
     */
    public String getTransformCode() {
        return transformCode;
    }

    /**
     * 转化编号
     */
    public void setTransformCode(String transformCode) {
        this.transformCode = transformCode;
    }

    /**
     * 质粒名称
     */
    public String getPlasmidName() {
        return plasmidName;
    }

    /**
     * 质粒名称
     */
    public void setPlasmidName(String plasmidName) {
        this.plasmidName = plasmidName;
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
        BioPrintSampleTb other = (BioPrintSampleTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getPrintId() == null ? other.getPrintId() == null : this.getPrintId().equals(other.getPrintId()))
            && (this.getVectorTaskCode() == null ? other.getVectorTaskCode() == null : this.getVectorTaskCode().equals(other.getVectorTaskCode()))
            && (this.getSampleCode() == null ? other.getSampleCode() == null : this.getSampleCode().equals(other.getSampleCode()))
            && (this.getTransformCode() == null ? other.getTransformCode() == null : this.getTransformCode().equals(other.getTransformCode()))
            && (this.getPlasmidName() == null ? other.getPlasmidName() == null : this.getPlasmidName().equals(other.getPlasmidName()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPrintId() == null) ? 0 : getPrintId().hashCode());
        result = prime * result + ((getVectorTaskCode() == null) ? 0 : getVectorTaskCode().hashCode());
        result = prime * result + ((getSampleCode() == null) ? 0 : getSampleCode().hashCode());
        result = prime * result + ((getTransformCode() == null) ? 0 : getTransformCode().hashCode());
        result = prime * result + ((getPlasmidName() == null) ? 0 : getPlasmidName().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", printId=").append(printId);
        sb.append(", vectorTaskCode=").append(vectorTaskCode);
        sb.append(", sampleCode=").append(sampleCode);
        sb.append(", transformCode=").append(transformCode);
        sb.append(", plasmidName=").append(plasmidName);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}