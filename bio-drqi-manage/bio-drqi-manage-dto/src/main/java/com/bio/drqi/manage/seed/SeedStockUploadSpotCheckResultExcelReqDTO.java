package com.bio.drqi.manage.seed;

import com.bio.common.core.util.EnumValue;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SeedStockUploadSpotCheckResultExcelReqDTO {

    @NotNull(message = "上传文件缺失")
    private MultipartFile file;

    /**
     * 是否覆盖旧的值
     */
    @NotBlank(message = "是否覆盖旧值参数缺失")
    @EnumValue(strValues = {"Y", "N"}, message = "是否覆盖旧值参数异常")
    private String overFlag;
}
