package com.bio.drqi.bsm.req;

import lombok.Data;

@Data
public class BmsOrderDetailUploadPaymentVoucherReqDTO {
    /**
     * 订单编号
     */
    private Integer id;

    private String paymentVoucherUrls;
}
