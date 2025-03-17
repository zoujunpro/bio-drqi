package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class BmsOrderListAllRspDTO {

    private Integer id;

    /**
     * 订单编号
     */
    private String orderNum;

    /**
     * 采购申请人
     */
    private Integer applyUserId;

    /**
     * 采购负责人
     */
    private String applyUserName;

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
     * 申请单位名称
     */
    private String applyUnitName;



}
