package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * 供应商信息表
 * @TableName bms_supplier_tb
 */
@TableName(value ="bms_supplier_tb")
public class BmsSupplierTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 供应商编号
     */
    private String supplierCode;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 开户行
     */
    private String openingBank;

    /**
     * 银行账户
     */
    private String bankAccount;

    /**
     * 税号
     */
    private String taxId;

    /**
     * 资质证明
     */
    private String qualificationLocation;

    /**
     * 经营范围
     */
    private String businessScope;

    /**
     * 合作形式
     */
    private String cooperateForm;

    /**
     * 框架协议编号
     */
    private Integer frameworkAgreementNumber;

    /**
     * 框架协议附件
     */
    private String frameworkAgreementAnnex;

    /**
     * 框架协议到期时间
     */
    private String expirationDate;

    /**
     * 供应商联系人名称
     */
    private String contactUserName;

    /**
     * 供应商联系人电话
     */
    private String contactUserTelephone;

    /**
     * 我方负责人名称
     */
    private String kahunaUserName;

    /**
     * 我方负责人ID
     */
    private Integer kahunaUserId;

    /**
     * 备注
     */
    private String remak;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 删除标识
     */
    private String deleteFlag;

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
     * 开户行
     */
    public String getOpeningBank() {
        return openingBank;
    }

    /**
     * 开户行
     */
    public void setOpeningBank(String openingBank) {
        this.openingBank = openingBank;
    }

    /**
     * 银行账户
     */
    public String getBankAccount() {
        return bankAccount;
    }

    /**
     * 银行账户
     */
    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    /**
     * 税号
     */
    public String getTaxId() {
        return taxId;
    }

    /**
     * 税号
     */
    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    /**
     * 资质证明
     */
    public String getQualificationLocation() {
        return qualificationLocation;
    }

    /**
     * 资质证明
     */
    public void setQualificationLocation(String qualificationLocation) {
        this.qualificationLocation = qualificationLocation;
    }

    /**
     * 经营范围
     */
    public String getBusinessScope() {
        return businessScope;
    }

    /**
     * 经营范围
     */
    public void setBusinessScope(String businessScope) {
        this.businessScope = businessScope;
    }

    /**
     * 合作形式
     */
    public String getCooperateForm() {
        return cooperateForm;
    }

    /**
     * 合作形式
     */
    public void setCooperateForm(String cooperateForm) {
        this.cooperateForm = cooperateForm;
    }

    /**
     * 框架协议编号
     */
    public Integer getFrameworkAgreementNumber() {
        return frameworkAgreementNumber;
    }

    /**
     * 框架协议编号
     */
    public void setFrameworkAgreementNumber(Integer frameworkAgreementNumber) {
        this.frameworkAgreementNumber = frameworkAgreementNumber;
    }

    /**
     * 框架协议附件
     */
    public String getFrameworkAgreementAnnex() {
        return frameworkAgreementAnnex;
    }

    /**
     * 框架协议附件
     */
    public void setFrameworkAgreementAnnex(String frameworkAgreementAnnex) {
        this.frameworkAgreementAnnex = frameworkAgreementAnnex;
    }

    /**
     * 框架协议到期时间
     */
    public String getExpirationDate() {
        return expirationDate;
    }

    /**
     * 框架协议到期时间
     */
    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
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
     * 我方负责人名称
     */
    public String getKahunaUserName() {
        return kahunaUserName;
    }

    /**
     * 我方负责人名称
     */
    public void setKahunaUserName(String kahunaUserName) {
        this.kahunaUserName = kahunaUserName;
    }

    /**
     * 我方负责人ID
     */
    public Integer getKahunaUserId() {
        return kahunaUserId;
    }

    /**
     * 我方负责人ID
     */
    public void setKahunaUserId(Integer kahunaUserId) {
        this.kahunaUserId = kahunaUserId;
    }

    /**
     * 备注
     */
    public String getRemak() {
        return remak;
    }

    /**
     * 备注
     */
    public void setRemak(String remak) {
        this.remak = remak;
    }

    /**
     * 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 创建人名称
     */
    public String getCreateUserName() {
        return createUserName;
    }

    /**
     * 创建人名称
     */
    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    /**
     * 创建人ID
     */
    public Integer getCreateUserId() {
        return createUserId;
    }

    /**
     * 创建人ID
     */
    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    /**
     * 删除标识
     */
    public String getDeleteFlag() {
        return deleteFlag;
    }

    /**
     * 删除标识
     */
    public void setDeleteFlag(String deleteFlag) {
        this.deleteFlag = deleteFlag;
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
        BmsSupplierTb other = (BmsSupplierTb) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getSupplierCode() == null ? other.getSupplierCode() == null : this.getSupplierCode().equals(other.getSupplierCode()))
            && (this.getSupplierName() == null ? other.getSupplierName() == null : this.getSupplierName().equals(other.getSupplierName()))
            && (this.getOpeningBank() == null ? other.getOpeningBank() == null : this.getOpeningBank().equals(other.getOpeningBank()))
            && (this.getBankAccount() == null ? other.getBankAccount() == null : this.getBankAccount().equals(other.getBankAccount()))
            && (this.getTaxId() == null ? other.getTaxId() == null : this.getTaxId().equals(other.getTaxId()))
            && (this.getQualificationLocation() == null ? other.getQualificationLocation() == null : this.getQualificationLocation().equals(other.getQualificationLocation()))
            && (this.getBusinessScope() == null ? other.getBusinessScope() == null : this.getBusinessScope().equals(other.getBusinessScope()))
            && (this.getCooperateForm() == null ? other.getCooperateForm() == null : this.getCooperateForm().equals(other.getCooperateForm()))
            && (this.getFrameworkAgreementNumber() == null ? other.getFrameworkAgreementNumber() == null : this.getFrameworkAgreementNumber().equals(other.getFrameworkAgreementNumber()))
            && (this.getFrameworkAgreementAnnex() == null ? other.getFrameworkAgreementAnnex() == null : this.getFrameworkAgreementAnnex().equals(other.getFrameworkAgreementAnnex()))
            && (this.getExpirationDate() == null ? other.getExpirationDate() == null : this.getExpirationDate().equals(other.getExpirationDate()))
            && (this.getContactUserName() == null ? other.getContactUserName() == null : this.getContactUserName().equals(other.getContactUserName()))
            && (this.getContactUserTelephone() == null ? other.getContactUserTelephone() == null : this.getContactUserTelephone().equals(other.getContactUserTelephone()))
            && (this.getKahunaUserName() == null ? other.getKahunaUserName() == null : this.getKahunaUserName().equals(other.getKahunaUserName()))
            && (this.getKahunaUserId() == null ? other.getKahunaUserId() == null : this.getKahunaUserId().equals(other.getKahunaUserId()))
            && (this.getRemak() == null ? other.getRemak() == null : this.getRemak().equals(other.getRemak()))
            && (this.getCreateTime() == null ? other.getCreateTime() == null : this.getCreateTime().equals(other.getCreateTime()))
            && (this.getCreateUserName() == null ? other.getCreateUserName() == null : this.getCreateUserName().equals(other.getCreateUserName()))
            && (this.getCreateUserId() == null ? other.getCreateUserId() == null : this.getCreateUserId().equals(other.getCreateUserId()))
            && (this.getDeleteFlag() == null ? other.getDeleteFlag() == null : this.getDeleteFlag().equals(other.getDeleteFlag()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getSupplierCode() == null) ? 0 : getSupplierCode().hashCode());
        result = prime * result + ((getSupplierName() == null) ? 0 : getSupplierName().hashCode());
        result = prime * result + ((getOpeningBank() == null) ? 0 : getOpeningBank().hashCode());
        result = prime * result + ((getBankAccount() == null) ? 0 : getBankAccount().hashCode());
        result = prime * result + ((getTaxId() == null) ? 0 : getTaxId().hashCode());
        result = prime * result + ((getQualificationLocation() == null) ? 0 : getQualificationLocation().hashCode());
        result = prime * result + ((getBusinessScope() == null) ? 0 : getBusinessScope().hashCode());
        result = prime * result + ((getCooperateForm() == null) ? 0 : getCooperateForm().hashCode());
        result = prime * result + ((getFrameworkAgreementNumber() == null) ? 0 : getFrameworkAgreementNumber().hashCode());
        result = prime * result + ((getFrameworkAgreementAnnex() == null) ? 0 : getFrameworkAgreementAnnex().hashCode());
        result = prime * result + ((getExpirationDate() == null) ? 0 : getExpirationDate().hashCode());
        result = prime * result + ((getContactUserName() == null) ? 0 : getContactUserName().hashCode());
        result = prime * result + ((getContactUserTelephone() == null) ? 0 : getContactUserTelephone().hashCode());
        result = prime * result + ((getKahunaUserName() == null) ? 0 : getKahunaUserName().hashCode());
        result = prime * result + ((getKahunaUserId() == null) ? 0 : getKahunaUserId().hashCode());
        result = prime * result + ((getRemak() == null) ? 0 : getRemak().hashCode());
        result = prime * result + ((getCreateTime() == null) ? 0 : getCreateTime().hashCode());
        result = prime * result + ((getCreateUserName() == null) ? 0 : getCreateUserName().hashCode());
        result = prime * result + ((getCreateUserId() == null) ? 0 : getCreateUserId().hashCode());
        result = prime * result + ((getDeleteFlag() == null) ? 0 : getDeleteFlag().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", supplierCode=").append(supplierCode);
        sb.append(", supplierName=").append(supplierName);
        sb.append(", openingBank=").append(openingBank);
        sb.append(", bankAccount=").append(bankAccount);
        sb.append(", taxId=").append(taxId);
        sb.append(", qualificationLocation=").append(qualificationLocation);
        sb.append(", businessScope=").append(businessScope);
        sb.append(", cooperateForm=").append(cooperateForm);
        sb.append(", frameworkAgreementNumber=").append(frameworkAgreementNumber);
        sb.append(", frameworkAgreementAnnex=").append(frameworkAgreementAnnex);
        sb.append(", expirationDate=").append(expirationDate);
        sb.append(", contactUserName=").append(contactUserName);
        sb.append(", contactUserTelephone=").append(contactUserTelephone);
        sb.append(", kahunaUserName=").append(kahunaUserName);
        sb.append(", kahunaUserId=").append(kahunaUserId);
        sb.append(", remak=").append(remak);
        sb.append(", createTime=").append(createTime);
        sb.append(", createUserName=").append(createUserName);
        sb.append(", createUserId=").append(createUserId);
        sb.append(", deleteFlag=").append(deleteFlag);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}