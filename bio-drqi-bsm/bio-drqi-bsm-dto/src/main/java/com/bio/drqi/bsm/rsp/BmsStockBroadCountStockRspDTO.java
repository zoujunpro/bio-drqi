package com.bio.drqi.bsm.rsp;

import lombok.Data;

@Data
public class BmsStockBroadCountStockRspDTO {

    private String totalStockInAmount;

    private String totalStockOutAmount;

    private String totalStockReturnAmount;

}
