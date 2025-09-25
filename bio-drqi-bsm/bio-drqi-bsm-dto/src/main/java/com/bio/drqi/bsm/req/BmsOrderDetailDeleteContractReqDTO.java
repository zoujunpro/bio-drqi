package com.bio.drqi.bsm.req;

import lombok.Data;

import java.util.List;

@Data
public class BmsOrderDetailDeleteContractReqDTO {
    /**
     * 订单单号
     */
    private Integer id;

    /**
     * 合同地址
     */
    private String contractUrl;
}
