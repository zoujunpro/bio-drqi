package com.bio.drqi.bsm.rsp;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BmsOrderDetailDirectionQueryReportNoInStockListPageRspDTO {
    /**
     * 订单编号
     */
    private String orderNum;

    /**
     * 子订单编号
     */
    private String orderDetailNum;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 供应商编号
     */
    private String supplierCode;


    /**
     * 品牌编号
     */
    private String brandCode;


    /**
     * 品牌编号
     */
    private String brandName;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品规格
     */
    private String productSpecs;

    /**
     * 商品外部编号
     */
    private String productOutCode;


    private String productInnerCode;

    /**
     * 采购单价
     */
    private BigDecimal purchasePrice;

    /**
     * 采购数量
     */
    private Integer purchaseNumber;

    /**
     * 付款金额
     */
    private BigDecimal payAmount;

    /**
     * 商品类别编号
     */
    private String productCategoryCode;

    private String productCategoryName;

    private String applyUnitCode;

    private String applyUnitName;

    private Integer receiveNumber;





}
