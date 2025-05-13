package com.bio.drqi.tc.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TcSampleTestDownTestTemplateReqDTO {

    @NotBlank(message = "取样申请任务工单号")
    private String applyNo;
}
