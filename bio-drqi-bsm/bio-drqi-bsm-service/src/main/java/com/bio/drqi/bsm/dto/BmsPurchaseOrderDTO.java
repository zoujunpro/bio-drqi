package com.bio.drqi.bsm.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class BmsPurchaseOrderDTO {

    /**
     * 采购单位
     */
    private String unitName;


    /**
     *采购类型编号
     */
    private String purchaseTypeCode;

    /**
     *采购类型描述
     */
    private String purchaseTypeName;


    /**
     * 需求提出日期
     */
    private String demandRequireTime;

    /**
     * 需求使用
     */
    private String demandUsageTime;

    /**
     * 申购事由描述
     */
    private String purchaseReasonRemark;


    private List<Product> productList;

    /**
     * 采购总金额
     */
    private BigDecimal purchaseTotalAmount;

    @Data
    public static class Product{
        /**
         * 归属项目编号
         */
        private String projectCode;

        /**
         * 品牌编号
         */
        private String brandCode;

        /**
         * 供应商编号
         */
        private String supplierCode;

        /**
         * 商品名称
         */

        private String productName;

        /**
         * 商品编号
         */
        private String productCode;

        /**
         * 商品类别
         */
        private String productSpecs;

        /**
         * 商品类型编号
         */
        private String productTypeCode;
        /**
         * 商品类型名称
         */
        private String productTypeName;
        /**
         * 商品类别编号
         */
        private String productCategoryCode;
        /**
         * 商品类别名称
         */
        private String productCategoryName;

        /**
         * 当前剩余库存
         */
        private Integer currentStockNum;

        /**
         *采购数量
         */
        private String purchaseNumber;
        /**
         *采购单价
         */
        private BigDecimal purchasePrice;
        /**
         *采购金额
         */
        private BigDecimal purchaseAmount;

    }

}


