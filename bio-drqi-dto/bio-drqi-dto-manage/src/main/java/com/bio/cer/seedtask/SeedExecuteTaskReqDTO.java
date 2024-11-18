package com.bio.cer.seedtask;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SeedExecuteTaskReqDTO {

    private Integer taskId;

    /**任务表单*/
    private String formObject;
}
