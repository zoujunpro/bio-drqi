package com.bio.drqi.bsm.rsp;

import lombok.Data;

@Data
public class BmsStockPeriodCountListPageRspDTO {

    private Integer id;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品外部编号
     */
    private String productOutCode;

    /**
     * 所属类别编号
     */
    private String productCategoryCode;

    /**
     * 所属类别编号
     */
    private String productCategoryName;

    /**
     * 商品批次
     */
    private String batchNo;

    /**
     * 单位
     */
    private String unitCode;

    /**
     * 商品内部编号
     */
    private String productInnerCode;

    /**
     * 唯一编号
     */
    private String uniqueCode;

    /**
     * 库房编号
     */
    private String stockCode;

    private String stockName;

    /**
     * 期初数据
     */
    private Integer periodBeginNumber;

    /**
     * 期末数据
     */
    private Integer periodEndNumber;

    /**
     * 入库数量
     */
    private Integer totalInNumber;

    /**
     * 出库数量
     */
    private Integer totalOutNumber;

    /**
     * 期数
     */
    private String periodTime;

    /**
     * 退货总数量
     */
    private Integer returnNumber;

    /**
     * 调入数量
     */
    private Integer moveInNumber;

    /**
     * 调出数量
     */
    private Integer moveOutNumber;
}
