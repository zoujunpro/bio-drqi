package com.bio.drqi.bsm.rsp;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BmsReturnOrderDetailListPageRspDTO {

    @ExcelIgnore
    private Integer id;
    /**
     * 订单编号
     */
    @ExcelProperty("采购订单编号")
    private String orderNum;
    /**
     * 子订单编号
     */
    @ExcelProperty("订单明细编号")
    private String orderDetailNum;

    /**
     * 退货数量
     */
    @ExcelProperty("退货数量")
    private BigDecimal returnNumber;

    /**
     * 退货金额
     */
    @ExcelProperty("退货金额")
    private BigDecimal returnAmount;

    /**
     * 申请人ID
     */
    @ExcelIgnore
    private Integer applyUserId;

    /**
     * 申请人名称
     */
    @ExcelProperty("申请人名称")
    private String applyUserName;

    /**
     * 商品名称
     */
    @ExcelProperty("商品名称")
    private String productName;

    /**
     * 商品单价
     */
    @ExcelProperty("商品单价")
    private BigDecimal productPrice;

    /**
     * 退货备注
     */
    @ExcelProperty("退货备注")
    private String remark;

    /**
     * 创建时间
     */
    @ExcelProperty("退货时间")
    private Date createTime;



    /**
     * 商品规格
     */
    @ExcelProperty("商品规格")
    private String productSpecs;

    /**
     * 品牌编号
     */
    @ExcelIgnore
    private String brandCode;

    /**
     * 品牌名称
     */
    @ExcelProperty("品牌名称")
    private String brandName;

    /**
     * 商品批次
     */
    @ExcelProperty("商品批次")
    private String batchNo;

    /**
     * 单位
     */
    @ExcelProperty("单位")
    private String unitCode;

    /**
     * 供应商名称
     */
    @ExcelProperty("供应商名称")
    private String supplierName;

    /**
     * 供应商编号
     */
    @ExcelProperty("供应商编号")
    private String supplierCode;

    /**
     * 商品内部编号
     */
    @ExcelProperty("商品内部编号")
    private String productInnerCode;

    /**
     * 过期时间
     */
    @ExcelProperty("过期时间")
    private String expirationDate;

    /**
     * 生产日期
     */
    @ExcelProperty("生产日期")
    private String produceDate;

    /**
     * 商品外部编号
     */
    @ExcelProperty("商品外部编号")
    private String productOutCode;

    /**
     * 税率
     */
    @ExcelProperty("税率")
    private String taxRate;

    @ExcelIgnore
    private String stockCode;


    @ExcelProperty("库房")
    private String stockName;

    @ExcelProperty("项目编号")
    private String projectCode;

    @ExcelIgnore
    private String productCategoryCode;

    @ExcelProperty("商品类别")
    private String productCategoryName;

    @ExcelIgnore
    private String payType;


    @ExcelProperty("付款类型")
    private String payTypeName;

    @ExcelProperty("金蝶同步反馈结果")
    private String kdNumber;
}
