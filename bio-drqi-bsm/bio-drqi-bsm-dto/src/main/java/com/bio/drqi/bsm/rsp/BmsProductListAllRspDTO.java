package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class BmsProductListAllRspDTO {
    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品内部编号
     */
    private String productInnerCode;
}
