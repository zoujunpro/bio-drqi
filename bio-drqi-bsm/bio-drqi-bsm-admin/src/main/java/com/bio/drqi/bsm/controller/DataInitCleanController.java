package com.bio.drqi.bsm.controller;

import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.contents.BioBsmContents;
import com.bio.drqi.bsm.enums.CooperateFormEnum;
import com.bio.drqi.domain.BmsSupplierTb;
import com.bio.drqi.mapper.BmsSupplierTbMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 数据初始化清洗
 */
@RestController
@RequestMapping("/bmsDataClean")
@Slf4j
public class DataInitCleanController {

    @Resource
    private BmsSupplierTbMapper bmsSupplierTbMapper;


    /**
     * 供应商数据清洗
     *
     * @return
     */
    @GetMapping("/supplierDataClean")
    @WebLog(desc = "数据清洗")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> supplierDataClean() {

        List<SupplierCleanDataExcel> supplierCleanDataExcelList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\供应商数据清洗excel.xlsx", SupplierCleanDataExcel.class);
        for (SupplierCleanDataExcel supplierCleanDataExcel : supplierCleanDataExcelList) {
            log.info("清洗" + supplierCleanDataExcel.getSupplierCode());
            BmsSupplierTb bmsSupplierTb = bmsSupplierTbMapper.selectOneBySupplierName(supplierCleanDataExcel.supplierName);
            if (bmsSupplierTb != null) {
                log.info("供应商重复*****" + bmsSupplierTb.getSupplierName());
                continue;
            }
            bmsSupplierTb = new BmsSupplierTb();
            bmsSupplierTb.setSupplierCode(supplierCleanDataExcel.supplierCode);
            bmsSupplierTb.setSupplierName(supplierCleanDataExcel.supplierName);
            bmsSupplierTb.setOpeningBank(supplierCleanDataExcel.openingBank);
            bmsSupplierTb.setBankAccount(supplierCleanDataExcel.bankAccount);
            bmsSupplierTb.setTaxId(supplierCleanDataExcel.taxId);
            bmsSupplierTb.setQualificationLocation(null);
            bmsSupplierTb.setBusinessScope(supplierCleanDataExcel.business_scope);
            if ("合同".equals(supplierCleanDataExcel.cooperate_form)) {
                bmsSupplierTb.setCooperateForm(CooperateFormEnum.contract.name());
            } else if ("订单".equals(supplierCleanDataExcel.cooperate_form)) {
                bmsSupplierTb.setCooperateForm(CooperateFormEnum.order.name());
            } else if ("框架协议".equals(supplierCleanDataExcel.cooperate_form)) {
                bmsSupplierTb.setCooperateForm(CooperateFormEnum.protocol.name());
            } else {
                bmsSupplierTb.setCooperateForm(CooperateFormEnum.other.name());
            }

            bmsSupplierTb.setFrameworkAgreementNumber(supplierCleanDataExcel.framework_agreement_number);
            bmsSupplierTb.setFrameworkAgreementAnnex(supplierCleanDataExcel.framework_agreement_annex);
            bmsSupplierTb.setExpirationDate(supplierCleanDataExcel.expiration_date);
            bmsSupplierTb.setContactUserName(supplierCleanDataExcel.getContact_user_name());
            bmsSupplierTb.setContactUserTelephone(supplierCleanDataExcel.contact_user_name);
            bmsSupplierTb.setLeaderUserName(supplierCleanDataExcel.leaderUserName);
            bmsSupplierTb.setLeaderUserId(null);
            bmsSupplierTb.setRemark(supplierCleanDataExcel.remark);
            bmsSupplierTb.setCreateTime(new Date());
            bmsSupplierTb.setCreateUserName(null);
            bmsSupplierTb.setCreateUserId(null);
            bmsSupplierTb.setDeleteFlag(BioBsmContents.N);
            bmsSupplierTbMapper.insert(bmsSupplierTb);
        }

        return ResponseResult.getSuccess("OK");
    }


    @Data
    public static class SupplierCleanDataExcel {


        @ExcelProperty("供应商编号（合同编号）")
        private String supplierCode;

        @ExcelProperty("供应商名称")
        private String supplierName;

        @ExcelProperty("开户行")
        private String openingBank;

        @ExcelProperty("银行账号")
        private String bankAccount;

        @ExcelProperty("我方负责人")
        private String leaderUserName;

        @ExcelProperty("税号")
        private String taxId;

        @ExcelProperty("联系人")
        private String contact_user_name;

        @ExcelProperty("联系电话")
        private String contact_user_telephone;

        @ExcelProperty("经营范围")
        private String business_scope;

        @ExcelProperty("合作形式")
        private String cooperate_form;

        @ExcelProperty("框架协议编号")
        private String framework_agreement_number;

        @ExcelProperty("框架协议附件")
        private String framework_agreement_annex;

        @ExcelProperty("框架协议到期时间")
        private String expiration_date;
        @ExcelProperty("备注")
        private String remark;

    }

}
