package com.bio.drqi.bsm.req;

import lombok.Data;

@Data
public class BmsProductCategoryEditReqDTO {
    private Integer id;

    private String productCategoryName;


    private String kdCategoryCode;


    private String kdParentId;
}
