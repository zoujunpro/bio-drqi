package com.bio.drqi.bsm.rsp;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class BmsStockPeriodCountListPageRspDTO {

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
    @ExcelIgnore
    private String productOutCode;

    /**
     * 所属类别编号
     */
    @ExcelIgnore
    private String productCategoryCode;

    /**
     * 所属类别编号
     */
    @ExcelProperty("所属类别编号")
    private String productCategoryName;

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
     * 商品内部编号
     */
    @ExcelProperty("商品内部编号")
    private String productInnerCode;

    /**
     * 唯一编号
     */
    @ExcelIgnore
    private String uniqueCode;

    /**
     * 库房编号
     */
    @ExcelIgnore
    private String stockCode;

    @ExcelProperty("库房编号")
    private String stockName;

    /**
     * 期数
     */
    @ExcelProperty("日期")
    private String periodTime;
    /**
     * 期初数据
     */
    @ExcelProperty("期初数量")
    private Integer periodBeginNumber;

    /**
     * 期末数据
     */
    @ExcelProperty("期末数量")
    private Integer periodEndNumber;

    /**
     * 入库数量
     */
    @ExcelProperty("入库数量")
    private Integer totalInNumber;

    /**
     * 出库数量
     */
    @ExcelProperty("出库数量")
    private Integer totalOutNumber;
    /**
     * 退货总数量
     */
    @ExcelProperty("退货数量")
    private Integer returnNumber;

    /**
     * 调入数量
     */
    @ExcelProperty("调入数量")
    private Integer moveInNumber;

    /**
     * 调出数量
     */
    @ExcelProperty("调出数量")
    private Integer moveOutNumber;
}
