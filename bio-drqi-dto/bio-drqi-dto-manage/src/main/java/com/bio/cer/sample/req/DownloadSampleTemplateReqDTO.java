package com.bio.cer.sample.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class DownloadSampleTemplateReqDTO {


    @NotBlank(message = "取样申请任务编码")
    private String applyNo;
}
