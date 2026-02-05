package com.bio.drqi.bsm.dto;

import com.bio.drqi.common.validator.EnumValue;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.util.List;

@Data
public class BmsPurchaseOrderDTO {



    private String applyUserName;


    private String applyUserDept;


    private String applyFullDeptName;


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
    @EnumValue(strValues = {"1","2"},message = "采购类型填写错误")
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


    @NotEmpty(message = "商品信息必填写")
    private List<Product> productList;

    /**
     * 采购总金额
     */
    private BigDecimal purchaseTotalAmount;

    @Data
    @Valid
    public static class Product {
        /**
         * 归属项目编号
         */
        @NotBlank(message = "参数缺失：归属项目")
        private String projectCode;

        /**
         * 供应商名称
         */
        private String supplierName;

        /**
         * 供应商编号
         */
        private String supplierCode;

        /**
         * 采购数量
         */
        private BigDecimal purchaseNumber;
        /**
         * 采购单价
         */
        private String purchasePrice;

        /**
         * 采购金额
         */
        private String purchaseAmount;
        /**
         * 品牌名称
         */
        private String brandName;

        private String brandCode;
        /**
         * 商品名称
         */
        private String productName;

        /**
         * 商品编码
         */
        private String productOutCode;

        /**
         * 内部编号
         */
        private String productInnerCode;

        /**
         * 商品规格
         */
        private String productSpecs;

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


        private String expectedDeliveryTime;

        @NotBlank(message = "参数缺失：付款类型")
        private String payType;


    }


    public String getAllProductName(){
        StringBuilder stringBuilder=new StringBuilder("");
        for (Product product:productList){
            stringBuilder.append(product.getProductName()).append(";");
        }
        String str=stringBuilder.toString();
        if(str.length()>1){
            return str.substring(0,str.length()-1);
        }else {
            return null;
        }

    }

}


