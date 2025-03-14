package com.bio.drqi.bsm.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class BmsOrderListPageReqDTO extends PageDTO {

    /**
     * 订单编号
     */
    private String orderNum;

    /**
     * 采购申请人
     */
    private Integer applyUserId;


    /**
     * 申请时间
     */
    private String applyTime;

    /**
     * 采购部门
     */
    private String purchaseDepartment;

    /**
     * 申请单位编号
     */
    private String applyUnitCode;


    /**
     * 采购日期
     */
    private String purchaseDate;


    /**
     * 报账日期
     */
    private String reportAccountTime;


}
