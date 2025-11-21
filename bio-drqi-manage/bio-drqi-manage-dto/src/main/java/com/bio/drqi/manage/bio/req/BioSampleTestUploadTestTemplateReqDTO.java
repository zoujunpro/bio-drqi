package com.bio.drqi.manage.bio.req;


import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BioSampleTestUploadTestTemplateReqDTO {

    @NotBlank(message = "取样申请任务工单号")
    private String applyNo;

    @NotBlank(message = "数据缺失")
    private String excelUrl;
}
