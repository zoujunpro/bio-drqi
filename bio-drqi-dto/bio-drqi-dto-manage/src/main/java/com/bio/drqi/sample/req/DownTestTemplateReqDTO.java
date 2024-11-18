package com.bio.drqi.sample.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DownTestTemplateReqDTO {

    @NotBlank(message = "取样申请任务工单号")
    private String applyNo;
}
