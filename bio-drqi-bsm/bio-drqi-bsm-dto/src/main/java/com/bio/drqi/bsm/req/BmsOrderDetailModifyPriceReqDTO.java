package com.bio.drqi.bsm.req;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class BmsOrderDetailModifyPriceReqDTO {

    /**
     * 主键ID
     */
    @NotNull(message = "缺失主键")
    private Integer id;
    /**
     * 采购数量
     */
    private BigDecimal purchasePrice;
}
