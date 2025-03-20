package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.bsm.req.BmsProductStockListPageReqDTO;
import com.bio.drqi.bsm.req.BmsProductStockQueryListReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockDetailRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockQueryListRspDTO;
import com.bio.drqi.bsm.service.BmsProductStockService;
import com.bio.drqi.domain.BmsProductStockTb;
import com.bio.drqi.mapper.BmsProductStockTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

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
        if (bmsProductStockTb == null) {
            throw new BusinessException("数据找不到");
        }
        return BeanUtils.copyProperties(bmsProductStockTb, BmsProductStockDetailRspDTO.class);
    }

    @Override
    public List<String> queryStockByUnitCode(String unitCode) {
        List<String> productNameList = bmsProductStockTbMapper.selectProductNameByUniqueCode(unitCode);
        if(CollectionUtil.isNotEmpty(productNameList)){
            return productNameList.stream().distinct().collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<BmsProductStockQueryListRspDTO> queryList(BmsProductStockQueryListReqDTO bmsProductStockQueryListReqDTO) {
       List<BmsProductStockTb> bmsProductStockTbList= bmsProductStockTbMapper.selectSelective(BeanUtils.copyProperties(bmsProductStockQueryListReqDTO,BmsProductStockTb.class));
        return BeanUtils.copyListProperties(bmsProductStockTbList,BmsProductStockQueryListRspDTO.class);
    }
}
