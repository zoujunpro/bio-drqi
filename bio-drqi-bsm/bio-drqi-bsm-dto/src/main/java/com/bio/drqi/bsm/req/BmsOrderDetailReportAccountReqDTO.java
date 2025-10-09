package com.bio.drqi.bsm.req;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class BmsOrderDetailReportAccountReqDTO {
    /**
     * 订单编号
     */
    @NotEmpty(message = "参数缺失，订单编号")
    private List<Integer> idList;

    /**
     * 报账结算日期 yyyy-mm-dd
     */
    private String accountTime;
}
