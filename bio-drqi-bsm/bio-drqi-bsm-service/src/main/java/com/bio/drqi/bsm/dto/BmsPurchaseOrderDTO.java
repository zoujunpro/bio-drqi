package com.bio.drqi.bsm.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

@Data
public class BmsPurchaseOrderDTO {

    private String applyUserName;


    private String applyUserDept;


    private String applyTime;

    /**
     * 采购单位
     */
    @NotBlank(message = "请填写采购单位")
    private String unitName;

    @NotBlank(message = "请填写采购单位")
    private String unitCode;

    /**
     * 采购部门
     */
    @NotBlank(message = "请填写采购部门")
    private String purchaseDepartment;

    /**
     * 采购类型描述
     */
    private String purchaseTypeCode;


    /**
     * 需求提出日期
     */
    @NotBlank(message = "请填写需求提出日期")
    private String demandRequireTime;

    /**
     * 需求使用
     */
    @NotBlank(message = "请填写需求使用日期")
    private String demandUsageTime;

    /**
     * 申购事由描述
     */
    private String purchaseReasonRemark;

    /**
     * 附件地址
     */
    private String attachmentUrls;


    private List<Product> productList;

    /**
     * 采购总金额
     */
    private BigDecimal purchaseTotalAmount;

    @Data
    public static class Product {
        /**
         * 归属项目编号
         */
        private String projectCode;

        /**
         * 品牌编号
         */
        private String brandCode;

        /**
         * 供应商名称
         */

        private String supplierName;

        /**
         * 供应商编号
         */
        private String supplierCode;

        /**
         * 商品id
         */
        private Integer productId;

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
         * 商品图片
         */
        private String pictureUrls;

        /**
         * 当前剩余库存
         */
        private Integer currentStockNum;

        /**
         * 采购数量
         */
        private Integer purchaseNumber;
        /**
         * 采购单价
         */
        private String purchasePrice;

        /**
         * 采购金额
         */
        private String purchaseAmount;

    }

}


