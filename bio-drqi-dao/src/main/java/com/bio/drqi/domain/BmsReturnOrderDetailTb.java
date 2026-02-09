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
 * 退货订单明细表
 * @TableName bms_return_order_detail_tb
 */
@TableName(value ="bms_return_order_detail_tb")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BmsReturnOrderDetailTb implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
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
     * 商品批次
     */
    private String batchNo;

    /**
     * 单位
     */
    private String unitCode;


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

    private Integer inStockId;

    private String stockCode;

    private String uniqueCode;

    private String projectCode;

    private String payType;


    @TableField(exist = false)
    private String startDate;

    @TableField(exist = false)
    private String endDate;

    private String kdNumber;

    private String productCategoryCode;

    @TableField(exist = false)
    private String countType;

    @TableField(exist = false)
    private String beginDateTime;

    @TableField(exist = false)
    private String endDateTime;

    @TableField(exist = false)
    private String dateTime;


    @TableField(exist = false)
    private String ifSynJinDieFlag;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}