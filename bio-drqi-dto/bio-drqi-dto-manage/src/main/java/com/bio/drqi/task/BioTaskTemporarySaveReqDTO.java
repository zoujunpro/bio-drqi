package com.bio.drqi.task;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class BioTaskTemporarySaveReqDTO {

    /**
     * 工单类型
     */
    @NotNull(message = "任务主键缺失")
    private Integer id;
    /**
     * 任务表单
     */
    @NotBlank(message = "任务表单参数缺失")
    private String formObject;

}
