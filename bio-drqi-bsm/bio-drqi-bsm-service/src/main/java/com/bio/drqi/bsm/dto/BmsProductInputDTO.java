package com.bio.drqi.bsm.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class BmsProductInputDTO {

    /**
     * 订单编号
     */
    @NotBlank(message = "订单信息缺失")
    private String orderNum;

    /**
     * 采购负责人
     */
    private String applyUserName;

    /**
     * 申请时间
     */
    private String applyTime;

    /**
     * 采购部门
     */
    @NotBlank(message = "采购部门缺失")
    private String purchaseDepartment;

    /**
     * 申请单位编号
     */
    @NotBlank(message = "单位信息缺失")
    private String applyUnitCode;

    /**
     * 申请单位名称
     */
    @NotBlank(message = "单位信息缺失")
    private String applyUnitName;


    private List<OrderDetail> orderDetailList;


    @Data
    public static class OrderDetail {

        /**
         * 项目编号
         */
        private String projectCode;

        /**
         * 项目名称
         */
        private String projectName;

        /**
         * 品牌编号
         */
        private String brandCode;

        /**
         * 品牌名称
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

        /**
         * 商品类别名称
         */
        private String productCategoryName;


        private String productTypeCode;

        private String productTypeName;

        private String applyUnitCode;

        private String applyUnitName;

        /**
         * 到货数量
         */
        private Integer receiveNumber;


        @NotBlank(message = "入库参数缺少：订单明细")
        private String orderDetailNum;

        /**
         * 批次号
         */

        private String batchNo;

        /**
         * 入库数量
         */
        @NotNull(message = "入库参数缺少：入库数量")
        private Integer number;

        /**
         * 库存位置号
         */
        private List<String> stockLocationNumberList;
    }


}
