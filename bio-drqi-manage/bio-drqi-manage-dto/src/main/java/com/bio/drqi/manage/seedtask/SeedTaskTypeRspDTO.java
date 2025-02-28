package com.bio.drqi.manage.seedtask;

import lombok.Data;

@Data
public class SeedTaskTypeRspDTO {

    /**
     * 任务工单类型名称
     */
    private String taskTypeName;

    /**
     * 任务工单类型
     */
    private String taskTypeCode;


    private String processId;
}
