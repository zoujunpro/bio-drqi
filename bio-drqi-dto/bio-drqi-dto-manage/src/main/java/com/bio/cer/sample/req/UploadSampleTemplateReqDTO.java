package com.bio.cer.sample.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UploadSampleTemplateReqDTO {


    @NotBlank(message = "取样申请任务编码")
    private String applyNo;

    @NotBlank(message = "文件地址缺失")
    private String excelUrl;
}
