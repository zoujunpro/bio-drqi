package com.bio.drqi.bsm.rsp;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BmsStockBroadCountStockDetailListRspDTO {
    /**
     * 商品名称
     */
    @ExcelProperty(value = "商品名称",index = 0)
    private String productName;


    /**
     * 所属类别编号
     */
    @ExcelIgnore
    private String productCategoryCode;

    /**
     * 所属类别编号
     */
    @ExcelProperty(value = "类别编号",index = 1)
    private String productCategoryName;


    /**
     * 品牌编号
     */
    @ExcelIgnore
    private String brandCode;

    /**
     * 品牌名称
     */
    @ExcelProperty(value = "品牌名称",index = 2)
    private String brandName;

    /**
     * 商品规格
     */
    @ExcelProperty(value = "商品规格",index = 3)
    private String productSpecs;

    /**
     * 商品批次
     */
    @ExcelProperty(value = "商品批次",index = 4)
    private String batchNo;
    /**
     * 单位
     */
    @ExcelProperty(value = "单位",index = 5)
    private String unitCode;

    @ExcelProperty(value = "商品编号",index = 6)
    private String productInnerCode;


    @ExcelIgnore
    private String stockCode;


    @ExcelProperty(value = "库房名称",index = 7)
    private String stockName;
    /**
     * 积累入库数量
     */
    @ExcelProperty(value = "入库数量",index = 8)
    private BigDecimal inNumber;
    /**
     * 积累入库金额
     */
    @ExcelProperty(value = "入库金额",index = 9)
    private BigDecimal inAmount;

    /**
     * 累计出库数量
     */
    @ExcelProperty(value = "出库数量",index = 10)
    private BigDecimal outNumber;
    /**
     * 累计出库金额
     */
    @ExcelProperty(value = "出库金额",index = 11)
    private BigDecimal outAmount;


    /**
     * 累计退货数量
     */
    @ExcelProperty(value = "退货数量",index = 12)
    private BigDecimal returnNumber;
    /**
     * 累计退货金额
     */
    @ExcelProperty(value = "退货金额",index = 13)
    private BigDecimal returnAmount;

    /**
     * 调入数量
     */
    @ExcelProperty(value = "调入数量",index = 14)
    private BigDecimal moveInNumber;

    /**
     * 调出数量
     */
    @ExcelProperty(value = "调出数量",index = 15)
    private BigDecimal moveOutNumber;

    /**
     * 调入金额
     */
    @ExcelProperty(value = "调入金额",index = 16)
    private BigDecimal moveInAmount;

    /**
     * 调出金额
     */
    @ExcelProperty(value = "调出金额",index = 17)
    private BigDecimal moveOutAmount;



}
