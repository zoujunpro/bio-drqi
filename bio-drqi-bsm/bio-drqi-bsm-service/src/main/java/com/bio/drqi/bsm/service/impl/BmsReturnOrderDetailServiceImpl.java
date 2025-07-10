package com.bio.drqi.bsm.service.impl;

import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.bsm.req.BmsReturnOrderDetailListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsReturnOrderDetailListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsReturnOrderDetailQueryByOrderDetailNumRspDTO;
import com.bio.drqi.bsm.service.BmsReturnOrderDetailService;
import com.bio.drqi.domain.BmsReturnOrderDetailTb;
import com.bio.drqi.mapper.BmsReturnOrderDetailTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BmsReturnOrderDetailServiceImpl implements BmsReturnOrderDetailService {

    @Resource
    private BmsReturnOrderDetailTbMapper bmsReturnOrderDetailTbMapper;

    @Override
    public PageInfo<BmsReturnOrderDetailListPageRspDTO> listPage(BmsReturnOrderDetailListPageReqDTO bmsReturnOrderDetailListPageReqDTO) {
        PageHelper.startPage(bmsReturnOrderDetailListPageReqDTO.getPageNum(), bmsReturnOrderDetailListPageReqDTO.getPageSize());
        List<BmsReturnOrderDetailTb> bmsReturnOrderDetailTbList = bmsReturnOrderDetailTbMapper.selectSelective(BeanUtils.copyProperties(bmsReturnOrderDetailListPageReqDTO, BmsReturnOrderDetailTb.class));
        PageInfo<BmsReturnOrderDetailTb>  srcPageInfo=new PageInfo<>(bmsReturnOrderDetailTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo,BmsReturnOrderDetailListPageRspDTO.class);
    }

    @Override
    public List<BmsReturnOrderDetailQueryByOrderDetailNumRspDTO> queryByOrderDetailNum(String orderDetailNum) {
        List<BmsReturnOrderDetailTb> bmsReturnOrderDetailTbList =   bmsReturnOrderDetailTbMapper.selectAllByOrderDetailNum(orderDetailNum);
        return BeanUtils.copyListProperties(bmsReturnOrderDetailTbList,BmsReturnOrderDetailQueryByOrderDetailNumRspDTO.class);
    }
}
