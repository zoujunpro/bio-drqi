package com.bio.drqi.bsm.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BmsProductQueryListReqDTO {

    @NotBlank(message = "参数缺失：brandCode")
    private String brandCode;

}
