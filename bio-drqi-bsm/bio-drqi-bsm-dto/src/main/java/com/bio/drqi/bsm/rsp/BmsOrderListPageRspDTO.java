package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class BmsOrderListPageRspDTO {
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
     * 申请人部门
     */
    private String applyUserDepartment;

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

    /**
     * 合同地址
     */
    private String contractUrls;

    /**
     * 采购日期
     */
    private String purchaseDate;

    /**
     * 采购类别编号
     */
    private String purchaseTypeCode;

    /**
     * 采购类别名称
     */
    private String purchaseTypeName;

    /**
     * 申购事由描述
     */
    private String purchaseReasonRemark;

    /**
     * 需求提出时间
     */
    private String demandRequireTime;

    /**
     * 需求使用时间
     */
    private String demandUsageTime;

    /**
     * 附件地址
     */
    private String attachmentUrls;
    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 任务编号
     */
    private String taskNum;

    private String overFlag;


}
