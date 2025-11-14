package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName plant_multiple_stock_tb
 */
@TableName(value ="plant_multiple_stock_tb")
public class PlantMultipleStockTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId
    private Integer id;

    /**
     * 种子编号
     */
    private Integer seedNum;

    /**
     * 移苗编号
     */
    private String transformCode;

    /**
     * 数量
     */
    private Integer plantNumber;

    /**
     * 来源
     */
    private String sourceCode;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    private Date createTime;

    /**
     * 创建人
     */
    private Integer createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 工单编号
     */
    private String taskNum;

    /**
     * 物种
     */
    private String speciesCode;

    /**
     * 品种
     */
    private String breedCode;

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
     * 种子编号
     */
    public Integer getSeedNum() {
        return seedNum;
    }

    /**
     * 种子编号
     */
    public void setSeedNum(Integer seedNum) {
        this.seedNum = seedNum;
    }

    /**
     * 移苗编号
     */
    public String getTransformCode() {
        return transformCode;
    }

    /**
     * 移苗编号
     */
    public void setTransformCode(String transformCode) {
        this.transformCode = transformCode;
    }

    /**
     * 数量
     */
    public Integer getPlantNumber() {
        return plantNumber;
    }

    /**
     * 数量
     */
    public void setPlantNumber(Integer plantNumber) {
        this.plantNumber = plantNumber;
    }

    /**
     * 来源
     */
    public String getSourceCode() {
        return sourceCode;
    }

    /**
     * 来源
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

    /**
     * 创建人
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建人
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 创建人
     */
    public Integer getCreateUserId() {
        return createUserId;
    }

    /**
     * 创建人
     */
    public void setCreateUserId(Integer createUserId) {
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
     * 工单编号
     */
    public String getTaskNum() {
        return taskNum;
    }

    /**
     * 工单编号
     */
    public void setTaskNum(String taskNum) {
        this.taskNum = taskNum;
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
    public String getBreedCode() {
        return breedCode;
    }

    /**
     * 品种
     */
    public void setBreedCode(String breedCode) {
        this.breedCode = breedCode;
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
        PlantMultipleStockTb other = (PlantMultipleStockTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSeedNum() == null ? other.getSeedNum() == null : this.getSeedNum().equals(other.getSeedNum()))
            && (this.getTransformCode() == null ? other.getTransformCode() == null : this.getTransformCode().equals(other.getTransformCode()))
            && (this.getPlantNumber() == null ? other.getPlantNumber() == null : this.getPlantNumber().equals(other.getPlantNumber()))
            && (this.getSourceCode() == null ? other.getSourceCode() == null : this.getSourceCode().equals(other.getSourceCode()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getCreateUserId() == null ? other.getCreateUserId() == null : this.getCreateUserId().equals(other.getCreateUserId()))
            && (this.getCreateUserName() == null ? other.getCreateUserName() == null : this.getCreateUserName().equals(other.getCreateUserName()))
            && (this.getTaskNum() == null ? other.getTaskNum() == null : this.getTaskNum().equals(other.getTaskNum()))
            && (this.getSpeciesCode() == null ? other.getSpeciesCode() == null : this.getSpeciesCode().equals(other.getSpeciesCode()))
            && (this.getBreedCode() == null ? other.getBreedCode() == null : this.getBreedCode().equals(other.getBreedCode()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSeedNum() == null) ? 0 : getSeedNum().hashCode());
        result = prime * result + ((getTransformCode() == null) ? 0 : getTransformCode().hashCode());
        result = prime * result + ((getPlantNumber() == null) ? 0 : getPlantNumber().hashCode());
        result = prime * result + ((getSourceCode() == null) ? 0 : getSourceCode().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getCreateUserId() == null) ? 0 : getCreateUserId().hashCode());
        result = prime * result + ((getCreateUserName() == null) ? 0 : getCreateUserName().hashCode());
        result = prime * result + ((getTaskNum() == null) ? 0 : getTaskNum().hashCode());
        result = prime * result + ((getSpeciesCode() == null) ? 0 : getSpeciesCode().hashCode());
        result = prime * result + ((getBreedCode() == null) ? 0 : getBreedCode().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", seedNum=").append(seedNum);
        sb.append(", transformCode=").append(transformCode);
        sb.append(", plantNumber=").append(plantNumber);
        sb.append(", sourceCode=").append(sourceCode);
        sb.append(", remark=").append(remark);
        sb.append(", createTime=").append(createTime);
        sb.append(", createUserId=").append(createUserId);
        sb.append(", createUserName=").append(createUserName);
        sb.append(", taskNum=").append(taskNum);
        sb.append(", speciesCode=").append(speciesCode);
        sb.append(", breedCode=").append(breedCode);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}