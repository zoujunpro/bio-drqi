package com.bio.flow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class BioTaskDetailRspDTO {

    private Integer id;

    /**
     * 任务工单类型
     */
    private String taskTypeCode;

    /**
     * 任务工单号
     */
    private String taskNum;

    /**
     * 任务工单状态 1审批中 2审批通过，3任务工单取消
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date applyTime;

    /**
     * 创建日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新日期
     */
    private Date updateTime;


    /**
     * 项目ID
     */
    private Integer projectId;
    /**
     * 项目编码
     */
    private String projectCode;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 任务表单
     */
    private String taskForm;

    /**
     * 工单类型名称
     */
    private String taskTypeName;

    /**
     * 流程ID
     */
    private String instanceId;

    /**
     * 关联工单
     */
    private String refTaskNum;
    /**
     * 关联工单
     */
    private String refTaskDesc;

}
