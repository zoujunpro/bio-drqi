package com.bio.cer.projecttask;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ProjectExecuteTaskReqDTO {

    private Integer id;

    /**任务表单*/
    @NotBlank(message = "任务表单参数缺失")
    private String formObject;
}
