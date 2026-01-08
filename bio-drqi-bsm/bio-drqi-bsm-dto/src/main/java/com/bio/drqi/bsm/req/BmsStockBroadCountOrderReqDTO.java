package com.bio.drqi.bsm.req;

import lombok.Data;

@Data
public class BmsStockBroadCountOrderReqDTO {

    private String countType;

    private String beginDateTime;

    private String endDateTime;

    private String productInnerCode;

    private String productCategoryCode;

    private String unitCode;
}
