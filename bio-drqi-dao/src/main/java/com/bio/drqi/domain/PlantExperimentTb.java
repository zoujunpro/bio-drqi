package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName plant_experiment_tb
 */
@TableName(value ="plant_experiment_tb")
public class PlantExperimentTb implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 物种
     */
    private String speciesCode;

    /**
     * 试验类型 1供试  2分离提存 3扩繁  4法规测试
     */
    private String experimentType;

    /**
     * 试验目标
     */
    private String experimentTarget;

    /**
     * 试验方案
     */
    private String designUrl;

    /**
     * 试验附件
     */
    private String fileUrl;

    /**
     * 试验编号
     */
    private String experimentNum;

    /**
     * 创建时间
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
     * 试验方案
     */
    private String vectorTaskCodes;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    public Integer getId() {
        return id;
    }

    /**
     * 主键id
     */
    public void setId(Integer id) {
        this.id = id;
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
     * 试验类型 1供试  2分离提存 3扩繁  4法规测试
     */
    public String getExperimentType() {
        return experimentType;
    }

    /**
     * 试验类型 1供试  2分离提存 3扩繁  4法规测试
     */
    public void setExperimentType(String experimentType) {
        this.experimentType = experimentType;
    }

    /**
     * 试验目标
     */
    public String getExperimentTarget() {
        return experimentTarget;
    }

    /**
     * 试验目标
     */
    public void setExperimentTarget(String experimentTarget) {
        this.experimentTarget = experimentTarget;
    }

    /**
     * 试验方案
     */
    public String getDesignUrl() {
        return designUrl;
    }

    /**
     * 试验方案
     */
    public void setDesignUrl(String designUrl) {
        this.designUrl = designUrl;
    }

    /**
     * 试验附件
     */
    public String getFileUrl() {
        return fileUrl;
    }

    /**
     * 试验附件
     */
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
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
     * 试验方案
     */
    public String getVectorTaskCodes() {
        return vectorTaskCodes;
    }

    /**
     * 试验方案
     */
    public void setVectorTaskCodes(String vectorTaskCodes) {
        this.vectorTaskCodes = vectorTaskCodes;
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
        PlantExperimentTb other = (PlantExperimentTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSpeciesCode() == null ? other.getSpeciesCode() == null : this.getSpeciesCode().equals(other.getSpeciesCode()))
            && (this.getExperimentType() == null ? other.getExperimentType() == null : this.getExperimentType().equals(other.getExperimentType()))
            && (this.getExperimentTarget() == null ? other.getExperimentTarget() == null : this.getExperimentTarget().equals(other.getExperimentTarget()))
            && (this.getDesignUrl() == null ? other.getDesignUrl() == null : this.getDesignUrl().equals(other.getDesignUrl()))
            && (this.getFileUrl() == null ? other.getFileUrl() == null : this.getFileUrl().equals(other.getFileUrl()))
            && (this.getExperimentNum() == null ? other.getExperimentNum() == null : this.getExperimentNum().equals(other.getExperimentNum()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getCreateUserId() == null ? other.getCreateUserId() == null : this.getCreateUserId().equals(other.getCreateUserId()))
            && (this.getCreateUserName() == null ? other.getCreateUserName() == null : this.getCreateUserName().equals(other.getCreateUserName()))
            && (this.getVectorTaskCodes() == null ? other.getVectorTaskCodes() == null : this.getVectorTaskCodes().equals(other.getVectorTaskCodes()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSpeciesCode() == null) ? 0 : getSpeciesCode().hashCode());
        result = prime * result + ((getExperimentType() == null) ? 0 : getExperimentType().hashCode());
        result = prime * result + ((getExperimentTarget() == null) ? 0 : getExperimentTarget().hashCode());
        result = prime * result + ((getDesignUrl() == null) ? 0 : getDesignUrl().hashCode());
        result = prime * result + ((getFileUrl() == null) ? 0 : getFileUrl().hashCode());
        result = prime * result + ((getExperimentNum() == null) ? 0 : getExperimentNum().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getCreateUserId() == null) ? 0 : getCreateUserId().hashCode());
        result = prime * result + ((getCreateUserName() == null) ? 0 : getCreateUserName().hashCode());
        result = prime * result + ((getVectorTaskCodes() == null) ? 0 : getVectorTaskCodes().hashCode());
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
        sb.append(", experimentType=").append(experimentType);
        sb.append(", experimentTarget=").append(experimentTarget);
        sb.append(", designUrl=").append(designUrl);
        sb.append(", fileUrl=").append(fileUrl);
        sb.append(", experimentNum=").append(experimentNum);
        sb.append(", createTime=").append(createTime);
        sb.append(", createUserId=").append(createUserId);
        sb.append(", createUserName=").append(createUserName);
        sb.append(", vectorTaskCodes=").append(vectorTaskCodes);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}