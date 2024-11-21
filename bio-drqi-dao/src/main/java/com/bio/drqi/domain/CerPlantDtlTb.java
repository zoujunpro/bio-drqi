package com.bio.drqi.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * cer种植表
 * @TableName cer_plant_dtl_tb
 */
public class CerPlantDtlTb implements Serializable {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 所属项目ID
     */
    private Integer projectId;

    /**
     * 所属项目编码
     */
    private String projectCode;

    /**
     * 种子编号
     */
    private String plantCode;

    /**
     * 子项目编号
     */
    private String subProjectCode;

    /**
     * 子项目ID
     */
    private Integer subProjectId;

    /**
     * 转化组合名称
     */
    private String transformGroupName;

    /**
     * 任务ID
     */
    private Integer vectorTaskId;

    /**
     * 任务编码
     */
    private String vectorTaskCode;

    /**
     * 质粒信息
     */
    private String plasmidName;

    /**
     * 转化编号
     */
    private String transformCode;

    /**
     * 取样编号
     */
    private String sampleCode;

    /**
     * 代次
     */
    private String generation;

    /**
     * 株树
     */
    private Integer plantNumber;

    /**
     * 播种/移苗日期
     */
    private String plantDate;

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
     * 植株状态 1正常，异常
     */
    private String plantStatus;

    /**
     * 父本信息
     */
    private String fatherInfo;

    /**
     * 母本信息
     */
    private String motherInfo;

    /**
     * 授粉时间
     */
    private String pollinationDate;

    /**
     * 收获日期
     */
    private String harvestDate;

    /**
     * 其他字段
     */
    private Object otherField;

    /**
     * 编辑类型
     */
    private String editType;

    /**
     * 项目物种
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
     * 备注
     */
    private String remark;

    /**
     * 唯一约束
     */
    private String uniqueCode;

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
     * 所属项目ID
     */
    public Integer getProjectId() {
        return projectId;
    }

    /**
     * 所属项目ID
     */
    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    /**
     * 所属项目编码
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * 所属项目编码
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * 种子编号
     */
    public String getPlantCode() {
        return plantCode;
    }

    /**
     * 种子编号
     */
    public void setPlantCode(String plantCode) {
        this.plantCode = plantCode;
    }

    /**
     * 子项目编号
     */
    public String getSubProjectCode() {
        return subProjectCode;
    }

    /**
     * 子项目编号
     */
    public void setSubProjectCode(String subProjectCode) {
        this.subProjectCode = subProjectCode;
    }

    /**
     * 子项目ID
     */
    public Integer getSubProjectId() {
        return subProjectId;
    }

    /**
     * 子项目ID
     */
    public void setSubProjectId(Integer subProjectId) {
        this.subProjectId = subProjectId;
    }

    /**
     * 转化组合名称
     */
    public String getTransformGroupName() {
        return transformGroupName;
    }

    /**
     * 转化组合名称
     */
    public void setTransformGroupName(String transformGroupName) {
        this.transformGroupName = transformGroupName;
    }

    /**
     * 任务ID
     */
    public Integer getVectorTaskId() {
        return vectorTaskId;
    }

    /**
     * 任务ID
     */
    public void setVectorTaskId(Integer vectorTaskId) {
        this.vectorTaskId = vectorTaskId;
    }

    /**
     * 任务编码
     */
    public String getVectorTaskCode() {
        return vectorTaskCode;
    }

    /**
     * 任务编码
     */
    public void setVectorTaskCode(String vectorTaskCode) {
        this.vectorTaskCode = vectorTaskCode;
    }

    /**
     * 质粒信息
     */
    public String getPlasmidName() {
        return plasmidName;
    }

    /**
     * 质粒信息
     */
    public void setPlasmidName(String plasmidName) {
        this.plasmidName = plasmidName;
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
     * 播种/移苗日期
     */
    public String getPlantDate() {
        return plantDate;
    }

    /**
     * 播种/移苗日期
     */
    public void setPlantDate(String plantDate) {
        this.plantDate = plantDate;
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
     * 植株状态 1正常，异常
     */
    public String getPlantStatus() {
        return plantStatus;
    }

    /**
     * 植株状态 1正常，异常
     */
    public void setPlantStatus(String plantStatus) {
        this.plantStatus = plantStatus;
    }

    /**
     * 父本信息
     */
    public String getFatherInfo() {
        return fatherInfo;
    }

    /**
     * 父本信息
     */
    public void setFatherInfo(String fatherInfo) {
        this.fatherInfo = fatherInfo;
    }

    /**
     * 母本信息
     */
    public String getMotherInfo() {
        return motherInfo;
    }

    /**
     * 母本信息
     */
    public void setMotherInfo(String motherInfo) {
        this.motherInfo = motherInfo;
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
     * 项目物种
     */
    public String getSpeciesCode() {
        return speciesCode;
    }

    /**
     * 项目物种
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
     * 唯一约束
     */
    public String getUniqueCode() {
        return uniqueCode;
    }

    /**
     * 唯一约束
     */
    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
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
        CerPlantDtlTb other = (CerPlantDtlTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getProjectId() == null ? other.getProjectId() == null : this.getProjectId().equals(other.getProjectId()))
            && (this.getProjectCode() == null ? other.getProjectCode() == null : this.getProjectCode().equals(other.getProjectCode()))
            && (this.getPlantCode() == null ? other.getPlantCode() == null : this.getPlantCode().equals(other.getPlantCode()))
            && (this.getSubProjectCode() == null ? other.getSubProjectCode() == null : this.getSubProjectCode().equals(other.getSubProjectCode()))
            && (this.getSubProjectId() == null ? other.getSubProjectId() == null : this.getSubProjectId().equals(other.getSubProjectId()))
            && (this.getTransformGroupName() == null ? other.getTransformGroupName() == null : this.getTransformGroupName().equals(other.getTransformGroupName()))
            && (this.getVectorTaskId() == null ? other.getVectorTaskId() == null : this.getVectorTaskId().equals(other.getVectorTaskId()))
            && (this.getVectorTaskCode() == null ? other.getVectorTaskCode() == null : this.getVectorTaskCode().equals(other.getVectorTaskCode()))
            && (this.getPlasmidName() == null ? other.getPlasmidName() == null : this.getPlasmidName().equals(other.getPlasmidName()))
            && (this.getTransformCode() == null ? other.getTransformCode() == null : this.getTransformCode().equals(other.getTransformCode()))
            && (this.getSampleCode() == null ? other.getSampleCode() == null : this.getSampleCode().equals(other.getSampleCode()))
            && (this.getGeneration() == null ? other.getGeneration() == null : this.getGeneration().equals(other.getGeneration()))
            && (this.getPlantNumber() == null ? other.getPlantNumber() == null : this.getPlantNumber().equals(other.getPlantNumber()))
            && (this.getPlantDate() == null ? other.getPlantDate() == null : this.getPlantDate().equals(other.getPlantDate()))
            && (this.getTransplantDate() == null ? other.getTransplantDate() == null : this.getTransplantDate().equals(other.getTransplantDate()))
            && (this.getVernalizationBeginDate() == null ? other.getVernalizationBeginDate() == null : this.getVernalizationBeginDate().equals(other.getVernalizationBeginDate()))
            && (this.getVernalizationEndDate() == null ? other.getVernalizationEndDate() == null : this.getVernalizationEndDate().equals(other.getVernalizationEndDate()))
            && (this.getPollinationMethod() == null ? other.getPollinationMethod() == null : this.getPollinationMethod().equals(other.getPollinationMethod()))
            && (this.getPlantStatus() == null ? other.getPlantStatus() == null : this.getPlantStatus().equals(other.getPlantStatus()))
            && (this.getFatherInfo() == null ? other.getFatherInfo() == null : this.getFatherInfo().equals(other.getFatherInfo()))
            && (this.getMotherInfo() == null ? other.getMotherInfo() == null : this.getMotherInfo().equals(other.getMotherInfo()))
            && (this.getPollinationDate() == null ? other.getPollinationDate() == null : this.getPollinationDate().equals(other.getPollinationDate()))
            && (this.getHarvestDate() == null ? other.getHarvestDate() == null : this.getHarvestDate().equals(other.getHarvestDate()))
            && (this.getOtherField() == null ? other.getOtherField() == null : this.getOtherField().equals(other.getOtherField()))
            && (this.getEditType() == null ? other.getEditType() == null : this.getEditType().equals(other.getEditType()))
            && (this.getSpeciesCode() == null ? other.getSpeciesCode() == null : this.getSpeciesCode().equals(other.getSpeciesCode()))
            && (this.getAcceptorMaterial() == null ? other.getAcceptorMaterial() == null : this.getAcceptorMaterial().equals(other.getAcceptorMaterial()))
            && (this.getCreateDate() == null ? other.getCreateDate() == null : this.getCreateDate().equals(other.getCreateDate()))
            && (this.getUpdateTime() == null ? other.getUpdateTime() == null : this.getUpdateTime().equals(other.getUpdateTime()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
            && (this.getUniqueCode() == null ? other.getUniqueCode() == null : this.getUniqueCode().equals(other.getUniqueCode()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getProjectId() == null) ? 0 : getProjectId().hashCode());
        result = prime * result + ((getProjectCode() == null) ? 0 : getProjectCode().hashCode());
        result = prime * result + ((getPlantCode() == null) ? 0 : getPlantCode().hashCode());
        result = prime * result + ((getSubProjectCode() == null) ? 0 : getSubProjectCode().hashCode());
        result = prime * result + ((getSubProjectId() == null) ? 0 : getSubProjectId().hashCode());
        result = prime * result + ((getTransformGroupName() == null) ? 0 : getTransformGroupName().hashCode());
        result = prime * result + ((getVectorTaskId() == null) ? 0 : getVectorTaskId().hashCode());
        result = prime * result + ((getVectorTaskCode() == null) ? 0 : getVectorTaskCode().hashCode());
        result = prime * result + ((getPlasmidName() == null) ? 0 : getPlasmidName().hashCode());
        result = prime * result + ((getTransformCode() == null) ? 0 : getTransformCode().hashCode());
        result = prime * result + ((getSampleCode() == null) ? 0 : getSampleCode().hashCode());
        result = prime * result + ((getGeneration() == null) ? 0 : getGeneration().hashCode());
        result = prime * result + ((getPlantNumber() == null) ? 0 : getPlantNumber().hashCode());
        result = prime * result + ((getPlantDate() == null) ? 0 : getPlantDate().hashCode());
        result = prime * result + ((getTransplantDate() == null) ? 0 : getTransplantDate().hashCode());
        result = prime * result + ((getVernalizationBeginDate() == null) ? 0 : getVernalizationBeginDate().hashCode());
        result = prime * result + ((getVernalizationEndDate() == null) ? 0 : getVernalizationEndDate().hashCode());
        result = prime * result + ((getPollinationMethod() == null) ? 0 : getPollinationMethod().hashCode());
        result = prime * result + ((getPlantStatus() == null) ? 0 : getPlantStatus().hashCode());
        result = prime * result + ((getFatherInfo() == null) ? 0 : getFatherInfo().hashCode());
        result = prime * result + ((getMotherInfo() == null) ? 0 : getMotherInfo().hashCode());
        result = prime * result + ((getPollinationDate() == null) ? 0 : getPollinationDate().hashCode());
        result = prime * result + ((getHarvestDate() == null) ? 0 : getHarvestDate().hashCode());
        result = prime * result + ((getOtherField() == null) ? 0 : getOtherField().hashCode());
        result = prime * result + ((getEditType() == null) ? 0 : getEditType().hashCode());
        result = prime * result + ((getSpeciesCode() == null) ? 0 : getSpeciesCode().hashCode());
        result = prime * result + ((getAcceptorMaterial() == null) ? 0 : getAcceptorMaterial().hashCode());
        result = prime * result + ((getCreateDate() == null) ? 0 : getCreateDate().hashCode());
        result = prime * result + ((getUpdateTime() == null) ? 0 : getUpdateTime().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getUniqueCode() == null) ? 0 : getUniqueCode().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", projectId=").append(projectId);
        sb.append(", projectCode=").append(projectCode);
        sb.append(", plantCode=").append(plantCode);
        sb.append(", subProjectCode=").append(subProjectCode);
        sb.append(", subProjectId=").append(subProjectId);
        sb.append(", transformGroupName=").append(transformGroupName);
        sb.append(", vectorTaskId=").append(vectorTaskId);
        sb.append(", vectorTaskCode=").append(vectorTaskCode);
        sb.append(", plasmidName=").append(plasmidName);
        sb.append(", transformCode=").append(transformCode);
        sb.append(", sampleCode=").append(sampleCode);
        sb.append(", generation=").append(generation);
        sb.append(", plantNumber=").append(plantNumber);
        sb.append(", plantDate=").append(plantDate);
        sb.append(", transplantDate=").append(transplantDate);
        sb.append(", vernalizationBeginDate=").append(vernalizationBeginDate);
        sb.append(", vernalizationEndDate=").append(vernalizationEndDate);
        sb.append(", pollinationMethod=").append(pollinationMethod);
        sb.append(", plantStatus=").append(plantStatus);
        sb.append(", fatherInfo=").append(fatherInfo);
        sb.append(", motherInfo=").append(motherInfo);
        sb.append(", pollinationDate=").append(pollinationDate);
        sb.append(", harvestDate=").append(harvestDate);
        sb.append(", otherField=").append(otherField);
        sb.append(", editType=").append(editType);
        sb.append(", speciesCode=").append(speciesCode);
        sb.append(", acceptorMaterial=").append(acceptorMaterial);
        sb.append(", createDate=").append(createDate);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", remark=").append(remark);
        sb.append(", uniqueCode=").append(uniqueCode);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}