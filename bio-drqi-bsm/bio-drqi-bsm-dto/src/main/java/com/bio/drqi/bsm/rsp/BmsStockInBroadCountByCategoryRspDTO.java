package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BmsStockInBroadCountByCategoryRspDTO {

    private String productCategoryCode;
    private String productCategoryName;
    private String dateTime;
    private BigDecimal countAmount;
}
