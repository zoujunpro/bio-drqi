package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.uuid.IdUtils;
import com.bio.drqi.bsm.req.BmsStockLocationAddReqDTO;
import com.bio.drqi.bsm.req.BmsStockLocationEditReqDTO;
import com.bio.drqi.bsm.req.BmsStockLocationListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsStockLocationListAllStockRspDTO;
import com.bio.drqi.bsm.rsp.BmsStockLocationListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsStockLocationQueryByUnitRspDTO;
import com.bio.drqi.bsm.service.BmsStockLocationService;
import com.bio.drqi.domain.BmsProductStockOutLog;
import com.bio.drqi.domain.BmsStockDict;
import com.bio.drqi.domain.BmsStockLocationDict;
import com.bio.drqi.mapper.BmsProductStockTbMapper;
import com.bio.drqi.mapper.BmsStockDictMapper;
import com.bio.drqi.mapper.BmsStockLocationDictMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BmsStockLocationServiceImpl implements BmsStockLocationService {

    @Resource
    private BmsStockLocationDictMapper bmsStockLocationDictMapper;

    @Resource
    private BmsStockDictMapper bmsStockDictMapper;

    @Override
    public List<BmsStockLocationQueryByUnitRspDTO> queryByUnit(String unitCode) {
        List<BmsStockLocationQueryByUnitRspDTO> bmsStockLocationQueryByUnitRspDTOList = new ArrayList<>();
        List<BmsStockLocationDict> bmsStockLocationDictList = bmsStockLocationDictMapper.selectAllByUnitCode(unitCode);
        if (CollectionUtil.isNotEmpty(bmsStockLocationDictList)) {
            Map<String, List<BmsStockLocationDict>> bmsStockLocationDictMap = bmsStockLocationDictList.stream().collect(Collectors.groupingBy(BmsStockLocationDict::getStockCode));
            bmsStockLocationDictMap.forEach((stockCode, list) -> {
                BmsStockLocationQueryByUnitRspDTO bmsStockLocationQueryByUnitRspDTO = new BmsStockLocationQueryByUnitRspDTO();
                bmsStockLocationQueryByUnitRspDTO.setStockName(list.get(0).getStockName());
                bmsStockLocationQueryByUnitRspDTO.setStockCode(stockCode);
                bmsStockLocationQueryByUnitRspDTO.setStockLocationNumber(list.stream().map(BmsStockLocationDict::getLocationNumber).collect(Collectors.toList()));
                bmsStockLocationQueryByUnitRspDTOList.add(bmsStockLocationQueryByUnitRspDTO);
            });


        }
        return bmsStockLocationQueryByUnitRspDTOList;
    }

    @Override
    public PageInfo<BmsStockLocationListPageRspDTO> listPage(BmsStockLocationListPageReqDTO bmsStockLocationListPageReqDTO) {
        PageHelper.startPage(bmsStockLocationListPageReqDTO.getPageNum(), bmsStockLocationListPageReqDTO.getPageSize());
        List<BmsStockLocationDict> bmsStockLocationDictList = bmsStockLocationDictMapper.selectSelective(BeanUtils.copyProperties(bmsStockLocationListPageReqDTO, BmsStockLocationDict.class));
        PageInfo<BmsStockLocationDict> srcPageInfo = new PageInfo<>(bmsStockLocationDictList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, BmsStockLocationListPageRspDTO.class);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(BmsStockLocationAddReqDTO bmsStockLocationAddReqDTO) {
        BmsStockDict bmsStockDict = bmsStockDictMapper.selectOneByStockName(bmsStockLocationAddReqDTO.getStockName().trim());
        if (bmsStockDict == null) {
            bmsStockDict = new BmsStockDict();
            bmsStockDict.setStockName(bmsStockLocationAddReqDTO.getStockName());
            bmsStockDict.setStockCode(IdUtils.simpleUUID());
            bmsStockDict.setUnitCode(bmsStockLocationAddReqDTO.getUnitCode());
            bmsStockDict.setKdNumber(null);
            bmsStockDict.setCreateTime(new Date());
            bmsStockDict.setCreateUserId(SecurityContextHolder.getUserId());
            bmsStockDict.setCreateUserName(SecurityContextHolder.getUserName());
            bmsStockDictMapper.insert(bmsStockDict);
        } else {
            BmsStockLocationDict bmsStockLocationDict = bmsStockLocationDictMapper.selectOneByStockCodeAndLocationNumber(bmsStockDict.getStockCode(), bmsStockLocationAddReqDTO.getLocationNumber());
            if (bmsStockLocationDict != null) {
                throw new BusinessException("请不到重复添加库存");
            }
        }
        BmsStockLocationDict bmsStockLocationDict = new BmsStockLocationDict();
        bmsStockLocationDict.setUnitCode(bmsStockLocationAddReqDTO.getUnitCode());
        bmsStockLocationDict.setStockCode(bmsStockDict.getStockCode());
        bmsStockLocationDict.setStockName(bmsStockLocationAddReqDTO.getStockName());
        bmsStockLocationDict.setLocationNumber(bmsStockLocationAddReqDTO.getLocationNumber());
        bmsStockLocationDict.setCreateUserId(SecurityContextHolder.getUserId());
        bmsStockLocationDict.setCreateUserName(SecurityContextHolder.getNickName());
        bmsStockLocationDict.setCreateTime(new Date());
        bmsStockLocationDictMapper.insert(bmsStockLocationDict);
    }

    @Override
    public void delete(Integer id) {
        bmsStockLocationDictMapper.deleteById(id);
    }

    @Override
    public void edit(BmsStockLocationEditReqDTO bmsStockLocationEditReqDTO) {


    }

    @Override
    public List<BmsStockLocationListAllStockRspDTO> listAllStock() {
        List<BmsStockLocationListAllStockRspDTO> result = new ArrayList<>();
        List<BmsStockLocationDict> bmsStockLocationDictList = bmsStockLocationDictMapper.selectSelective(null);
        if (CollectionUtil.isNotEmpty(bmsStockLocationDictList)) {
            Map<String, List<BmsStockLocationDict>> stockMap = bmsStockLocationDictList.stream().collect(Collectors.groupingBy(BmsStockLocationDict::getStockCode));
            stockMap.forEach((stockCode, list) -> {
                BmsStockLocationListAllStockRspDTO bmsStockLocationListAllStockRspDTO = new BmsStockLocationListAllStockRspDTO();
                bmsStockLocationListAllStockRspDTO.setStockCode(stockCode);
                bmsStockLocationListAllStockRspDTO.setStockName(list.get(0).getStockName());
                result.add(bmsStockLocationListAllStockRspDTO);
            });


        }
        return result;
    }
}
