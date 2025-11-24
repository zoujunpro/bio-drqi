package com.bio.drqi.manage.service.plant.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.common.enums.SourceCodeEnum;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.domain.PlantMultipleStockTb;
import com.bio.drqi.manage.plant.req.PlantMultipleStockListPageReqDTO;
import com.bio.drqi.manage.plant.req.PlantMultipleStockQueryListForTaskReqDTO;
import com.bio.drqi.manage.plant.rsp.PlantMultipleStockListPageRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantMultipleStockQueryListForTaskRspDTO;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import com.bio.drqi.mapper.PlantMultipleStockTbMapper;
import com.bio.drqi.manage.plant.req.PlantMultipleStockQueryListReqDTO;
import com.bio.drqi.manage.plant.rsp.PlantMultipleStockQueryListRspDTO;
import com.bio.drqi.manage.service.plant.PlantMultipleStockService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PlantMultipleStockServiceImpl implements PlantMultipleStockService {


    @Resource
    private PlantMultipleStockTbMapper plantMultipleStockTbMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Override
    public PageInfo<PlantMultipleStockListPageRspDTO> listPage(PlantMultipleStockListPageReqDTO plantMultipleStockListPageReqDTO) {
        PageHelper.startPage(plantMultipleStockListPageReqDTO.getPageNum(), plantMultipleStockListPageReqDTO.getPageSize());
        List<PlantMultipleStockTb> plantMultipleStockTbList = plantMultipleStockTbMapper.selectSelective(BeanUtils.copyProperties(plantMultipleStockListPageReqDTO, PlantMultipleStockTb.class));
        PageInfo<PlantMultipleStockTb> srcPageInfo = new PageInfo<>(plantMultipleStockTbList);
        PageInfo<PlantMultipleStockListPageRspDTO> targetPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, PlantMultipleStockListPageRspDTO.class);
        if (CollectionUtil.isNotEmpty(targetPageInfo.getList())) {
            Map<String, String> cerBreedDictMap = cerBreedDictMapper.selectAll().stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
            Map<String, String> cerSpeciesConfMap = cerSpeciesConfMapper.selectAll().stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName));
            targetPageInfo.getList().forEach(plantMultipleStockListPageRspDTO -> {
                plantMultipleStockListPageRspDTO.setBreedName(cerBreedDictMap.get(plantMultipleStockListPageRspDTO.getBreedCode()));
                plantMultipleStockListPageRspDTO.setSpeciesCode(cerSpeciesConfMap.get(plantMultipleStockListPageRspDTO.getSpeciesCode()));
            });
        }
        return targetPageInfo;
    }

    @Override
    public List<PlantMultipleStockQueryListRspDTO> queryList(PlantMultipleStockQueryListReqDTO plantMultipleStockQueryListReqDTO) {
        List<PlantMultipleStockTb> plantMultipleStockTbList = plantMultipleStockTbMapper.selectSelective(BeanUtils.copyProperties(plantMultipleStockQueryListReqDTO, PlantMultipleStockTb.class));
        List<PlantMultipleStockQueryListRspDTO> targetList = BeanUtils.copyListProperties(plantMultipleStockTbList, PlantMultipleStockQueryListRspDTO.class);
        if (CollectionUtil.isNotEmpty(targetList)) {
            Map<String, String> cerBreedDictMap = cerBreedDictMapper.selectAll().stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
            Map<String, String> cerSpeciesConfMap = cerSpeciesConfMapper.selectAll().stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName));
            targetList.forEach(plantMultipleStockQueryListRspDTO -> {
                plantMultipleStockQueryListRspDTO.setBreedName(cerBreedDictMap.get(plantMultipleStockQueryListRspDTO.getBreedCode()));
                plantMultipleStockQueryListRspDTO.setSpeciesCode(cerSpeciesConfMap.get(plantMultipleStockQueryListRspDTO.getSpeciesCode()));
            });
        }
        return targetList;
    }

    @Override
    public List<PlantMultipleStockQueryListForTaskRspDTO> queryListForTask(PlantMultipleStockQueryListForTaskReqDTO plantMultipleStockQueryListForTaskReqDTO) {
        List<PlantMultipleStockQueryListForTaskRspDTO> result=new ArrayList<>();
        if (SourceCodeEnum.project.name().equals(plantMultipleStockQueryListForTaskReqDTO.getSourceCode())) {
            List<String> vectorTaskCodeList = plantMultipleStockTbMapper.selectVectorTaskCodeBySourceCodeAndSpeciesCode(plantMultipleStockQueryListForTaskReqDTO.getSourceCode(), plantMultipleStockQueryListForTaskReqDTO.getSpeciesCode());
            if (CollectionUtil.isNotEmpty(vectorTaskCodeList)) {
                vectorTaskCodeList.forEach(vectorTaskCode -> {
                    List<PlantMultipleStockTb> plantMultipleStockTbList = plantMultipleStockTbMapper.selectSelective(PlantMultipleStockTb.builder().vectorTaskCode(vectorTaskCode).sourceCode(plantMultipleStockQueryListForTaskReqDTO.getSourceCode()).speciesCode(plantMultipleStockQueryListForTaskReqDTO.getSpeciesCode()).build());
                    PlantMultipleStockQueryListForTaskRspDTO.PlantMultipleStockQueryListForProjectTaskRspDTO plantMultipleStockQueryListForProjectTaskRspDTO = new PlantMultipleStockQueryListForTaskRspDTO.PlantMultipleStockQueryListForProjectTaskRspDTO();
                    plantMultipleStockQueryListForProjectTaskRspDTO.setVectorTaskCode(vectorTaskCode);
                    plantMultipleStockQueryListForProjectTaskRspDTO.setPlantMultipleStockDTOList(BeanUtils.copyToList(plantMultipleStockTbList, PlantMultipleStockQueryListForTaskRspDTO.PlantMultipleStockDTO.class));
                    result.add(plantMultipleStockQueryListForProjectTaskRspDTO);
                });
            }
        }else if(SourceCodeEnum.cer.name().equals(plantMultipleStockQueryListForTaskReqDTO.getSourceCode())){
            List<String> regionCodeList = plantMultipleStockTbMapper.selectRegionNumBySourceCodeAndSpeciesCode(plantMultipleStockQueryListForTaskReqDTO.getSourceCode(), plantMultipleStockQueryListForTaskReqDTO.getSpeciesCode());
            if (CollectionUtil.isNotEmpty(regionCodeList)) {
               ;
                regionCodeList.forEach(regionCode -> {
                    List<PlantMultipleStockTb> plantMultipleStockTbList = plantMultipleStockTbMapper.selectSelective(PlantMultipleStockTb.builder().regionNum(regionCode).sourceCode(plantMultipleStockQueryListForTaskReqDTO.getSourceCode()).speciesCode(plantMultipleStockQueryListForTaskReqDTO.getSpeciesCode()).build());
                    PlantMultipleStockQueryListForTaskRspDTO.PlantMultipleStockQueryListForCerTaskRspDTO plantMultipleStockQueryListForCerTaskRspDTO = new PlantMultipleStockQueryListForTaskRspDTO.PlantMultipleStockQueryListForCerTaskRspDTO();
                    plantMultipleStockQueryListForCerTaskRspDTO.setRegionMum(regionCode);
                    plantMultipleStockQueryListForCerTaskRspDTO.setPlantMultipleStockDTOList(BeanUtils.copyToList(plantMultipleStockTbList, PlantMultipleStockQueryListForTaskRspDTO.PlantMultipleStockDTO.class));
                    result.add(plantMultipleStockQueryListForCerTaskRspDTO);
                });
            }
        }
        return result;
    }
}
