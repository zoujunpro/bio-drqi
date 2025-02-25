package com.bio.flow.dto;

import lombok.Data;

@Data
public class BioTaskTypeListRspDTO {

    /**
     * 任务工单类型名称
     */
    private String taskTypeName;

    /**
     * 任务工单类型
     */
    private String taskTypeCode;

    /**
     * 流程ID
     */
    private String processId;

    private  String taskCategory;

}
