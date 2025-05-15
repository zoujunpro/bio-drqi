package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * 田间试验设计表
 * @TableName tc_experiment_design_tb
 */
@TableName(value ="tc_experiment_design_tb")
public class TcExperimentDesignTb implements Serializable {
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
     * 小区编号
     */
    private String regionNum;

    /**
     * 种子编号
     */
    private String seedNum;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;

    /**
     * 物种
     */
    private String speciesCode;

    /**
     * 品种
     */
    private String breedName;

    /**
     * 目标性状
     */
    private String targetCharacter;

    /**
     * 代次编号
     */
    private String generationCode;

    /**
     * 田测基因型
     */
    private String tcGene;

    /**
     * 小区面积
     */
    private String regionArea;

    /**
     * 面积单位
     */
    private String areaUnit;

    /**
     * 株距
     */
    private String plantSpace;

    /**
     * 行数
     */
    private String rowsNumber;

    /**
     * 行长
     */
    private String rowsLength;

    /**
     * 行距
     */
    private String rowsSpace;

    /**
     * 播种方式
     */
    private String seedingType;

    /**
     * 播种数量
     */
    private Integer seedingNumber;

    /**
     * 播种单位
     */
    private String seedingUnit;

    /**
     * 播种时间
     */
    private String seedingTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人
     */
    private String createUserName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 出苗率
     */
    private String emergenceRate;

    /**
     * 移栽时间
     */
    private String transplantTime;

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
     * 小区编号
     */
    public String getRegionNum() {
        return regionNum;
    }

    /**
     * 小区编号
     */
    public void setRegionNum(String regionNum) {
        this.regionNum = regionNum;
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
     * 品种
     */
    public String getBreedName() {
        return breedName;
    }

    /**
     * 品种
     */
    public void setBreedName(String breedName) {
        this.breedName = breedName;
    }

    /**
     * 目标性状
     */
    public String getTargetCharacter() {
        return targetCharacter;
    }

    /**
     * 目标性状
     */
    public void setTargetCharacter(String targetCharacter) {
        this.targetCharacter = targetCharacter;
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
     * 田测基因型
     */
    public String getTcGene() {
        return tcGene;
    }

    /**
     * 田测基因型
     */
    public void setTcGene(String tcGene) {
        this.tcGene = tcGene;
    }

    /**
     * 小区面积
     */
    public String getRegionArea() {
        return regionArea;
    }

    /**
     * 小区面积
     */
    public void setRegionArea(String regionArea) {
        this.regionArea = regionArea;
    }

    /**
     * 面积单位
     */
    public String getAreaUnit() {
        return areaUnit;
    }

    /**
     * 面积单位
     */
    public void setAreaUnit(String areaUnit) {
        this.areaUnit = areaUnit;
    }

    /**
     * 株距
     */
    public String getPlantSpace() {
        return plantSpace;
    }

    /**
     * 株距
     */
    public void setPlantSpace(String plantSpace) {
        this.plantSpace = plantSpace;
    }

    /**
     * 行数
     */
    public String getRowsNumber() {
        return rowsNumber;
    }

    /**
     * 行数
     */
    public void setRowsNumber(String rowsNumber) {
        this.rowsNumber = rowsNumber;
    }

    /**
     * 行长
     */
    public String getRowsLength() {
        return rowsLength;
    }

    /**
     * 行长
     */
    public void setRowsLength(String rowsLength) {
        this.rowsLength = rowsLength;
    }

    /**
     * 行距
     */
    public String getRowsSpace() {
        return rowsSpace;
    }

    /**
     * 行距
     */
    public void setRowsSpace(String rowsSpace) {
        this.rowsSpace = rowsSpace;
    }

    /**
     * 播种方式
     */
    public String getSeedingType() {
        return seedingType;
    }

    /**
     * 播种方式
     */
    public void setSeedingType(String seedingType) {
        this.seedingType = seedingType;
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
     * 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 任务编号
     */
    public String getTaskNum() {
        return taskNum;
    }

    /**
     * 任务编号
     */
    public void setTaskNum(String taskNum) {
        this.taskNum = taskNum;
    }

    /**
     * 创建人ID
     */
    public Integer getCreateUserId() {
        return createUserId;
    }

    /**
     * 创建人ID
     */
    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    /**
     * 创建人
     */
    public String getCreateUserName() {
        return createUserName;
    }

    /**
     * 创建人
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

    /**
     * 出苗率
     */
    public String getEmergenceRate() {
        return emergenceRate;
    }

    /**
     * 出苗率
     */
    public void setEmergenceRate(String emergenceRate) {
        this.emergenceRate = emergenceRate;
    }

    /**
     * 移栽时间
     */
    public String getTransplantTime() {
        return transplantTime;
    }

    /**
     * 移栽时间
     */
    public void setTransplantTime(String transplantTime) {
        this.transplantTime = transplantTime;
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
        TcExperimentDesignTb other = (TcExperimentDesignTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getExperimentNum() == null ? other.getExperimentNum() == null : this.getExperimentNum().equals(other.getExperimentNum()))
            && (this.getRegionNum() == null ? other.getRegionNum() == null : this.getRegionNum().equals(other.getRegionNum()))
            && (this.getSeedNum() == null ? other.getSeedNum() == null : this.getSeedNum().equals(other.getSeedNum()))
            && (this.getProjectCode() == null ? other.getProjectCode() == null : this.getProjectCode().equals(other.getProjectCode()))
            && (this.getVectorTaskCode() == null ? other.getVectorTaskCode() == null : this.getVectorTaskCode().equals(other.getVectorTaskCode()))
            && (this.getSpeciesCode() == null ? other.getSpeciesCode() == null : this.getSpeciesCode().equals(other.getSpeciesCode()))
            && (this.getBreedName() == null ? other.getBreedName() == null : this.getBreedName().equals(other.getBreedName()))
            && (this.getTargetCharacter() == null ? other.getTargetCharacter() == null : this.getTargetCharacter().equals(other.getTargetCharacter()))
            && (this.getGenerationCode() == null ? other.getGenerationCode() == null : this.getGenerationCode().equals(other.getGenerationCode()))
            && (this.getTcGene() == null ? other.getTcGene() == null : this.getTcGene().equals(other.getTcGene()))
            && (this.getRegionArea() == null ? other.getRegionArea() == null : this.getRegionArea().equals(other.getRegionArea()))
            && (this.getAreaUnit() == null ? other.getAreaUnit() == null : this.getAreaUnit().equals(other.getAreaUnit()))
            && (this.getPlantSpace() == null ? other.getPlantSpace() == null : this.getPlantSpace().equals(other.getPlantSpace()))
            && (this.getRowsNumber() == null ? other.getRowsNumber() == null : this.getRowsNumber().equals(other.getRowsNumber()))
            && (this.getRowsLength() == null ? other.getRowsLength() == null : this.getRowsLength().equals(other.getRowsLength()))
            && (this.getRowsSpace() == null ? other.getRowsSpace() == null : this.getRowsSpace().equals(other.getRowsSpace()))
            && (this.getSeedingType() == null ? other.getSeedingType() == null : this.getSeedingType().equals(other.getSeedingType()))
            && (this.getSeedingNumber() == null ? other.getSeedingNumber() == null : this.getSeedingNumber().equals(other.getSeedingNumber()))
            && (this.getSeedingUnit() == null ? other.getSeedingUnit() == null : this.getSeedingUnit().equals(other.getSeedingUnit()))
            && (this.getSeedingTime() == null ? other.getSeedingTime() == null : this.getSeedingTime().equals(other.getSeedingTime()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
            && (this.getTaskNum() == null ? other.getTaskNum() == null : this.getTaskNum().equals(other.getTaskNum()))
            && (this.getCreateUserId() == null ? other.getCreateUserId() == null : this.getCreateUserId().equals(other.getCreateUserId()))
            && (this.getCreateUserName() == null ? other.getCreateUserName() == null : this.getCreateUserName().equals(other.getCreateUserName()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getEmergenceRate() == null ? other.getEmergenceRate() == null : this.getEmergenceRate().equals(other.getEmergenceRate()))
            && (this.getTransplantTime() == null ? other.getTransplantTime() == null : this.getTransplantTime().equals(other.getTransplantTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getExperimentNum() == null) ? 0 : getExperimentNum().hashCode());
        result = prime * result + ((getRegionNum() == null) ? 0 : getRegionNum().hashCode());
        result = prime * result + ((getSeedNum() == null) ? 0 : getSeedNum().hashCode());
        result = prime * result + ((getProjectCode() == null) ? 0 : getProjectCode().hashCode());
        result = prime * result + ((getVectorTaskCode() == null) ? 0 : getVectorTaskCode().hashCode());
        result = prime * result + ((getSpeciesCode() == null) ? 0 : getSpeciesCode().hashCode());
        result = prime * result + ((getBreedName() == null) ? 0 : getBreedName().hashCode());
        result = prime * result + ((getTargetCharacter() == null) ? 0 : getTargetCharacter().hashCode());
        result = prime * result + ((getGenerationCode() == null) ? 0 : getGenerationCode().hashCode());
        result = prime * result + ((getTcGene() == null) ? 0 : getTcGene().hashCode());
        result = prime * result + ((getRegionArea() == null) ? 0 : getRegionArea().hashCode());
        result = prime * result + ((getAreaUnit() == null) ? 0 : getAreaUnit().hashCode());
        result = prime * result + ((getPlantSpace() == null) ? 0 : getPlantSpace().hashCode());
        result = prime * result + ((getRowsNumber() == null) ? 0 : getRowsNumber().hashCode());
        result = prime * result + ((getRowsLength() == null) ? 0 : getRowsLength().hashCode());
        result = prime * result + ((getRowsSpace() == null) ? 0 : getRowsSpace().hashCode());
        result = prime * result + ((getSeedingType() == null) ? 0 : getSeedingType().hashCode());
        result = prime * result + ((getSeedingNumber() == null) ? 0 : getSeedingNumber().hashCode());
        result = prime * result + ((getSeedingUnit() == null) ? 0 : getSeedingUnit().hashCode());
        result = prime * result + ((getSeedingTime() == null) ? 0 : getSeedingTime().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getTaskNum() == null) ? 0 : getTaskNum().hashCode());
        result = prime * result + ((getCreateUserId() == null) ? 0 : getCreateUserId().hashCode());
        result = prime * result + ((getCreateUserName() == null) ? 0 : getCreateUserName().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getEmergenceRate() == null) ? 0 : getEmergenceRate().hashCode());
        result = prime * result + ((getTransplantTime() == null) ? 0 : getTransplantTime().hashCode());
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
        sb.append(", regionNum=").append(regionNum);
        sb.append(", seedNum=").append(seedNum);
        sb.append(", projectCode=").append(projectCode);
        sb.append(", vectorTaskCode=").append(vectorTaskCode);
        sb.append(", speciesCode=").append(speciesCode);
        sb.append(", breedName=").append(breedName);
        sb.append(", targetCharacter=").append(targetCharacter);
        sb.append(", generationCode=").append(generationCode);
        sb.append(", tcGene=").append(tcGene);
        sb.append(", regionArea=").append(regionArea);
        sb.append(", areaUnit=").append(areaUnit);
        sb.append(", plantSpace=").append(plantSpace);
        sb.append(", rowsNumber=").append(rowsNumber);
        sb.append(", rowsLength=").append(rowsLength);
        sb.append(", rowsSpace=").append(rowsSpace);
        sb.append(", seedingType=").append(seedingType);
        sb.append(", seedingNumber=").append(seedingNumber);
        sb.append(", seedingUnit=").append(seedingUnit);
        sb.append(", seedingTime=").append(seedingTime);
        sb.append(", remark=").append(remark);
        sb.append(", taskNum=").append(taskNum);
        sb.append(", createUserId=").append(createUserId);
        sb.append(", createUserName=").append(createUserName);
        sb.append(", createTime=").append(createTime);
        sb.append(", emergenceRate=").append(emergenceRate);
        sb.append(", transplantTime=").append(transplantTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}