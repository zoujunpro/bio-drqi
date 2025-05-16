package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 大田种子收获表
 * @TableName tc_harvest_seed_tb
 */
@TableName(value ="tc_harvest_seed_tb")
public class TcHarvestSeedTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 实验编号
     */
    private String experimentNum;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;

    /**
     * 物种编号
     */
    private String speciesCode;

    /**
     * 代次
     */
    private String generationCode;

    /**
     * 品种编号
     */
    private String breedCode;

    /**
     * 授粉方式
     */
    private String pollinationMethodCode;

    /**
     * 授粉时间
     */
    private String pollinationDate;

    /**
     * 父本信息
     */
    private String fartherSeedNum;

    /**
     * 父本信息
     */
    private String matherSeedNum;

    /**
     * 实验地点
     */
    private String experimentAddress;

    /**
     * 种子数量
     */
    private BigDecimal seedNumber;

    /**
     * 计量单位g/kg/粒 
     */
    private String unit;

    /**
     * 种子类型
     */
    private String seedType;

    /**
     * 收获方式名称
     */
    private String harvestTypeName;

    /**
     * 收获方式编号
     */
    private String harvestTypeCode;

    /**
     * 收获时间
     */
    private String harvestTime;

    /**
     * 基因型性状
     */
    private String geneticCharacter;

    /**
     * 绑定基因类型
     */
    private String geneType;

    /**
     * 备注
     */
    private String remakName;

    /**
     * 收获批次号
     */
    private String harvestApplyNum;

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
     * 实验编号
     */
    public String getExperimentNum() {
        return experimentNum;
    }

    /**
     * 实验编号
     */
    public void setExperimentNum(String experimentNum) {
        this.experimentNum = experimentNum;
    }

    /**
     * 项目编号
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * 项目编号
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
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
     * 物种编号
     */
    public String getSpeciesCode() {
        return speciesCode;
    }

    /**
     * 物种编号
     */
    public void setSpeciesCode(String speciesCode) {
        this.speciesCode = speciesCode;
    }

    /**
     * 代次
     */
    public String getGenerationCode() {
        return generationCode;
    }

    /**
     * 代次
     */
    public void setGenerationCode(String generationCode) {
        this.generationCode = generationCode;
    }

    /**
     * 品种编号
     */
    public String getBreedCode() {
        return breedCode;
    }

    /**
     * 品种编号
     */
    public void setBreedCode(String breedCode) {
        this.breedCode = breedCode;
    }

    /**
     * 授粉方式
     */
    public String getPollinationMethodCode() {
        return pollinationMethodCode;
    }

    /**
     * 授粉方式
     */
    public void setPollinationMethodCode(String pollinationMethodCode) {
        this.pollinationMethodCode = pollinationMethodCode;
    }

    /**
     * 授粉时间
     */
    public String getPollinationDate() {
        return pollinationDate;
    }

    /**
     * 授粉时间
     */
    public void setPollinationDate(String pollinationDate) {
        this.pollinationDate = pollinationDate;
    }

    /**
     * 父本信息
     */
    public String getFartherSeedNum() {
        return fartherSeedNum;
    }

    /**
     * 父本信息
     */
    public void setFartherSeedNum(String fartherSeedNum) {
        this.fartherSeedNum = fartherSeedNum;
    }

    /**
     * 父本信息
     */
    public String getMatherSeedNum() {
        return matherSeedNum;
    }

    /**
     * 父本信息
     */
    public void setMatherSeedNum(String matherSeedNum) {
        this.matherSeedNum = matherSeedNum;
    }

    /**
     * 实验地点
     */
    public String getExperimentAddress() {
        return experimentAddress;
    }

    /**
     * 实验地点
     */
    public void setExperimentAddress(String experimentAddress) {
        this.experimentAddress = experimentAddress;
    }

    /**
     * 种子数量
     */
    public BigDecimal getSeedNumber() {
        return seedNumber;
    }

    /**
     * 种子数量
     */
    public void setSeedNumber(BigDecimal seedNumber) {
        this.seedNumber = seedNumber;
    }

    /**
     * 计量单位g/kg/粒 
     */
    public String getUnit() {
        return unit;
    }

    /**
     * 计量单位g/kg/粒 
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }

    /**
     * 种子类型
     */
    public String getSeedType() {
        return seedType;
    }

    /**
     * 种子类型
     */
    public void setSeedType(String seedType) {
        this.seedType = seedType;
    }

    /**
     * 收获方式名称
     */
    public String getHarvestTypeName() {
        return harvestTypeName;
    }

    /**
     * 收获方式名称
     */
    public void setHarvestTypeName(String harvestTypeName) {
        this.harvestTypeName = harvestTypeName;
    }

    /**
     * 收获方式编号
     */
    public String getHarvestTypeCode() {
        return harvestTypeCode;
    }

    /**
     * 收获方式编号
     */
    public void setHarvestTypeCode(String harvestTypeCode) {
        this.harvestTypeCode = harvestTypeCode;
    }

    /**
     * 收获时间
     */
    public String getHarvestTime() {
        return harvestTime;
    }

    /**
     * 收获时间
     */
    public void setHarvestTime(String harvestTime) {
        this.harvestTime = harvestTime;
    }

    /**
     * 基因型性状
     */
    public String getGeneticCharacter() {
        return geneticCharacter;
    }

    /**
     * 基因型性状
     */
    public void setGeneticCharacter(String geneticCharacter) {
        this.geneticCharacter = geneticCharacter;
    }

    /**
     * 绑定基因类型
     */
    public String getGeneType() {
        return geneType;
    }

    /**
     * 绑定基因类型
     */
    public void setGeneType(String geneType) {
        this.geneType = geneType;
    }

    /**
     * 备注
     */
    public String getRemakName() {
        return remakName;
    }

    /**
     * 备注
     */
    public void setRemakName(String remakName) {
        this.remakName = remakName;
    }

    /**
     * 收获批次号
     */
    public String getHarvestApplyNum() {
        return harvestApplyNum;
    }

    /**
     * 收获批次号
     */
    public void setHarvestApplyNum(String harvestApplyNum) {
        this.harvestApplyNum = harvestApplyNum;
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
        TcHarvestSeedTb other = (TcHarvestSeedTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getExperimentNum() == null ? other.getExperimentNum() == null : this.getExperimentNum().equals(other.getExperimentNum()))
            && (this.getProjectCode() == null ? other.getProjectCode() == null : this.getProjectCode().equals(other.getProjectCode()))
            && (this.getVectorTaskCode() == null ? other.getVectorTaskCode() == null : this.getVectorTaskCode().equals(other.getVectorTaskCode()))
            && (this.getSpeciesCode() == null ? other.getSpeciesCode() == null : this.getSpeciesCode().equals(other.getSpeciesCode()))
            && (this.getGenerationCode() == null ? other.getGenerationCode() == null : this.getGenerationCode().equals(other.getGenerationCode()))
            && (this.getBreedCode() == null ? other.getBreedCode() == null : this.getBreedCode().equals(other.getBreedCode()))
            && (this.getPollinationMethodCode() == null ? other.getPollinationMethodCode() == null : this.getPollinationMethodCode().equals(other.getPollinationMethodCode()))
            && (this.getPollinationDate() == null ? other.getPollinationDate() == null : this.getPollinationDate().equals(other.getPollinationDate()))
            && (this.getFartherSeedNum() == null ? other.getFartherSeedNum() == null : this.getFartherSeedNum().equals(other.getFartherSeedNum()))
            && (this.getMatherSeedNum() == null ? other.getMatherSeedNum() == null : this.getMatherSeedNum().equals(other.getMatherSeedNum()))
            && (this.getExperimentAddress() == null ? other.getExperimentAddress() == null : this.getExperimentAddress().equals(other.getExperimentAddress()))
            && (this.getSeedNumber() == null ? other.getSeedNumber() == null : this.getSeedNumber().equals(other.getSeedNumber()))
            && (this.getUnit() == null ? other.getUnit() == null : this.getUnit().equals(other.getUnit()))
            && (this.getSeedType() == null ? other.getSeedType() == null : this.getSeedType().equals(other.getSeedType()))
            && (this.getHarvestTypeName() == null ? other.getHarvestTypeName() == null : this.getHarvestTypeName().equals(other.getHarvestTypeName()))
            && (this.getHarvestTypeCode() == null ? other.getHarvestTypeCode() == null : this.getHarvestTypeCode().equals(other.getHarvestTypeCode()))
            && (this.getHarvestTime() == null ? other.getHarvestTime() == null : this.getHarvestTime().equals(other.getHarvestTime()))
            && (this.getGeneticCharacter() == null ? other.getGeneticCharacter() == null : this.getGeneticCharacter().equals(other.getGeneticCharacter()))
            && (this.getGeneType() == null ? other.getGeneType() == null : this.getGeneType().equals(other.getGeneType()))
            && (this.getRemakName() == null ? other.getRemakName() == null : this.getRemakName().equals(other.getRemakName()))
            && (this.getHarvestApplyNum() == null ? other.getHarvestApplyNum() == null : this.getHarvestApplyNum().equals(other.getHarvestApplyNum()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getExperimentNum() == null) ? 0 : getExperimentNum().hashCode());
        result = prime * result + ((getProjectCode() == null) ? 0 : getProjectCode().hashCode());
        result = prime * result + ((getVectorTaskCode() == null) ? 0 : getVectorTaskCode().hashCode());
        result = prime * result + ((getSpeciesCode() == null) ? 0 : getSpeciesCode().hashCode());
        result = prime * result + ((getGenerationCode() == null) ? 0 : getGenerationCode().hashCode());
        result = prime * result + ((getBreedCode() == null) ? 0 : getBreedCode().hashCode());
        result = prime * result + ((getPollinationMethodCode() == null) ? 0 : getPollinationMethodCode().hashCode());
        result = prime * result + ((getPollinationDate() == null) ? 0 : getPollinationDate().hashCode());
        result = prime * result + ((getFartherSeedNum() == null) ? 0 : getFartherSeedNum().hashCode());
        result = prime * result + ((getMatherSeedNum() == null) ? 0 : getMatherSeedNum().hashCode());
        result = prime * result + ((getExperimentAddress() == null) ? 0 : getExperimentAddress().hashCode());
        result = prime * result + ((getSeedNumber() == null) ? 0 : getSeedNumber().hashCode());
        result = prime * result + ((getUnit() == null) ? 0 : getUnit().hashCode());
        result = prime * result + ((getSeedType() == null) ? 0 : getSeedType().hashCode());
        result = prime * result + ((getHarvestTypeName() == null) ? 0 : getHarvestTypeName().hashCode());
        result = prime * result + ((getHarvestTypeCode() == null) ? 0 : getHarvestTypeCode().hashCode());
        result = prime * result + ((getHarvestTime() == null) ? 0 : getHarvestTime().hashCode());
        result = prime * result + ((getGeneticCharacter() == null) ? 0 : getGeneticCharacter().hashCode());
        result = prime * result + ((getGeneType() == null) ? 0 : getGeneType().hashCode());
        result = prime * result + ((getRemakName() == null) ? 0 : getRemakName().hashCode());
        result = prime * result + ((getHarvestApplyNum() == null) ? 0 : getHarvestApplyNum().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", experimentNum=").append(experimentNum);
        sb.append(", projectCode=").append(projectCode);
        sb.append(", vectorTaskCode=").append(vectorTaskCode);
        sb.append(", speciesCode=").append(speciesCode);
        sb.append(", generationCode=").append(generationCode);
        sb.append(", breedCode=").append(breedCode);
        sb.append(", pollinationMethodCode=").append(pollinationMethodCode);
        sb.append(", pollinationDate=").append(pollinationDate);
        sb.append(", fartherSeedNum=").append(fartherSeedNum);
        sb.append(", matherSeedNum=").append(matherSeedNum);
        sb.append(", experimentAddress=").append(experimentAddress);
        sb.append(", seedNumber=").append(seedNumber);
        sb.append(", unit=").append(unit);
        sb.append(", seedType=").append(seedType);
        sb.append(", harvestTypeName=").append(harvestTypeName);
        sb.append(", harvestTypeCode=").append(harvestTypeCode);
        sb.append(", harvestTime=").append(harvestTime);
        sb.append(", geneticCharacter=").append(geneticCharacter);
        sb.append(", geneType=").append(geneType);
        sb.append(", remakName=").append(remakName);
        sb.append(", harvestApplyNum=").append(harvestApplyNum);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}