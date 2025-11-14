package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName plant_experiment_detail_tb
 */
@TableName(value ="plant_experiment_detail_tb")
public class PlantExperimentDetailTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * PD号
     */
    private String pdNum;

    /**
     * 试验编号
     */
    private String experimentNum;

    /**
     * 区域
     */
    private String regionNum;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;

    /**
     * 种子编号
     */
    private String seedNum;

    /**
     * 种植编号
     */
    private String plantNum;

    /**
     * 代次编号
     */
    private String generationCode;

    /**
     * 物种
     */
    private String speciesCode;

    /**
     * 播种时间
     */
    private String seedingTime;

    /**
     * 播种数量
     */
    private Integer seedingNumber;

    /**
     * 播种单位
     */
    private String seedingUnit;

    /**
     * 实验地点
     */
    private String experimentAddressCode;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 基因型
     */
    private String geneType;

    /**
     * 创建人ID
     */
    private String createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 创建时间
     */
    private Date createTime;

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
     * PD号
     */
    public String getPdNum() {
        return pdNum;
    }

    /**
     * PD号
     */
    public void setPdNum(String pdNum) {
        this.pdNum = pdNum;
    }

    /**
     * 试验编号
     */
    public String getExperimentNum() {
        return experimentNum;
    }

    /**
     * 试验编号
     */
    public void setExperimentNum(String experimentNum) {
        this.experimentNum = experimentNum;
    }

    /**
     * 区域
     */
    public String getRegionNum() {
        return regionNum;
    }

    /**
     * 区域
     */
    public void setRegionNum(String regionNum) {
        this.regionNum = regionNum;
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
     * 种子编号
     */
    public String getSeedNum() {
        return seedNum;
    }

    /**
     * 种子编号
     */
    public void setSeedNum(String seedNum) {
        this.seedNum = seedNum;
    }

    /**
     * 种植编号
     */
    public String getPlantNum() {
        return plantNum;
    }

    /**
     * 种植编号
     */
    public void setPlantNum(String plantNum) {
        this.plantNum = plantNum;
    }

    /**
     * 代次编号
     */
    public String getGenerationCode() {
        return generationCode;
    }

    /**
     * 代次编号
     */
    public void setGenerationCode(String generationCode) {
        this.generationCode = generationCode;
    }

    /**
     * 物种
     */
    public String getSpeciesCode() {
        return speciesCode;
    }

    /**
     * 物种
     */
    public void setSpeciesCode(String speciesCode) {
        this.speciesCode = speciesCode;
    }

    /**
     * 播种时间
     */
    public String getSeedingTime() {
        return seedingTime;
    }

    /**
     * 播种时间
     */
    public void setSeedingTime(String seedingTime) {
        this.seedingTime = seedingTime;
    }

    /**
     * 播种数量
     */
    public Integer getSeedingNumber() {
        return seedingNumber;
    }

    /**
     * 播种数量
     */
    public void setSeedingNumber(Integer seedingNumber) {
        this.seedingNumber = seedingNumber;
    }

    /**
     * 播种单位
     */
    public String getSeedingUnit() {
        return seedingUnit;
    }

    /**
     * 播种单位
     */
    public void setSeedingUnit(String seedingUnit) {
        this.seedingUnit = seedingUnit;
    }

    /**
     * 实验地点
     */
    public String getExperimentAddressCode() {
        return experimentAddressCode;
    }

    /**
     * 实验地点
     */
    public void setExperimentAddressCode(String experimentAddressCode) {
        this.experimentAddressCode = experimentAddressCode;
    }

    /**
     * 备注
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * 备注
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * 基因型
     */
    public String getGeneType() {
        return geneType;
    }

    /**
     * 基因型
     */
    public void setGeneType(String geneType) {
        this.geneType = geneType;
    }

    /**
     * 创建人ID
     */
    public String getCreateUserId() {
        return createUserId;
    }

    /**
     * 创建人ID
     */
    public void setCreateUserId(String createUserId) {
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
        PlantExperimentDetailTb other = (PlantExperimentDetailTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getPdNum() == null ? other.getPdNum() == null : this.getPdNum().equals(other.getPdNum()))
            && (this.getExperimentNum() == null ? other.getExperimentNum() == null : this.getExperimentNum().equals(other.getExperimentNum()))
            && (this.getRegionNum() == null ? other.getRegionNum() == null : this.getRegionNum().equals(other.getRegionNum()))
            && (this.getVectorTaskCode() == null ? other.getVectorTaskCode() == null : this.getVectorTaskCode().equals(other.getVectorTaskCode()))
            && (this.getSeedNum() == null ? other.getSeedNum() == null : this.getSeedNum().equals(other.getSeedNum()))
            && (this.getPlantNum() == null ? other.getPlantNum() == null : this.getPlantNum().equals(other.getPlantNum()))
            && (this.getGenerationCode() == null ? other.getGenerationCode() == null : this.getGenerationCode().equals(other.getGenerationCode()))
            && (this.getSpeciesCode() == null ? other.getSpeciesCode() == null : this.getSpeciesCode().equals(other.getSpeciesCode()))
            && (this.getSeedingTime() == null ? other.getSeedingTime() == null : this.getSeedingTime().equals(other.getSeedingTime()))
            && (this.getSeedingNumber() == null ? other.getSeedingNumber() == null : this.getSeedingNumber().equals(other.getSeedingNumber()))
            && (this.getSeedingUnit() == null ? other.getSeedingUnit() == null : this.getSeedingUnit().equals(other.getSeedingUnit()))
            && (this.getExperimentAddressCode() == null ? other.getExperimentAddressCode() == null : this.getExperimentAddressCode().equals(other.getExperimentAddressCode()))
            && (this.getRemarks() == null ? other.getRemarks() == null : this.getRemarks().equals(other.getRemarks()))
            && (this.getGeneType() == null ? other.getGeneType() == null : this.getGeneType().equals(other.getGeneType()))
            && (this.getCreateUserId() == null ? other.getCreateUserId() == null : this.getCreateUserId().equals(other.getCreateUserId()))
            && (this.getCreateUserName() == null ? other.getCreateUserName() == null : this.getCreateUserName().equals(other.getCreateUserName()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPdNum() == null) ? 0 : getPdNum().hashCode());
        result = prime * result + ((getExperimentNum() == null) ? 0 : getExperimentNum().hashCode());
        result = prime * result + ((getRegionNum() == null) ? 0 : getRegionNum().hashCode());
        result = prime * result + ((getVectorTaskCode() == null) ? 0 : getVectorTaskCode().hashCode());
        result = prime * result + ((getSeedNum() == null) ? 0 : getSeedNum().hashCode());
        result = prime * result + ((getPlantNum() == null) ? 0 : getPlantNum().hashCode());
        result = prime * result + ((getGenerationCode() == null) ? 0 : getGenerationCode().hashCode());
        result = prime * result + ((getSpeciesCode() == null) ? 0 : getSpeciesCode().hashCode());
        result = prime * result + ((getSeedingTime() == null) ? 0 : getSeedingTime().hashCode());
        result = prime * result + ((getSeedingNumber() == null) ? 0 : getSeedingNumber().hashCode());
        result = prime * result + ((getSeedingUnit() == null) ? 0 : getSeedingUnit().hashCode());
        result = prime * result + ((getExperimentAddressCode() == null) ? 0 : getExperimentAddressCode().hashCode());
        result = prime * result + ((getRemarks() == null) ? 0 : getRemarks().hashCode());
        result = prime * result + ((getGeneType() == null) ? 0 : getGeneType().hashCode());
        result = prime * result + ((getCreateUserId() == null) ? 0 : getCreateUserId().hashCode());
        result = prime * result + ((getCreateUserName() == null) ? 0 : getCreateUserName().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", pdNum=").append(pdNum);
        sb.append(", experimentNum=").append(experimentNum);
        sb.append(", regionNum=").append(regionNum);
        sb.append(", vectorTaskCode=").append(vectorTaskCode);
        sb.append(", seedNum=").append(seedNum);
        sb.append(", plantNum=").append(plantNum);
        sb.append(", generationCode=").append(generationCode);
        sb.append(", speciesCode=").append(speciesCode);
        sb.append(", seedingTime=").append(seedingTime);
        sb.append(", seedingNumber=").append(seedingNumber);
        sb.append(", seedingUnit=").append(seedingUnit);
        sb.append(", experimentAddressCode=").append(experimentAddressCode);
        sb.append(", remarks=").append(remarks);
        sb.append(", geneType=").append(geneType);
        sb.append(", createUserId=").append(createUserId);
        sb.append(", createUserName=").append(createUserName);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}