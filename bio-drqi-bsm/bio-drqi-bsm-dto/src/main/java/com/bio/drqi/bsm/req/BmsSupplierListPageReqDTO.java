package com.bio.drqi.bsm.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class BmsSupplierListPageReqDTO extends PageDTO {
    /**
     * 供应商编号
     */
    private String supplierCode;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * Y代表已经删除，在回收站
     */
    private String deleteFlag;
}
