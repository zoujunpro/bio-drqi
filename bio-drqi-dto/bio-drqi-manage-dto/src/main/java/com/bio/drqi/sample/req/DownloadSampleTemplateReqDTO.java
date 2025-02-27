package com.bio.drqi.sample.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DownloadSampleTemplateReqDTO {


    @NotBlank(message = "取样申请任务编码")
    private String applyNo;
}
