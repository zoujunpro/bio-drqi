package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BmsOrderDetailCountAmountByProjectCodeRspDTO {
    private String projectCode;
    private BigDecimal countAmount;
}
