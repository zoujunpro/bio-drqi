package com.bio.drqi.bsm.req;

import lombok.Data;

@Data
public class BmsBrandEditReqDTO {

    private Integer id;
    /**
     * 品牌名称
     */
    private String brandName;
}
