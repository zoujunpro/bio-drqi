package com.bio.cer.task;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BioExecuteTaskReqDTO {

    private Integer id;

    /**任务表单*/
    private String formObject;
}
