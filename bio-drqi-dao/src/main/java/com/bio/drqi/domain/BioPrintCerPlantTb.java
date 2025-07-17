package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

/**
 * 
 * @TableName bio_print_cer_plant_tb
 */
public class BioPrintCerPlantTb implements Serializable {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 载体任务编码
     */
    private String vectorTaskCode;

    /**
     * 质粒
     */
    private String plasmidName;

    /**
     * 取样编号
     */
    private String sampleCode;

    /**
     * 转化编号
     */
    private String transformCode;

    /**
     * 打印ID
     */
    private Long printId;

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
     * 载体任务编码
     */
    public String getVectorTaskCode() {
        return vectorTaskCode;
    }

    /**
     * 载体任务编码
     */
    public void setVectorTaskCode(String vectorTaskCode) {
        this.vectorTaskCode = vectorTaskCode;
    }

    /**
     * 质粒
     */
    public String getPlasmidName() {
        return plasmidName;
    }

    /**
     * 质粒
     */
    public void setPlasmidName(String plasmidName) {
        this.plasmidName = plasmidName;
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
     * 打印ID
     */
    public Long getPrintId() {
        return printId;
    }

    /**
     * 打印ID
     */
    public void setPrintId(Long printId) {
        this.printId = printId;
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
        BioPrintCerPlantTb other = (BioPrintCerPlantTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getVectorTaskCode() == null ? other.getVectorTaskCode() == null : this.getVectorTaskCode().equals(other.getVectorTaskCode()))
            && (this.getPlasmidName() == null ? other.getPlasmidName() == null : this.getPlasmidName().equals(other.getPlasmidName()))
            && (this.getSampleCode() == null ? other.getSampleCode() == null : this.getSampleCode().equals(other.getSampleCode()))
            && (this.getTransformCode() == null ? other.getTransformCode() == null : this.getTransformCode().equals(other.getTransformCode()))
            && (this.getPrintId() == null ? other.getPrintId() == null : this.getPrintId().equals(other.getPrintId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getVectorTaskCode() == null) ? 0 : getVectorTaskCode().hashCode());
        result = prime * result + ((getPlasmidName() == null) ? 0 : getPlasmidName().hashCode());
        result = prime * result + ((getSampleCode() == null) ? 0 : getSampleCode().hashCode());
        result = prime * result + ((getTransformCode() == null) ? 0 : getTransformCode().hashCode());
        result = prime * result + ((getPrintId() == null) ? 0 : getPrintId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", vectorTaskCode=").append(vectorTaskCode);
        sb.append(", plasmidName=").append(plasmidName);
        sb.append(", sampleCode=").append(sampleCode);
        sb.append(", transformCode=").append(transformCode);
        sb.append(", printId=").append(printId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}