package com.bio.drqi.bsm.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class BmsProductStockListPageReqDTO extends PageDTO {
    /**
     * 商品名称
     */
    private String productName;


    /**
     * 商品类别编号
     */
    private String productCategoryCode;

    /**
     * 商品类别名称
     */
    private String productCategoryName;


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

    private String stockCode;

}
