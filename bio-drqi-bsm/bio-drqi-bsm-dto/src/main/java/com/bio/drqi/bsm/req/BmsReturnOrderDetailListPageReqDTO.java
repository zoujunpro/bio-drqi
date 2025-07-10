package com.bio.drqi.bsm.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BmsReturnOrderDetailListPageReqDTO extends PageDTO {


    /**
     * 商品名称
     */
    private String productName;


    /**
     * 订单编号
     */
    private String orderNum;

    /**
     * 品牌编号
     */
    private String brandCode;

    /**
     * 单位
     */
    private String unitCode;

    /**
     * 供应商编号
     */
    private String supplierCode;

    /**
     * 商品内部编号
     */
    private String productInnerCode;


}
