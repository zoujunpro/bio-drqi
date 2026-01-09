package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.bsm.req.BmsStockPeriodCountListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsStockPeriodCountListPageRspDTO;
import com.bio.drqi.bsm.service.BmsStockPeriodCountService;
import com.bio.drqi.domain.BmsProductCategoryTb;
import com.bio.drqi.domain.BmsProductStockPeriodCountTb;
import com.bio.drqi.mapper.BmsProductCategoryTbMapper;
import com.bio.drqi.mapper.BmsProductStockPeriodCountTbMapper;
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
public class BmsStockPeriodCountServiceImpl implements BmsStockPeriodCountService {

    @Resource
    private BmsProductStockPeriodCountTbMapper bmsProductStockPeriodCountTbMapper;

    @Resource
    private BmsProductCategoryTbMapper bmsProductCategoryTbMapper;

    @Override
    public PageInfo<BmsStockPeriodCountListPageRspDTO> listPage(BmsStockPeriodCountListPageReqDTO bmsStockPeriodCountListPageReqDTO) {
        PageHelper.startPage(bmsStockPeriodCountListPageReqDTO.getPageNum(), bmsStockPeriodCountListPageReqDTO.getPageSize());
        List<BmsProductStockPeriodCountTb> list = bmsProductStockPeriodCountTbMapper.selectSelective(BeanUtils.copyProperties(bmsStockPeriodCountListPageReqDTO, BmsProductStockPeriodCountTb.class));
        PageInfo<BmsStockPeriodCountListPageRspDTO> srcPageInfo = new PageInfo(list);
        PageInfo<BmsStockPeriodCountListPageRspDTO> resultPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, BmsStockPeriodCountListPageRspDTO.class);
        if (CollectionUtil.isNotEmpty(list)) {
            Map<String, String> map = bmsProductCategoryTbMapper.selectSelective(null).stream().collect(Collectors.toMap(BmsProductCategoryTb::getProductCategoryCode, BmsProductCategoryTb::getProductCategoryName));
            resultPageInfo.getList().forEach(bmsStockPeriodCountListPageRspDTO -> {
                bmsStockPeriodCountListPageRspDTO.setProductCategoryName(map.get(bmsStockPeriodCountListPageRspDTO.getProductCategoryCode()));
            });
        }
        return resultPageInfo;
    }
}
