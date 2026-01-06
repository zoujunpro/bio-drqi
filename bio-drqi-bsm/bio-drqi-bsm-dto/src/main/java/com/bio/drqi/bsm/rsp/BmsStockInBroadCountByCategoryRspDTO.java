package com.bio.drqi.bsm.rsp;

import lombok.Data;

@Data
public class BmsStockInBroadCountByCategoryRspDTO {

    private String productCategoryCode;
    private String productCategoryName;
    private String countAmount;
}
