package com.bio.drqi.manage.dto.project;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class SampleTestBioInfoExcelDTO {

    @ExcelProperty("取样编号")
    private String sampleCode;

    @ExcelProperty("手工编号")
    private String sampleId;

    @ExcelProperty("Run")
    private String runId;

}
