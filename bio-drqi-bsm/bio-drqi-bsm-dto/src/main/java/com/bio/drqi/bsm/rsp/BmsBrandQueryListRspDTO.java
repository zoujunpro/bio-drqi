package com.bio.drqi.bsm.rsp;

import lombok.Data;

@Data
public class BmsBrandQueryListRspDTO {

    private String id;
    /**
     * 品牌编号
     */
    private String brandCode;

    /**
     * 品牌名称
     */
    private String brandName;
}
