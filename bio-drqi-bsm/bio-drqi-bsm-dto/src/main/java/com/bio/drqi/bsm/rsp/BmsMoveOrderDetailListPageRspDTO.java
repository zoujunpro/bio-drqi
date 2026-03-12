package com.bio.drqi.bsm.rsp;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BmsMoveOrderDetailListPageRspDTO {


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
     * 所属类别编号
     */
    @ExcelIgnore
    private String productCategoryCode;

    /**
     * 所属类别编号
     */
    @ExcelProperty("类别")
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
     * 生产日期
     */
    @ExcelProperty("生产日期")
    private String produceDate;

    /**
     * 过期时间
     */
    @ExcelProperty("过期时间")
    private String expirationDate;

    /**
     * from库房编号
     */
    @ExcelIgnore
    private String fromStockCode;

    /**
     * from库房
     */
    @ExcelProperty("移出库房")
    private String fromStockName;

    /**
     * from库存位置编号
     */
    @ExcelIgnore
    private String fromStockLocationNumber;

    /**
     * to库房编号
     */
    @ExcelIgnore
    private String toStockCode;

    /**
     * to库房
     */
    @ExcelProperty("移入库房")
    private String toStockName;

    /**
     * to库存位置编号
     */
    @ExcelIgnore
    private String toStockLocationNumber;

    /**
     * 移库数量
     */
    @ExcelProperty("移库数量")
    private BigDecimal moveNumber;

    /**
     * 创建人
     */
    @ExcelIgnore
    private Integer createUserId;

    /**
     * 创建人名称
     */
    @ExcelProperty("移库人")
    private String createUserName;

    /**
     * 创建时间
     */
    @ExcelProperty("移库时间")
    private Date createTime;

    @ExcelProperty("商品单价")
    private BigDecimal productPrice;

    @ExcelProperty("金额")
    private BigDecimal moveAmount;


    @ExcelIgnore
    private String payType;

    @ExcelProperty("付款类型")
    private String payTypeName;

    @ExcelProperty("金蝶同步反馈结果")
    private String kdNumber;

}
