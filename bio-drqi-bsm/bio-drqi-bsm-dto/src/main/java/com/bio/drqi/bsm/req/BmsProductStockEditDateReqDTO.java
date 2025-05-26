package com.bio.drqi.bsm.req;

import lombok.Data;

@Data
public class BmsProductStockEditDateReqDTO {

    private Integer id;

    private String produceDate;

    private String expirationDate;

}
