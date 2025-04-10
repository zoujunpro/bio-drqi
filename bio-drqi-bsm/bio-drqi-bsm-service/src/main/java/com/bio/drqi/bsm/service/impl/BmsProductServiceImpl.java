package com.bio.drqi.bsm.service.impl;

import java.util.Date;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.uuid.IdUtils;
import com.bio.drqi.bsm.contents.BioBsmContents;
import com.bio.drqi.bsm.req.*;
import com.bio.drqi.bsm.rsp.BmsProductListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductQueryListRspDTO;
import com.bio.drqi.bsm.service.BmsProductService;
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BmsProductServiceImpl implements BmsProductService {


    @Resource
    private BmsProductTbMapper bmsProductTbMapper;

    @Resource
    private BmsBrandTbMapper bmsBrandTbMapper;

    @Resource
    private BmsSupplierTbMapper bmsSupplierTbMapper;

    @Resource
    private BmsProductTypeTbMapper bmsProductTypeTbMapper;

    @Resource
    private BmsProductCategoryTbMapper bmsProductCategoryTbMapper;

    @Override
    public PageInfo<BmsProductListPageRspDTO> listPage(BmsProductListPageReqDTO bmsProductListPageReqDTO) {
        PageHelper.startPage(bmsProductListPageReqDTO.getPageNum(), bmsProductListPageReqDTO.getPageSize());
        List<BmsProductTb> bmsProductTbLit = bmsProductTbMapper.selectSelective(BmsProductTb.builder().brandCode(bmsProductListPageReqDTO.getBrandCode()).productName(bmsProductListPageReqDTO.getProductName()).deleteFlag(bmsProductListPageReqDTO.getDeleteFlag()).build());
        PageInfo<BmsProductTb> srcPageInfo = new PageInfo<>(bmsProductTbLit);
        PageInfo<BmsProductListPageRspDTO> resultPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, BmsProductListPageRspDTO.class);

        if (CollectionUtil.isNotEmpty(resultPageInfo.getList())) {
            //类性
            List<BmsProductTypeTb> bmsProductTypeTbList = bmsProductTypeTbMapper.selectSelective(null);
            Map<String, String> bmsProductTypeTbMap = bmsProductTypeTbList.stream().collect(Collectors.toMap(BmsProductTypeTb::getProductTypeCode, BmsProductTypeTb::getProductTypeName));

            //类别
            List<BmsProductCategoryTb> bmsProductCategoryTbList = bmsProductCategoryTbMapper.selectSelective(null);
            Map<String, String> bmsProductCategoryMap = bmsProductCategoryTbList.stream().collect(Collectors.toMap(BmsProductCategoryTb::getProductCategoryCode, BmsProductCategoryTb::getProductCategoryName));

            resultPageInfo.getList().forEach(bmsProductListPageRspDTO -> {
                bmsProductListPageRspDTO.setProductCategoryName(bmsProductCategoryMap.get(bmsProductListPageRspDTO.getProductCategoryCode()));
                bmsProductListPageRspDTO.setProductTypeName(bmsProductTypeTbMap.get(bmsProductListPageRspDTO.getProductTypeCode()));
            });
        }

        return resultPageInfo;
    }

    @Override
    public List<String> listAllProductName() {
        List<String> productNameList = bmsProductTbMapper.selectProductNameOrderByIdDesc();
        return productNameList.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public List<BmsProductQueryListRspDTO> queryList(BmsProductQueryListReqDTO bmsProductQueryListReqDTO) {
        BmsBrandTb bmsBrandTb = bmsBrandTbMapper.selectOneByBrandCode(bmsProductQueryListReqDTO.getBrandCode());
        if (bmsBrandTb == null) {
            throw new BusinessException("品牌不存在");
        }

        List<BmsProductTb> bmsProductTbLit = bmsProductTbMapper.selectSelective(BmsProductTb.builder().brandCode(bmsProductQueryListReqDTO.getBrandCode()).deleteFlag(bmsBrandTb.getDeleteFlag()).build());

        List<BmsProductQueryListRspDTO> result = BeanUtils.copyListProperties(bmsProductTbLit, BmsProductQueryListRspDTO.class);
        if (CollectionUtil.isNotEmpty(result)) {
            //类性
            List<BmsProductTypeTb> bmsProductTypeTbList = bmsProductTypeTbMapper.selectSelective(null);
            Map<String, String> bmsProductTypeTbMap = bmsProductTypeTbList.stream().collect(Collectors.toMap(BmsProductTypeTb::getProductTypeCode, BmsProductTypeTb::getProductTypeName));

            //类别
            List<BmsProductCategoryTb> bmsProductCategoryTbList = bmsProductCategoryTbMapper.selectSelective(null);
            Map<String, String> bmsProductCategoryMap = bmsProductCategoryTbList.stream().collect(Collectors.toMap(BmsProductCategoryTb::getProductCategoryCode, BmsProductCategoryTb::getProductCategoryName));

            result.forEach(bmsProductQueryListRspDTO -> {
                bmsProductQueryListRspDTO.setProductCategoryName(bmsProductCategoryMap.get(bmsProductQueryListRspDTO.getProductCategoryCode()));
                bmsProductQueryListRspDTO.setProductTypeName(bmsProductTypeTbMap.get(bmsProductQueryListRspDTO.getProductTypeCode()));
            });
        }
        return result;


    }

    @Override
    public void exportExcel(BmsProductExportExcelReqDTO bmsProductExportExcelReqDTO) {

    }

    @Override
    public BmsProductTb add(BmsProductAddReqDTO bmsProductAddReqDTO) {
        BmsBrandTb bmsBrandTb = null;
        if (StringUtils.isNotEmpty(bmsProductAddReqDTO.getBrandCode())) {
            bmsBrandTb = bmsBrandTbMapper.selectOneByBrandCode(bmsProductAddReqDTO.getBrandCode());
            if (bmsBrandTb == null) {
                throw new BusinessException("无此品牌");
            }
            if (BioDrQiContents.Y.equals(bmsBrandTb.getDeleteFlag())) {
                throw new BusinessException("此品牌已经删除");
            }
        }
        String productInnerCode = null;
        String maxProductInnerCode = bmsProductTbMapper.selectMaxProductInnerCode();
        if (StringUtils.isEmpty(maxProductInnerCode)) {
            productInnerCode = BioBsmContents.product_prefix + StringUtils.padl("1", 5, '0');
        } else {
            String nextProductInnerCode = String.valueOf(Integer.valueOf(maxProductInnerCode.substring(2)) + 1);
            productInnerCode = BioBsmContents.product_prefix + StringUtils.padl(nextProductInnerCode, 5, '0');
        }
        BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductNameAndBrandCodeAndProductSpecs(bmsProductAddReqDTO.getProductName(), bmsProductAddReqDTO.getBrandCode(), bmsProductAddReqDTO.getProductSpecs());
        if(bmsProductTb!=null){
            throw new BusinessException("重复添加商品");
        }
         bmsProductTb = new BmsProductTb();
        bmsProductTb.setProductName(bmsProductAddReqDTO.getProductName());
        bmsProductTb.setProductOutCode(bmsProductAddReqDTO.getProductOutCode());
        bmsProductTb.setProductInnerCode(productInnerCode);
        bmsProductTb.setProductCategoryCode(bmsProductAddReqDTO.getProductCategoryCode());
        bmsProductTb.setProductTypeCode(bmsProductAddReqDTO.getProductTypeCode());
        if (bmsBrandTb != null) {
            bmsProductTb.setBrandName(bmsBrandTb.getBrandName());
            bmsProductTb.setBrandCode(bmsProductAddReqDTO.getBrandCode());
        }

        bmsProductTb.setProductSpecs(bmsProductAddReqDTO.getProductSpecs());
        bmsProductTb.setCreateTime(new Date());
        bmsProductTb.setCreateUserId(SecurityContextHolder.getUserId());
        bmsProductTb.setCreateUserName(SecurityContextHolder.getNickName());
        bmsProductTb.setDeleteFlag(BioDrQiContents.N);
        log.info("商品入库={}", bmsProductTb);
        try {

            bmsProductTbMapper.insert(bmsProductTb);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("请不要重复添加此材料");
        }
        return bmsProductTb;
    }

    @Override
    public void delete(Integer id) {
        BmsProductTb bmsProductTb = bmsProductTbMapper.selectById(id);
        if (bmsProductTb == null) {
            throw new BusinessException("材料不存在");
        }
        bmsProductTb.setDeleteFlag(BioDrQiContents.Y);
        bmsProductTbMapper.updateById(bmsProductTb);
    }

    @Override
    public void edit(BmsProductEditReqDTO bmsProductEditReqDTO) {
        BmsProductTb bmsProductTb = bmsProductTbMapper.selectById(bmsProductEditReqDTO.getId());
        if (bmsProductTb == null) {
            throw new BusinessException("材料不存在");
        }
        if (BioDrQiContents.Y.equals(bmsProductTb.getDeleteFlag())) {
            throw new BusinessException("材料已经删除，无法修改");
        }
        bmsProductTb.setProductName(bmsProductEditReqDTO.getProductName());
        bmsProductTb.setProductOutCode(bmsProductEditReqDTO.getProductOutCode());
        bmsProductTb.setProductCategoryCode(bmsProductEditReqDTO.getProductCategoryCode());
        bmsProductTb.setProductTypeCode(bmsProductEditReqDTO.getProductTypeCode());
        bmsProductTb.setProductSpecs(bmsProductEditReqDTO.getProductSpecs());
        try {
            bmsProductTbMapper.updateById(bmsProductTb);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("系统已存在此材料信息，不能修改");
        }

    }
}
