package com.bio.drqi.bsm.service.impl;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.bsm.req.BmsProductStockListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockDetailRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockListPageRspDTO;
import com.bio.drqi.bsm.service.BmsProductStockService;
import com.bio.drqi.domain.BmsProductStockTb;
import com.bio.drqi.mapper.BmsProductStockTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class BmsProductStockServiceImpl implements BmsProductStockService {

    @Resource
    private BmsProductStockTbMapper bmsProductStockTbMapper;

    @Override
    public PageInfo<BmsProductStockListPageRspDTO> listPage(BmsProductStockListPageReqDTO bmsProductStockListPageReqDTO) {
        PageHelper.startPage(bmsProductStockListPageReqDTO.getPageNum(), bmsProductStockListPageReqDTO.getPageSize());
        List<BmsProductStockTb> bmsProductStockTbList = bmsProductStockTbMapper.selectSelective(BeanUtils.copyProperties(bmsProductStockListPageReqDTO, BmsProductStockTb.class));
        PageInfo<BmsProductStockTb> srcPageInfo = new PageInfo<>(bmsProductStockTbList);

        return BeanUtils.copyPageInfoProperties(srcPageInfo, BmsProductStockListPageRspDTO.class);
    }

    @Override
    public BmsProductStockDetailRspDTO detail(Integer id) {
        BmsProductStockTb bmsProductStockTb = bmsProductStockTbMapper.selectById(id);
        if(bmsProductStockTb==null){
            throw new BusinessException("数据找不到");
        }
        return BeanUtils.copyProperties(bmsProductStockTb,BmsProductStockDetailRspDTO.class);
    }
}
