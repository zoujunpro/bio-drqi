package com.bio.drqi.manage.service.plant.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.enums.BioDictTypeEnum;
import com.bio.drqi.common.enums.PlantStatusEnum;
import com.bio.drqi.common.enums.SourceCodeEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.plant.req.PlantDtlListDetailReqDTO;
import com.bio.drqi.manage.plant.rsp.PlantDtlCountRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantDtlListDetailRspDTO;
import com.bio.drqi.mapper.*;
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

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;


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
                    if (pollinationMethodBioDict != null) {
                        plantSingleStockListPageRspDTO.setPollinationMethodName(pollinationMethodBioDict.getDictValueName());
                    }
                }
                if (StringUtils.isNotEmpty(plantSingleStockListPageRspDTO.getHarvestType())) {
                    BioDict pollinationMethodBioDict = bioDictMap.get(BioDictTypeEnum.HARVEST_TYPE + ":" + plantSingleStockListPageRspDTO.getHarvestType());
                    if (pollinationMethodBioDict != null) {
                        plantSingleStockListPageRspDTO.setHarvestTypeName(pollinationMethodBioDict.getDictValueName());
                    }


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

    @Override
    public PageInfo<PlantDtlListDetailRspDTO> listByVectorTaskIdDetail(PlantDtlListDetailReqDTO plantDtlListDetailReqDTO) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(plantDtlListDetailReqDTO.getVectorTaskId());
        if(cerVectorTaskTb==null){
            throw new BusinessException("找不到实施方案信息");
        }
        PageHelper.startPage(plantDtlListDetailReqDTO.getPageNum(), plantDtlListDetailReqDTO.getPageSize());
        List<PlantSingleStockTb> plantSingleStockTbList = plantSingleStockTbMapper.selectSelective(PlantSingleStockTb.builder().plantStatus(plantDtlListDetailReqDTO.getPlantStatus()).vectorTaskCode(cerVectorTaskTb.getVectorTaskCode()).sourceCode(SourceCodeEnum.project.name()).build());
        PageInfo<PlantSingleStockTb> srcPageInfo = new PageInfo<>(plantSingleStockTbList);
        PageInfo<PlantDtlListDetailRspDTO> result = BeanUtils.copyPageInfoProperties(srcPageInfo, PlantDtlListDetailRspDTO.class);
        if (CollectionUtil.isNotEmpty(result.getList())) {
            List<BioDict> bioDictList = bioDictMapper.selectAll();
            Map<String, BioDict> bioDictMap = bioDictList.stream().collect(Collectors.toMap(bioDict -> bioDict.getDictType() + ":" + bioDict.getDictValueCode(), bioDict -> bioDict));
            result.getList().forEach(plantDtlListRspDTO -> {
                if (StringUtils.isNotEmpty(plantDtlListRspDTO.getPollinationMethod())) {
                    BioDict pollinationMethodBioDict = bioDictMap.get(BioDictTypeEnum.POLLINATE_TYPE + ":" + plantDtlListRspDTO.getPollinationMethod());
                    if (pollinationMethodBioDict == null) {
                        throw new BusinessException("授粉方式填写错误：" + plantDtlListRspDTO.getPollinationMethod());
                    }
                    plantDtlListRspDTO.setPollinationMethodName(pollinationMethodBioDict.getDictValueName());
                }
            });
        }
        return result;
    }

    @Override
    public PlantDtlCountRspDTO count(String vectorTaskCode) {
        PlantDtlCountRspDTO plantDtlCountRspDTO = new PlantDtlCountRspDTO();
        List<PlantSingleStockTb> plantSingleStockTbList = plantSingleStockTbMapper.selectCountGroupByPlantStatus(vectorTaskCode,SourceCodeEnum.project.name());
        for (PlantSingleStockTb plantSingleStockTb : plantSingleStockTbList) {
            if (PlantStatusEnum.STATUS_1.code.equals(plantSingleStockTb.getPlantStatus())) {
                plantDtlCountRspDTO.setNormalCountNum(plantSingleStockTb.getCountNum());
            } else if (PlantStatusEnum.STATUS_2.code.equals(plantSingleStockTb.getPlantStatus())) {
                plantDtlCountRspDTO.setAbnormalCountNum(plantSingleStockTb.getCountNum());
            } else if (PlantStatusEnum.STATUS_3.code.equals(plantSingleStockTb.getPlantStatus())) {
                plantDtlCountRspDTO.setDeleteCountNum(plantSingleStockTb.getCountNum());
            } else if (PlantStatusEnum.STATUS_4.code.equals(plantSingleStockTb.getPlantStatus())) {
                plantDtlCountRspDTO.setHarvestCountNum(plantSingleStockTb.getCountNum());
            } else if (PlantStatusEnum.STATUS_5.code.equals(plantSingleStockTb.getPlantStatus())) {
                plantDtlCountRspDTO.setDeathCountNum(plantSingleStockTb.getCountNum());
            } else if (PlantStatusEnum.STATUS_6.code.equals(plantSingleStockTb.getPlantStatus())) {
                plantDtlCountRspDTO.setReproductivePeriodCountNum(plantSingleStockTb.getCountNum());
            }
        }
        return plantDtlCountRspDTO.buildTotalCountNum();
    }

}
