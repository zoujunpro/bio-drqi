package com.bio.drqi.bsm.req;

import lombok.Data;

@Data
public class BmsProductStockInLogReturnStockReqDTO {

    private Integer  id;

    private Integer returnNumber;

    private String remark;
}
