package com.bio.drqi.manage.dto.plant.task;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class SampleTestDownCloneRepeatSampleTemplateExcelDTO {

    @ExcelProperty("取样编号")
    private String sampleCode;


    @ExcelProperty("克隆苗数量")
    private String cloneSeedNum;
}
