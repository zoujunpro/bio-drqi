package com.bio.drqi.bsm.req;

import lombok.Data;

@Data
public class BmsOrderDetailUploadInvoiceReqDTO {

    /**
     * 订单编号
     */
    private Integer id;

    /**
     * 发票地址（支持多个）
     */
    private String invoiceUrls;
}
