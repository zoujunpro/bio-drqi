package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BmsOrderDetailQueryByOrderNumRspDTO {
    private Integer id;

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
     * 项目名称
     */
    private String projectName;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 供应商编号
     */
    private String supplierCode;

    /**
     * 供应商联系人电话
     */
    private String contactUserTelephone;

    /**
     * 供应商联系人名称
     */
    private String contactUserName;

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

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 申请人ID
     */
    private Integer applyUserId;

    /**
     * 申请人名称
     */
    private String applyUserName;

    /**
     * 任务编号
     */
    private String taskNum;


    private String productTypeCode;

    private String productTypeName;
}
