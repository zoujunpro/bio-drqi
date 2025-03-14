package com.bio.drqi.bsm.service.impl;

import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.bsm.req.BmsOrderListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsOrderListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderListPageRspDTO;
import com.bio.drqi.bsm.service.BmsOrderService;
import com.bio.drqi.domain.BmsOrderTb;
import com.bio.drqi.mapper.BmsOrderTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
@Slf4j
public class BmsOrderServiceImpl implements BmsOrderService {

    @Resource
    private BmsOrderTbMapper bmsOrderTbMapper;

    @Override
    public PageInfo<BmsOrderListPageRspDTO> listPage(BmsOrderListPageReqDTO bmsOrderListPageReqDTO) {
        PageHelper.startPage(bmsOrderListPageReqDTO.getPageNum(), bmsOrderListPageReqDTO.getPageSize());

        List<BmsOrderTb> bmsOrderTbList = bmsOrderTbMapper.selectSelective(BeanUtils.copyProperties(bmsOrderListPageReqDTO, BmsOrderTb.class));

        PageInfo<BmsOrderTb> srcPageInfo = new PageInfo<>(bmsOrderTbList);

        return BeanUtils.copyPageInfoProperties(srcPageInfo, BmsOrderListPageRspDTO.class);
    }

    @Override
    public List<BmsOrderListAllRspDTO> listALl() {
        List<BmsOrderTb> bmsOrderTbList = bmsOrderTbMapper.selectAllOrderByIdDesc();
        return BeanUtils.copyListProperties(bmsOrderTbList, BmsOrderListAllRspDTO.class);
    }
}
