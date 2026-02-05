package com.bio.drqi.bsm.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BmsOrderDetailExcelDTO {

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
     * 项目名称
     */
    @ExcelProperty("项目名称")
    private String projectName;

    /**
     * 供应商名称
     */
    @ExcelProperty("供应商名称")
    private String supplierName;

    /**
     * 供应商编号
     */
    @ExcelProperty("供应商编号")
    private String supplierCode;

    /**
     * 供应商联系人电话
     */
    @ExcelProperty("供应商联系人电话")
    private String contactUserTelephone;

    /**
     * 供应商联系人名称
     */
    @ExcelProperty("供应商联系人名称")
    private String contactUserName;
    /**
     * 品牌名称
     */
    @ExcelProperty("品牌名称")
    private String brandName;


    @ExcelIgnore
    private String brandCode;

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
     * 采购单价
     */
    @ExcelProperty("采购单价")
    private BigDecimal purchasePrice;

    /**
     * 采购数量
     */
    @ExcelProperty("采购数量")
    private Integer purchaseNumber;

    /**
     * 付款金额
     */
    @ExcelProperty("付款金额")
    private BigDecimal payAmount;

    /**
     * 到货数量
     */
    @ExcelProperty("到货数量")
    private BigDecimal receiveNumber;

    /**
     * 退货数量
     */
    @ExcelProperty("退货数量")
    private BigDecimal returnNumber;

    /**
     * 商品类别名称
     */
    @ExcelProperty("商品类别")
    private String productCategoryName;

    @ExcelIgnore
    private String productCategoryCode;
    /**
     * 申请人名称
     */
    @ExcelProperty("申请人")
    private String applyUserName;


    @ExcelProperty("采购单位")
    private String applyUnitName;

    @ExcelProperty("合同编号")
    private String contractNumber;

    /**
     * 报账日期
     */
    @ExcelProperty("报账日期")
    private String reportAccountTime;

    @ExcelProperty("需求提出日期")
    private String demandRequireTime;

    @ExcelProperty("需求使用日期")
    private String demandUsageTime;

    @ExcelProperty("货期")
    private String expectedDeliveryTime;

    @ExcelIgnore
    private String payType;

    @ExcelProperty("付款类型")
    private String payTypeName;

}
