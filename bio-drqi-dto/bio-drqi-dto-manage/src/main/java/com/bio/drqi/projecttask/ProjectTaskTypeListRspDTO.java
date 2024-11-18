package com.bio.drqi.projecttask;

import lombok.Data;

@Data
public class ProjectTaskTypeListRspDTO {

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

}
