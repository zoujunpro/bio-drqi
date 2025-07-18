package com.bio.drqi.bsm.req;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BmsOrderDetailTaxRateReqDTO {
    /**
     * 订单编号
     */
    @NotNull(message = "订单ID缺失")
    private Integer id;

    private String taxRate;
}
