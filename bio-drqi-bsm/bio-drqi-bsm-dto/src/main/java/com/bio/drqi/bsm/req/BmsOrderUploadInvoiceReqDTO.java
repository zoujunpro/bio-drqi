package com.bio.drqi.bsm.req;

import lombok.Data;

@Data
public class BmsOrderUploadInvoiceReqDTO {

    /**
     * 订单编号
     */
    private String orderNum;

    /**
     * 发票地址（支持多个）
     */
    private String invoiceUrls;
}
