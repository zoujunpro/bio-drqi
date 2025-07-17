package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;
import java.util.Date;

/**
 * 小程序登录表
 * @TableName bio_applet_login_tb
 */
public class BioAppletLoginTb implements Serializable {
    /**
     *                                                                                                                                                                                                                                                                                            
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 小程序appId
     */
    private String appId;

    /**
     * 用户在此小程序的唯一ID
     */
    private String openId;

    /**
     * 电话
     */
    private String telephone;

    /**
     * 工号
     */
    private String jobNum;

    /**
     * 创建日期
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;

    /**
     *                                                                                                                                                                                                                                                                                            
     */
    public Integer getId() {
        return id;
    }

    /**
     *                                                                                                                                                                                                                                                                                            
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 小程序appId
     */
    public String getAppId() {
        return appId;
    }

    /**
     * 小程序appId
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * 用户在此小程序的唯一ID
     */
    public String getOpenId() {
        return openId;
    }

    /**
     * 用户在此小程序的唯一ID
     */
    public void setOpenId(String openId) {
        this.openId = openId;
    }

    /**
     * 电话
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * 电话
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * 工号
     */
    public String getJobNum() {
        return jobNum;
    }

    /**
     * 工号
     */
    public void setJobNum(String jobNum) {
        this.jobNum = jobNum;
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
        BioAppletLoginTb other = (BioAppletLoginTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getAppId() == null ? other.getAppId() == null : this.getAppId().equals(other.getAppId()))
            && (this.getOpenId() == null ? other.getOpenId() == null : this.getOpenId().equals(other.getOpenId()))
            && (this.getTelephone() == null ? other.getTelephone() == null : this.getTelephone().equals(other.getTelephone()))
            && (this.getJobNum() == null ? other.getJobNum() == null : this.getJobNum().equals(other.getJobNum()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getAppId() == null) ? 0 : getAppId().hashCode());
        result = prime * result + ((getOpenId() == null) ? 0 : getOpenId().hashCode());
        result = prime * result + ((getTelephone() == null) ? 0 : getTelephone().hashCode());
        result = prime * result + ((getJobNum() == null) ? 0 : getJobNum().hashCode());
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
        sb.append(", appId=").append(appId);
        sb.append(", openId=").append(openId);
        sb.append(", telephone=").append(telephone);
        sb.append(", jobNum=").append(jobNum);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}