package com.bio.drqi.bsm.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class BmsProductOutDTO {


    @NotBlank(message = "入库参数缺少：订单明细")
    private String orderDetailNum;


}
