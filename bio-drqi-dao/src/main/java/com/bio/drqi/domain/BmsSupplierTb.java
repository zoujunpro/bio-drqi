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
 * 供应商信息表
 * @TableName bms_supplier_tb
 */
@TableName(value ="bms_supplier_tb")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
    private String frameworkAgreementNumber;

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
    private String leaderUserName;

    /**
     * 我方负责人ID
     */
    private Integer leaderUserId;

    /**
     * 备注
     */
    private String remark;

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

    private String kdNumber;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}