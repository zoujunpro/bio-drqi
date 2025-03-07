package com.bio.drqi.bsm.req;

import lombok.Data;

@Data
public class BmsBrandQueryListReqDTO {
    /**
     * 商品编号
     */
    private String supplierCode;

    /**
     * Y已删除，回收站   , N正常
     */
    private String deleteFlag;
}
