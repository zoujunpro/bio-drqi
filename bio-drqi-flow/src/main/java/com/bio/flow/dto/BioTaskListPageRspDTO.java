package com.bio.flow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class BioTaskListPageRspDTO {
    /**
     * 主键ID
     */
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date applyTime;


    /**
     * 项目描述
     */
    private String projectDesc;
    /**
     * 任务表单
     */
    private String taskForm;

    /**项目编码
     *
     */
    private String projectCode;

    /**
     * 项目名称
     */
    private String projectName;

    private Integer projectId;

    private String instanceId;




}
