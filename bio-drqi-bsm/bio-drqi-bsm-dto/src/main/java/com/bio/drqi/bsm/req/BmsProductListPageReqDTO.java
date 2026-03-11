package com.bio.drqi.bsm.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class BmsProductListPageReqDTO extends PageDTO {

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品外部编号
     */
    private String productOutCode;

    /**
     * 商品内部编号
     */
    private String productInnerCode;

    /**
     * 商品类别编号
     */
    private String productCategoryCode;

    /**
     * 品牌编号
     */
    private String brandCode;

    /**
     * Y启用， 禁用N
     */
    private String productStatus;

    private String purchaseTypeCode;
}
