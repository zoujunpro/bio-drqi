package com.bio.drqi.manage.dto.bio;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DownLoadIdentifyPrimerTemplateExcelDTO {

    @ExcelProperty(value = "取样编号")
    @NotBlank(message = "数据不全：缺失取样编号")
    private String sampleCode;


    @ExcelProperty(value = "鉴定引物")
    @NotBlank(message = "数据不全：缺失鉴定引物")
    private String identifyPrimer;
}
