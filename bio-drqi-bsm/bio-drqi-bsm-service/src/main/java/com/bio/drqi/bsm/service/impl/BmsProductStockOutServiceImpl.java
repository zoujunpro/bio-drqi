package com.bio.drqi.bsm.service.impl;

import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.bsm.req.BmsProductStockOutLogListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockOutLogDetailRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockOutLogListPageRspDTO;
import com.bio.drqi.bsm.service.BmsProductStockOutService;
import com.bio.drqi.domain.BmsProductStockOutLog;
import com.bio.drqi.mapper.BmsProductStockOutLogMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class BmsProductStockOutServiceImpl implements BmsProductStockOutService {

    @Resource
    private BmsProductStockOutLogMapper bmsProductStockOutLogMapper;

    @Override
    public PageInfo<BmsProductStockOutLogListPageRspDTO> listPage(BmsProductStockOutLogListPageReqDTO bmsProductStockOutLogListPageReqDTO) {
        PageHelper.startPage(bmsProductStockOutLogListPageReqDTO.getPageNum(), bmsProductStockOutLogListPageReqDTO.getPageSize());
        List<BmsProductStockOutLog> bmsProductStockOutLogList = bmsProductStockOutLogMapper.selectSelective(BeanUtils.copyProperties(bmsProductStockOutLogListPageReqDTO, BmsProductStockOutLog.class));
        PageInfo<BmsProductStockOutLog> srcPageInfo = new PageInfo<>(bmsProductStockOutLogList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, BmsProductStockOutLogListPageRspDTO.class);
    }

    @Override
    public BmsProductStockOutLogDetailRspDTO detail(Integer id) {
        BmsProductStockOutLog bmsProductStockOutLog = bmsProductStockOutLogMapper.selectById(id);
        return BeanUtils.copyProperties(bmsProductStockOutLog,BmsProductStockOutLogDetailRspDTO.class);
    }
}
