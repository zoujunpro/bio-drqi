package com.bio.drqi.bsm.req;

import lombok.Data;

@Data
public class BmsProductStockInLogReturnStockReqDTO {

    private String  orderDetailNum;

    private Integer num;

    private String returnNumber;
}
