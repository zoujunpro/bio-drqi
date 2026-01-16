package com.bio.drqi.bsm.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

import java.util.Date;

@Data
public class BmsProductStockOutLogListPageReqDTO  extends PageDTO {
    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品外部编号
     */
    private String productOutCode;

    /**
     * 商品内部编号
     */
    private String productInnerCode;



    /**
     * 品牌编号
     */
    private String brandCode;

    /**
     * 商品批次
     */
    private String batchNo;

    /**
     * 申请人ID
     */
    private Integer applyUserId;




    /**
     * 出库类型 1正常出库 2退货出库
     */
    private String outType;

    /**
     * 单位编号
     */
    private String unitCode;

    /**
     * 供应商编号
     */
    private String supplierCode;

    private String dateTime;


}
