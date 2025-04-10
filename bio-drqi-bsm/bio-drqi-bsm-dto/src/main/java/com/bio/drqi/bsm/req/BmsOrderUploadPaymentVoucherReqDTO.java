package com.bio.drqi.bsm.req;

import lombok.Data;

@Data
public class BmsOrderUploadPaymentVoucherReqDTO {
    /**
     * 订单编号
     */
    private String orderNum;

    private String paymentVoucherUrls;
}
