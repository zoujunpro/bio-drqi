package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

/**
 * 
 * @TableName bio_print_transform_tb
 */
public class BioPrintTransformTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 转化编号
     */
    private String transformCode;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;

    /**
     * 打印id（编号）
     */
    private Long printId;

    /**
     * 质粒组
     */
    private String plasmidName;

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
     * 打印id（编号）
     */
    public Long getPrintId() {
        return printId;
    }

    /**
     * 打印id（编号）
     */
    public void setPrintId(Long printId) {
        this.printId = printId;
    }

    /**
     * 质粒组
     */
    public String getPlasmidName() {
        return plasmidName;
    }

    /**
     * 质粒组
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
        BioPrintTransformTb other = (BioPrintTransformTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTransformCode() == null ? other.getTransformCode() == null : this.getTransformCode().equals(other.getTransformCode()))
            && (this.getVectorTaskCode() == null ? other.getVectorTaskCode() == null : this.getVectorTaskCode().equals(other.getVectorTaskCode()))
            && (this.getPrintId() == null ? other.getPrintId() == null : this.getPrintId().equals(other.getPrintId()))
            && (this.getPlasmidName() == null ? other.getPlasmidName() == null : this.getPlasmidName().equals(other.getPlasmidName()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTransformCode() == null) ? 0 : getTransformCode().hashCode());
        result = prime * result + ((getVectorTaskCode() == null) ? 0 : getVectorTaskCode().hashCode());
        result = prime * result + ((getPrintId() == null) ? 0 : getPrintId().hashCode());
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
        sb.append(", transformCode=").append(transformCode);
        sb.append(", vectorTaskCode=").append(vectorTaskCode);
        sb.append(", printId=").append(printId);
        sb.append(", plasmidName=").append(plasmidName);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}