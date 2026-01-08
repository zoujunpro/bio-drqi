package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BmsOrderDetailDirectionSupplierCountCountRspDTO {

    private String supplierName;
    private String supplierCode;
    private BigDecimal amount;
}
