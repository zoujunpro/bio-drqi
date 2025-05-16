package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName tc_harvest_seed_apply_tb
 */
@TableName(value ="tc_harvest_seed_apply_tb")
public class TcHarvestSeedApplyTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 授粉批次号
     */
    private String pollinationApplyNum;

    /**
     * 收获批次号
     */
    private String harvestApplyNum;

    /**
     * 收获时间
     */
    private String harvestTime;

    /**
     * 创建日期
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
     * 实验编号
     */
    private String experimentNum;

    /**
     * 收获文件
     */
    private String harvestFileUrl;

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
     * 授粉批次号
     */
    public String getPollinationApplyNum() {
        return pollinationApplyNum;
    }

    /**
     * 授粉批次号
     */
    public void setPollinationApplyNum(String pollinationApplyNum) {
        this.pollinationApplyNum = pollinationApplyNum;
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
     * 创建日期
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建日期
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
     * 收获文件
     */
    public String getHarvestFileUrl() {
        return harvestFileUrl;
    }

    /**
     * 收获文件
     */
    public void setHarvestFileUrl(String harvestFileUrl) {
        this.harvestFileUrl = harvestFileUrl;
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
        TcHarvestSeedApplyTb other = (TcHarvestSeedApplyTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getTaskNum() == null ? other.getTaskNum() == null : this.getTaskNum().equals(other.getTaskNum()))
            && (this.getPollinationApplyNum() == null ? other.getPollinationApplyNum() == null : this.getPollinationApplyNum().equals(other.getPollinationApplyNum()))
            && (this.getHarvestApplyNum() == null ? other.getHarvestApplyNum() == null : this.getHarvestApplyNum().equals(other.getHarvestApplyNum()))
            && (this.getHarvestTime() == null ? other.getHarvestTime() == null : this.getHarvestTime().equals(other.getHarvestTime()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getCreateUserId() == null ? other.getCreateUserId() == null : this.getCreateUserId().equals(other.getCreateUserId()))
            && (this.getCreateUserName() == null ? other.getCreateUserName() == null : this.getCreateUserName().equals(other.getCreateUserName()))
            && (this.getExperimentNum() == null ? other.getExperimentNum() == null : this.getExperimentNum().equals(other.getExperimentNum()))
            && (this.getHarvestFileUrl() == null ? other.getHarvestFileUrl() == null : this.getHarvestFileUrl().equals(other.getHarvestFileUrl()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getTaskNum() == null) ? 0 : getTaskNum().hashCode());
        result = prime * result + ((getPollinationApplyNum() == null) ? 0 : getPollinationApplyNum().hashCode());
        result = prime * result + ((getHarvestApplyNum() == null) ? 0 : getHarvestApplyNum().hashCode());
        result = prime * result + ((getHarvestTime() == null) ? 0 : getHarvestTime().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getCreateUserId() == null) ? 0 : getCreateUserId().hashCode());
        result = prime * result + ((getCreateUserName() == null) ? 0 : getCreateUserName().hashCode());
        result = prime * result + ((getExperimentNum() == null) ? 0 : getExperimentNum().hashCode());
        result = prime * result + ((getHarvestFileUrl() == null) ? 0 : getHarvestFileUrl().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", taskNum=").append(taskNum);
        sb.append(", pollinationApplyNum=").append(pollinationApplyNum);
        sb.append(", harvestApplyNum=").append(harvestApplyNum);
        sb.append(", harvestTime=").append(harvestTime);
        sb.append(", createTime=").append(createTime);
        sb.append(", createUserId=").append(createUserId);
        sb.append(", createUserName=").append(createUserName);
        sb.append(", experimentNum=").append(experimentNum);
        sb.append(", harvestFileUrl=").append(harvestFileUrl);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}