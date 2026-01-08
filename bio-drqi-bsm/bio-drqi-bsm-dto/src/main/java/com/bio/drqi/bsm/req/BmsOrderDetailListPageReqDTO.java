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
     * 供应商编号
     */
    private String supplierCode;
    /**
     * 品牌
     */
    private String brandCode;
    /**
     * 材料名称
     */
    private String productName;

    /**
     * 材料名称
     */
    private String productinnerCode;
    /**
     * 申请单位
     */
    private String applyUnitCode;

    /**
     * 采购部门
     */
    private String purchaseDepartment;


    /**
     * 采购申请人
     */
    private Integer applyUserId;

    private String filterZeroFlag;

    /**
     * 报账日期是否为空
     */
    private String reportAccountTimeNullFlag;

    /**
     * 品证非空
     */
    private String paymentVoucherUrlsNullFlag;

    /**
     * 报账日期
     */
    private String reportAccountTime;


}
