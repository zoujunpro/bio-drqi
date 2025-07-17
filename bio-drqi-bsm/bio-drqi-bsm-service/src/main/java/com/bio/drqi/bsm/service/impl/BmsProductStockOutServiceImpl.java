package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.bsm.req.BmsProductStockOutLogListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockDetailRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockOutLogDetailRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockOutLogListPageRspDTO;
import com.bio.drqi.bsm.service.BmsProductStockOutService;
import com.bio.drqi.domain.BmsProductStockOutLog;
import com.bio.drqi.domain.BmsStockDict;
import com.bio.drqi.mapper.BmsProductStockOutLogMapper;
import com.bio.drqi.mapper.BmsStockDictMapper;
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
    private BmsStockDictMapper bmsStockDictMapper;

    @Override
    public PageInfo<BmsProductStockOutLogListPageRspDTO> listPage(BmsProductStockOutLogListPageReqDTO bmsProductStockOutLogListPageReqDTO) {
        PageHelper.startPage(bmsProductStockOutLogListPageReqDTO.getPageNum(), bmsProductStockOutLogListPageReqDTO.getPageSize());
        List<BmsProductStockOutLog> bmsProductStockOutLogList = bmsProductStockOutLogMapper.selectSelective(BeanUtils.copyProperties(bmsProductStockOutLogListPageReqDTO, BmsProductStockOutLog.class));
        PageInfo<BmsProductStockOutLog> srcPageInfo = new PageInfo<>(bmsProductStockOutLogList);
        PageInfo<BmsProductStockOutLogListPageRspDTO> targetPage= BeanUtils.copyPageInfoProperties(srcPageInfo, BmsProductStockOutLogListPageRspDTO.class);
        if (CollectionUtil.isNotEmpty(targetPage.getList())) {
            List<BmsStockDict> bmsStockDictList = bmsStockDictMapper.selectList(null);
            Map<String, String> bmsStockDictMap = bmsStockDictList.stream().collect(Collectors.toMap(BmsStockDict::getStockCode, BmsStockDict::getStockName));
            targetPage.getList().forEach(bmsProductStockOutLogListPageRspDTO -> {
                bmsProductStockOutLogListPageRspDTO.setStockName(bmsStockDictMap.get(bmsProductStockOutLogListPageRspDTO.getStockCode()));
            });
        }
        return targetPage;
    }

    @Override
    public BmsProductStockOutLogDetailRspDTO detail(Integer id) {
        BmsProductStockOutLog bmsProductStockOutLog = bmsProductStockOutLogMapper.selectById(id);
        BmsProductStockOutLogDetailRspDTO bmsProductStockOutLogDetailRspDTO= BeanUtils.copyProperties(bmsProductStockOutLog,BmsProductStockOutLogDetailRspDTO.class);
        BmsStockDict bmsStockDict = bmsStockDictMapper.selectOneByStockCode(bmsProductStockOutLog.getStockCode());
        bmsProductStockOutLogDetailRspDTO.setStockName(bmsStockDict != null ? bmsStockDict.getStockName() : null);
        return bmsProductStockOutLogDetailRspDTO;
    }
}
