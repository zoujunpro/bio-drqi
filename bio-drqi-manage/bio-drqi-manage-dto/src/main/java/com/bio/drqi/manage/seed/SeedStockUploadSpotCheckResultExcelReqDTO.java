package com.bio.drqi.manage.seed;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SeedStockUploadSpotCheckResultExcelReqDTO {

    @NotBlank(message = "上传的excel参数缺失")
    private String excelUrl;
}
