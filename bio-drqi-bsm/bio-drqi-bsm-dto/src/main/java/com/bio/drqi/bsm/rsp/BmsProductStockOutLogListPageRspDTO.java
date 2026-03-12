package com.bio.drqi.bsm.rsp;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BmsProductStockOutLogListPageRspDTO {


    @ExcelIgnore
    private Integer id;

    /**
     * 商品名称
     */
    @ExcelProperty("商品名称")
    private String productName;

    /**
     * 商品外部编号
     */
    @ExcelProperty("商品外部编号")
    private String productOutCode;

    /**
     * 商品内部编号
     */
    @ExcelProperty("商品内部编号")
    private String productInnerCode;

    /**
     * 所属类别编号
     */
    @ExcelIgnore
    private String productCategoryCode;
    /**
     * 所属类别编号
     */
    @ExcelProperty("商品类别")
    private String productCategoryName;

    /**
     * 品牌编号
     */
    @ExcelIgnore
    private String brandCode;

    /**
     * 品牌名称
     */
    @ExcelProperty("品牌")
    private String brandName;

    /**
     * 商品规格
     */
    @ExcelProperty("商品规格")
    private String productSpecs;

    /**
     * 商品批次
     */
    @ExcelProperty("商品批次")
    private String batchNo;

    /**
     * 出库数量
     */
    @ExcelProperty("出库数量")
    private BigDecimal outNumber;

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
     * 创建时间
     */
    @ExcelProperty("出库时间")
    private Date createTime;

    /**
     * 任务编号
     */
    @ExcelIgnore
    private String taskNum;

    /**
     * 出库备注
     */
    @ExcelProperty("出库备注")
    private String remark;

    /**
     * 出库类型 1正常出库 2退货出库
     */
    @ExcelIgnore
    private String outType;

    /**
     * 出库类型 1正常出库 2退货出库
     */
    @ExcelProperty("出库类型")
    private String outTypeName;

    /**
     * 单位编号
     */
    @ExcelProperty("单位编号")
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
     * 唯一编号
     */
    @ExcelProperty("商品库存唯一编码")
    private String uniqueCode;

    @ExcelProperty("商品生产日期")
    private String produceDate;

    @ExcelProperty("商品有效期")
    private String expirationDate;

    @ExcelIgnore
    private String stockCode;

    @ExcelProperty("库房")
    private String stockName;

    @ExcelProperty("金蝶同步反馈结果")
    private Integer kdNumber;

    @ExcelProperty("单价")
    private BigDecimal productPrice;

    @ExcelProperty("出库金额")
    private BigDecimal outAmount;


    @ExcelIgnore
    private String payType;

    @ExcelProperty("付款类型")
    private String payTypeName;

}
