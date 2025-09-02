package com.bio.drqi.manage.dto.project;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class IdentifyPrimerTemplateExcelDTO {
    @ExcelProperty(value = "实施方案编号", index = 0)
    @NotBlank(message = "数据不全：实施方案编号")
    private String vectorTaskCode;

    @ExcelProperty(value = "转化编号", index = 1)
    @NotBlank(message = "数据不全：缺失转化编号")
    private String transformCode;

    @ExcelProperty(value = "取样编号", index = 2)
    @NotBlank(message = "数据不全：缺失取样编号")
    private String sampleCode;

    @ExcelProperty(value = "鉴定引物", index = 3)
    @NotBlank(message = "数据不全：缺失鉴定引物")
    private String identifyPrimer;
}
