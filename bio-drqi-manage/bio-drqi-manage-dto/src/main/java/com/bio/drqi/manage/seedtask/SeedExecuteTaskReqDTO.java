package com.bio.drqi.manage.seedtask;

import lombok.Data;

@Data
public class SeedExecuteTaskReqDTO {

    private Integer taskId;

    /**任务表单*/
    private String formObject;
}
