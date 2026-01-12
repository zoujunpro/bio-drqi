package com.bio.drqi.bsm.rsp;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BmsOrderDetailDirectionQueryReportNoInStockListPageRspDTO {



    /**
     * 订单编号
     */
    @ExcelProperty("订单编号")
    private String orderNum;
    /**
     * 项目编号
     */
    @ExcelProperty("项目编号")
    private String projectCode;

    /**
     * 供应商编号
     */
    @ExcelProperty("供应商编号")
    private String supplierCode;


    /**
     * 品牌编号
     */
    @ExcelIgnore
    private String brandCode;


    /**
     * 品牌编号
     */
    @ExcelProperty("品牌")
    private String brandName;

    /**
     * 商品名称
     */
    @ExcelProperty("商品名称")
    private String productName;

    /**
     * 商品规格
     */
    @ExcelProperty("商品规格")
    private String productSpecs;

    /**
     * 商品外部编号
     */
    @ExcelProperty("商品外部编号")
    private String productOutCode;

    @ExcelProperty("商品内部编号")
    private String productInnerCode;

    /**
     * 商品类别编号
     */
    @ExcelIgnore
    private String productCategoryCode;

    @ExcelProperty("商品类别")
    private String productCategoryName;

    @ExcelIgnore
    private String applyUnitCode;

    @ExcelProperty("单位")
    private String applyUnitName;
    /**
     * 采购单价
     */
    @ExcelProperty("采购单价")
    private BigDecimal purchasePrice;

    /**
     * 采购数量
     */
    @ExcelProperty("采购数量")
    private Integer purchaseNumber;


    @ExcelProperty("接收数量")
    private Integer receiveNumber;


    /**
     * 付款金额
     */
    @ExcelProperty("付款金额")
    private BigDecimal payAmount;






}
