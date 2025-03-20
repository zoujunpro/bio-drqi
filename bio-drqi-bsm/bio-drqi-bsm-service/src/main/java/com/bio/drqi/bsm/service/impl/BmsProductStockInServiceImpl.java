package com.bio.drqi.bsm.service.impl;

import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.bsm.req.BmsProductStockInLogListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockInLogDetailRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockInLogListPageRspDTO;
import com.bio.drqi.bsm.service.BmsProductStockInService;
import com.bio.drqi.domain.BmsProductStockInLog;
import com.bio.drqi.mapper.BmsProductStockInLogMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
@Slf4j
public class BmsProductStockInServiceImpl implements BmsProductStockInService {

    @Resource
    private BmsProductStockInLogMapper bmsProductStockInLogMapper;

    @Override
    public PageInfo<BmsProductStockInLogListPageRspDTO> listPage(BmsProductStockInLogListPageReqDTO bmsProductStockInLogListPageReqDTO) {
        PageHelper.startPage(bmsProductStockInLogListPageReqDTO.getPageNum(), bmsProductStockInLogListPageReqDTO.getPageSize());
        List<BmsProductStockInLog> bmsProductStockInLogList = bmsProductStockInLogMapper.selectSelective(BeanUtils.copyProperties(bmsProductStockInLogListPageReqDTO, BmsProductStockInLog.class));
        PageInfo<BmsProductStockInLog> srcPageInfo = new PageInfo<>(bmsProductStockInLogList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, BmsProductStockInLogListPageRspDTO.class);
    }

    @Override
    public BmsProductStockInLogDetailRspDTO detail(Integer id) {
        BmsProductStockInLog bmsProductStockInLog = bmsProductStockInLogMapper.selectById(id);
        return BeanUtils.copyProperties(bmsProductStockInLog,BmsProductStockInLogDetailRspDTO.class);
    }
}
