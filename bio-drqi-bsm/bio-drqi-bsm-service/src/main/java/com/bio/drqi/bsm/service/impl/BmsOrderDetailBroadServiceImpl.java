package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.bsm.req.BmsStockBroadCountOrderReqDTO;
import com.bio.drqi.bsm.rsp.*;
import com.bio.drqi.bsm.service.BmsOrderDetailBroadService;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.BmsBrandTbMapper;
import com.bio.drqi.mapper.BmsOrderDetailTbMapper;
import com.bio.drqi.mapper.BmsProductCategoryTbMapper;
import com.bio.drqi.mapper.BmsSupplierTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BmsOrderDetailBroadServiceImpl implements BmsOrderDetailBroadService {

    @Resource
    private BmsOrderDetailTbMapper bmsOrderDetailTbMapper;

    @Resource
    private BmsSupplierTbMapper bmsSupplierTbMapper;

    @Resource
    private BmsBrandTbMapper bmsBrandTbMapper;

    @Resource
    private BmsProductCategoryTbMapper bmsProductCategoryTbMapper;


    @Override
    public BmsOrderDetailBroadOrderCountRspDTO orderCount(BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO) {
        BmsOrderDetailBroadOrderCountRspDTO bmsOrderDetailBroadOrderCountRspDTO = new BmsOrderDetailBroadOrderCountRspDTO();
        bmsOrderDetailBroadOrderCountRspDTO.setCountPurchaseAmount(bmsOrderDetailTbMapper.selectForOrderCount1(BeanUtils.copyProperties(bmsStockBroadCountOrderReqDTO, BmsOrderDetailTb.class)));
        bmsOrderDetailBroadOrderCountRspDTO.setCountReportAmount(bmsOrderDetailTbMapper.selectForOrderCount2(BeanUtils.copyProperties(bmsStockBroadCountOrderReqDTO, BmsOrderDetailTb.class)));
        return bmsOrderDetailBroadOrderCountRspDTO.build();
    }

    @Override
    public List<BmsOrderBroadCountByCategoryRspDTO> countAmountByByCategory(BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO) {
        if (StringUtils.isEmpty(bmsStockBroadCountOrderReqDTO.getCountType())) {
            bmsStockBroadCountOrderReqDTO.setCountType("month");
            bmsStockBroadCountOrderReqDTO.setBeginDateTime("2025-05");
            bmsStockBroadCountOrderReqDTO.setEndDateTime(DateUtil.format(new Date(), DatePattern.NORM_MONTH_PATTERN));
        }
        List<BmsOrderBroadCountByCategoryRspDTO> resultList=new ArrayList<>();
        List<BmsOrderDetailTb> bmsOrderDetailTbList = bmsOrderDetailTbMapper.selectForCountAmountGroupByCategory(BeanUtils.copyProperties(bmsStockBroadCountOrderReqDTO, BmsOrderDetailTb.class));
        List<BmsProductCategoryTb> bmsProductCategoryTbList = bmsProductCategoryTbMapper.selectSelective(null);
        if (CollectionUtil.isNotEmpty(bmsOrderDetailTbList)) {
            bmsOrderDetailTbList.stream().collect(Collectors.groupingBy(BmsOrderDetailTb::getDateTime)).forEach((dateTime, list) -> {
                BmsOrderBroadCountByCategoryRspDTO bmsOrderBroadCountByCategoryRspDTO = new BmsOrderBroadCountByCategoryRspDTO();
                bmsOrderBroadCountByCategoryRspDTO.setDateTime(dateTime);
                for (BmsProductCategoryTb bmsProductCategoryTb : bmsProductCategoryTbList) {
                    Map<String, BigDecimal> map = list.stream().collect(Collectors.toMap(BmsOrderDetailTb::getProductCategoryCode, BmsOrderDetailTb::getPayAmount));
                    bmsOrderBroadCountByCategoryRspDTO.addContent(bmsProductCategoryTb.getProductCategoryCode(), bmsProductCategoryTb.getProductCategoryName(), map.get(bmsProductCategoryTb.getProductCategoryCode()) == null ? new BigDecimal(0) : map.get(bmsProductCategoryTb.getProductCategoryCode()));
                }
                resultList.add(bmsOrderBroadCountByCategoryRspDTO);
            });
        }
        return resultList.stream().sorted(Comparator.comparing(bmsStockInBroadCountByCategoryRspDTO -> bmsStockInBroadCountByCategoryRspDTO.getDateTime())).collect(Collectors.toList());
    }


    @Override
    public List<BmsOrderDetailDirectionAmountCountCountRspDTO> directionAmountCount(BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO) {
        if (StringUtils.isEmpty(bmsStockBroadCountOrderReqDTO.getCountType())) {
            bmsStockBroadCountOrderReqDTO.setCountType("month");
            bmsStockBroadCountOrderReqDTO.setBeginDateTime("2025-05");
            bmsStockBroadCountOrderReqDTO.setEndDateTime(DateUtil.format(new Date(), DatePattern.NORM_MONTH_PATTERN));
        }
        List<BmsOrderDetailDirectionAmountCountCountRspDTO> resultList = new ArrayList<>();
        List<BmsOrderDetailTb> list = bmsOrderDetailTbMapper.selectForDirectionAmountCount(BeanUtils.copyProperties(bmsStockBroadCountOrderReqDTO, BmsOrderDetailTb.class));
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(bmsOrderDetailTb -> {
                BmsOrderDetailDirectionAmountCountCountRspDTO bmsOrderDetailDirectionAmountCountCountRspDTO = new BmsOrderDetailDirectionAmountCountCountRspDTO();
                bmsOrderDetailDirectionAmountCountCountRspDTO.setDateTime(bmsOrderDetailTb.getDateTime());
                bmsOrderDetailDirectionAmountCountCountRspDTO.setPurchaseAmount(bmsOrderDetailTb.getPayAmount());
                bmsOrderDetailDirectionAmountCountCountRspDTO.setReportAmount(bmsOrderDetailTb.getReportAmount());
                resultList.add(bmsOrderDetailDirectionAmountCountCountRspDTO);
            });
        }
        return resultList;
    }

    @Override
    public List<BmsOrderDetailDirectionSupplierCountCountRspDTO> directionSupplierCount(BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO) {
        List<BmsOrderDetailDirectionSupplierCountCountRspDTO> resultList = new ArrayList<>();
        List<BmsOrderDetailTb> list = bmsOrderDetailTbMapper.selectForDirectionSupplierCount(BeanUtils.copyProperties(bmsStockBroadCountOrderReqDTO, BmsOrderDetailTb.class));
        if (CollectionUtil.isNotEmpty(list)) {
            list.forEach(bmsOrderDetailTb -> {
                BmsSupplierTb bmsSupplierTb = bmsSupplierTbMapper.selectOneBySupplierCode(bmsOrderDetailTb.getSupplierCode());
                BmsOrderDetailDirectionSupplierCountCountRspDTO bmsOrderDetailDirectionSupplierCountCountRspDTO = new BmsOrderDetailDirectionSupplierCountCountRspDTO();
                bmsOrderDetailDirectionSupplierCountCountRspDTO.setSupplierCode(bmsOrderDetailTb.getSupplierCode());
                bmsOrderDetailDirectionSupplierCountCountRspDTO.setSupplierName(bmsSupplierTb.getSupplierName());
                bmsOrderDetailDirectionSupplierCountCountRspDTO.setAmount(bmsOrderDetailTb.getPayAmount());
                resultList.add(bmsOrderDetailDirectionSupplierCountCountRspDTO);
            });
        }
        return resultList;
    }

    @Override
    public PageInfo<BmsOrderDetailDirectionQueryReportNoInStockListPageRspDTO> queryReportNoInStockListPage(BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO) {
        PageHelper.startPage(bmsStockBroadCountOrderReqDTO.getPageNum(), bmsStockBroadCountOrderReqDTO.getPageSize());
        List<BmsOrderDetailTb> bmsOrderDetailTbList = bmsOrderDetailTbMapper.selectForReportNoInStockListPage(BeanUtils.copyProperties(bmsStockBroadCountOrderReqDTO, BmsOrderDetailTb.class));
        PageInfo<BmsOrderDetailTb> bmsOrderDetailTbPageInfo = new PageInfo<>(bmsOrderDetailTbList);
        PageInfo<BmsOrderDetailDirectionQueryReportNoInStockListPageRspDTO> result = BeanUtils.copyPageInfoProperties(bmsOrderDetailTbPageInfo, BmsOrderDetailDirectionQueryReportNoInStockListPageRspDTO.class);
        if (CollectionUtil.isNotEmpty(bmsOrderDetailTbList)) {
            List<BmsBrandTb> bmsBrandTbList = bmsBrandTbMapper.selectSelective(null);
            List<BmsProductCategoryTb> bmsProductCategoryTbList = bmsProductCategoryTbMapper.selectSelective(null);
            Map<String, String> bmsBrandMap = bmsBrandTbList.stream().collect(Collectors.toMap(BmsBrandTb::getBrandCode, BmsBrandTb::getBrandName));
            Map<String, String> bmsProductCategoryTbMap = bmsProductCategoryTbList.stream().collect(Collectors.toMap(BmsProductCategoryTb::getProductCategoryCode, BmsProductCategoryTb::getProductCategoryName));
            result.getList().forEach(bmsOrderDetailDirectionQueryReportNoInStockListPageRspDTO -> {
                bmsOrderDetailDirectionQueryReportNoInStockListPageRspDTO.setBrandName(bmsBrandMap.get(bmsOrderDetailDirectionQueryReportNoInStockListPageRspDTO.getBrandCode()));
                bmsOrderDetailDirectionQueryReportNoInStockListPageRspDTO.setProductCategoryName(bmsProductCategoryTbMap.get(bmsOrderDetailDirectionQueryReportNoInStockListPageRspDTO.getProductCategoryCode()));
            });
        }
        return result;
    }

    @Override
    public void exportReportNoInStockListPage(BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO, HttpServletResponse httpServletResponse) {
        List<BmsOrderDetailTb> bmsOrderDetailTbList = bmsOrderDetailTbMapper.selectForReportNoInStockListPage(BeanUtils.copyProperties(bmsStockBroadCountOrderReqDTO, BmsOrderDetailTb.class));
        List<BmsOrderDetailDirectionQueryReportNoInStockListPageRspDTO> result = BeanUtils.copyListProperties(bmsOrderDetailTbList, BmsOrderDetailDirectionQueryReportNoInStockListPageRspDTO.class);
        if (CollectionUtil.isNotEmpty(bmsOrderDetailTbList)) {
            List<BmsBrandTb> bmsBrandTbList = bmsBrandTbMapper.selectSelective(null);
            List<BmsProductCategoryTb> bmsProductCategoryTbList = bmsProductCategoryTbMapper.selectSelective(null);
            Map<String, String> bmsBrandMap = bmsBrandTbList.stream().collect(Collectors.toMap(BmsBrandTb::getBrandCode, BmsBrandTb::getBrandName));
            Map<String, String> bmsProductCategoryTbMap = bmsProductCategoryTbList.stream().collect(Collectors.toMap(BmsProductCategoryTb::getProductCategoryCode, BmsProductCategoryTb::getProductCategoryName));
            result.forEach(bmsOrderDetailDirectionQueryReportNoInStockListPageRspDTO -> {
                bmsOrderDetailDirectionQueryReportNoInStockListPageRspDTO.setBrandName(bmsBrandMap.get(bmsOrderDetailDirectionQueryReportNoInStockListPageRspDTO.getBrandCode()));
                bmsOrderDetailDirectionQueryReportNoInStockListPageRspDTO.setProductCategoryName(bmsProductCategoryTbMap.get(bmsOrderDetailDirectionQueryReportNoInStockListPageRspDTO.getProductCategoryCode()));
            });
        }
        ExcelUtil.writeExcel("已入库未报账数据", "sheet1", result, BmsOrderDetailDirectionQueryReportNoInStockListPageRspDTO.class, httpServletResponse);
    }
}
