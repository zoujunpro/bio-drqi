package com.bio.drqi.bsm.req;

import lombok.Data;

@Data
public class BmsBrandAddReqDTO {
    /**
     * 商品编号
     */
    private String supplierCode;

    /**
     * 品牌名称
     */
    private String brandName;
}
