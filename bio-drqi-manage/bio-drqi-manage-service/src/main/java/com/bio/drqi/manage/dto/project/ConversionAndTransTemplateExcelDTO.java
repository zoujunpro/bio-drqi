package com.bio.drqi.manage.dto.project;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ConversionAndTransTemplateExcelDTO {

    @ExcelProperty("实施方案编号")
    private String vectorTaskCode;

    @ExcelProperty("取样编号")
    private String sampleCode;

    @ExcelProperty("品种（受体材料）")
    private String acceptorMaterial;

    @ExcelProperty("是否编辑纯合")
    private String editPureUnion;

    @ExcelProperty("是否转基因")
    private String transGeneFlag;

    @ExcelProperty("备注")
    private String remark;
}
