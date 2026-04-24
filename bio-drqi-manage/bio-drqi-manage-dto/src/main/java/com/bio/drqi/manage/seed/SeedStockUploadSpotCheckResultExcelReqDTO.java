package com.bio.drqi.manage.seed;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SeedStockUploadSpotCheckResultExcelReqDTO {

    @NotNull(message = "上传文件缺失")
    private String excelUrl;

}
