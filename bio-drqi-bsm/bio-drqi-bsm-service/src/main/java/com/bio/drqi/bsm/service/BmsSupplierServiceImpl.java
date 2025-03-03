package com.bio.drqi.bsm.service;

import cn.hutool.core.bean.BeanUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.bsm.req.BmsSupplierAddReqDTO;
import com.bio.drqi.bsm.req.BmsSupplierExportExcelReqDTO;
import com.bio.drqi.bsm.req.BmsSupplierListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsSupplierListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsSupplierListPageRspDTO;
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.domain.BmsSupplierTb;
import com.bio.drqi.mapper.BmsSupplierTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class BmsSupplierServiceImpl implements BmsSupplierService {

    @Resource
    private BmsSupplierTbMapper bmsSupplierTbMapper;

    @Override
    public PageInfo<BmsSupplierListPageRspDTO> listPage(BmsSupplierListPageReqDTO bmsSupplierListPageReqDTO) {
        PageHelper.startPage(bmsSupplierListPageReqDTO.getPageNum(), bmsSupplierListPageReqDTO.getPageSize());
        List<BmsSupplierTb> bmsSupplierTbList = bmsSupplierTbMapper.selectSelective(BmsSupplierTb.builder().supplierCode(bmsSupplierListPageReqDTO.getSupplierCode()).deleteFlag(BioDrQiContents.N).supplierName(bmsSupplierListPageReqDTO.getSupplierName()).build());
        PageInfo<BmsSupplierTb> srcPageInfo = new PageInfo<>(bmsSupplierTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, BmsSupplierListPageRspDTO.class);
    }

    @Override
    public List<BmsSupplierListAllRspDTO> listALl() {
        List<BmsSupplierTb> bmsSupplierTbList = bmsSupplierTbMapper.selectSupplierCodeAndSupplierCodeByDeleteFlagOrderByIdDesc(BioDrQiContents.N);
        return BeanUtils.copyListProperties(bmsSupplierTbList, BmsSupplierListAllRspDTO.class);
    }

    @Override
    public void add(BmsSupplierAddReqDTO bmsSupplierAddReqDTO) {
        if (Objects.nonNull(bmsSupplierTbMapper.selectOneBySupplierCode(bmsSupplierAddReqDTO.getSupplierCode()))) {
            throw new BusinessException("供应商编码重复");
        }
        if (Objects.nonNull(bmsSupplierTbMapper.selectOneBySupplierName(bmsSupplierAddReqDTO.getSupplierName()))) {
            throw new BusinessException("供应商名称重复");
        }

        BmsSupplierTb bmsSupplierTb=new BmsSupplierTb();
        bmsSupplierTb.setSupplierCode(bmsSupplierAddReqDTO.getSupplierCode());
        bmsSupplierTb.setSupplierName(bmsSupplierAddReqDTO.getSupplierName());
        bmsSupplierTb.setOpeningBank(bmsSupplierAddReqDTO.getOpeningBank());
        bmsSupplierTb.setBankAccount(bmsSupplierAddReqDTO.getBankAccount());
        bmsSupplierTb.setTaxId(bmsSupplierAddReqDTO.getTaxId());
        bmsSupplierTb.setQualificationLocation(bmsSupplierAddReqDTO.getQualificationLocation());
        bmsSupplierTb.setBusinessScope(bmsSupplierAddReqDTO.getBusinessScope());
        bmsSupplierTb.setCooperateForm(bmsSupplierAddReqDTO.getCooperateForm());
        bmsSupplierTb.setFrameworkAgreementNumber(bmsSupplierAddReqDTO.getFrameworkAgreementNumber());
        bmsSupplierTb.setFrameworkAgreementAnnex(bmsSupplierAddReqDTO.getFrameworkAgreementAnnex());
        bmsSupplierTb.setExpirationDate(bmsSupplierAddReqDTO.getExpirationDate());
        bmsSupplierTb.setContactUserName(bmsSupplierAddReqDTO.getContactUserName());
        bmsSupplierTb.setContactUserTelephone(bmsSupplierAddReqDTO.getContactUserTelephone());
        bmsSupplierTb.setKahunaUserName(bmsSupplierAddReqDTO.getKahunaUserName());
        bmsSupplierTb.setKahunaUserId(bmsSupplierAddReqDTO.getKahunaUserId());
        bmsSupplierTb.setRemak(bmsSupplierAddReqDTO.getRemak());
        bmsSupplierTb.setCreateTime(new Date());
        bmsSupplierTb.setCreateUserName(SecurityContextHolder.getNickName());
        bmsSupplierTb.setCreateUserId(SecurityContextHolder.getUserId());
        bmsSupplierTb.setDeleteFlag(BioDrQiContents.N);


    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public void exportExcel(BmsSupplierExportExcelReqDTO bmsSupplierExportExcelReqDTO) {

    }

    @Override
    public void importExcel() {

    }
}
