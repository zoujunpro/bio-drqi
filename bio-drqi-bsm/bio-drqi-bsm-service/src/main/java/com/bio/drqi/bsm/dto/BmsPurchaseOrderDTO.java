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
    @NotBlank(message = "请填写采购类型描述")
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
        @NotBlank(message = "参数缺失：归属项目")
        private String projectCode;

        /**
         * 供应商名称
         */
        @NotBlank(message = "参数缺失：供应商名称")
        private String supplierName;

        /**
         * 供应商编号
         */
        @NotBlank(message = "参数缺失：供应商名称")
        private String supplierCode;

        /**
         * 采购数量
         */
        @NotBlank(message = "参数缺失：采购数量")
        private Integer purchaseNumber;
        /**
         * 采购单价
         */
        @NotBlank(message = "参数缺失：采购单价")
        private String purchasePrice;

        /**
         * 采购金额
         */
        @NotBlank(message = "参数缺失：采购金额")
        private String purchaseAmount;
        /**
         * 品牌名称
         */
        @NotBlank(message = "参数缺失：品牌名称")
        private String brandName;

        private String brandCode;
        /**
         * 商品名称
         */
        @NotBlank(message = "参数缺失：商品名称")
        private String productName;

        /**
         * 商品编码
         */
        @NotBlank(message = "参数缺失：商品编码")
        private String productOutCode;

        /**
         * 内部编号
         */
        private String productInnerCode;

        /**
         * 商品规格
         */
        @NotBlank(message = "参数缺失：商品规格")
        private String productSpecs;

        /**
         * 商品类别编号
         */
        @NotBlank(message = "参数缺失：商品类别编号")
        private String productCategoryCode;
        /**
         * 商品类别名称
         */
        @NotBlank(message = "参数缺失：商品类别名称")
        private String productCategoryName;

        /**
         * 商品图片
         */
        private String pictureUrls;


    }

}


