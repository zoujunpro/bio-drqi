package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName bio_notice_log
 */
@TableName(value ="bio_notice_log")
@Data
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

    private String openId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}