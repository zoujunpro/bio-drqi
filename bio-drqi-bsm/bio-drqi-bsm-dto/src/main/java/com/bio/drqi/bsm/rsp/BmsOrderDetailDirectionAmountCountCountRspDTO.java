package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BmsOrderDetailDirectionAmountCountCountRspDTO {

    private String dateTime;

    private BigDecimal purchaseAmount;

    private BigDecimal reportAmount;
}
