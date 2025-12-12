package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.bsm.req.BmsReturnOrderDetailListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsReturnOrderDetailListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsReturnOrderDetailQueryByOrderDetailNumRspDTO;
import com.bio.drqi.bsm.service.BmsReturnOrderDetailService;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BmsReturnOrderDetailServiceImpl implements BmsReturnOrderDetailService {

    @Resource
    private BmsReturnOrderDetailTbMapper bmsReturnOrderDetailTbMapper;

    @Resource
    private BmsSupplierTbMapper bmsSupplierTbMapper;

    @Resource
    private BmsStockDictMapper bmsStockDictMapper;

    @Resource
    private BmsBrandTbMapper bmsBrandTbMapper;

    @Resource
    private BmsProductCategoryTbMapper bmsProductCategoryTbMapper;

    @Override
    public PageInfo<BmsReturnOrderDetailListPageRspDTO> listPage(BmsReturnOrderDetailListPageReqDTO bmsReturnOrderDetailListPageReqDTO) {
        PageHelper.startPage(bmsReturnOrderDetailListPageReqDTO.getPageNum(), bmsReturnOrderDetailListPageReqDTO.getPageSize());
        List<BmsReturnOrderDetailTb> bmsReturnOrderDetailTbList = bmsReturnOrderDetailTbMapper.selectSelective(BeanUtils.copyProperties(bmsReturnOrderDetailListPageReqDTO, BmsReturnOrderDetailTb.class));
        PageInfo<BmsReturnOrderDetailTb> srcPageInfo = new PageInfo<>(bmsReturnOrderDetailTbList);
        PageInfo<BmsReturnOrderDetailListPageRspDTO> targetPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, BmsReturnOrderDetailListPageRspDTO.class);
        if (CollectionUtil.isNotEmpty(targetPageInfo.getList())) {
            List<BmsStockDict> bmsStockDictList = bmsStockDictMapper.selectList(null);
            List<BmsBrandTb> bmsBrandTbList = bmsBrandTbMapper.selectSelective(null);
            List<BmsProductCategoryTb> bmsProductCategoryTbList = bmsProductCategoryTbMapper.selectSelective(null);
            Map<String, String> bmsSupplierTbMap = bmsSupplierTbMapper.selectSelective(null).stream().collect(Collectors.toMap(BmsSupplierTb::getSupplierCode, BmsSupplierTb::getSupplierName));
            Map<String, String> bmsBrandMap = bmsBrandTbList.stream().collect(Collectors.toMap(BmsBrandTb::getBrandCode, BmsBrandTb::getBrandName));
            Map<String, String> bmsProductCategoryTbMap = bmsProductCategoryTbList.stream().collect(Collectors.toMap(BmsProductCategoryTb::getProductCategoryCode, BmsProductCategoryTb::getProductCategoryName));
            Map<String, String> bmsStockDictMap = bmsStockDictList.stream().collect(Collectors.toMap(BmsStockDict::getStockCode, BmsStockDict::getStockName));
            targetPageInfo.getList().forEach(bmsReturnOrderDetailListPageRspDTO -> {
                bmsReturnOrderDetailListPageRspDTO.setStockName(bmsStockDictMap.get(bmsReturnOrderDetailListPageRspDTO.getStockCode()));
                bmsReturnOrderDetailListPageRspDTO.setBrandName(bmsBrandMap.get(bmsReturnOrderDetailListPageRspDTO.getBrandCode()));
                bmsReturnOrderDetailListPageRspDTO.setSupplierName(bmsSupplierTbMap.get(bmsReturnOrderDetailListPageRspDTO.getSupplierCode()));
                bmsReturnOrderDetailListPageRspDTO.setProductCategoryName(bmsProductCategoryTbMap.get(bmsReturnOrderDetailListPageRspDTO.getProductCategoryCode()));
            });
        }
        return targetPageInfo;
    }

    @Override
    public List<BmsReturnOrderDetailQueryByOrderDetailNumRspDTO> queryByOrderDetailNum(String orderDetailNum) {
        List<BmsReturnOrderDetailTb> bmsReturnOrderDetailTbList = bmsReturnOrderDetailTbMapper.selectAllByOrderDetailNum(orderDetailNum);
        List<BmsReturnOrderDetailQueryByOrderDetailNumRspDTO> list= BeanUtils.copyListProperties(bmsReturnOrderDetailTbList, BmsReturnOrderDetailQueryByOrderDetailNumRspDTO.class);
        if (CollectionUtil.isNotEmpty(list)) {
            List<BmsStockDict> bmsStockDictList = bmsStockDictMapper.selectList(null);
            List<BmsBrandTb> bmsBrandTbList = bmsBrandTbMapper.selectSelective(null);
            List<BmsProductCategoryTb> bmsProductCategoryTbList = bmsProductCategoryTbMapper.selectSelective(null);
            Map<String, String> bmsSupplierTbMap = bmsSupplierTbMapper.selectSelective(null).stream().collect(Collectors.toMap(BmsSupplierTb::getSupplierCode, BmsSupplierTb::getSupplierName));
            Map<String, String> bmsBrandMap = bmsBrandTbList.stream().collect(Collectors.toMap(BmsBrandTb::getBrandCode, BmsBrandTb::getBrandName));
            Map<String, String> bmsProductCategoryTbMap = bmsProductCategoryTbList.stream().collect(Collectors.toMap(BmsProductCategoryTb::getProductCategoryCode, BmsProductCategoryTb::getProductCategoryName));
            Map<String, String> bmsStockDictMap = bmsStockDictList.stream().collect(Collectors.toMap(BmsStockDict::getStockCode, BmsStockDict::getStockName));
            list.forEach(bmsReturnOrderDetailQueryByOrderDetailNumRspDTO -> {
                bmsReturnOrderDetailQueryByOrderDetailNumRspDTO.setStockName(bmsStockDictMap.get(bmsReturnOrderDetailQueryByOrderDetailNumRspDTO.getStockCode()));
                bmsReturnOrderDetailQueryByOrderDetailNumRspDTO.setBrandName(bmsBrandMap.get(bmsReturnOrderDetailQueryByOrderDetailNumRspDTO.getBrandCode()));
                bmsReturnOrderDetailQueryByOrderDetailNumRspDTO.setSupplierName(bmsSupplierTbMap.get(bmsReturnOrderDetailQueryByOrderDetailNumRspDTO.getSupplierCode()));
                bmsReturnOrderDetailQueryByOrderDetailNumRspDTO.setProductCategoryName(bmsProductCategoryTbMap.get(bmsReturnOrderDetailQueryByOrderDetailNumRspDTO.getProductCategoryCode()));
            });
        }
        return list;
      }
}
