package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 实施方案时间计划表
 * @TableName cer_vector_task_plan_log
 */
@TableName(value ="cer_vector_task_plan_log")
@Data
public class CerVectorTaskPlanLog implements Serializable {
    /**
     * 主键ID
     */
    @TableId
    private Integer id;

    /**
     * 实施方案ID
     */
    private Integer vectorTaskId;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 预估开始时间
     */
    private String estimatedStartTime;

    /**
     * 预估结束时间
     */
    private String estimatedEndTime;

    /**
     * 实际开始时间
     */
    private String actualStartTime;

    /**
     * 实际结束时间
     */
    private String actualEndTime;

    /**
     * 用户ID
     */
    private Integer userId;

    /**
     * 用户名
     */
    private String userName;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}