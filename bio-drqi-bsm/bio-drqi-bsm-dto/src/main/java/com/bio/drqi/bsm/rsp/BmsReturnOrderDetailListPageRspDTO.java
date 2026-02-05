package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BmsReturnOrderDetailListPageRspDTO {
    private Integer id;

    /**
     * 子订单编号
     */
    private String orderDetailNum;

    /**
     * 退货数量
     */
    private BigDecimal returnNumber;

    /**
     * 退货金额
     */
    private BigDecimal returnAmount;

    /**
     * 申请人ID
     */
    private Integer applyUserId;

    /**
     * 申请人名称
     */
    private String applyUserName;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品单价
     */
    private BigDecimal productPrice;

    /**
     * 退货备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 订单编号
     */
    private String orderNum;

    /**
     * 商品规格
     */
    private String productSpecs;

    /**
     * 品牌编号
     */
    private String brandCode;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 商品批次
     */
    private String batchNo;

    /**
     * 单位
     */
    private String unitCode;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 供应商编号
     */
    private String supplierCode;

    /**
     * 商品内部编号
     */
    private String productInnerCode;

    /**
     * 过期时间
     */
    private String expirationDate;

    /**
     * 生产日期
     */
    private String produceDate;

    /**
     * 商品外部编号
     */
    private String productOutCode;

    /**
     * 税率
     */
    private String taxRate;

    private String stockCode;

    private String stockName;

    private String projectCode;

    private String productCategoryCode;

    private String productCategoryName;

    private String payType;
}
