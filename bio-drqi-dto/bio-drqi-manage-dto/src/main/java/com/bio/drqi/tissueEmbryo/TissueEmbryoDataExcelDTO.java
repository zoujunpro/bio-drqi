package com.bio.drqi.tissueEmbryo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class TissueEmbryoDataExcelDTO {


    @ExcelProperty("取样编号")
    private String sampleCode;

    @ExcelProperty("备注")
    private String remark;


    @ExcelProperty("打印数量")
    private Integer printNum;
}
