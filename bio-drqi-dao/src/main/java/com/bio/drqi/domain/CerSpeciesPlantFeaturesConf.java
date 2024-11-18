package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;

/**
 * 不同物种种植特性信息配置表
 * @TableName cer_species_plant_features_conf
 */
@TableName(value ="cer_species_plant_features_conf")
public class CerSpeciesPlantFeaturesConf implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 物种编码
     */
    private String speciesCode;

    /**
     * 种植特性名称
     */
    private String plantFeaturesName;

    /**
     * 种植特性描述
     */
    private String plantFeaturesDesc;

    /**
     * 序号
     */
    private Integer orderNum;

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
     * 物种编码
     */
    public String getSpeciesCode() {
        return speciesCode;
    }

    /**
     * 物种编码
     */
    public void setSpeciesCode(String speciesCode) {
        this.speciesCode = speciesCode;
    }

    /**
     * 种植特性名称
     */
    public String getPlantFeaturesName() {
        return plantFeaturesName;
    }

    /**
     * 种植特性名称
     */
    public void setPlantFeaturesName(String plantFeaturesName) {
        this.plantFeaturesName = plantFeaturesName;
    }

    /**
     * 种植特性描述
     */
    public String getPlantFeaturesDesc() {
        return plantFeaturesDesc;
    }

    /**
     * 种植特性描述
     */
    public void setPlantFeaturesDesc(String plantFeaturesDesc) {
        this.plantFeaturesDesc = plantFeaturesDesc;
    }

    /**
     * 序号
     */
    public Integer getOrderNum() {
        return orderNum;
    }

    /**
     * 序号
     */
    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
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
        CerSpeciesPlantFeaturesConf other = (CerSpeciesPlantFeaturesConf) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSpeciesCode() == null ? other.getSpeciesCode() == null : this.getSpeciesCode().equals(other.getSpeciesCode()))
            && (this.getPlantFeaturesName() == null ? other.getPlantFeaturesName() == null : this.getPlantFeaturesName().equals(other.getPlantFeaturesName()))
            && (this.getPlantFeaturesDesc() == null ? other.getPlantFeaturesDesc() == null : this.getPlantFeaturesDesc().equals(other.getPlantFeaturesDesc()))
            && (this.getOrderNum() == null ? other.getOrderNum() == null : this.getOrderNum().equals(other.getOrderNum()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSpeciesCode() == null) ? 0 : getSpeciesCode().hashCode());
        result = prime * result + ((getPlantFeaturesName() == null) ? 0 : getPlantFeaturesName().hashCode());
        result = prime * result + ((getPlantFeaturesDesc() == null) ? 0 : getPlantFeaturesDesc().hashCode());
        result = prime * result + ((getOrderNum() == null) ? 0 : getOrderNum().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", speciesCode=").append(speciesCode);
        sb.append(", plantFeaturesName=").append(plantFeaturesName);
        sb.append(", plantFeaturesDesc=").append(plantFeaturesDesc);
        sb.append(", orderNum=").append(orderNum);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}