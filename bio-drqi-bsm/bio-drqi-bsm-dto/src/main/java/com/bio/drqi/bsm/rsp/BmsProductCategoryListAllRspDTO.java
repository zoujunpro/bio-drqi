package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class BmsProductCategoryListAllRspDTO {
    private Integer id;
    /**
     * 商品类型名称
     */
    private String productCategoryName;

    /**
     * 商品类别编号
     */
    private String productCategoryCode;


}
