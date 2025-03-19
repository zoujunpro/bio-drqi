package com.bio.drqi.bsm.rsp;

import lombok.Data;

@Data
public class BmsProductStockDetailRspDTO {
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
     * 商品类别编号
     */
    private String productCategoryCode;

    /**
     * 商品类别名称
     */
    private String productCategoryName;

    /**
     * 品牌编号
     */
    private String brandCode;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 商品规格
     */
    private String productSpecs;

    /**
     * 商品批次
     */
    private String batchNo;

    /**
     * 累计入库数量
     */
    private Integer totalStoreNumber;

    /**
     * 当前库存数量
     */
    private Integer currentStockNumber;

    /**
     * 累计出库数量
     */
    private Integer totalOutNumber;



    /**
     * 单位
     */
    private String unitCode;

    /**
     * 库存位置编号
     */
    private String stockLocationNumber;

    private String productInnerCode;


    private String supplierCode;

    private String supplierName;

}
