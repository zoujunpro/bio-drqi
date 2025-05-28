package com.bio.flow.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class BioTaskExcelDTO {

    /**
     * 工单类型名称
     */
    @ExcelProperty("工单类型")
    private String taskTypeName;

    /**
     * 任务工单号
     */
    @ExcelProperty("任务工单号")
    private String taskNum;

    /**
     * 任务工单状态 1审批中 2审批通过，3审批拒绝，4任务工单取消
     */
    @ExcelIgnore
    private String taskStatus;


    @ExcelProperty("任务工单状态")
    private String taskStatusName;


    /**
     * 任务工单描述
     */
    @ExcelProperty("任务工单描述")
    private String taskDesc;

    /**
     * 申请人姓名
     */
    @ExcelProperty("申请人")
    private String applyUserName;

    /**
     * 工单发起日期
     */
    @ExcelProperty("申请时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date applyTime;


}
