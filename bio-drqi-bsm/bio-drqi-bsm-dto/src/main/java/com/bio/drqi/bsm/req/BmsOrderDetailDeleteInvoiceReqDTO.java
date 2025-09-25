package com.bio.drqi.bsm.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class BmsOrderDetailDeleteInvoiceReqDTO {

    /**
     * 订单编号
     */
    @NotNull(message = "参数缺失：ID")
    private Integer id;

    /**
     * 发票地址（单个）
     */
    @NotBlank(message = "发票地址缺失")
    private String invoiceUrl;
}
