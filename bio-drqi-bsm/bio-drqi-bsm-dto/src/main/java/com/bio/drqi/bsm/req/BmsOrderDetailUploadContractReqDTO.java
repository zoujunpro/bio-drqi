package com.bio.drqi.bsm.req;

import lombok.Data;

@Data
public class BmsOrderDetailUploadContractReqDTO {
    /**
     * 订单单号
     */
    private Integer id;

    /**
     * 合同地址
     */
    private String contractUrls;

    /**
     * 合同编号
     */
    private String contractNumber;
}
