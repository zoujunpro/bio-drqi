package com.bio.drqi.bsm.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class BmsProductTyListPageReqDTO extends PageDTO {

    /**
     * 商品类型名称
     */
    private String productTypeName;
}
