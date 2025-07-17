package com.bio.drqi.bsm.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BmsStockLocationAddReqDTO {

    /**
     * 库存名称
     */
    @NotBlank(message = "参数缺失：库房名称")
    private String stockCode;

    /**
     * 库位号
     */
    @NotBlank(message = "参数缺失：库位号")
    private String locationNumber;
}
