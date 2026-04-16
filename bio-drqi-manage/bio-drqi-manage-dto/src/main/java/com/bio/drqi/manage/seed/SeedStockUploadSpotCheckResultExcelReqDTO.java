package com.bio.drqi.manage.seed;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Data
public class SeedStockUploadSpotCheckResultExcelReqDTO {

    @NotNull(message = "上传文件缺失")
    private MultipartFile file;

}
