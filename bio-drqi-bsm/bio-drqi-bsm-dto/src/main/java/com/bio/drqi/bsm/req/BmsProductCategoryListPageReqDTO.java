package com.bio.drqi.bsm.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class BmsProductCategoryListPageReqDTO extends PageDTO {

    private String productCategoryName;

    private String kdParentId;

    private String kdCategoryCode;
}
