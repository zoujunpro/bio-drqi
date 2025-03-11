package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单信息表
 * @TableName bms_order_tb
 */
@TableName(value ="bms_order_tb")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BmsOrderTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 订单编号
     */
    private String orderNum;

    /**
     * 供应商编号
     */
    private String supplierCode;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 供应商联系人名称
     */
    private String contactUserName;

    /**
     * 供应商联系人电话
     */
    private String contactUserTelephone;

    /**
     * 项目编号
     */
    private String projectCode;

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
     * 申购原因
     */
    private String applyReason;

    /**
     * 需求提出时间
     */
    private String requireTime;

    /**
     * 需求使用时间
     */
    private String usageTime;

    /**
     * 发票信息
     */
    private String invoiceUrls;

    /**
     * 汇款回执单
     */
    private String remittanceUrls;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 报账日期
     */
    private String reportAccountTime;

    /**
     * 是否结算标识 N Y
     */
    private String closeAccountFlag;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}