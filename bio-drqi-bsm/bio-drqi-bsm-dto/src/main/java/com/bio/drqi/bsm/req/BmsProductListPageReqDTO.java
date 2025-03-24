package com.bio.drqi.bsm.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class BmsProductListPageReqDTO extends PageDTO {
    private String brandCode;

    private String productName;

    private String deleteFlag;
    /**
     * 供应商编号
     */
    private String supplierCode;
}
