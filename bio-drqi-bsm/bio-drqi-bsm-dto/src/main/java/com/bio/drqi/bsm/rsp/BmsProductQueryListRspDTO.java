package com.bio.drqi.bsm.rsp;

import lombok.Data;

@Data
public class BmsProductQueryListRspDTO {
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
    private String projectInnerCode;
}
