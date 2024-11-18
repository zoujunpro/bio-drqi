package com.bio.drqi.manage.dto.project;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TestExcelDTO {

    @ExcelProperty(value = "实施方案编号")
    @NotBlank(message = "参数缺失：实施方案编号")
    private String vectorTaskCode;

    @ExcelProperty(value = "取样编号")
    @NotBlank(message = "参数缺失：取样编号")
    private String sampleCode;

    @ExcelProperty(value = "取样时间")
    private String sampleTime;

    @ExcelProperty(value = "代次")
    private String generation;

    @ExcelProperty(value = "鉴定引物")
    private String identifyPrimer;

    @ExcelProperty(value = "检测方法")
    private String testMethod;

    @ExcelProperty(value = "编辑类型")
    private String editType;

    @ExcelProperty(value = "非转鉴定引物")
    private String noTransIdentityPrimer;

    @ExcelProperty(value = "是否为转基因阳性")
    private String isGeneModifyPositive;

    @ExcelProperty(value = "是否为定点插入")
    private String ifFixedPoint;

    @ExcelProperty(value = "定点纯合/定点杂合")
    private String fixedPointType;

    @ExcelProperty(value = "donor载体残留情况")
    private String donorResidueInfo;

    @ExcelProperty(value = "编辑工具残留情况")
    private String editResidueInfo;

    @ExcelProperty(value = "是否为单拷贝插入")
    private String ifCopyInsert;

    @ExcelProperty(value = "插入位点")
    private String insertionSite;

    @ExcelProperty(value = "ELISA结果（蛋白表达量)")
    private String elisaResult;

    @ExcelProperty(value = "qbzr表达量")
    private String qbzrSeq;
}
