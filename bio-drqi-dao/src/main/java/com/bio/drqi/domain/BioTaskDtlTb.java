package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 项目任务表
 * @TableName bio_task_dtl_tb
 */
@TableName(value ="bio_task_dtl_tb")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BioTaskDtlTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 任务工单类型
     */
    private String taskTypeCode;

    /**
     * 工单类型名称
     */
    private String taskTypeName;

    /**
     * 任务工单号
     */
    private String taskNum;

    /**
     * 任务工单状态 1审批中 2审批通过，3审批拒绝，4任务工单取消
     */
    private String taskStatus;

    /**
     * 任务工单描述
     */
    private String taskDesc;

    /**
     * 申请人ID
     */
    private Integer applyUserId;

    /**
     * 申请人姓名
     */
    private String applyUserName;

    /**
     * 工单发起日期
     */
    private Date applyTime;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 更新日期
     */
    private Date updateTime;

    /**
     * 任务表单
     */
    private String taskForm;

    /**
     * 流程实例ID
     */
    private Long instanceId;

    /**
     * 关联工单
     */
    private String refTaskNum;

    private String taskCategory;

    @TableField(exist = false)
    private Integer countNum;

    @TableField(exist = false)
    private String applyDate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}