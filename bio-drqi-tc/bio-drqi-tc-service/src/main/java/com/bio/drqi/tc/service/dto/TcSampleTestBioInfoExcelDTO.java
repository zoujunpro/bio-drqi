package com.bio.drqi.tc.service.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class TcSampleTestBioInfoExcelDTO {

    @ExcelProperty("取样编号")
    private String sampleCode;

    @ExcelProperty("手工编号")
    private String sampleId;

    @ExcelProperty("Run")
    private String runId;

}
