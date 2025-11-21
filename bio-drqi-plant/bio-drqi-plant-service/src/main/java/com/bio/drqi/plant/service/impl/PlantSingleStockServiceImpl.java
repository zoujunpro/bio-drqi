package com.bio.drqi.plant.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.domain.PlantSingleStockTb;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import com.bio.drqi.mapper.PlantSingleStockTbMapper;
import com.bio.drqi.plant.req.PlantSingleStockListPageReqDTO;
import com.bio.drqi.plant.req.PlantSingleStockQueryListReqDTO;
import com.bio.drqi.plant.rsp.PlantSingleStockListPageRspDTO;
import com.bio.drqi.plant.rsp.PlantSingleStockQueryListRspDTO;
import com.bio.drqi.plant.service.PlantSingleStockService;
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
public class PlantSingleStockServiceImpl implements PlantSingleStockService {

    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Override
    public PageInfo<PlantSingleStockListPageRspDTO> listPage(PlantSingleStockListPageReqDTO plantSingleStockListPageReqDTO) {
        PageHelper.startPage(plantSingleStockListPageReqDTO.getPageNum(), plantSingleStockListPageReqDTO.getPageSize());
        List<PlantSingleStockTb> plantSingleStockTbList = plantSingleStockTbMapper.selectSelective(BeanUtils.copyProperties(plantSingleStockListPageReqDTO, PlantSingleStockTb.class));
        PageInfo<PlantSingleStockTb> srcPageInfo = new PageInfo<>(plantSingleStockTbList);
        PageInfo<PlantSingleStockListPageRspDTO> targetPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, PlantSingleStockListPageRspDTO.class);
        if (CollectionUtil.isNotEmpty(targetPageInfo.getList())) {
            Map<String, String> cerBreedDictMap = cerBreedDictMapper.selectAll().stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
            Map<String, String> cerSpeciesConfMap = cerSpeciesConfMapper.selectAll().stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName));
            targetPageInfo.getList().forEach(plantSingleStockListPageRspDTO -> {
                plantSingleStockListPageRspDTO.setBreedName(cerBreedDictMap.get(plantSingleStockListPageRspDTO.getBreedCode()));
                plantSingleStockListPageRspDTO.setSpeciesCode(cerSpeciesConfMap.get(plantSingleStockListPageRspDTO.getSpeciesCode()));
            });
        }
        return targetPageInfo;
    }

    @Override
    public List<PlantSingleStockQueryListRspDTO> queryList(PlantSingleStockQueryListReqDTO plantSingleStockListPageReqDTO) {
        List<PlantSingleStockTb> plantSingleStockTbList = plantSingleStockTbMapper.selectSelective(BeanUtils.copyProperties(plantSingleStockListPageReqDTO, PlantSingleStockTb.class));
        List<PlantSingleStockQueryListRspDTO> resultList = BeanUtils.copyListProperties(plantSingleStockTbList, PlantSingleStockQueryListRspDTO.class);
        if(CollectionUtil.isNotEmpty(resultList)){
            Map<String, String> cerBreedDictMap = cerBreedDictMapper.selectAll().stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
            Map<String, String> cerSpeciesConfMap = cerSpeciesConfMapper.selectAll().stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName));
            resultList.forEach(plantSingleStockQueryListRspDTO -> {
                plantSingleStockQueryListRspDTO.setBreedName(cerBreedDictMap.get(plantSingleStockQueryListRspDTO.getBreedCode()));
                plantSingleStockQueryListRspDTO.setSpeciesCode(cerSpeciesConfMap.get(plantSingleStockQueryListRspDTO.getSpeciesCode()));
            });
        }
        return resultList;

    }
}
