package com.bio.drqi.bsm.req;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BmsProductStockInLogReturnStockReqDTO {

    private Integer  id;

    private BigDecimal returnNumber;

    private String remark;
}
