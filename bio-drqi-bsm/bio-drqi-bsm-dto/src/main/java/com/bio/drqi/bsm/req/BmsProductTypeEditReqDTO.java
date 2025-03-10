package com.bio.drqi.bsm.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BmsProductTypeEditReqDTO {

    private String  productTypeName;
}
