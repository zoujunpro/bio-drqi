package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.bsm.req.BmsProductStockEditDateReqDTO;
import com.bio.drqi.bsm.req.BmsProductStockListPageReqDTO;
import com.bio.drqi.bsm.req.BmsProductStockQueryListReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockDetailRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductStockQueryListRspDTO;
import com.bio.drqi.bsm.service.BmsProductStockService;
import com.bio.drqi.domain.BmsProductStockTb;
import com.bio.drqi.domain.BmsStockDict;
import com.bio.drqi.mapper.BmsProductStockTbMapper;
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
public class BmsProductStockServiceImpl implements BmsProductStockService {

    @Resource
    private BmsProductStockTbMapper bmsProductStockTbMapper;


    @Resource
    private BmsStockDictMapper bmsStockDictMapper;

    @Override
    public PageInfo<BmsProductStockListPageRspDTO> listPage(BmsProductStockListPageReqDTO bmsProductStockListPageReqDTO) {
        PageHelper.startPage(bmsProductStockListPageReqDTO.getPageNum(), bmsProductStockListPageReqDTO.getPageSize());
        List<BmsProductStockTb> bmsProductStockTbList = bmsProductStockTbMapper.selectSelective(BeanUtils.copyProperties(bmsProductStockListPageReqDTO, BmsProductStockTb.class));
        PageInfo<BmsProductStockTb> srcPageInfo = new PageInfo<>(bmsProductStockTbList);
        PageInfo<BmsProductStockListPageRspDTO> targetPage = BeanUtils.copyPageInfoProperties(srcPageInfo, BmsProductStockListPageRspDTO.class);
        if (CollectionUtil.isNotEmpty(targetPage.getList())) {
            List<BmsStockDict> bmsStockDictList = bmsStockDictMapper.selectList(null);
            Map<String, String> bmsStockDictMap = bmsStockDictList.stream().collect(Collectors.toMap(BmsStockDict::getStockCode, BmsStockDict::getStockName));
            targetPage.getList().forEach(bmsProductStockListPageRspDTO -> {
                bmsProductStockListPageRspDTO.setStockName(bmsStockDictMap.get(bmsProductStockListPageRspDTO.getStockCode()));
            });
        }
        return targetPage;
    }

    @Override
    public BmsProductStockDetailRspDTO detail(Integer id) {
        BmsProductStockTb bmsProductStockTb = bmsProductStockTbMapper.selectById(id);
        if (bmsProductStockTb == null) {
            throw new BusinessException("数据找不到");
        }
        BmsProductStockDetailRspDTO bmsProductStockDetailRspDTO = BeanUtils.copyProperties(bmsProductStockTb, BmsProductStockDetailRspDTO.class);
        BmsStockDict bmsStockDict = bmsStockDictMapper.selectOneByStockCode(bmsProductStockDetailRspDTO.getStockCode());
        bmsProductStockDetailRspDTO.setStockName(bmsStockDict != null ? bmsStockDict.getStockName() : null);
        return bmsProductStockDetailRspDTO;
    }

    @Override
    public List<String> queryStockByUnitCode(String unitCode) {
        List<String> productNameList = bmsProductStockTbMapper.selectProductNameByUnitCode(unitCode);
        if (CollectionUtil.isNotEmpty(productNameList)) {
            return productNameList.stream().distinct().collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<BmsProductStockQueryListRspDTO> queryList(BmsProductStockQueryListReqDTO bmsProductStockQueryListReqDTO) {
        List<BmsProductStockTb> bmsProductStockTbList = bmsProductStockTbMapper.selectSelective(BeanUtils.copyProperties(bmsProductStockQueryListReqDTO, BmsProductStockTb.class));
        List<BmsProductStockQueryListRspDTO> bmsProductStockQueryListRspDTOList= BeanUtils.copyListProperties(bmsProductStockTbList, BmsProductStockQueryListRspDTO.class);
        if (CollectionUtil.isNotEmpty(bmsProductStockQueryListRspDTOList)) {
            List<BmsStockDict> bmsStockDictList = bmsStockDictMapper.selectList(null);
            Map<String, String> bmsStockDictMap = bmsStockDictList.stream().collect(Collectors.toMap(BmsStockDict::getStockCode, BmsStockDict::getStockName));
            bmsProductStockQueryListRspDTOList.forEach(bmsProductStockQueryListRspDTO -> {
                bmsProductStockQueryListRspDTO.setStockName(bmsStockDictMap.get(bmsProductStockQueryListRspDTO.getStockCode()));
            });
        }
        return bmsProductStockQueryListRspDTOList;
    }

    @Override
    public void editDate(BmsProductStockEditDateReqDTO bmsProductStockEditDateReqDTO) {
        BmsProductStockTb bmsProductStockTb = bmsProductStockTbMapper.selectById(bmsProductStockEditDateReqDTO.getId());
        if (bmsProductStockTb == null) {
            throw new BusinessException("不存在此库存");
        }
        bmsProductStockTb.setExpirationDate(bmsProductStockEditDateReqDTO.getExpirationDate());
        bmsProductStockTb.setProduceDate(bmsProductStockEditDateReqDTO.getProduceDate());
        bmsProductStockTbMapper.updateById(bmsProductStockTb);

    }
}
