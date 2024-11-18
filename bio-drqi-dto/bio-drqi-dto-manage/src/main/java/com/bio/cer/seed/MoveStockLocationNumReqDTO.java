package com.bio.cer.seed;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MoveStockLocationNumReqDTO {

    @NotNull(message = "参数缺失：id")
    private Integer id;
    @NotBlank(message = "参数缺失：stockLocationNum")
    private String stockLocationNum;
}
