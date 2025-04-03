package com.bio.drqi.bsm.req;

import lombok.Data;

@Data
public class BmsOrderReportAccountReqDTO {
    /**
     * 订单编号
     */
    private String orderNum;

    /**
     * 报账结算日期 yyyy-mm-dd
     */
    private String accountTime;
}
