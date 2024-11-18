package com.bio.drqi.manage.dto.project;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class CerPlantExcelDTO {


    /**
     * 种子编号/取样编号
     */
    @ExcelProperty(value = "种子编号/取样编号",index = 0)
    private String cerNumber;

    /**
     * 代次
     */
    @ExcelProperty(value = "代次",index = 1)
    private String generation;

    /**
     * 温室编号
     */
    @ExcelProperty(value = "温室编号",index = 2)
    private String greenhouseNumber;

    /**
     * 株数
     */
    @ExcelProperty(value = "株数",index = 3)
    private Integer plantNumber;

    /**
     * 播种/移苗日期
     */
    @ExcelProperty(value = "播种/移苗日期",index = 4)
    private String firstPlantDate;

    /**
     * 移栽日期
     */
    @ExcelProperty(value = "移栽日期",index = 5)
    private String transplantDate;

    /**
     * 植株状态
     */
    @ExcelProperty(value = "植株状态",index = 6)
    private String plantStatus;

    /**
     * 春化开始日期
     */
    @ExcelProperty(value = "春化开始日期",index = 7)
    private String vernalizationBeginDate;

    /**
     * 春化结束日期
     */
    @ExcelProperty(value = "春化结束日期",index = 8)
    private String vernalizationEndDate;

    /**
     * 授粉方式
     */
    @ExcelProperty(value = "授粉方式",index = 9)
    private String pollinationMethod;

    /**
     * 父本信息
     */
    @ExcelProperty(value = "父本信息",index = 10)
    private String fatherInfo;

    /**
     * 母本信息
     */
    @ExcelProperty(value = "母本信息",index = 11)
    private String motherInfo;

    /**
     * 散粉期
     */
    @ExcelProperty(value = "散粉期",index = 12)
    private String powderDispersionDate;

    /**
     * 吐丝期
     */
    @ExcelProperty(value = "吐丝期",index = 13)
    private String spinningDate;

    /**
     * 抽穗期
     */
    @ExcelProperty(value = "抽穗期",index = 14)
    private String headingDate;

    /**
     * 开花期
     */
    @ExcelProperty(value = "开花期",index = 15)
    private String floweringDate;

    /**
     * 鼓粒期
     */
    @ExcelProperty(value = "鼓粒期",index = 16)
    private String podFillDate;

    /**
     * 授粉时间
     */
    @ExcelProperty(value = "授粉时间",index = 17)
    private String pollinationDate;

    /**
     * 收获日期
     */
    @ExcelProperty(value = "收获日期",index = 18)
    private String harvestDate;




}
