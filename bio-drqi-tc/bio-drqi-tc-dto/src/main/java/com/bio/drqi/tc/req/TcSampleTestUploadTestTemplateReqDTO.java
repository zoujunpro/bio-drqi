package com.bio.drqi.tc.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TcSampleTestUploadTestTemplateReqDTO {

    @NotBlank(message = "取样申请任务工单号")
    private String applyNo;

    @NotBlank(message = "数据缺失")
    private String excelUrl;
}
