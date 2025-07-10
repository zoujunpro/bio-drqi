package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单明细表
 * @TableName bms_order_detail_tb
 */
@TableName(value ="bms_order_detail_tb")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BmsOrderDetailTb implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
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

    private String pictureUrls;

    private String purchaseDate;

    private String applyUnitCode;

    private String applyUnitName;

    private String purchaseDepartment;

    private Integer receiveNumber;


    private String contractNumber;

    private String paymentVoucherUrls;

    /**
     * 合同地址
     */
    private String contractUrls;
    /**
     * 发票信息
     */
    private String invoiceUrls;
    /**
     * 报账日期
     */
    private String reportAccountTime;


    private String demandRequireTime;

    private String demandUsageTime;

    private String expectedDeliveryTime;

    private String taxRate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}