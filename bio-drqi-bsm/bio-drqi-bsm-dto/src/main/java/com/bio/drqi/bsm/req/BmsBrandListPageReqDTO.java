package com.bio.drqi.bsm.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class BmsBrandListPageReqDTO extends PageDTO {

    private String brandName;


    /**
     * Y启用 N禁用
     */
    private String brandStatus;
}
