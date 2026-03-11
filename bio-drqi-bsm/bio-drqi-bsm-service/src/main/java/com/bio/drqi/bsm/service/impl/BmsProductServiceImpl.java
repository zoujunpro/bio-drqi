package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.bsm.contents.BioBsmContents;
import com.bio.drqi.bsm.req.*;
import com.bio.drqi.bsm.rsp.BmsProductListAllRspDTO;
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
import java.util.Date;
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
    private BmsProductCategoryTbMapper bmsProductCategoryTbMapper;

    @Resource
    private BmsOrderDetailTbMapper bmsOrderDetailTbMapper;

    @Override
    public PageInfo<BmsProductListPageRspDTO> listPage(BmsProductListPageReqDTO bmsProductListPageReqDTO) {
        PageHelper.startPage(bmsProductListPageReqDTO.getPageNum(), bmsProductListPageReqDTO.getPageSize());
        List<BmsProductTb> bmsProductTbLit = bmsProductTbMapper.selectSelective(BeanUtils.copyProperties(bmsProductListPageReqDTO, BmsProductTb.class));
        PageInfo<BmsProductTb> srcPageInfo = new PageInfo<>(bmsProductTbLit);
        PageInfo<BmsProductListPageRspDTO> resultPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, BmsProductListPageRspDTO.class);
        if (CollectionUtil.isNotEmpty(resultPageInfo.getList())) {
            //类性
            List<BmsBrandTb> bmsBrandTbList = bmsBrandTbMapper.selectSelective(null);
            Map<String, String> bmsBrandTbMap = bmsBrandTbList.stream().collect(Collectors.toMap(BmsBrandTb::getBrandCode, BmsBrandTb::getBrandName));

            //类别
            List<BmsProductCategoryTb> bmsProductCategoryTbList = bmsProductCategoryTbMapper.selectSelective(null);
            Map<String, String> bmsProductCategoryMap = bmsProductCategoryTbList.stream().collect(Collectors.toMap(BmsProductCategoryTb::getProductCategoryCode, BmsProductCategoryTb::getProductCategoryName));

            resultPageInfo.getList().forEach(bmsProductListPageRspDTO -> {
                bmsProductListPageRspDTO.setProductCategoryName(bmsProductCategoryMap.get(bmsProductListPageRspDTO.getProductCategoryCode()));
                bmsProductListPageRspDTO.setBrandName(bmsBrandTbMap.get(bmsProductListPageRspDTO.getBrandCode()));
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
    public List<BmsProductListAllRspDTO> listAll() {
        return BeanUtils.copyListProperties(bmsProductTbMapper.selectSelective(null), BmsProductListAllRspDTO.class);
    }

    @Override
    public List<BmsProductQueryListRspDTO> queryList(BmsProductQueryListReqDTO bmsProductQueryListReqDTO) {

        BmsProductTb bmsProductTb = BeanUtils.copyProperties(bmsProductQueryListReqDTO, BmsProductTb.class);
        bmsProductTb.setProductStatus(BioDrQiContents.Y);
        List<BmsProductTb> bmsProductTbLit = bmsProductTbMapper.selectSelective(bmsProductTb);

        List<BmsProductQueryListRspDTO> result = BeanUtils.copyListProperties(bmsProductTbLit, BmsProductQueryListRspDTO.class);
        if (CollectionUtil.isNotEmpty(result)) {
            //类别
            List<BmsBrandTb> bmsBrandTbList = bmsBrandTbMapper.selectSelective(null);
            List<BmsProductCategoryTb> bmsProductCategoryTbList = bmsProductCategoryTbMapper.selectSelective(null);
            Map<String, String> bmsBrandMap = bmsBrandTbList.stream().collect(Collectors.toMap(BmsBrandTb::getBrandCode, BmsBrandTb::getBrandName));
            Map<String, String> bmsProductCategoryMap = bmsProductCategoryTbList.stream().collect(Collectors.toMap(BmsProductCategoryTb::getProductCategoryCode, BmsProductCategoryTb::getProductCategoryName));

            result.forEach(bmsProductQueryListRspDTO -> {
                bmsProductQueryListRspDTO.setProductCategoryName(bmsProductCategoryMap.get(bmsProductQueryListRspDTO.getProductCategoryCode()));
                bmsProductQueryListRspDTO.setBrandName(bmsBrandMap.get(bmsProductQueryListRspDTO.getBrandCode()));
            });
        }
        return result;


    }

    @Override
    public void exportExcel(BmsProductExportExcelReqDTO bmsProductExportExcelReqDTO) {

    }

    @Override
    public BmsProductTb add(BmsProductAddReqDTO bmsProductAddReqDTO) {
        BmsBrandTb bmsBrandTb = bmsBrandTbMapper.selectOneByBrandCode(bmsProductAddReqDTO.getBrandCode());
        if (bmsBrandTb == null) {
            throw new BusinessException("无此品牌");
        }
        if (BioDrQiContents.N.equals(bmsBrandTb.getBrandStatus())) {
            throw new BusinessException("此品牌已经禁用");
        }
        String productInnerCode = null;
        Integer maxProductInnerCode = bmsProductTbMapper.selectList(null).stream().map(BmsProductTb::getProductInnerCode).map(code -> Integer.valueOf(code.substring(2))).max(Integer::compareTo).get();
        if (maxProductInnerCode == null) {
            productInnerCode = BioBsmContents.product_prefix + StringUtils.padl("1", 5, '0');
        } else {
            String nextProductInnerCode = String.valueOf(maxProductInnerCode + 1);
            productInnerCode = BioBsmContents.product_prefix + StringUtils.padl(nextProductInnerCode, 5, '0');
        }
        BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductNameAndBrandCodeAndProductSpecsAndProductOutCode(bmsProductAddReqDTO.getProductName(), bmsProductAddReqDTO.getBrandCode(), bmsProductAddReqDTO.getProductSpecs(), bmsProductAddReqDTO.getProductOutCode());
        if (bmsProductTb != null) {
            throw new BusinessException("已有此商品");
        }
        bmsProductTb = new BmsProductTb();
        bmsProductTb.setProductName(bmsProductAddReqDTO.getProductName());
        bmsProductTb.setProductOutCode(bmsProductAddReqDTO.getProductOutCode());
        bmsProductTb.setProductInnerCode(productInnerCode);
        bmsProductTb.setProductCategoryCode(bmsProductAddReqDTO.getProductCategoryCode());
        bmsProductTb.setBrandCode(bmsBrandTb.getBrandCode());
        bmsProductTb.setProductSpecs(bmsProductAddReqDTO.getProductSpecs());
        bmsProductTb.setCreateTime(new Date());
        bmsProductTb.setCreateUserId(SecurityContextHolder.getUserId());
        bmsProductTb.setCreateUserName(SecurityContextHolder.getNickName());
        bmsProductTb.setProductStatus(BioDrQiContents.Y);
        bmsProductTb.setUpdateTime(new Date());
        log.info("商品入库={}", bmsProductTb);
        try {
            bmsProductTbMapper.insert(bmsProductTb);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("请不要重复添加此材料");
        }

        return bmsProductTb;
    }

    @Override
    public void disable(Integer id) {
        BmsProductTb bmsProductTb = bmsProductTbMapper.selectById(id);
        if (bmsProductTb == null) {
            throw new BusinessException("材料不存在");
        }
        bmsProductTb.setProductStatus(BioDrQiContents.N);
        bmsProductTbMapper.updateById(bmsProductTb);
    }

    @Override
    public void enable(Integer id) {
        BmsProductTb bmsProductTb = bmsProductTbMapper.selectById(id);
        if (bmsProductTb == null) {
            throw new BusinessException("材料不存在");
        }
        bmsProductTb.setProductStatus(BioDrQiContents.Y);
        bmsProductTbMapper.updateById(bmsProductTb);
    }

    @Override
    public void edit(BmsProductEditReqDTO bmsProductEditReqDTO) {
        BmsProductTb bmsProductTb = bmsProductTbMapper.selectById(bmsProductEditReqDTO.getId());
        if (bmsProductTb == null) {
            throw new BusinessException("材料不存在");
        }
        if (!BioDrQiContents.Y.equals(bmsProductTb.getProductStatus())) {
            throw new BusinessException("材料已经禁用，无法修改");
        }
        List<BmsOrderDetailTb> bmsOrderDetailTbList = bmsOrderDetailTbMapper.selectAllByProductInnerCode(bmsProductTb.getProductInnerCode());
        if (CollectionUtil.isNotEmpty(bmsOrderDetailTbList)) {
            throw new BusinessException("已经采购的商品无法更改商品信息");
        }
        bmsProductTb.setProductName(bmsProductEditReqDTO.getProductName());
        bmsProductTb.setProductOutCode(bmsProductEditReqDTO.getProductOutCode());
        bmsProductTb.setProductCategoryCode(bmsProductEditReqDTO.getProductCategoryCode());
        bmsProductTb.setProductSpecs(bmsProductEditReqDTO.getProductSpecs());
        bmsProductTb.setUpdateTime(new Date());
        try {
            bmsProductTbMapper.updateById(bmsProductTb);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("系统已存在此材料信息，不能修改");
        }

    }

    @Override
    public void modifyPurchaseTypeCode(BmsProductModifyPurchaseTypeCodeReqDTO bmsProductModifyPurchaseTypeCodeReqDTO) {
        BmsProductTb bmsProductTb = bmsProductTbMapper.selectById(bmsProductModifyPurchaseTypeCodeReqDTO.getId());
        bmsProductTb.setPurchaseTypeCode(bmsProductModifyPurchaseTypeCodeReqDTO.getPurchaseTypeCode());
        bmsProductTbMapper.updateById(bmsProductTb);
    }
}
