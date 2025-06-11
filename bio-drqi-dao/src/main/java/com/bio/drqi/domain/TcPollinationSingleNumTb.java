package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * 授粉无取样编号的单珠编号区间
 * @TableName tc_pollination_single_num_tb
 */
@TableName(value ="tc_pollination_single_num_tb")
public class TcPollinationSingleNumTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 试验编号
     */
    private String experimentNum;

    /**
     * 授粉批次号
     */
    private String pollinationApplyNum;

    /**
     * 种子编号
     */
    private String seedNum;

    /**
     * 小区编号
     */
    private String regionNum;

    /**
     * 单株编号
     */
    private String singleNumber;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createUserName;

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
     * 单株编号
     */
    public String getSingleNumber() {
        return singleNumber;
    }

    /**
     * 单株编号
     */
    public void setSingleNumber(String singleNumber) {
        this.singleNumber = singleNumber;
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
    public String getCreateUserName() {
        return createUserName;
    }

    /**
     * 创建人
     */
    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
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
        TcPollinationSingleNumTb other = (TcPollinationSingleNumTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getExperimentNum() == null ? other.getExperimentNum() == null : this.getExperimentNum().equals(other.getExperimentNum()))
            && (this.getPollinationApplyNum() == null ? other.getPollinationApplyNum() == null : this.getPollinationApplyNum().equals(other.getPollinationApplyNum()))
            && (this.getSeedNum() == null ? other.getSeedNum() == null : this.getSeedNum().equals(other.getSeedNum()))
            && (this.getRegionNum() == null ? other.getRegionNum() == null : this.getRegionNum().equals(other.getRegionNum()))
            && (this.getSingleNumber() == null ? other.getSingleNumber() == null : this.getSingleNumber().equals(other.getSingleNumber()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getCreateUserName() == null ? other.getCreateUserName() == null : this.getCreateUserName().equals(other.getCreateUserName()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getExperimentNum() == null) ? 0 : getExperimentNum().hashCode());
        result = prime * result + ((getPollinationApplyNum() == null) ? 0 : getPollinationApplyNum().hashCode());
        result = prime * result + ((getSeedNum() == null) ? 0 : getSeedNum().hashCode());
        result = prime * result + ((getRegionNum() == null) ? 0 : getRegionNum().hashCode());
        result = prime * result + ((getSingleNumber() == null) ? 0 : getSingleNumber().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getCreateUserName() == null) ? 0 : getCreateUserName().hashCode());
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
        sb.append(", pollinationApplyNum=").append(pollinationApplyNum);
        sb.append(", seedNum=").append(seedNum);
        sb.append(", regionNum=").append(regionNum);
        sb.append(", singleNumber=").append(singleNumber);
        sb.append(", createTime=").append(createTime);
        sb.append(", createUserName=").append(createUserName);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}