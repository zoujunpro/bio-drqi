package com.bio.drqi.bsm.req;

import lombok.Data;
@Data
public class BmsProductAddReqDTO {

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
     * 商品类型编号
     */
    private String productTypeCode;
    /**
     * 品牌编号
     */
    private String brandCode;

    private String supplierCode;

    /**
     * 商品规格
     */
    private String productSpecs;

}
