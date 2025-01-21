package com.bio.drqi.tissueEmbryo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class TissueEmbryoDataExcelDTO {

    @ExcelProperty("实施方案编号")
    private String vectorTaskCode;

    @ExcelProperty("取样编号")
    private String sampleCode;

    @ExcelProperty("备注")
    private String remark;
}
