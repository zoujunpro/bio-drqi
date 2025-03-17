package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.bsm.rsp.BmsOrderDetailListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderDetailQueryByOrderNumRspDTO;
import com.bio.drqi.bsm.service.BmsOrderDetailService;
import com.bio.drqi.domain.BmsOrderDetailTb;
import com.bio.drqi.mapper.BmsOrderDetailTbMapper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class BmsOrderDetailServiceImpl implements BmsOrderDetailService {

    @Resource
    private BmsOrderDetailTbMapper bmsOrderDetailTbMapper;

    @Override
    public PageInfo<BmsOrderDetailListPageRspDTO> listPage(BmsOrderDetailListPageRspDTO bmsOrderDetailListPageRspDTO) {
        return null;
    }

    @Override
    public List<BmsOrderDetailQueryByOrderNumRspDTO> queryByOrderNum(String orderNum) {
        List<BmsOrderDetailTb> bmsOrderDetailTbList = bmsOrderDetailTbMapper.selectAllByOrderNum(orderNum);
        return BeanUtils.copyListProperties(bmsOrderDetailTbList,BmsOrderDetailQueryByOrderNumRspDTO.class);
    }
}
