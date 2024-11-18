package com.bio.cer.seedtask;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SeedTaskDetailRspDTO {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 任务类型名称
     */
    private String taskTypeName;

    /**
     * 任务类型编码
     */
    private String taskTypeCode;

    /**
     * 任务工单号
     */
    private String taskNum;

    /**
     * 任务工单状态 1执行中    2已执行  3拒绝执行
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
     * 任务表单
     */
    private String taskForm;

    /**
     * 创建日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    private String instanceId;
}
