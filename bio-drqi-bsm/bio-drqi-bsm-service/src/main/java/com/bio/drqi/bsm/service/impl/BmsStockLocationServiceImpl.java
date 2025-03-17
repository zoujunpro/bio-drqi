package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.drqi.bsm.rsp.BmsStockLocationQueryByUnitRspDTO;
import com.bio.drqi.bsm.service.BmsStockLocationService;
import com.bio.drqi.domain.BmsStockLocationDict;
import com.bio.drqi.mapper.BmsStockLocationDictMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BmsStockLocationServiceImpl implements BmsStockLocationService {

    @Resource
    private BmsStockLocationDictMapper bmsStockLocationDictMapper;

    @Override
    public List<BmsStockLocationQueryByUnitRspDTO> queryByUnit(String unitCode) {
        List<BmsStockLocationQueryByUnitRspDTO> bmsStockLocationQueryByUnitRspDTOList=new ArrayList<>();
        List<BmsStockLocationDict> bmsStockLocationDictList = bmsStockLocationDictMapper.selectAllByUnitCode(unitCode);
        if(CollectionUtil.isNotEmpty(bmsStockLocationDictList)){
         Map<String,List<BmsStockLocationDict>> bmsStockLocationDictMap=   bmsStockLocationDictList.stream().collect(Collectors.groupingBy(BmsStockLocationDict::getStockCode));
            bmsStockLocationDictMap.forEach((stockCode,list)->{
                BmsStockLocationQueryByUnitRspDTO bmsStockLocationQueryByUnitRspDTO=new BmsStockLocationQueryByUnitRspDTO();
                bmsStockLocationQueryByUnitRspDTO.setStockName(list.get(0).getStockName());
                bmsStockLocationQueryByUnitRspDTO.setStockCode(stockCode);
                bmsStockLocationQueryByUnitRspDTO.setStockLocationNumber(list.stream().map(BmsStockLocationDict::getLocaltionNumber).collect(Collectors.toList()));
                bmsStockLocationQueryByUnitRspDTOList.add(bmsStockLocationQueryByUnitRspDTO);
            });



        }
        return bmsStockLocationQueryByUnitRspDTOList;
    }
}
