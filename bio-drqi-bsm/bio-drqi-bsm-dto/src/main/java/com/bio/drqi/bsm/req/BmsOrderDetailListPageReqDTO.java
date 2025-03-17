package com.bio.drqi.bsm.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class BmsOrderDetailListPageReqDTO extends PageDTO {

    private String orderNum;

    /**
     * 项目编号
     */
    private String projectCode;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 供应商名称
     */
    private String supplierName;
    /**
     * 供应商编号
     */
    private String supplierCode;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 商品名称
     */
    private String productName;
}
