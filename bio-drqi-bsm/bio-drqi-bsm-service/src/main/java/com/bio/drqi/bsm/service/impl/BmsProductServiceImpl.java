package com.bio.drqi.bsm.service.impl;

import java.util.Date;

import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.uuid.IdUtils;
import com.bio.drqi.bsm.req.*;
import com.bio.drqi.bsm.rsp.BmsProductListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductQueryListRspDTO;
import com.bio.drqi.bsm.service.BmsProductService;
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.domain.BmsBrandTb;
import com.bio.drqi.domain.BmsProductTb;
import com.bio.drqi.domain.BmsSupplierTb;
import com.bio.drqi.mapper.BmsBrandTbMapper;
import com.bio.drqi.mapper.BmsProductTbMapper;
import com.bio.drqi.mapper.BmsSupplierTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BmsProductServiceImpl implements BmsProductService {


    @Resource
    private BmsProductTbMapper bmsProductTbMapper;

    @Resource
    private BmsBrandTbMapper bmsBrandTbMapper;

    @Resource
    private BmsSupplierTbMapper bmsSupplierTbMapper;

    @Override
    public PageInfo<BmsProductListPageRspDTO> listPage(BmsProductListPageReqDTO bmsProductListPageReqDTO) {
        PageHelper.startPage(bmsProductListPageReqDTO.getPageNum(), bmsProductListPageReqDTO.getPageSize());
        List<BmsProductTb> bmsProductTbLit = bmsProductTbMapper.selectSelective(BmsProductTb.builder().brandCode(bmsProductListPageReqDTO.getBrandCode()).productName(bmsProductListPageReqDTO.getProductName()).deleteFlag(bmsProductListPageReqDTO.getDeleteFlag()).build());
        PageInfo<BmsProductTb> srcPageInfo = new PageInfo<>(bmsProductTbLit);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, BmsProductListPageRspDTO.class);
    }

    @Override
    public List<BmsProductQueryListRspDTO> queryList(BmsProductQueryListReqDTO bmsProductQueryListReqDTO) {
        String deleteFlag = null;
        if (StringUtils.isNotEmpty(bmsProductQueryListReqDTO.getSupplierCode())) {
            BmsSupplierTb bmsSupplierTb = bmsSupplierTbMapper.selectOneBySupplierCode(bmsProductQueryListReqDTO.getSupplierCode());
            if (bmsSupplierTb == null) {
                throw new BusinessException("供应商不存在");
            }
            deleteFlag = bmsSupplierTb.getDeleteFlag();
        }
        if (StringUtils.isNotEmpty(bmsProductQueryListReqDTO.getBrandCode())) {
            BmsBrandTb bmsBrandTb = bmsBrandTbMapper.selectOneByBrandCode(bmsProductQueryListReqDTO.getBrandCode());
            if (bmsBrandTb == null) {
                throw new BusinessException("品牌不存在");
            }
            deleteFlag = bmsBrandTb.getDeleteFlag();
        }

        List<BmsProductTb> bmsProductTbLit = bmsProductTbMapper.selectSelective(BmsProductTb.builder().brandCode(bmsProductQueryListReqDTO.getBrandCode()).deleteFlag(deleteFlag).build());
        return BeanUtils.copyListProperties(bmsProductTbLit, BmsProductQueryListRspDTO.class);
    }

    @Override
    public void exportExcel(BmsProductExportExcelReqDTO bmsProductExportExcelReqDTO) {

    }

    @Override
    public void add(BmsProductAddReqDTO bmsProductAddReqDTO) {
        BmsBrandTb bmsBrandTb = bmsBrandTbMapper.selectOneByBrandCode(bmsProductAddReqDTO.getBrandCode());
        if (bmsBrandTb == null) {
            throw new BusinessException("无此品牌");
        }
        if (BioDrQiContents.Y.equals(bmsBrandTb.getDeleteFlag())) {
            throw new BusinessException("此品牌已经删除");
        }

        BmsProductTb bmsProductTb = new BmsProductTb();
        bmsProductTb.setProductName(bmsProductAddReqDTO.getProductName());
        bmsProductTb.setProductOutCode(bmsProductAddReqDTO.getProductOutCode());
        bmsProductTb.setProductInnerCode(IdUtils.simpleUUID());
        bmsProductTb.setProductCategoryCode(bmsProductAddReqDTO.getProductCategoryCode());
        bmsProductTb.setProductTypeCode(bmsProductAddReqDTO.getProductTypeCode());
        bmsProductTb.setSupplierCode(bmsBrandTb.getSupplierCode());
        bmsProductTb.setBrandName(bmsBrandTb.getBrandName());
        bmsProductTb.setBrandCode(bmsProductAddReqDTO.getBrandCode());
        bmsProductTb.setProductSpecs(bmsProductAddReqDTO.getProductSpecs());
        bmsProductTb.setCreateTime(new Date());
        bmsProductTb.setCreateUserId(SecurityContextHolder.getUserId());
        bmsProductTb.setCreateUserName(SecurityContextHolder.getNickName());
        bmsProductTb.setDeleteFlag(BioDrQiContents.N);
        try {
            bmsProductTbMapper.insert(bmsProductTb);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("请不要重复添加此商品");
        }

    }

    @Override
    public void delete(Integer id) {
        BmsProductTb bmsProductTb = bmsProductTbMapper.selectById(id);
        if(bmsProductTb==null){
            throw  new BusinessException("商品不存在");
        }
        bmsProductTb.setDeleteFlag(BioDrQiContents.Y);
        bmsProductTbMapper.updateById(bmsProductTb);
    }

    @Override
    public void edit(BmsProductEditReqDTO bmsProductEditReqDTO) {


    }
}
