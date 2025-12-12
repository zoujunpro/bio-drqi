package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.bsm.req.BmsProductStockOutLogListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockDetailRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockOutLogDetailRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockOutLogListPageRspDTO;
import com.bio.drqi.bsm.service.BmsProductStockOutService;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BmsProductStockOutServiceImpl implements BmsProductStockOutService {

    @Resource
    private BmsProductStockOutLogMapper bmsProductStockOutLogMapper;


    @Resource
    private BmsSupplierTbMapper bmsSupplierTbMapper;

    @Resource
    private BmsStockDictMapper bmsStockDictMapper;


    @Resource
    private BmsBrandTbMapper bmsBrandTbMapper;

    @Resource
    private BmsProductCategoryTbMapper bmsProductCategoryTbMapper;

    @Override
    public PageInfo<BmsProductStockOutLogListPageRspDTO> listPage(BmsProductStockOutLogListPageReqDTO bmsProductStockOutLogListPageReqDTO) {
        PageHelper.startPage(bmsProductStockOutLogListPageReqDTO.getPageNum(), bmsProductStockOutLogListPageReqDTO.getPageSize());
        List<BmsProductStockOutLog> bmsProductStockOutLogList = bmsProductStockOutLogMapper.selectSelective(BeanUtils.copyProperties(bmsProductStockOutLogListPageReqDTO, BmsProductStockOutLog.class));
        PageInfo<BmsProductStockOutLog> srcPageInfo = new PageInfo<>(bmsProductStockOutLogList);
        PageInfo<BmsProductStockOutLogListPageRspDTO> targetPage = BeanUtils.copyPageInfoProperties(srcPageInfo, BmsProductStockOutLogListPageRspDTO.class);
        if (CollectionUtil.isNotEmpty(targetPage.getList())) {
            List<BmsStockDict> bmsStockDictList = bmsStockDictMapper.selectList(null);
            List<BmsBrandTb> bmsBrandTbList = bmsBrandTbMapper.selectSelective(null);
            List<BmsProductCategoryTb> bmsProductCategoryTbList = bmsProductCategoryTbMapper.selectSelective(null);
            Map<String, String> bmsSupplierTbMap = bmsSupplierTbMapper.selectSelective(null).stream().collect(Collectors.toMap(BmsSupplierTb::getSupplierCode, BmsSupplierTb::getSupplierName));
            Map<String, String> bmsBrandMap = bmsBrandTbList.stream().collect(Collectors.toMap(BmsBrandTb::getBrandCode, BmsBrandTb::getBrandName));
            Map<String, String> bmsProductCategoryTbMap = bmsProductCategoryTbList.stream().collect(Collectors.toMap(BmsProductCategoryTb::getProductCategoryCode, BmsProductCategoryTb::getProductCategoryName));
            Map<String, String> bmsStockDictMap = bmsStockDictList.stream().collect(Collectors.toMap(BmsStockDict::getStockCode, BmsStockDict::getStockName));
            targetPage.getList().forEach(bmsProductStockOutLogListPageRspDTO -> {
                bmsProductStockOutLogListPageRspDTO.setStockName(bmsStockDictMap.get(bmsProductStockOutLogListPageRspDTO.getStockCode()));
                bmsProductStockOutLogListPageRspDTO.setSupplierName(bmsSupplierTbMap.get(bmsProductStockOutLogListPageRspDTO.getSupplierCode()));
                bmsProductStockOutLogListPageRspDTO.setProductCategoryName(bmsProductCategoryTbMap.get(bmsProductStockOutLogListPageRspDTO.getProductCategoryCode()));
                bmsProductStockOutLogListPageRspDTO.setBrandName(bmsBrandMap.get(bmsProductStockOutLogListPageRspDTO.getBrandCode()));
            });
        }
        return targetPage;
    }

    @Override
    public BmsProductStockOutLogDetailRspDTO detail(Integer id) {
        BmsProductStockOutLog bmsProductStockOutLog = bmsProductStockOutLogMapper.selectById(id);
        BmsProductStockOutLogDetailRspDTO bmsProductStockOutLogDetailRspDTO = BeanUtils.copyProperties(bmsProductStockOutLog, BmsProductStockOutLogDetailRspDTO.class);
        BmsStockDict bmsStockDict = bmsStockDictMapper.selectOneByStockCode(bmsProductStockOutLog.getStockCode());
        BmsProductCategoryTb bmsProductCategoryTb = bmsProductCategoryTbMapper.selectOneByProductCategoryCode(bmsProductStockOutLog.getProductCategoryCode());
        BmsSupplierTb bmsSupplierTb = bmsSupplierTbMapper.selectOneBySupplierCode(bmsProductStockOutLog.getSupplierCode());
        BmsBrandTb bmsBrandTb = bmsBrandTbMapper.selectOneByBrandCode(bmsProductStockOutLog.getBrandCode());
        bmsProductStockOutLogDetailRspDTO.setStockName(bmsStockDict != null ? bmsStockDict.getStockName() : null);
        bmsProductStockOutLogDetailRspDTO.setSupplierName(bmsBrandTb == null ? null : bmsBrandTb.getBrandName());
        bmsProductStockOutLogDetailRspDTO.setSupplierName(bmsSupplierTb == null ? null : bmsSupplierTb.getSupplierName());
        bmsProductStockOutLogDetailRspDTO.setProductCategoryName(bmsProductCategoryTb == null ? null : bmsProductCategoryTb.getProductCategoryName());
        return bmsProductStockOutLogDetailRspDTO;
    }
}
