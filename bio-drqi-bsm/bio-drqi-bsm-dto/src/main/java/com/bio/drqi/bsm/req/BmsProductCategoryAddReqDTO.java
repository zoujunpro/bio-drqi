package com.bio.drqi.bsm.req;

import lombok.Data;

@Data
public class BmsProductCategoryAddReqDTO {
    /**
     * 商品类型名称
     */
    private String productCategoryName;


    private String kdCategoryCode;


    private String kdParentId;
}
