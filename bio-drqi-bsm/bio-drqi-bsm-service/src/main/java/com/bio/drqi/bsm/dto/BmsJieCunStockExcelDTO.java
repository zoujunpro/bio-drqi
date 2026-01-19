package com.bio.drqi.bsm.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;


@Data
public class BmsJieCunStockExcelDTO {
    /**
     * 主键ID
     */
    @ExcelProperty("主键ID")
    private Integer id;

    /**
     * 商品名称
     */
    @ExcelProperty("商品名称")
    private String productName;


    /**
     * 所属类别编号
     */
    @ExcelProperty("所属类别编号")
    private String productCategoryCode;


    /**
     * 品牌编号
     */
    @ExcelProperty("品牌编号")
    private String brandCode;

    /**
     * 品牌名称
     */
    @ExcelProperty("品牌名称")
    private String brandName;

    /**
     * 商品规格
     */
    @ExcelProperty("商品规格")
    private String productSpecs;

    /**
     * 商品批次
     */
    @ExcelProperty("商品批次")
    private String batchNo;

    /**
     * 当前库存数量
     */
    @ExcelProperty("当前库存数量")
    private Integer currentStockNumber;

    /**
     * 单位
     */
    @ExcelProperty("单位")
    private String unitCode;


    @ExcelProperty("商品编号")
    private String productInnerCode;

    @ExcelProperty("供应商编号")
    private String supplierCode;

    @ExcelProperty("供应商名称")
    private String supplierName;


    @ExcelProperty("生产日期")
    private String produceDate;


    @ExcelProperty("库房编号")
    private String stockCode;

    @ExcelProperty("项目编号")
    private String projectCode;

    @ExcelProperty("项目名称")
    private String projectName;

    @ExcelProperty("项目类型")
    private String projectType;
    /**
     * 唯一编号
     */
    @ExcelProperty("唯一编号")
    private String uniqueCode;

    @ExcelProperty("采购单价")
    private String productPrice;
}
