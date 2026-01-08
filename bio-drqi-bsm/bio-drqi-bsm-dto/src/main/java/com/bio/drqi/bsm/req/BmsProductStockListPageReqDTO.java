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
     * 商品名称
     */
    private String productInnerCode;


    /**
     * 商品类别编号
     */
    private String productCategoryCode;


    /**
     * 品牌名称
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

    private String stockCode;

    private String filterZeroFlag;

}
