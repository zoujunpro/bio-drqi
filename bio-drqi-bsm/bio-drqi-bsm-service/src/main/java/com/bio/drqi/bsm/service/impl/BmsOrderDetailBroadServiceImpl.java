package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.bsm.req.BmsStockBroadCountOrderReqDTO;
import com.bio.drqi.bsm.rsp.BmsOrderDetailBroadOrderCountRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderDetailDirectionAmountCountCountRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderDetailDirectionSupplierCountCountRspDTO;
import com.bio.drqi.bsm.service.BmsOrderDetailBroadService;
import com.bio.drqi.domain.BmsOrderDetailTb;
import com.bio.drqi.domain.BmsSupplierTb;
import com.bio.drqi.mapper.BmsOrderDetailTbMapper;
import com.bio.drqi.mapper.BmsSupplierTbMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class BmsOrderDetailBroadServiceImpl implements BmsOrderDetailBroadService {

    @Resource
    private BmsOrderDetailTbMapper bmsOrderDetailTbMapper;

    @Resource
    private BmsSupplierTbMapper bmsSupplierTbMapper;

    @Override
    public BmsOrderDetailBroadOrderCountRspDTO orderCount(BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO) {
        BmsOrderDetailBroadOrderCountRspDTO bmsOrderDetailBroadOrderCountRspDTO = new BmsOrderDetailBroadOrderCountRspDTO();
        bmsOrderDetailBroadOrderCountRspDTO.setCountPurchaseAmount(bmsOrderDetailTbMapper.selectForOrderCount1(BeanUtils.copyProperties(bmsStockBroadCountOrderReqDTO, BmsOrderDetailTb.class)));
        bmsOrderDetailBroadOrderCountRspDTO.setCountReportAmount(bmsOrderDetailTbMapper.selectForOrderCount2(BeanUtils.copyProperties(bmsStockBroadCountOrderReqDTO, BmsOrderDetailTb.class)));
        return bmsOrderDetailBroadOrderCountRspDTO.build();
    }

    @Override
    public List<BmsOrderDetailDirectionAmountCountCountRspDTO> directionAmountCount(BmsStockBroadCountOrderReqDTO bmsStockBroadCountOrderReqDTO,String reportFlag) {
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
                bmsOrderDetailDirectionAmountCountCountRspDTO.setAmount(bmsOrderDetailTb.getPayAmount());
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
}
