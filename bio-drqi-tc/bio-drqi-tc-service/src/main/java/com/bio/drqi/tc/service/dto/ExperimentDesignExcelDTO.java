package com.bio.drqi.tc.service.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ExperimentDesignExcelDTO {

    /**
     * 实验编号
     */
    @ExcelProperty("实验编号")
    private String experimentNum;
    /**
     * 小区编号
     */
    @ExcelProperty("小区编号")
    private String regionNum;

    /**
     * 种子编号
     */
    @ExcelProperty("种子编号")
    private String seedNum;

    /**
     * 品种
     */
    @ExcelProperty("品种")
    private String breedName;
    /**
     * 实施方案编号
     */
    @ExcelProperty("实施方案编号")
    private String vectorTaskCode;
    /**
     * 目标性状
     */
    @ExcelProperty("目标性状")
    private String targetCharacter;

    /**
     * 世代
     */
    @ExcelProperty("世代")
    private String generationCode;

    /**
     * 基因类型
     */
    @ExcelProperty("基因型")
    private String tcGene;
    /**
     * 项目编号
     */
    @ExcelProperty("项目编号")
    private String projectCode;
    /**
     * 小区面积
     */
    @ExcelProperty("小区面积")
    private String regionArea;

    /**
     * 面积单位
     */
    @ExcelProperty("面积单位")
    private String areaUnit;

    /**
     * 行数
     */
    @ExcelProperty("小区行数")
    private String rowsNumber;

    /**
     * 行长
     */
    @ExcelProperty("小区行长(m)")
    private String rowsLength;

    /**
     * 行距
     */
    @ExcelProperty("行距(cm)")
    private String rowsSpace;

    /**
     * 株距(cm)
     */
    @ExcelProperty("株距(cm)")
    private String plantSpace;

    /**
     * 播种方式
     */
    @ExcelProperty("播种方式")
    private String seedingType;

    /**
     * 播种数量
     */
    @ExcelProperty("播种数量")
    private Integer seedingNumber;

    /**
     * 播种单位
     */
    @ExcelProperty("播种单位")
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
