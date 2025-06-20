package com.bio.drqi.bsm.req;

import lombok.Data;

@Data
public class BmsOrderDetailReportAccountReqDTO {
    /**
     * 订单编号
     */
    private Integer id;

    /**
     * 报账结算日期 yyyy-mm-dd
     */
    private String accountTime;
}
