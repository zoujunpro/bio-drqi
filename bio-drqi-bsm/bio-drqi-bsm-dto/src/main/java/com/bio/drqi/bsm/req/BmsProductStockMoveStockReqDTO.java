package com.bio.drqi.bsm.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class BmsProductStockMoveStockReqDTO {

    @NotNull(message = "主键缺失")
    private Integer id;

    @NotBlank(message = "新库存必填")
    private String newStockCode;

    @NotBlank(message = "旧库存必填")
    private List<String> oldStockLocationList;

    @NotEmpty(message = "新库位必填")
    private List<String> newStockLocationList;

    @NotNull(message = "调拨数量必填")
    private Integer moveNumber;
}
