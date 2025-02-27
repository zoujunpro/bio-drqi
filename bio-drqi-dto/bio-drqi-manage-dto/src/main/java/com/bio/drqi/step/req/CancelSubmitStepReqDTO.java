package com.bio.drqi.step.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CancelSubmitStepReqDTO {

    @NotNull(message = "流程关联主键缺失")
    private Integer refId;

    /**步骤编码*/
    @NotBlank(message = "缺失步骤编码")
    private String flowStepCode;
}
