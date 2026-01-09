package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.drqi.bsm.req.BmsStockPeriodCountListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsStockPeriodCountListPageRspDTO;
import com.bio.drqi.bsm.service.BmsStockPeriodCountService;
import com.bio.drqi.domain.BmsProductCategoryTb;
import com.bio.drqi.domain.BmsProductStockPeriodCountTb;
import com.bio.drqi.domain.BmsStockDict;
import com.bio.drqi.mapper.BmsProductCategoryTbMapper;
import com.bio.drqi.mapper.BmsProductStockPeriodCountTbMapper;
import com.bio.drqi.mapper.BmsStockDictMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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

    @Resource
    private BmsStockDictMapper bmsStockDictMapper;

    @Override
    public PageInfo<BmsStockPeriodCountListPageRspDTO> listPage(BmsStockPeriodCountListPageReqDTO bmsStockPeriodCountListPageReqDTO) {
        PageHelper.startPage(bmsStockPeriodCountListPageReqDTO.getPageNum(), bmsStockPeriodCountListPageReqDTO.getPageSize());
        List<BmsProductStockPeriodCountTb> list = bmsProductStockPeriodCountTbMapper.selectSelective(BeanUtils.copyProperties(bmsStockPeriodCountListPageReqDTO, BmsProductStockPeriodCountTb.class));
        PageInfo<BmsStockPeriodCountListPageRspDTO> srcPageInfo = new PageInfo(list);
        PageInfo<BmsStockPeriodCountListPageRspDTO> resultPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, BmsStockPeriodCountListPageRspDTO.class);
        if (CollectionUtil.isNotEmpty(list)) {
            Map<String, String> map = bmsProductCategoryTbMapper.selectSelective(null).stream().collect(Collectors.toMap(BmsProductCategoryTb::getProductCategoryCode, BmsProductCategoryTb::getProductCategoryName));
            Map<String, String> bmsStockDictMap = bmsStockDictMapper.selectList(null).stream().collect(Collectors.toMap(BmsStockDict::getStockCode, BmsStockDict::getStockName));
            resultPageInfo.getList().forEach(bmsStockPeriodCountListPageRspDTO -> {
                bmsStockPeriodCountListPageRspDTO.setProductCategoryName(map.get(bmsStockPeriodCountListPageRspDTO.getProductCategoryCode()));
                bmsStockPeriodCountListPageRspDTO.setStockName(bmsStockDictMap.get(bmsStockPeriodCountListPageRspDTO.getStockCode()));
            });
        }
        return resultPageInfo;
    }

    @Override
    public void exportExcel(BmsStockPeriodCountListPageReqDTO bmsStockPeriodCountListPageReqDTO, HttpServletResponse httpServletResponse) {
        List<BmsProductStockPeriodCountTb> list = bmsProductStockPeriodCountTbMapper.selectSelective(BeanUtils.copyProperties(bmsStockPeriodCountListPageReqDTO, BmsProductStockPeriodCountTb.class));
        List<BmsStockPeriodCountListPageRspDTO> resultList = BeanUtils.copyListProperties(list, BmsStockPeriodCountListPageRspDTO.class);
        ExcelUtil.writeExcel("期初期末数据","sheet1",resultList,BmsStockPeriodCountListPageRspDTO.class,httpServletResponse);
    }
}
