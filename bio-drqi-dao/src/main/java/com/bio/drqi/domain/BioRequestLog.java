package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 请求日志记录表
 * @TableName bio_request_log
 */
@TableName(value ="bio_request_log")
@Data
public class BioRequestLog implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 请求用户ID
     */
    private Integer requestUserId;

    /**
     * 请求用户名称
     */
    private String requestUserName;

    /**
     * 请求时间
     */
    private Date requestTime;

    /**
     * 请求参数
     */
    private String requestParam;

    /**
     * 请求方法
     */
    private String requestMethod;

    /**
     * 跟踪ID
     */
    private String requestId;


    private String requestDesc;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}