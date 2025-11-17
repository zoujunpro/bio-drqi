package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * cer种植表
 * @TableName plant_single_stock_tb
 */
@TableName(value ="plant_single_stock_tb")
public class PlantSingleStockTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 种植编号
     */
    private String plantCode;

    /**
     * 代次
     */
    private String generation;

    /**
     * 株树
     */
    private Integer plantNumber;

    /**
     * 播种日期
     */
    private String plantDate;

    /**
     * 取样编号
     */
    private String sampleCode;

    /**
     * 移栽日期
     */
    private String transplantDate;

    /**
     * 春化开始日期
     */
    private String vernalizationBeginDate;

    /**
     * 春化结束日期
     */
    private String vernalizationEndDate;

    /**
     * 授粉方式
     */
    private String pollinationMethod;

    /**
     * 植株状态 1正常，2异常, 3已剔除，4已收获
     */
    private String plantStatus;

    /**
     * 授粉时间
     */
    private String pollinationDate;

    /**
     * 收获日期
     */
    private String harvestDate;

    /**
     * 收获方式
     */
    private String harvestType;

    /**
     * 其他字段
     */
    private Object otherField;

    /**
     * 编辑类型
     */
    private String editType;

    /**
     * 物种
     */
    private String speciesCode;

    /**
     * 受体材料
     */
    private String acceptorMaterial;

    /**
     * 创建日期
     */
    private Date createDate;

    /**
     * 更新日期
     */
    private Date updateTime;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人姓名
     */
    private String createUserName;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 品种
     */
    private String breedCode;

    /**
     * 来源渠道 1项目，4种子库
     */
    private String sourceCode;

    /**
     * 备注
     */
    private String remark;

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
     * 种植编号
     */
    public String getPlantCode() {
        return plantCode;
    }

    /**
     * 种植编号
     */
    public void setPlantCode(String plantCode) {
        this.plantCode = plantCode;
    }

    /**
     * 代次
     */
    public String getGeneration() {
        return generation;
    }

    /**
     * 代次
     */
    public void setGeneration(String generation) {
        this.generation = generation;
    }

    /**
     * 株树
     */
    public Integer getPlantNumber() {
        return plantNumber;
    }

    /**
     * 株树
     */
    public void setPlantNumber(Integer plantNumber) {
        this.plantNumber = plantNumber;
    }

    /**
     * 播种日期
     */
    public String getPlantDate() {
        return plantDate;
    }

    /**
     * 播种日期
     */
    public void setPlantDate(String plantDate) {
        this.plantDate = plantDate;
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
     * 移栽日期
     */
    public String getTransplantDate() {
        return transplantDate;
    }

    /**
     * 移栽日期
     */
    public void setTransplantDate(String transplantDate) {
        this.transplantDate = transplantDate;
    }

    /**
     * 春化开始日期
     */
    public String getVernalizationBeginDate() {
        return vernalizationBeginDate;
    }

    /**
     * 春化开始日期
     */
    public void setVernalizationBeginDate(String vernalizationBeginDate) {
        this.vernalizationBeginDate = vernalizationBeginDate;
    }

    /**
     * 春化结束日期
     */
    public String getVernalizationEndDate() {
        return vernalizationEndDate;
    }

    /**
     * 春化结束日期
     */
    public void setVernalizationEndDate(String vernalizationEndDate) {
        this.vernalizationEndDate = vernalizationEndDate;
    }

    /**
     * 授粉方式
     */
    public String getPollinationMethod() {
        return pollinationMethod;
    }

    /**
     * 授粉方式
     */
    public void setPollinationMethod(String pollinationMethod) {
        this.pollinationMethod = pollinationMethod;
    }

    /**
     * 植株状态 1正常，2异常, 3已剔除，4已收获
     */
    public String getPlantStatus() {
        return plantStatus;
    }

    /**
     * 植株状态 1正常，2异常, 3已剔除，4已收获
     */
    public void setPlantStatus(String plantStatus) {
        this.plantStatus = plantStatus;
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
     * 收获日期
     */
    public String getHarvestDate() {
        return harvestDate;
    }

    /**
     * 收获日期
     */
    public void setHarvestDate(String harvestDate) {
        this.harvestDate = harvestDate;
    }

    /**
     * 收获方式
     */
    public String getHarvestType() {
        return harvestType;
    }

    /**
     * 收获方式
     */
    public void setHarvestType(String harvestType) {
        this.harvestType = harvestType;
    }

    /**
     * 其他字段
     */
    public Object getOtherField() {
        return otherField;
    }

    /**
     * 其他字段
     */
    public void setOtherField(Object otherField) {
        this.otherField = otherField;
    }

    /**
     * 编辑类型
     */
    public String getEditType() {
        return editType;
    }

    /**
     * 编辑类型
     */
    public void setEditType(String editType) {
        this.editType = editType;
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
     * 受体材料
     */
    public String getAcceptorMaterial() {
        return acceptorMaterial;
    }

    /**
     * 受体材料
     */
    public void setAcceptorMaterial(String acceptorMaterial) {
        this.acceptorMaterial = acceptorMaterial;
    }

    /**
     * 创建日期
     */
    public Date getCreateDate() {
        return createDate;
    }

    /**
     * 创建日期
     */
    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
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
     * 创建人姓名
     */
    public String getCreateUserName() {
        return createUserName;
    }

    /**
     * 创建人姓名
     */
    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
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
     * 品种
     */
    public String getBreedCode() {
        return breedCode;
    }

    /**
     * 品种
     */
    public void setBreedCode(String breedCode) {
        this.breedCode = breedCode;
    }

    /**
     * 来源渠道 1项目，4种子库
     */
    public String getSourceCode() {
        return sourceCode;
    }

    /**
     * 来源渠道 1项目，4种子库
     */
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
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
        PlantSingleStockTb other = (PlantSingleStockTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getPlantCode() == null ? other.getPlantCode() == null : this.getPlantCode().equals(other.getPlantCode()))
            && (this.getGeneration() == null ? other.getGeneration() == null : this.getGeneration().equals(other.getGeneration()))
            && (this.getPlantNumber() == null ? other.getPlantNumber() == null : this.getPlantNumber().equals(other.getPlantNumber()))
            && (this.getPlantDate() == null ? other.getPlantDate() == null : this.getPlantDate().equals(other.getPlantDate()))
            && (this.getSampleCode() == null ? other.getSampleCode() == null : this.getSampleCode().equals(other.getSampleCode()))
            && (this.getTransplantDate() == null ? other.getTransplantDate() == null : this.getTransplantDate().equals(other.getTransplantDate()))
            && (this.getVernalizationBeginDate() == null ? other.getVernalizationBeginDate() == null : this.getVernalizationBeginDate().equals(other.getVernalizationBeginDate()))
            && (this.getVernalizationEndDate() == null ? other.getVernalizationEndDate() == null : this.getVernalizationEndDate().equals(other.getVernalizationEndDate()))
            && (this.getPollinationMethod() == null ? other.getPollinationMethod() == null : this.getPollinationMethod().equals(other.getPollinationMethod()))
            && (this.getPlantStatus() == null ? other.getPlantStatus() == null : this.getPlantStatus().equals(other.getPlantStatus()))
            && (this.getPollinationDate() == null ? other.getPollinationDate() == null : this.getPollinationDate().equals(other.getPollinationDate()))
            && (this.getHarvestDate() == null ? other.getHarvestDate() == null : this.getHarvestDate().equals(other.getHarvestDate()))
            && (this.getHarvestType() == null ? other.getHarvestType() == null : this.getHarvestType().equals(other.getHarvestType()))
            && (this.getOtherField() == null ? other.getOtherField() == null : this.getOtherField().equals(other.getOtherField()))
            && (this.getEditType() == null ? other.getEditType() == null : this.getEditType().equals(other.getEditType()))
            && (this.getSpeciesCode() == null ? other.getSpeciesCode() == null : this.getSpeciesCode().equals(other.getSpeciesCode()))
            && (this.getAcceptorMaterial() == null ? other.getAcceptorMaterial() == null : this.getAcceptorMaterial().equals(other.getAcceptorMaterial()))
            && (this.getCreateDate() == null ? other.getCreateDate() == null : this.getCreateDate().equals(other.getCreateDate()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getCreateUserId() == null ? other.getCreateUserId() == null : this.getCreateUserId().equals(other.getCreateUserId()))
            && (this.getCreateUserName() == null ? other.getCreateUserName() == null : this.getCreateUserName().equals(other.getCreateUserName()))
            && (this.getTaskNum() == null ? other.getTaskNum() == null : this.getTaskNum().equals(other.getTaskNum()))
            && (this.getBreedCode() == null ? other.getBreedCode() == null : this.getBreedCode().equals(other.getBreedCode()))
            && (this.getSourceCode() == null ? other.getSourceCode() == null : this.getSourceCode().equals(other.getSourceCode()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPlantCode() == null) ? 0 : getPlantCode().hashCode());
        result = prime * result + ((getGeneration() == null) ? 0 : getGeneration().hashCode());
        result = prime * result + ((getPlantNumber() == null) ? 0 : getPlantNumber().hashCode());
        result = prime * result + ((getPlantDate() == null) ? 0 : getPlantDate().hashCode());
        result = prime * result + ((getSampleCode() == null) ? 0 : getSampleCode().hashCode());
        result = prime * result + ((getTransplantDate() == null) ? 0 : getTransplantDate().hashCode());
        result = prime * result + ((getVernalizationBeginDate() == null) ? 0 : getVernalizationBeginDate().hashCode());
        result = prime * result + ((getVernalizationEndDate() == null) ? 0 : getVernalizationEndDate().hashCode());
        result = prime * result + ((getPollinationMethod() == null) ? 0 : getPollinationMethod().hashCode());
        result = prime * result + ((getPlantStatus() == null) ? 0 : getPlantStatus().hashCode());
        result = prime * result + ((getPollinationDate() == null) ? 0 : getPollinationDate().hashCode());
        result = prime * result + ((getHarvestDate() == null) ? 0 : getHarvestDate().hashCode());
        result = prime * result + ((getHarvestType() == null) ? 0 : getHarvestType().hashCode());
        result = prime * result + ((getOtherField() == null) ? 0 : getOtherField().hashCode());
        result = prime * result + ((getEditType() == null) ? 0 : getEditType().hashCode());
        result = prime * result + ((getSpeciesCode() == null) ? 0 : getSpeciesCode().hashCode());
        result = prime * result + ((getAcceptorMaterial() == null) ? 0 : getAcceptorMaterial().hashCode());
        result = prime * result + ((getCreateDate() == null) ? 0 : getCreateDate().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getCreateUserId() == null) ? 0 : getCreateUserId().hashCode());
        result = prime * result + ((getCreateUserName() == null) ? 0 : getCreateUserName().hashCode());
        result = prime * result + ((getTaskNum() == null) ? 0 : getTaskNum().hashCode());
        result = prime * result + ((getBreedCode() == null) ? 0 : getBreedCode().hashCode());
        result = prime * result + ((getSourceCode() == null) ? 0 : getSourceCode().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", plantCode=").append(plantCode);
        sb.append(", generation=").append(generation);
        sb.append(", plantNumber=").append(plantNumber);
        sb.append(", plantDate=").append(plantDate);
        sb.append(", sampleCode=").append(sampleCode);
        sb.append(", transplantDate=").append(transplantDate);
        sb.append(", vernalizationBeginDate=").append(vernalizationBeginDate);
        sb.append(", vernalizationEndDate=").append(vernalizationEndDate);
        sb.append(", pollinationMethod=").append(pollinationMethod);
        sb.append(", plantStatus=").append(plantStatus);
        sb.append(", pollinationDate=").append(pollinationDate);
        sb.append(", harvestDate=").append(harvestDate);
        sb.append(", harvestType=").append(harvestType);
        sb.append(", otherField=").append(otherField);
        sb.append(", editType=").append(editType);
        sb.append(", speciesCode=").append(speciesCode);
        sb.append(", acceptorMaterial=").append(acceptorMaterial);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", createUserId=").append(createUserId);
        sb.append(", createUserName=").append(createUserName);
        sb.append(", taskNum=").append(taskNum);
        sb.append(", breedCode=").append(breedCode);
        sb.append(", sourceCode=").append(sourceCode);
        sb.append(", remark=").append(remark);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}