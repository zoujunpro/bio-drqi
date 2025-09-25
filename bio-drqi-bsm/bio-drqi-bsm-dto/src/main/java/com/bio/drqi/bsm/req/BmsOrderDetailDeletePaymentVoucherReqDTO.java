package com.bio.drqi.bsm.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class BmsOrderDetailDeletePaymentVoucherReqDTO {
    /**
     * 订单编号
     */
    @NotNull(message = "参数缺失，订单编号")
    private Integer id;

    @NotBlank(message = "入参付款凭证缺失")
    private String paymentVoucherUrl;
}
