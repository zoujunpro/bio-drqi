package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName bio_notice_log
 */
@TableName(value ="bio_notice_log")
public class BioNoticeLog implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 通知人
     */
    private String noticeUserName;

    /**
     * 通知的内容
     */
    private String noticeContent;

    /**
     * 通知的类型
     */
    private String noticeType;

    /**
     * 通知时间
     */
    private Date noticeTime;

    /**
     * 已读标识
     */
    private String readFlag;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 飞书ID
     */
    private String openId;

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
     * 通知人
     */
    public String getNoticeUserName() {
        return noticeUserName;
    }

    /**
     * 通知人
     */
    public void setNoticeUserName(String noticeUserName) {
        this.noticeUserName = noticeUserName;
    }

    /**
     * 通知的内容
     */
    public String getNoticeContent() {
        return noticeContent;
    }

    /**
     * 通知的内容
     */
    public void setNoticeContent(String noticeContent) {
        this.noticeContent = noticeContent;
    }

    /**
     * 通知的类型
     */
    public String getNoticeType() {
        return noticeType;
    }

    /**
     * 通知的类型
     */
    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    /**
     * 通知时间
     */
    public Date getNoticeTime() {
        return noticeTime;
    }

    /**
     * 通知时间
     */
    public void setNoticeTime(Date noticeTime) {
        this.noticeTime = noticeTime;
    }

    /**
     * 已读标识
     */
    public String getReadFlag() {
        return readFlag;
    }

    /**
     * 已读标识
     */
    public void setReadFlag(String readFlag) {
        this.readFlag = readFlag;
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
     * 飞书ID
     */
    public String getOpenId() {
        return openId;
    }

    /**
     * 飞书ID
     */
    public void setOpenId(String openId) {
        this.openId = openId;
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
        BioNoticeLog other = (BioNoticeLog) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getNoticeUserName() == null ? other.getNoticeUserName() == null : this.getNoticeUserName().equals(other.getNoticeUserName()))
            && (this.getNoticeContent() == null ? other.getNoticeContent() == null : this.getNoticeContent().equals(other.getNoticeContent()))
            && (this.getNoticeType() == null ? other.getNoticeType() == null : this.getNoticeType().equals(other.getNoticeType()))
            && (this.getNoticeTime() == null ? other.getNoticeTime() == null : this.getNoticeTime().equals(other.getNoticeTime()))
            && (this.getReadFlag() == null ? other.getReadFlag() == null : this.getReadFlag().equals(other.getReadFlag()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getOpenId() == null ? other.getOpenId() == null : this.getOpenId().equals(other.getOpenId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getNoticeUserName() == null) ? 0 : getNoticeUserName().hashCode());
        result = prime * result + ((getNoticeContent() == null) ? 0 : getNoticeContent().hashCode());
        result = prime * result + ((getNoticeType() == null) ? 0 : getNoticeType().hashCode());
        result = prime * result + ((getNoticeTime() == null) ? 0 : getNoticeTime().hashCode());
        result = prime * result + ((getReadFlag() == null) ? 0 : getReadFlag().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getOpenId() == null) ? 0 : getOpenId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", noticeUserName=").append(noticeUserName);
        sb.append(", noticeContent=").append(noticeContent);
        sb.append(", noticeType=").append(noticeType);
        sb.append(", noticeTime=").append(noticeTime);
        sb.append(", readFlag=").append(readFlag);
        sb.append(", createTime=").append(createTime);
        sb.append(", openId=").append(openId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}