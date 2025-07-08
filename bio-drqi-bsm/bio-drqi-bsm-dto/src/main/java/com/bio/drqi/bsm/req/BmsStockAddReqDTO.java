package com.bio.drqi.bsm.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BmsStockAddReqDTO {

    @NotBlank(message = "库房名字必填")
    private String stockName;

    @NotBlank(message = "单位必填")
    private String unitCode;
}
