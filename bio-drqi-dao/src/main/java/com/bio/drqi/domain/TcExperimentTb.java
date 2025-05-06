package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * 田测实验表
 * @TableName tc_experiment_tb
 */
@TableName(value ="tc_experiment_tb")
public class TcExperimentTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;

    /**
     * 物种编码
     */
    private String speciesCode;

    /**
     * 物种名称
     */
    private String speciesName;

    /**
     * 上传附件
     */
    private String fileUrl;

    /**
     * 实验目的
     */
    private String experimentGoal;

    /**
     * 实验地点
     */
    private String experimentAddress;

    /**
     * 申请人
     */
    private String applyUserName;

    /**
     * 申请人iD
     */
    private Integer applyUserId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 实验编号
     */
    private String experimentNum;

    /**
     * 任务编号
     */
    private String taskNum;

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
     * 物种名称
     */
    public String getSpeciesName() {
        return speciesName;
    }

    /**
     * 物种名称
     */
    public void setSpeciesName(String speciesName) {
        this.speciesName = speciesName;
    }

    /**
     * 上传附件
     */
    public String getFileUrl() {
        return fileUrl;
    }

    /**
     * 上传附件
     */
    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    /**
     * 实验目的
     */
    public String getExperimentGoal() {
        return experimentGoal;
    }

    /**
     * 实验目的
     */
    public void setExperimentGoal(String experimentGoal) {
        this.experimentGoal = experimentGoal;
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
     * 申请人
     */
    public String getApplyUserName() {
        return applyUserName;
    }

    /**
     * 申请人
     */
    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    /**
     * 申请人iD
     */
    public Integer getApplyUserId() {
        return applyUserId;
    }

    /**
     * 申请人iD
     */
    public void setApplyUserId(Integer applyUserId) {
        this.applyUserId = applyUserId;
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
        TcExperimentTb other = (TcExperimentTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getProjectCode() == null ? other.getProjectCode() == null : this.getProjectCode().equals(other.getProjectCode()))
            && (this.getVectorTaskCode() == null ? other.getVectorTaskCode() == null : this.getVectorTaskCode().equals(other.getVectorTaskCode()))
            && (this.getSpeciesCode() == null ? other.getSpeciesCode() == null : this.getSpeciesCode().equals(other.getSpeciesCode()))
            && (this.getSpeciesName() == null ? other.getSpeciesName() == null : this.getSpeciesName().equals(other.getSpeciesName()))
            && (this.getFileUrl() == null ? other.getFileUrl() == null : this.getFileUrl().equals(other.getFileUrl()))
            && (this.getExperimentGoal() == null ? other.getExperimentGoal() == null : this.getExperimentGoal().equals(other.getExperimentGoal()))
            && (this.getExperimentAddress() == null ? other.getExperimentAddress() == null : this.getExperimentAddress().equals(other.getExperimentAddress()))
            && (this.getApplyUserName() == null ? other.getApplyUserName() == null : this.getApplyUserName().equals(other.getApplyUserName()))
            && (this.getApplyUserId() == null ? other.getApplyUserId() == null : this.getApplyUserId().equals(other.getApplyUserId()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getExperimentNum() == null ? other.getExperimentNum() == null : this.getExperimentNum().equals(other.getExperimentNum()))
            && (this.getTaskNum() == null ? other.getTaskNum() == null : this.getTaskNum().equals(other.getTaskNum()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getProjectCode() == null) ? 0 : getProjectCode().hashCode());
        result = prime * result + ((getVectorTaskCode() == null) ? 0 : getVectorTaskCode().hashCode());
        result = prime * result + ((getSpeciesCode() == null) ? 0 : getSpeciesCode().hashCode());
        result = prime * result + ((getSpeciesName() == null) ? 0 : getSpeciesName().hashCode());
        result = prime * result + ((getFileUrl() == null) ? 0 : getFileUrl().hashCode());
        result = prime * result + ((getExperimentGoal() == null) ? 0 : getExperimentGoal().hashCode());
        result = prime * result + ((getExperimentAddress() == null) ? 0 : getExperimentAddress().hashCode());
        result = prime * result + ((getApplyUserName() == null) ? 0 : getApplyUserName().hashCode());
        result = prime * result + ((getApplyUserId() == null) ? 0 : getApplyUserId().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getExperimentNum() == null) ? 0 : getExperimentNum().hashCode());
        result = prime * result + ((getTaskNum() == null) ? 0 : getTaskNum().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", projectCode=").append(projectCode);
        sb.append(", vectorTaskCode=").append(vectorTaskCode);
        sb.append(", speciesCode=").append(speciesCode);
        sb.append(", speciesName=").append(speciesName);
        sb.append(", fileUrl=").append(fileUrl);
        sb.append(", experimentGoal=").append(experimentGoal);
        sb.append(", experimentAddress=").append(experimentAddress);
        sb.append(", applyUserName=").append(applyUserName);
        sb.append(", applyUserId=").append(applyUserId);
        sb.append(", createTime=").append(createTime);
        sb.append(", experimentNum=").append(experimentNum);
        sb.append(", taskNum=").append(taskNum);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}