package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.drqi.bsm.enums.PayTypeEnum;
import com.bio.drqi.bsm.req.BmsMoveOrderDetailListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsMoveOrderDetailListPageRspDTO;
import com.bio.drqi.bsm.service.BmsMoveOrderDetailService;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BmsMoveOrderDetailServiceImpl implements BmsMoveOrderDetailService {


    @Resource
    private BmsSupplierTbMapper bmsSupplierTbMapper;

    @Resource
    private BmsStockDictMapper bmsStockDictMapper;

    @Resource
    private BmsMoveOrderDetailTbMapper bmsMoveOrderDetailTbMapper;

    @Resource
    private BmsBrandTbMapper bmsBrandTbMapper;

    @Resource
    private BmsProductCategoryTbMapper bmsProductCategoryTbMapper;

    @Override
    public PageInfo<BmsMoveOrderDetailListPageRspDTO> listPage(BmsMoveOrderDetailListPageReqDTO bmsMoveOrderDetailListPageReqDTO) {
        PageHelper.startPage(bmsMoveOrderDetailListPageReqDTO.getPageNum(), bmsMoveOrderDetailListPageReqDTO.getPageSize());
        List<BmsMoveOrderDetailTb> bmsMoveOrderDetailTbList = bmsMoveOrderDetailTbMapper.selectSelective(BeanUtils.copyProperties(bmsMoveOrderDetailListPageReqDTO, BmsMoveOrderDetailTb.class));
        PageInfo<BmsMoveOrderDetailTb> srcPageInfo = new PageInfo<>(bmsMoveOrderDetailTbList);
        PageInfo<BmsMoveOrderDetailListPageRspDTO> targetPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, BmsMoveOrderDetailListPageRspDTO.class);
        if (CollectionUtil.isNotEmpty(targetPageInfo.getList())) {
            List<BmsStockDict> bmsStockDictList = bmsStockDictMapper.selectList(null);
            List<BmsBrandTb> bmsBrandTbList = bmsBrandTbMapper.selectSelective(null);
            List<BmsProductCategoryTb> bmsProductCategoryTbList = bmsProductCategoryTbMapper.selectSelective(null);
            Map<String, String> bmsSupplierTbMap = bmsSupplierTbMapper.selectSelective(null).stream().collect(Collectors.toMap(BmsSupplierTb::getSupplierCode, BmsSupplierTb::getSupplierName));
            Map<String, String> bmsBrandMap = bmsBrandTbList.stream().collect(Collectors.toMap(BmsBrandTb::getBrandCode, BmsBrandTb::getBrandName));
            Map<String, String> bmsProductCategoryTbMap = bmsProductCategoryTbList.stream().collect(Collectors.toMap(BmsProductCategoryTb::getProductCategoryCode, BmsProductCategoryTb::getProductCategoryName));
            Map<String, String> bmsStockDictMap = bmsStockDictList.stream().collect(Collectors.toMap(BmsStockDict::getStockCode, BmsStockDict::getStockName));
            targetPageInfo.getList().forEach(bmsMoveOrderDetailListPageRspDTO -> {
                bmsMoveOrderDetailListPageRspDTO.setFromStockName(bmsStockDictMap.get(bmsMoveOrderDetailListPageRspDTO.getFromStockCode()));
                bmsMoveOrderDetailListPageRspDTO.setToStockName(bmsStockDictMap.get(bmsMoveOrderDetailListPageRspDTO.getToStockCode()));
                bmsMoveOrderDetailListPageRspDTO.setProductCategoryName(bmsProductCategoryTbMap.get(bmsMoveOrderDetailListPageRspDTO.getProductCategoryCode()));
                bmsMoveOrderDetailListPageRspDTO.setBrandName(bmsBrandMap.get(bmsMoveOrderDetailListPageRspDTO.getBrandCode()));
                bmsMoveOrderDetailListPageRspDTO.setSupplierName(bmsSupplierTbMap.get(bmsMoveOrderDetailListPageRspDTO.getSupplierCode()));
            });
        }
        return targetPageInfo;
    }

    @Override
    public void exportExcel(BmsMoveOrderDetailListPageReqDTO bmsMoveOrderDetailListPageReqDTO, HttpServletResponse httpServletResponse) {
        List<BmsMoveOrderDetailTb> bmsMoveOrderDetailTbList = bmsMoveOrderDetailTbMapper.selectSelective(BeanUtils.copyProperties(bmsMoveOrderDetailListPageReqDTO, BmsMoveOrderDetailTb.class));
        List<BmsMoveOrderDetailListPageRspDTO> bmsMoveOrderDetailListPageRspDTOList = BeanUtils.copyListProperties(bmsMoveOrderDetailTbList, BmsMoveOrderDetailListPageRspDTO.class);
        if (CollectionUtil.isNotEmpty(bmsMoveOrderDetailListPageRspDTOList)) {
            List<BmsStockDict> bmsStockDictList = bmsStockDictMapper.selectList(null);
            List<BmsBrandTb> bmsBrandTbList = bmsBrandTbMapper.selectSelective(null);
            List<BmsProductCategoryTb> bmsProductCategoryTbList = bmsProductCategoryTbMapper.selectSelective(null);
            Map<String, String> bmsSupplierTbMap = bmsSupplierTbMapper.selectSelective(null).stream().collect(Collectors.toMap(BmsSupplierTb::getSupplierCode, BmsSupplierTb::getSupplierName));
            Map<String, String> bmsBrandMap = bmsBrandTbList.stream().collect(Collectors.toMap(BmsBrandTb::getBrandCode, BmsBrandTb::getBrandName));
            Map<String, String> bmsProductCategoryTbMap = bmsProductCategoryTbList.stream().collect(Collectors.toMap(BmsProductCategoryTb::getProductCategoryCode, BmsProductCategoryTb::getProductCategoryName));
            Map<String, String> bmsStockDictMap = bmsStockDictList.stream().collect(Collectors.toMap(BmsStockDict::getStockCode, BmsStockDict::getStockName));
            bmsMoveOrderDetailListPageRspDTOList.forEach(bmsMoveOrderDetailListPageRspDTO -> {
                bmsMoveOrderDetailListPageRspDTO.setFromStockName(bmsStockDictMap.get(bmsMoveOrderDetailListPageRspDTO.getFromStockCode()));
                bmsMoveOrderDetailListPageRspDTO.setToStockName(bmsStockDictMap.get(bmsMoveOrderDetailListPageRspDTO.getToStockCode()));
                bmsMoveOrderDetailListPageRspDTO.setProductCategoryName(bmsProductCategoryTbMap.get(bmsMoveOrderDetailListPageRspDTO.getProductCategoryCode()));
                bmsMoveOrderDetailListPageRspDTO.setBrandName(bmsBrandMap.get(bmsMoveOrderDetailListPageRspDTO.getBrandCode()));
                bmsMoveOrderDetailListPageRspDTO.setSupplierName(bmsSupplierTbMap.get(bmsMoveOrderDetailListPageRspDTO.getSupplierCode()));
                bmsMoveOrderDetailListPageRspDTO.setPayTypeName(PayTypeEnum.getNameByName(bmsMoveOrderDetailListPageRspDTO.getPayType()));
            });
        }
        ExcelUtil.writeExcel("移库记录导出", "sheet1",bmsMoveOrderDetailListPageRspDTOList, BmsMoveOrderDetailListPageRspDTO.class, httpServletResponse);
    }
}
