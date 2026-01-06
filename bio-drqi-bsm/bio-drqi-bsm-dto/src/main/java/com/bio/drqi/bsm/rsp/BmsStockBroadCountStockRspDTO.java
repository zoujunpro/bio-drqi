package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BmsStockBroadCountStockRspDTO {

    /**
     * 出库金额
     */
    private BigDecimal totalStockInAmount;

    /**
     * 入库金额
     */
    private BigDecimal totalStockOutAmount;

    /**
     * 退货金额
     */
    private BigDecimal totalStockReturnAmount;

    public BigDecimal getTotalStockInAmount() {
        return totalStockInAmount==null?new BigDecimal(0):totalStockInAmount;
    }

    public BigDecimal getTotalStockOutAmount() {
        return totalStockOutAmount==null?new BigDecimal(0):totalStockOutAmount;
    }

    public BigDecimal getTotalStockReturnAmount() {
        return totalStockReturnAmount==null?new BigDecimal(0):totalStockReturnAmount;
    }
}
