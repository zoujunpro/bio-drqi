package com.bio.drqi.bsm.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class BmsSupplierUnsyncedExcelDTO {

    @ExcelProperty("供应商编号")
    private String supplierCode;

    @ExcelProperty("供应商名称")
    private String supplierName;

    @ExcelProperty("开户行")
    private String openingBank;

    @ExcelProperty("银行账户")
    private String bankAccount;

    @ExcelProperty("税号")
    private String taxId;

    @ExcelProperty("供应商联系人")
    private String contactUserName;

    @ExcelProperty("供应商联系电话")
    private String contactUserTelephone;

    @ExcelProperty("我方负责人")
    private String leaderUserName;

    @ExcelProperty("备注")
    private String remark;

    @ExcelProperty("创建时间")
    private Date createTime;

    @ExcelProperty("创建人")
    private String createUserName;

    @ExcelProperty("状态")
    private String supplierStatus;

    @ExcelProperty("金蝶同步编号")
    private String kdNumber;
}
