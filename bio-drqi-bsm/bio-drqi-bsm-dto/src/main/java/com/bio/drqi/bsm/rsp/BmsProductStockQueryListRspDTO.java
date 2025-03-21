package com.bio.drqi.bsm.rsp;

import lombok.Data;

@Data
public class BmsProductStockQueryListRspDTO {

    /**
     * 商品名称
     */
    private String productName;
    /**
     * 品牌编号
     */
    private String brandCode;
    /**
     * 商品规格
     */
    private String productSpecs;
    /**
     * 商品批次
     */
    private String batchNo;
    /**
     * 当前库存数量
     */
    private Integer currentStockNumber;
    /**
     * 单位
     */
    private String unitCode;


    /**
     * 库存位置编号
     */
    private String stockLocationNumber;

    private String uniqueCode;
}
