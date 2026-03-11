package com.bio.drqi.bsm.req;

import com.bio.drqi.common.validator.EnumValue;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BmsProductQueryListReqDTO {


    private String brandCode;


    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品外部编号
     */
    private String productOutCode;

    /**
     * 商品内部编号
     */
    private String productInnerCode;


    private String purchaseTypeCode;

}
