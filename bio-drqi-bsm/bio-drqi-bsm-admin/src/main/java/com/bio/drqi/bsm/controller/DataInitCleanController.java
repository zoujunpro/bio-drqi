package com.bio.drqi.bsm.controller;

import cn.hutool.json.JSONUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.uuid.IdUtils;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.contents.BioBsmContents;
import com.bio.drqi.bsm.enums.CooperateFormEnum;
import com.bio.drqi.bsm.req.BmsProductAddReqDTO;
import com.bio.drqi.bsm.service.BmsProductService;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据初始化清洗
 */
@RestController
@RequestMapping("/bmsDataClean")
@Slf4j
public class DataInitCleanController {

    @Resource
    private BmsSupplierTbMapper bmsSupplierTbMapper;

    @Resource
    private SystemUserTbMapper systemUserTbMapper;

    @Resource
    private BmsProductStockTbMapper bmsProductStockTbMapper;

    @Resource
    private BmsBrandTbMapper bmsBrandTbMapper;

    @Resource
    private BmsProductService bmsProductService;

    @Resource
    private BmsProductCategoryTbMapper bmsProductCategoryTbMapper;

    @Resource
    private BmsProjectDictMapper bmsProjectDictMapper;


    @Resource
    private BmsProductTbMapper bmsProductTbMapper;


    /**
     * 供应商数据清洗
     *
     * @return
     */
    @GetMapping("/supplierDataClean")
    @WebLog(desc = "数据清洗")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> supplierDataClean() {

        List<SystemUserTb> systemUserTbList = systemUserTbMapper.selectList(null);
        Map<String, Integer> userMap = systemUserTbList.stream().collect(Collectors.toMap(SystemUserTb::getNickname, SystemUserTb::getId));
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
            bmsSupplierTb.setFrameworkAgreementAnnex(null);
            bmsSupplierTb.setExpirationDate(supplierCleanDataExcel.expiration_date);
            bmsSupplierTb.setContactUserName(supplierCleanDataExcel.contact_user_name);
            bmsSupplierTb.setContactUserTelephone(supplierCleanDataExcel.contact_user_telephone);
            bmsSupplierTb.setLeaderUserName(supplierCleanDataExcel.leaderUserName);
            bmsSupplierTb.setLeaderUserId(userMap.get(supplierCleanDataExcel.leaderUserName));
            bmsSupplierTb.setRemark(supplierCleanDataExcel.remark);
            bmsSupplierTb.setCreateTime(new Date());
            bmsSupplierTb.setCreateUserName(null);
            bmsSupplierTb.setCreateUserId(null);
            bmsSupplierTb.setDeleteFlag(BioBsmContents.N);
            bmsSupplierTbMapper.insert(bmsSupplierTb);


        }

        return ResponseResult.getSuccess("OK");
    }


    @Transactional(rollbackFor = Exception.class)
    @GetMapping("/cleanProductDataExcel")
    public ResponseResult<String> cleanProductDataExcel() {
        List<ProductCleanDataExcel> productCleanDataExcelList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\商品.xlsx", ProductCleanDataExcel.class);
        for (ProductCleanDataExcel productCleanDataExcel : productCleanDataExcelList) {
            log.info("清洗数据={}", productCleanDataExcel);
            String brandCode = null;
            BmsBrandTb bmsBrandTb = null;
            if (StringUtils.isNotEmpty(productCleanDataExcel.brandName)) {
                bmsBrandTb = bmsBrandTbMapper.selectOneByBrandName(productCleanDataExcel.brandName);
                if (bmsBrandTb == null) {
                    bmsBrandTb = new BmsBrandTb();
                    bmsBrandTb.setBrandName(productCleanDataExcel.brandName);
                    bmsBrandTb.setBrandCode(IdUtils.simpleUUID());
                    bmsBrandTb.setDeleteFlag(BioBsmContents.N);
                    bmsBrandTbMapper.insert(bmsBrandTb);
                }
                brandCode = bmsBrandTb.getBrandCode();
            }
            BmsSupplierTb bmsSupplierTb = bmsSupplierTbMapper.selectOneBySupplierCode(productCleanDataExcel.supplierCode);
            if (bmsSupplierTb == null) {
                throw new BusinessException("供应商不存在" + productCleanDataExcel.supplierCode);
            }

            BmsProductCategoryTb bmsProductCategoryTb = bmsProductCategoryTbMapper.selectOneByProductCategoryName(productCleanDataExcel.productCategory);
            if (bmsProductCategoryTb == null) {
                throw new BusinessException("类别找不到" + productCleanDataExcel.productCategory);
            }

            BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductNameAndBrandCodeAndProductSpecs(productCleanDataExcel.productName, bmsBrandTb != null ? bmsBrandTb.getBrandCode() : null, productCleanDataExcel.product_specs);
            if(bmsProductTb==null){
                BmsProductAddReqDTO bmsProductAddReqDTO = new BmsProductAddReqDTO();
                bmsProductAddReqDTO.setProductName(productCleanDataExcel.productName);
                bmsProductAddReqDTO.setProductOutCode(productCleanDataExcel.productCode);
                bmsProductAddReqDTO.setProductCategoryCode(bmsProductCategoryTb.getProductCategoryCode());
                bmsProductAddReqDTO.setProductTypeCode(null);
                bmsProductAddReqDTO.setBrandCode(brandCode);
                bmsProductAddReqDTO.setProductSpecs(productCleanDataExcel.product_specs);
                bmsProductService.add(bmsProductAddReqDTO);
            }else {
                log.info("商品重复添加："+JSONUtil.toJsonStr(productCleanDataExcel));
            }

        }


        return ResponseResult.getSuccess("OK");
    }


    @Data
    public static class ProductCleanDataExcel {
        @ExcelProperty("供应商编号")
        private String supplierCode;

        @ExcelProperty("品牌")
        private String brandName;

        @ExcelProperty("商品名称")
        private String productName;

        @ExcelProperty("商品编码")
        private String productCode;

        @ExcelProperty("商品分类")
        private String productCategory;

        @ExcelProperty("规格")
        private String product_specs;

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
