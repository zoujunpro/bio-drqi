package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BmsOrderDetailBroadOrderCountRspDTO {
    /**
     * 采购总金额
     */
    private BigDecimal countPurchaseAmount;

    /**
     * 报账总金额
     */
    private BigDecimal countReportAmount;

    public BmsOrderDetailBroadOrderCountRspDTO build() {
        if (countPurchaseAmount == null) {
            countPurchaseAmount = new BigDecimal(0);
        }
        if (countReportAmount == null) {
            countReportAmount = new BigDecimal(0);
        }
        return this;
    }

}
