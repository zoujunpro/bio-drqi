package com.bio.drqi.bsm.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class BmsBrandListPageReqDTO extends PageDTO {

    private String brandName;


    /**
     * Y已删除，回收站   , N正常
     */
    private String deleteFlag;
}
