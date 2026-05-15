package com.bio.drqi.tc.service.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ExperimentDesignExcelDTO {

    /**
     * 小区编号
     */
    @ExcelProperty("小区编号")
    @NotBlank(message = "小区编号必填")
    private String regionNum;

    /**
     * 种子编号
     */
    @ExcelProperty("种子编号")
    @NotBlank(message = "种子编号必填")
    private String seedNum;

    @ExcelIgnore
    private String breedCode;
    /**
     * 品种
     */
    /**
     * 品种
     */
    @ExcelProperty("品种")
    @NotBlank(message = "品种必填")
    private String breedName;
    /**
     * PD编号
     */
    @ExcelProperty("PD实施方案编号")
    private String pdImplementCode;
    /**
     * 实施方案编号
     */
    @ExcelProperty("实施方案编号")
    private String vectorTaskCode;
    /**
     * 目标性状
     */
    @ExcelProperty("目标性状")
    @NotBlank(message = "目标性状必填")
    private String targetCharacter;

    /**
     * 世代
     */
    @ExcelProperty("世代")
    @NotBlank(message = "世代必填")
    private String generationName;

    /**
     * 世代
     */
    @ExcelIgnore
    private String generationCode;

    /**
     * 基因类型
     */
    @ExcelProperty("基因型")
    @NotBlank(message = "基因型必填")
    private String tcGene;
    /**
     * 小区面积
     */
    @ExcelProperty("小区面积")
    @NotBlank(message = "小区面积必填")
    private String regionArea;

    /**
     * 面积单位
     */
    @ExcelProperty("面积单位")
    @NotBlank(message = "面积单位必填")
    private String areaUnit;

    /**
     * 行数
     */
    @ExcelProperty("小区行数")
    @NotBlank(message = "小区行数必填")
    private String rowsNumber;

    /**
     * 行长
     */
    @ExcelProperty("小区行长(m)")
    @NotBlank(message = "小区行长必填")
    private String rowsLength;

    /**
     * 行距
     */
    @ExcelProperty("行距(cm)")
    @NotBlank(message = "行距必填")
    private String rowsSpace;

    /**
     * 株距(cm)
     */
    @ExcelProperty("株距(cm)")
    @NotBlank(message = "株距必填")
    private String plantSpace;

    /**
     * 播种方式
     */
    @ExcelProperty("播种方式")
    @NotBlank(message = "播种方式必填")
    private String seedingType;

    /**
     * 播种数量
     */
    @ExcelProperty("播种数量")
    @NotNull(message = "播种数量必填")
    private Integer seedingNumber;

    /**
     * 播种单位
     */
    @ExcelProperty("播种单位")
    @NotBlank(message = "播种数量单位必填")
    private String seedingUnit;

    /**
     * 播种时间
     */
    @ExcelProperty("播种时间")
    private String seedingTime;
    /**
     * 出苗率
     */
    @ExcelProperty("出苗率")
    private String emergenceRate;

    /**
     * 移栽时间
     */
    @ExcelProperty("移栽时间")
    private String transplantTime;

    /**
     * 备注
     */
    @ExcelProperty("备注")
    private String remark;



}
