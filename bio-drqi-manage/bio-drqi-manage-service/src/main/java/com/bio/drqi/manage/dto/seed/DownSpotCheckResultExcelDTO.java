package com.bio.drqi.manage.dto.seed;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DownSpotCheckResultExcelDTO {



    @ExcelProperty("种子编号")
    @NotBlank(message = "excel中种子编号数据丢失")
    private String seedNum;

    @ExcelProperty("取样编号")
    private String sampleCode;

    @ExcelProperty("取样数量")
    private String sampleNumber;

    @ExcelProperty("鉴定结果")
    private String identityResult;

    @ExcelProperty("抽检反馈")
    private String spotCheckResult;
}
