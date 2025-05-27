package com.bio.drqi.manage.seed;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SeedStockRemarkReqDTO {

    @NotBlank(message = "参数缺失：备注")
    private String remarks;

    @NotNull(message = "参数缺失：id")
    private Integer id;
}
