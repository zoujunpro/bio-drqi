package com.bio.drqi.bsm.rsp;

import lombok.Data;

@Data
public class BmsSupplierListAllRspDTO {
    /**
     * 供应商编号
     */
    private String supplierCode;

    /**
     * 供应商名称
     */
    private String supplierName;
}
