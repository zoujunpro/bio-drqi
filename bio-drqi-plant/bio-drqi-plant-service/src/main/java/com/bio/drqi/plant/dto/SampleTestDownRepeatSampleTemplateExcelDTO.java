package com.bio.drqi.plant.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class SampleTestDownRepeatSampleTemplateExcelDTO {

    @ExcelProperty("取样编号")
    private String sampleCode;
}
