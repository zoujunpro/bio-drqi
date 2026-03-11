package com.bio.drqi.bsm.req;


import com.bio.drqi.common.validator.EnumValue;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BmsProductModifyPurchaseTypeCodeReqDTO {

    @NotNull(message = "缺失主键")
    private Integer id;

    @EnumValue(message = "物料常规采购状态入参错误",strValues = {"Y","N"})
    private String  purchaseTypeCode;
}
