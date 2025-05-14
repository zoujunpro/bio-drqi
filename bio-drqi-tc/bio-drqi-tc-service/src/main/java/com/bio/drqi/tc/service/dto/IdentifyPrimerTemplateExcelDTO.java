package com.bio.drqi.tc.service.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class IdentifyPrimerTemplateExcelDTO {


    @NotBlank(message = "数据不全：实验编号")
    @ExcelProperty("实验编号")
    private String experimentNum;

    @NotBlank(message = "数据不全：小区编号")
    @ExcelProperty("小区编号")
    private String regionNum;


    @NotBlank(message = "数据不全：种子编号")
    @ExcelProperty("种子编号")
    private String seedNum;

    @NotBlank(message = "数据不全：实施方案编号")
    @ExcelProperty("实施方案编号")
    private String vectorTaskCode;

    @NotBlank(message = "数据不全：取样编号")
    @ExcelProperty("取样编号")
    private String sampleCode;

    @NotBlank(message = "数据不全：鉴定引物")
    @ExcelProperty("鉴定引物")
    private String identifyPrimer;
}
