package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.bsm.req.BmsMoveOrderDetailListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsMoveOrderDetailListPageRspDTO;
import com.bio.drqi.bsm.service.BmsMoveOrderDetailService;
import com.bio.drqi.domain.BmsMoveOrderDetailTb;
import com.bio.drqi.domain.BmsStockDict;
import com.bio.drqi.mapper.BmsMoveOrderDetailTbMapper;
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
public class BmsMoveOrderDetailServiceImpl implements BmsMoveOrderDetailService {

    @Resource
    private BmsMoveOrderDetailTbMapper bmsMoveOrderDetailTbMapper;


    @Resource
    private BmsStockDictMapper bmsStockDictMapper;

    @Override
    public PageInfo<BmsMoveOrderDetailListPageRspDTO> listPage(BmsMoveOrderDetailListPageReqDTO bmsMoveOrderDetailListPageReqDTO) {
        PageHelper.startPage(bmsMoveOrderDetailListPageReqDTO.getPageNum(), bmsMoveOrderDetailListPageReqDTO.getPageSize());
        List<BmsMoveOrderDetailTb> bmsMoveOrderDetailTbList = bmsMoveOrderDetailTbMapper.selectSelective(BeanUtils.copyProperties(bmsMoveOrderDetailListPageReqDTO, BmsMoveOrderDetailTb.class));
        PageInfo<BmsMoveOrderDetailTb> srcPageInfo = new PageInfo<>(bmsMoveOrderDetailTbList);
        PageInfo<BmsMoveOrderDetailListPageRspDTO> targetPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, BmsMoveOrderDetailListPageRspDTO.class);
        if (CollectionUtil.isNotEmpty(targetPageInfo.getList())) {
            List<BmsStockDict> bmsStockDictList = bmsStockDictMapper.selectList(null);
            Map<String, String> bmsStockDictMap = bmsStockDictList.stream().collect(Collectors.toMap(BmsStockDict::getStockCode, BmsStockDict::getStockName));
            targetPageInfo.getList().forEach(bmsMoveOrderDetailListPageRspDTO -> {
                bmsMoveOrderDetailListPageRspDTO.setFromStockName(bmsStockDictMap.get(bmsMoveOrderDetailListPageRspDTO.getFromStockCode()));
                bmsMoveOrderDetailListPageRspDTO.setToStockName(bmsStockDictMap.get(bmsMoveOrderDetailListPageRspDTO.getToStockCode()));
            });
        }
        return targetPageInfo;
    }
}
