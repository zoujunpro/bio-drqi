package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

/**
 * 
 * @TableName bio_print_vector_tb
 */
public class BioPrintVectorTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 打印Id（编码）
     */
    private Long printId;

    /**
     * 任务编号
     */
    private String vectorTaskCode;

    /**
     * 浓度
     */
    private String concentration;

    /**
     * 质粒名称
     */
    private String plasmidName;

    /**
     * 体积
     */
    private String capacity;

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
     * 打印Id（编码）
     */
    public Long getPrintId() {
        return printId;
    }

    /**
     * 打印Id（编码）
     */
    public void setPrintId(Long printId) {
        this.printId = printId;
    }

    /**
     * 任务编号
     */
    public String getVectorTaskCode() {
        return vectorTaskCode;
    }

    /**
     * 任务编号
     */
    public void setVectorTaskCode(String vectorTaskCode) {
        this.vectorTaskCode = vectorTaskCode;
    }

    /**
     * 浓度
     */
    public String getConcentration() {
        return concentration;
    }

    /**
     * 浓度
     */
    public void setConcentration(String concentration) {
        this.concentration = concentration;
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

    /**
     * 体积
     */
    public String getCapacity() {
        return capacity;
    }

    /**
     * 体积
     */
    public void setCapacity(String capacity) {
        this.capacity = capacity;
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
        BioPrintVectorTb other = (BioPrintVectorTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getPrintId() == null ? other.getPrintId() == null : this.getPrintId().equals(other.getPrintId()))
            && (this.getVectorTaskCode() == null ? other.getVectorTaskCode() == null : this.getVectorTaskCode().equals(other.getVectorTaskCode()))
            && (this.getConcentration() == null ? other.getConcentration() == null : this.getConcentration().equals(other.getConcentration()))
            && (this.getPlasmidName() == null ? other.getPlasmidName() == null : this.getPlasmidName().equals(other.getPlasmidName()))
            && (this.getCapacity() == null ? other.getCapacity() == null : this.getCapacity().equals(other.getCapacity()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPrintId() == null) ? 0 : getPrintId().hashCode());
        result = prime * result + ((getVectorTaskCode() == null) ? 0 : getVectorTaskCode().hashCode());
        result = prime * result + ((getConcentration() == null) ? 0 : getConcentration().hashCode());
        result = prime * result + ((getPlasmidName() == null) ? 0 : getPlasmidName().hashCode());
        result = prime * result + ((getCapacity() == null) ? 0 : getCapacity().hashCode());
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
        sb.append(", concentration=").append(concentration);
        sb.append(", plasmidName=").append(plasmidName);
        sb.append(", capacity=").append(capacity);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}