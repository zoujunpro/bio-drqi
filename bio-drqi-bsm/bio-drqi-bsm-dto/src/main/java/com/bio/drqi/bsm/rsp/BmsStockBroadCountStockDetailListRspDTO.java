package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BmsStockBroadCountStockDetailListRspDTO {
    /**
     * 商品名称
     */
    private String productName;


    /**
     * 所属类别编号
     */
    private String productCategoryCode;

    /**
     * 所属类别编号
     */
    private String productCategoryName;


    /**
     * 品牌编号
     */
    private String brandCode;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 商品规格
     */
    private String productSpecs;

    /**
     * 商品批次
     */
    private String batchNo;
    /**
     * 单位
     */
    private String unitCode;


    private String productInnerCode;


    private String stockCode;

    private String stockName;
    /**
     * 积累入库金额
     */
    private BigDecimal inAmount;
    /**
     * 累计出库金额
     */
    private BigDecimal outAmount;

    /**
     * 累计退货金额
     */
    private BigDecimal returnAmount;

    /**
     * 积累入库数量
     */
    private Integer inNumber;
    /**
     * 累计出库数量
     */
    private Integer outNumber;
    /**
     * 累计退货数量
     */
    private Integer returnNumber;

    /**
     * 调入数量
     */
    private Integer moveInNumber;

    /**
     * 调出数量
     */
    private Integer moveOutNumber;

    /**
     * 调入金额
     */
    private BigDecimal moveInAmount;

    /**
     * 调出金额
     */
    private BigDecimal moveOutAmount;



}
