package com.bio.cer.step.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApproveStepReqDTO {

    @NotNull(message = "关联ID缺失")
    /**refId*/
    private Integer refId;

    /**步骤编码*/
    @NotBlank(message = "步骤编码必填")
    private String flowStepCode;

    /** 审批结果 pass,reject*/
    @NotBlank(message = "审批结果必填")
    private String status;

    /** 拒绝原因*/
    private String reason;
}
