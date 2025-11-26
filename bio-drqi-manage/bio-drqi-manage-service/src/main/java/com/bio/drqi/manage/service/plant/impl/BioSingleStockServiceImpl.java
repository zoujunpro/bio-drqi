package com.bio.drqi.manage.service.plant.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.enums.BioDictTypeEnum;
import com.bio.drqi.domain.BioDict;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.domain.PlantSingleStockTb;
import com.bio.drqi.mapper.BioDictMapper;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import com.bio.drqi.mapper.PlantSingleStockTbMapper;
import com.bio.drqi.manage.plant.req.PlantSingleStockListPageReqDTO;
import com.bio.drqi.manage.plant.req.PlantSingleStockQueryListReqDTO;
import com.bio.drqi.manage.plant.rsp.PlantSingleStockListPageRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantSingleStockQueryListRspDTO;
import com.bio.drqi.manage.service.plant.PlantSingleStockService;
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
public class BioSingleStockServiceImpl implements PlantSingleStockService {

    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private BioDictMapper bioDictMapper;


    @Override
    public PageInfo<PlantSingleStockListPageRspDTO> listPage(PlantSingleStockListPageReqDTO plantSingleStockListPageReqDTO) {
        PageHelper.startPage(plantSingleStockListPageReqDTO.getPageNum(), plantSingleStockListPageReqDTO.getPageSize());
        List<PlantSingleStockTb> plantSingleStockTbList = plantSingleStockTbMapper.selectSelective(BeanUtils.copyProperties(plantSingleStockListPageReqDTO, PlantSingleStockTb.class));
        PageInfo<PlantSingleStockTb> srcPageInfo = new PageInfo<>(plantSingleStockTbList);
        PageInfo<PlantSingleStockListPageRspDTO> targetPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, PlantSingleStockListPageRspDTO.class);
        if (CollectionUtil.isNotEmpty(targetPageInfo.getList())) {
            List<BioDict> bioDictList = bioDictMapper.selectAll();
            Map<String, BioDict> bioDictMap = bioDictList.stream().collect(Collectors.toMap(bioDict -> bioDict.getDictType() + ":" + bioDict.getDictValueCode(), bioDict -> bioDict));
            Map<String, String> cerBreedDictMap = cerBreedDictMapper.selectAll().stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
            Map<String, String> cerSpeciesConfMap = cerSpeciesConfMapper.selectAll().stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName));
            targetPageInfo.getList().forEach(plantSingleStockListPageRspDTO -> {
                plantSingleStockListPageRspDTO.setBreedName(cerBreedDictMap.get(plantSingleStockListPageRspDTO.getBreedCode()));
                plantSingleStockListPageRspDTO.setSpeciesName(cerSpeciesConfMap.get(plantSingleStockListPageRspDTO.getSpeciesCode()));
                if (StringUtils.isNotEmpty(plantSingleStockListPageRspDTO.getPollinationMethod())) {
                    BioDict pollinationMethodBioDict = bioDictMap.get(BioDictTypeEnum.POLLINATE_TYPE + ":" + plantSingleStockListPageRspDTO.getPollinationMethod());
                    if (pollinationMethodBioDict == null) {
                        throw new BusinessException("授粉方式填写错误：" + plantSingleStockListPageRspDTO.getPollinationMethod());
                    }
                    plantSingleStockListPageRspDTO.setPollinationMethodName(pollinationMethodBioDict.getDictValueName());

                }
                if (StringUtils.isNotEmpty(plantSingleStockListPageRspDTO.getHarvestType())) {
                    BioDict pollinationMethodBioDict = bioDictMap.get(BioDictTypeEnum.HARVEST_TYPE + ":" + plantSingleStockListPageRspDTO.getHarvestType());
                    if (pollinationMethodBioDict == null) {
                        throw new BusinessException("收获方式填写错误：" + plantSingleStockListPageRspDTO.getHarvestType());
                    }
                    plantSingleStockListPageRspDTO.setHarvestTypeName(pollinationMethodBioDict.getDictValueName());

                }
            });
        }
        return targetPageInfo;
    }

    @Override
    public List<PlantSingleStockQueryListRspDTO> queryList(PlantSingleStockQueryListReqDTO plantSingleStockListPageReqDTO) {
        List<PlantSingleStockTb> plantSingleStockTbList = plantSingleStockTbMapper.selectSelective(BeanUtils.copyProperties(plantSingleStockListPageReqDTO, PlantSingleStockTb.class));
        List<PlantSingleStockQueryListRspDTO> resultList = BeanUtils.copyListProperties(plantSingleStockTbList, PlantSingleStockQueryListRspDTO.class);
        if (CollectionUtil.isNotEmpty(resultList)) {
            Map<String, String> cerBreedDictMap = cerBreedDictMapper.selectAll().stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
            Map<String, String> cerSpeciesConfMap = cerSpeciesConfMapper.selectAll().stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName));
            resultList.forEach(plantSingleStockQueryListRspDTO -> {
                plantSingleStockQueryListRspDTO.setBreedName(cerBreedDictMap.get(plantSingleStockQueryListRspDTO.getBreedCode()));
                plantSingleStockQueryListRspDTO.setSpeciesName(cerSpeciesConfMap.get(plantSingleStockQueryListRspDTO.getSpeciesCode()));
            });
        }
        return resultList;

    }


}
