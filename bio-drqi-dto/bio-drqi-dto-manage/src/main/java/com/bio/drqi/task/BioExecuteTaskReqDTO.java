package com.bio.drqi.task;

import lombok.Data;

@Data
public class BioExecuteTaskReqDTO {

    private Integer id;

    /**任务表单*/
    private String formObject;
}
