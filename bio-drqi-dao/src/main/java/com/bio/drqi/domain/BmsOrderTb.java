package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * 订单信息表
 * @TableName bms_order_tb
 */
@TableName(value ="bms_order_tb")
public class BmsOrderTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId
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
     * 账期类型
     */
    private String accountPeriodType;

    /**
     * 账期描述
     */
    private String accountPeriodName;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 主键ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 订单编号
     */
    public String getOrderNum() {
        return orderNum;
    }

    /**
     * 订单编号
     */
    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    /**
     * 供应商编号
     */
    public String getSupplierCode() {
        return supplierCode;
    }

    /**
     * 供应商编号
     */
    public void setSupplierCode(String supplierCode) {
        this.supplierCode = supplierCode;
    }

    /**
     * 供应商名称
     */
    public String getSupplierName() {
        return supplierName;
    }

    /**
     * 供应商名称
     */
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    /**
     * 供应商联系人名称
     */
    public String getContactUserName() {
        return contactUserName;
    }

    /**
     * 供应商联系人名称
     */
    public void setContactUserName(String contactUserName) {
        this.contactUserName = contactUserName;
    }

    /**
     * 供应商联系人电话
     */
    public String getContactUserTelephone() {
        return contactUserTelephone;
    }

    /**
     * 供应商联系人电话
     */
    public void setContactUserTelephone(String contactUserTelephone) {
        this.contactUserTelephone = contactUserTelephone;
    }

    /**
     * 项目编号
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * 项目编号
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * 采购申请人
     */
    public Integer getApplyUserId() {
        return applyUserId;
    }

    /**
     * 采购申请人
     */
    public void setApplyUserId(Integer applyUserId) {
        this.applyUserId = applyUserId;
    }

    /**
     * 采购负责人
     */
    public String getApplyUserName() {
        return applyUserName;
    }

    /**
     * 采购负责人
     */
    public void setApplyUserName(String applyUserName) {
        this.applyUserName = applyUserName;
    }

    /**
     * 申请人部门
     */
    public String getApplyUserDepartment() {
        return applyUserDepartment;
    }

    /**
     * 申请人部门
     */
    public void setApplyUserDepartment(String applyUserDepartment) {
        this.applyUserDepartment = applyUserDepartment;
    }

    /**
     * 申请单位编号
     */
    public String getApplyUnitCode() {
        return applyUnitCode;
    }

    /**
     * 申请单位编号
     */
    public void setApplyUnitCode(String applyUnitCode) {
        this.applyUnitCode = applyUnitCode;
    }

    /**
     * 申请单位名称
     */
    public String getApplyUnitName() {
        return applyUnitName;
    }

    /**
     * 申请单位名称
     */
    public void setApplyUnitName(String applyUnitName) {
        this.applyUnitName = applyUnitName;
    }

    /**
     * 合同地址
     */
    public String getContractUrls() {
        return contractUrls;
    }

    /**
     * 合同地址
     */
    public void setContractUrls(String contractUrls) {
        this.contractUrls = contractUrls;
    }

    /**
     * 采购日期
     */
    public String getPurchaseDate() {
        return purchaseDate;
    }

    /**
     * 采购日期
     */
    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    /**
     * 采购类别编号
     */
    public String getPurchaseTypeCode() {
        return purchaseTypeCode;
    }

    /**
     * 采购类别编号
     */
    public void setPurchaseTypeCode(String purchaseTypeCode) {
        this.purchaseTypeCode = purchaseTypeCode;
    }

    /**
     * 采购类别名称
     */
    public String getPurchaseTypeName() {
        return purchaseTypeName;
    }

    /**
     * 采购类别名称
     */
    public void setPurchaseTypeName(String purchaseTypeName) {
        this.purchaseTypeName = purchaseTypeName;
    }

    /**
     * 申购原因
     */
    public String getApplyReason() {
        return applyReason;
    }

    /**
     * 申购原因
     */
    public void setApplyReason(String applyReason) {
        this.applyReason = applyReason;
    }

    /**
     * 需求提出时间
     */
    public String getRequireTime() {
        return requireTime;
    }

    /**
     * 需求提出时间
     */
    public void setRequireTime(String requireTime) {
        this.requireTime = requireTime;
    }

    /**
     * 需求使用时间
     */
    public String getUsageTime() {
        return usageTime;
    }

    /**
     * 需求使用时间
     */
    public void setUsageTime(String usageTime) {
        this.usageTime = usageTime;
    }

    /**
     * 账期类型
     */
    public String getAccountPeriodType() {
        return accountPeriodType;
    }

    /**
     * 账期类型
     */
    public void setAccountPeriodType(String accountPeriodType) {
        this.accountPeriodType = accountPeriodType;
    }

    /**
     * 账期描述
     */
    public String getAccountPeriodName() {
        return accountPeriodName;
    }

    /**
     * 账期描述
     */
    public void setAccountPeriodName(String accountPeriodName) {
        this.accountPeriodName = accountPeriodName;
    }

    /**
     * 发票信息
     */
    public String getInvoiceUrls() {
        return invoiceUrls;
    }

    /**
     * 发票信息
     */
    public void setInvoiceUrls(String invoiceUrls) {
        this.invoiceUrls = invoiceUrls;
    }

    /**
     * 汇款回执单
     */
    public String getRemittanceUrls() {
        return remittanceUrls;
    }

    /**
     * 汇款回执单
     */
    public void setRemittanceUrls(String remittanceUrls) {
        this.remittanceUrls = remittanceUrls;
    }

    /**
     * 创建日期
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建日期
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 任务编号
     */
    public String getTaskNum() {
        return taskNum;
    }

    /**
     * 任务编号
     */
    public void setTaskNum(String taskNum) {
        this.taskNum = taskNum;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        BmsOrderTb other = (BmsOrderTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOrderNum() == null ? other.getOrderNum() == null : this.getOrderNum().equals(other.getOrderNum()))
            && (this.getSupplierCode() == null ? other.getSupplierCode() == null : this.getSupplierCode().equals(other.getSupplierCode()))
            && (this.getSupplierName() == null ? other.getSupplierName() == null : this.getSupplierName().equals(other.getSupplierName()))
            && (this.getContactUserName() == null ? other.getContactUserName() == null : this.getContactUserName().equals(other.getContactUserName()))
            && (this.getContactUserTelephone() == null ? other.getContactUserTelephone() == null : this.getContactUserTelephone().equals(other.getContactUserTelephone()))
            && (this.getProjectCode() == null ? other.getProjectCode() == null : this.getProjectCode().equals(other.getProjectCode()))
            && (this.getApplyUserId() == null ? other.getApplyUserId() == null : this.getApplyUserId().equals(other.getApplyUserId()))
            && (this.getApplyUserName() == null ? other.getApplyUserName() == null : this.getApplyUserName().equals(other.getApplyUserName()))
            && (this.getApplyUserDepartment() == null ? other.getApplyUserDepartment() == null : this.getApplyUserDepartment().equals(other.getApplyUserDepartment()))
            && (this.getApplyUnitCode() == null ? other.getApplyUnitCode() == null : this.getApplyUnitCode().equals(other.getApplyUnitCode()))
            && (this.getApplyUnitName() == null ? other.getApplyUnitName() == null : this.getApplyUnitName().equals(other.getApplyUnitName()))
            && (this.getContractUrls() == null ? other.getContractUrls() == null : this.getContractUrls().equals(other.getContractUrls()))
            && (this.getPurchaseDate() == null ? other.getPurchaseDate() == null : this.getPurchaseDate().equals(other.getPurchaseDate()))
            && (this.getPurchaseTypeCode() == null ? other.getPurchaseTypeCode() == null : this.getPurchaseTypeCode().equals(other.getPurchaseTypeCode()))
            && (this.getPurchaseTypeName() == null ? other.getPurchaseTypeName() == null : this.getPurchaseTypeName().equals(other.getPurchaseTypeName()))
            && (this.getApplyReason() == null ? other.getApplyReason() == null : this.getApplyReason().equals(other.getApplyReason()))
            && (this.getRequireTime() == null ? other.getRequireTime() == null : this.getRequireTime().equals(other.getRequireTime()))
            && (this.getUsageTime() == null ? other.getUsageTime() == null : this.getUsageTime().equals(other.getUsageTime()))
            && (this.getAccountPeriodType() == null ? other.getAccountPeriodType() == null : this.getAccountPeriodType().equals(other.getAccountPeriodType()))
            && (this.getAccountPeriodName() == null ? other.getAccountPeriodName() == null : this.getAccountPeriodName().equals(other.getAccountPeriodName()))
            && (this.getInvoiceUrls() == null ? other.getInvoiceUrls() == null : this.getInvoiceUrls().equals(other.getInvoiceUrls()))
            && (this.getRemittanceUrls() == null ? other.getRemittanceUrls() == null : this.getRemittanceUrls().equals(other.getRemittanceUrls()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getTaskNum() == null ? other.getTaskNum() == null : this.getTaskNum().equals(other.getTaskNum()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrderNum() == null) ? 0 : getOrderNum().hashCode());
        result = prime * result + ((getSupplierCode() == null) ? 0 : getSupplierCode().hashCode());
        result = prime * result + ((getSupplierName() == null) ? 0 : getSupplierName().hashCode());
        result = prime * result + ((getContactUserName() == null) ? 0 : getContactUserName().hashCode());
        result = prime * result + ((getContactUserTelephone() == null) ? 0 : getContactUserTelephone().hashCode());
        result = prime * result + ((getProjectCode() == null) ? 0 : getProjectCode().hashCode());
        result = prime * result + ((getApplyUserId() == null) ? 0 : getApplyUserId().hashCode());
        result = prime * result + ((getApplyUserName() == null) ? 0 : getApplyUserName().hashCode());
        result = prime * result + ((getApplyUserDepartment() == null) ? 0 : getApplyUserDepartment().hashCode());
        result = prime * result + ((getApplyUnitCode() == null) ? 0 : getApplyUnitCode().hashCode());
        result = prime * result + ((getApplyUnitName() == null) ? 0 : getApplyUnitName().hashCode());
        result = prime * result + ((getContractUrls() == null) ? 0 : getContractUrls().hashCode());
        result = prime * result + ((getPurchaseDate() == null) ? 0 : getPurchaseDate().hashCode());
        result = prime * result + ((getPurchaseTypeCode() == null) ? 0 : getPurchaseTypeCode().hashCode());
        result = prime * result + ((getPurchaseTypeName() == null) ? 0 : getPurchaseTypeName().hashCode());
        result = prime * result + ((getApplyReason() == null) ? 0 : getApplyReason().hashCode());
        result = prime * result + ((getRequireTime() == null) ? 0 : getRequireTime().hashCode());
        result = prime * result + ((getUsageTime() == null) ? 0 : getUsageTime().hashCode());
        result = prime * result + ((getAccountPeriodType() == null) ? 0 : getAccountPeriodType().hashCode());
        result = prime * result + ((getAccountPeriodName() == null) ? 0 : getAccountPeriodName().hashCode());
        result = prime * result + ((getInvoiceUrls() == null) ? 0 : getInvoiceUrls().hashCode());
        result = prime * result + ((getRemittanceUrls() == null) ? 0 : getRemittanceUrls().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getTaskNum() == null) ? 0 : getTaskNum().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", orderNum=").append(orderNum);
        sb.append(", supplierCode=").append(supplierCode);
        sb.append(", supplierName=").append(supplierName);
        sb.append(", contactUserName=").append(contactUserName);
        sb.append(", contactUserTelephone=").append(contactUserTelephone);
        sb.append(", projectCode=").append(projectCode);
        sb.append(", applyUserId=").append(applyUserId);
        sb.append(", applyUserName=").append(applyUserName);
        sb.append(", applyUserDepartment=").append(applyUserDepartment);
        sb.append(", applyUnitCode=").append(applyUnitCode);
        sb.append(", applyUnitName=").append(applyUnitName);
        sb.append(", contractUrls=").append(contractUrls);
        sb.append(", purchaseDate=").append(purchaseDate);
        sb.append(", purchaseTypeCode=").append(purchaseTypeCode);
        sb.append(", purchaseTypeName=").append(purchaseTypeName);
        sb.append(", applyReason=").append(applyReason);
        sb.append(", requireTime=").append(requireTime);
        sb.append(", usageTime=").append(usageTime);
        sb.append(", accountPeriodType=").append(accountPeriodType);
        sb.append(", accountPeriodName=").append(accountPeriodName);
        sb.append(", invoiceUrls=").append(invoiceUrls);
        sb.append(", remittanceUrls=").append(remittanceUrls);
        sb.append(", createTime=").append(createTime);
        sb.append(", taskNum=").append(taskNum);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}