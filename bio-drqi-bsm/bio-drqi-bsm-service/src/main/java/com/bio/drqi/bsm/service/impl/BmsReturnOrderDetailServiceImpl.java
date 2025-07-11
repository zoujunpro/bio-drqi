package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.bsm.req.BmsReturnOrderDetailListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsReturnOrderDetailListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsReturnOrderDetailQueryByOrderDetailNumRspDTO;
import com.bio.drqi.bsm.service.BmsReturnOrderDetailService;
import com.bio.drqi.domain.BmsReturnOrderDetailTb;
import com.bio.drqi.domain.BmsStockDict;
import com.bio.drqi.mapper.BmsReturnOrderDetailTbMapper;
import com.bio.drqi.mapper.BmsStockDictMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BmsReturnOrderDetailServiceImpl implements BmsReturnOrderDetailService {

    @Resource
    private BmsReturnOrderDetailTbMapper bmsReturnOrderDetailTbMapper;

    @Resource
    private BmsStockDictMapper bmsStockDictMapper;

    @Override
    public PageInfo<BmsReturnOrderDetailListPageRspDTO> listPage(BmsReturnOrderDetailListPageReqDTO bmsReturnOrderDetailListPageReqDTO) {
        PageHelper.startPage(bmsReturnOrderDetailListPageReqDTO.getPageNum(), bmsReturnOrderDetailListPageReqDTO.getPageSize());
        List<BmsReturnOrderDetailTb> bmsReturnOrderDetailTbList = bmsReturnOrderDetailTbMapper.selectSelective(BeanUtils.copyProperties(bmsReturnOrderDetailListPageReqDTO, BmsReturnOrderDetailTb.class));
        PageInfo<BmsReturnOrderDetailTb> srcPageInfo = new PageInfo<>(bmsReturnOrderDetailTbList);
        PageInfo<BmsReturnOrderDetailListPageRspDTO> targetPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, BmsReturnOrderDetailListPageRspDTO.class);
        if (CollectionUtil.isNotEmpty(targetPageInfo.getList())) {
            List<BmsStockDict> bmsStockDictList = bmsStockDictMapper.selectList(null);
            Map<String, String> bmsStockDictMap = bmsStockDictList.stream().collect(Collectors.toMap(BmsStockDict::getStockCode, BmsStockDict::getStockName));
            targetPageInfo.getList().forEach(bmsReturnOrderDetailListPageRspDTO -> {
                bmsReturnOrderDetailListPageRspDTO.setStockName(bmsStockDictMap.get(bmsReturnOrderDetailListPageRspDTO.getStockCode()));
            });
        }
        return targetPageInfo;
    }

    @Override
    public List<BmsReturnOrderDetailQueryByOrderDetailNumRspDTO> queryByOrderDetailNum(String orderDetailNum) {
        List<BmsReturnOrderDetailTb> bmsReturnOrderDetailTbList = bmsReturnOrderDetailTbMapper.selectAllByOrderDetailNum(orderDetailNum);
        List<BmsReturnOrderDetailQueryByOrderDetailNumRspDTO> list= BeanUtils.copyListProperties(bmsReturnOrderDetailTbList, BmsReturnOrderDetailQueryByOrderDetailNumRspDTO.class);
        if (CollectionUtil.isNotEmpty(list)) {
            List<BmsStockDict> bmsStockDictList = bmsStockDictMapper.selectList(null);
            Map<String, String> bmsStockDictMap = bmsStockDictList.stream().collect(Collectors.toMap(BmsStockDict::getStockCode, BmsStockDict::getStockName));
            list.forEach(bmsReturnOrderDetailQueryByOrderDetailNumRspDTO -> {
                bmsReturnOrderDetailQueryByOrderDetailNumRspDTO.setStockName(bmsStockDictMap.get(bmsReturnOrderDetailQueryByOrderDetailNumRspDTO.getStockCode()));
            });
        }
        return list;
      }
}
