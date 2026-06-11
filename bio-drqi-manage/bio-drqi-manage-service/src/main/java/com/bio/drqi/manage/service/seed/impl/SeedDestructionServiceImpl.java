package com.bio.drqi.manage.service.seed.impl;

import cn.hutool.json.JSONUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.manage.seed.SeedDestructionPageReqDTO;
import com.bio.drqi.manage.seed.SeedDestructionPageRspDTO;
import com.bio.drqi.domain.SeedStockDestructionLog;
import com.bio.drqi.manage.service.seed.SeedDestructionService;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import com.bio.drqi.mapper.SeedStockDestructionLogMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SeedDestructionServiceImpl implements SeedDestructionService {

    @Resource
    private SeedStockDestructionLogMapper seedStockDestructionLogMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;


    @Override
    public PageInfo<SeedDestructionPageRspDTO> listPage(SeedDestructionPageReqDTO seedDestructionPageReqDTO) {
        PageInfo<SeedDestructionPageRspDTO> pageInfo = new PageInfo<SeedDestructionPageRspDTO>();
        PageHelper.startPage(seedDestructionPageReqDTO.getPageNum(), seedDestructionPageReqDTO.getPageSize());
        SeedStockDestructionLog selectSeedStockDestructionLog = new SeedStockDestructionLog();
        selectSeedStockDestructionLog.setId(seedDestructionPageReqDTO.getId());
        selectSeedStockDestructionLog.setSeedNum(seedDestructionPageReqDTO.getSeedNum());
        selectSeedStockDestructionLog.setTaskNum(StringUtils.isNotEmpty(seedDestructionPageReqDTO.getApplyTaskNum()) ? seedDestructionPageReqDTO.getApplyTaskNum() : seedDestructionPageReqDTO.getTaskNum());
        selectSeedStockDestructionLog.setApplyUserId(seedDestructionPageReqDTO.getApplyUserId());
        selectSeedStockDestructionLog.setApplyUserName(seedDestructionPageReqDTO.getApplyUserName());
        selectSeedStockDestructionLog.setDestructionLocation(seedDestructionPageReqDTO.getDestructionLocation());
        selectSeedStockDestructionLog.setDestructionMethod(seedDestructionPageReqDTO.getDestructionMethod());
        selectSeedStockDestructionLog.setSourceType(seedDestructionPageReqDTO.getSourceType());
        selectSeedStockDestructionLog.setPlantCode(seedDestructionPageReqDTO.getPlantCode());
        selectSeedStockDestructionLog.setParentNum(seedDestructionPageReqDTO.getParentNum());
        selectSeedStockDestructionLog.setFatherInfo(seedDestructionPageReqDTO.getFatherInfo());
        selectSeedStockDestructionLog.setMatherInfo(seedDestructionPageReqDTO.getMatherInfo());
        selectSeedStockDestructionLog.setGeneration(seedDestructionPageReqDTO.getGeneration());
        selectSeedStockDestructionLog.setSpeciesCode(seedDestructionPageReqDTO.getSpeciesCode());
        selectSeedStockDestructionLog.setBreedCode(seedDestructionPageReqDTO.getBreedCode());
        selectSeedStockDestructionLog.setPollinationMethod(seedDestructionPageReqDTO.getPollinationMethod());
        selectSeedStockDestructionLog.setHarvestType(seedDestructionPageReqDTO.getHarvestType());
        selectSeedStockDestructionLog.setHarvestTime(seedDestructionPageReqDTO.getHarvestTime());
        selectSeedStockDestructionLog.setProductionLocationCode(seedDestructionPageReqDTO.getProductionLocationCode());
        selectSeedStockDestructionLog.setStockLocationNum(seedDestructionPageReqDTO.getStockLocationNum());
        selectSeedStockDestructionLog.setTargetCharacter(seedDestructionPageReqDTO.getTargetCharacter());
        selectSeedStockDestructionLog.setAliasName(seedDestructionPageReqDTO.getAliasName());
        selectSeedStockDestructionLog.setGeneType(seedDestructionPageReqDTO.getGeneType());
        selectSeedStockDestructionLog.setMaterialType(seedDestructionPageReqDTO.getMaterialType());
        selectSeedStockDestructionLog.setMatherSeedNum(seedDestructionPageReqDTO.getMatherSeedNum());
        selectSeedStockDestructionLog.setFatherSeedNum(seedDestructionPageReqDTO.getFatherSeedNum());
        selectSeedStockDestructionLog.setMatherRegionNum(seedDestructionPageReqDTO.getMatherRegionNum());
        selectSeedStockDestructionLog.setFatherRegionNum(seedDestructionPageReqDTO.getFatherRegionNum());
        selectSeedStockDestructionLog.setGenealogy(seedDestructionPageReqDTO.getGenealogy());
        selectSeedStockDestructionLog.setGeneSeparateFlag(seedDestructionPageReqDTO.getGeneSeparateFlag());
        selectSeedStockDestructionLog.setTransFlag(seedDestructionPageReqDTO.getTransFlag());
        selectSeedStockDestructionLog.setVectorTaskCode(seedDestructionPageReqDTO.getVectorTaskCode());
        selectSeedStockDestructionLog.setExperimentNum(seedDestructionPageReqDTO.getExperimentNum());
        selectSeedStockDestructionLog.setProjectCode(seedDestructionPageReqDTO.getProjectCode());
        selectSeedStockDestructionLog.setFatherSingleNum(seedDestructionPageReqDTO.getFatherSingleNum());
        selectSeedStockDestructionLog.setMatherSingleNum(seedDestructionPageReqDTO.getMatherSingleNum());
        selectSeedStockDestructionLog.setPdImplementCode(seedDestructionPageReqDTO.getPdImplementCode());
        selectSeedStockDestructionLog.setBeginDate(seedDestructionPageReqDTO.getBeginDate());
        selectSeedStockDestructionLog.setEndDate(seedDestructionPageReqDTO.getEndDate());
        List<SeedStockDestructionLog> seedStockDestructionLogList = seedStockDestructionLogMapper.selectSelective(selectSeedStockDestructionLog);
        PageInfo<SeedStockDestructionLog> srcPageInfo = new PageInfo<>(seedStockDestructionLogList);
        List<SeedDestructionPageRspDTO> seedDestructionPageRspDTOList = new ArrayList<>();
        Map<String, String> speciesMap = cerSpeciesConfMapper.selectList(null).stream()
                .collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName, (a, b) -> a));
        Map<String, String> cerBreedDictMap = cerBreedDictMapper.selectAll().stream()
                .collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName, (a, b) -> a));
        for (SeedStockDestructionLog seedStockDestructionLog : seedStockDestructionLogList) {
            SeedDestructionPageRspDTO seedDestructionPageRspDTO = new SeedDestructionPageRspDTO();
            seedDestructionPageRspDTO.setId(seedStockDestructionLog.getId());
            seedDestructionPageRspDTO.setDestructionLocation(seedStockDestructionLog.getDestructionLocation());
            seedDestructionPageRspDTO.setSeedNum(seedStockDestructionLog.getSeedNum());
            seedDestructionPageRspDTO.setDestructionMethod(seedStockDestructionLog.getDestructionMethod());
            seedDestructionPageRspDTO.setUnit(seedStockDestructionLog.getUnit());
            seedDestructionPageRspDTO.setSeedNumber(seedStockDestructionLog.getSeedNumber());
            seedDestructionPageRspDTO.setRemarks(seedStockDestructionLog.getRemarks());
            seedDestructionPageRspDTO.setApplyTaskNum(seedStockDestructionLog.getTaskNum());
            seedDestructionPageRspDTO.setApplyUserId(seedStockDestructionLog.getApplyUserId());
            seedDestructionPageRspDTO.setApplyUserName(seedStockDestructionLog.getApplyUserName());
            seedDestructionPageRspDTO.setDestructionEvidenceList(JSONUtil.toList(seedStockDestructionLog.getDestructionEvidence(), String.class));
            seedDestructionPageRspDTO.setDestructionDate(seedStockDestructionLog.getDestructionTime());
            seedDestructionPageRspDTO.setPlantCode(seedStockDestructionLog.getPlantCode());
            seedDestructionPageRspDTO.setParentNum(seedStockDestructionLog.getParentNum());
            seedDestructionPageRspDTO.setFatherInfo(seedStockDestructionLog.getFatherInfo());
            seedDestructionPageRspDTO.setMatherInfo(seedStockDestructionLog.getMatherInfo());
            seedDestructionPageRspDTO.setGeneration(seedStockDestructionLog.getGeneration());
            seedDestructionPageRspDTO.setSpeciesCode(seedStockDestructionLog.getSpeciesCode());
            seedDestructionPageRspDTO.setSpeciesName(speciesMap.get(seedStockDestructionLog.getSpeciesCode()));
            seedDestructionPageRspDTO.setBreedCode(seedStockDestructionLog.getBreedCode());
            seedDestructionPageRspDTO.setBreedName(cerBreedDictMap.get(seedStockDestructionLog.getBreedCode()));
            seedDestructionPageRspDTO.setPollinationMethod(seedStockDestructionLog.getPollinationMethod());
            seedDestructionPageRspDTO.setHarvestType(seedStockDestructionLog.getHarvestType());
            seedDestructionPageRspDTO.setHarvestTime(seedStockDestructionLog.getHarvestTime());
            seedDestructionPageRspDTO.setSourceType(seedStockDestructionLog.getSourceType());
            seedDestructionPageRspDTO.setProductionLocationCode(seedStockDestructionLog.getProductionLocationCode());
            seedDestructionPageRspDTO.setStockLocationNum(seedStockDestructionLog.getStockLocationNum());
            seedDestructionPageRspDTO.setTotalNumber(seedStockDestructionLog.getTotalNumber());
            seedDestructionPageRspDTO.setTargetCharacter(seedStockDestructionLog.getTargetCharacter());
            seedDestructionPageRspDTO.setAliasName(seedStockDestructionLog.getAliasName());
            seedDestructionPageRspDTO.setGeneType(seedStockDestructionLog.getGeneType());
            seedDestructionPageRspDTO.setMaterialType(seedStockDestructionLog.getMaterialType());
            seedDestructionPageRspDTO.setMatherSeedNum(seedStockDestructionLog.getMatherSeedNum());
            seedDestructionPageRspDTO.setFatherSeedNum(seedStockDestructionLog.getFatherSeedNum());
            seedDestructionPageRspDTO.setMatherRegionNum(seedStockDestructionLog.getMatherRegionNum());
            seedDestructionPageRspDTO.setFatherRegionNum(seedStockDestructionLog.getFatherRegionNum());
            seedDestructionPageRspDTO.setGenealogy(seedStockDestructionLog.getGenealogy());
            seedDestructionPageRspDTO.setGeneSeparateFlag(seedStockDestructionLog.getGeneSeparateFlag());
            seedDestructionPageRspDTO.setTransFlag(seedStockDestructionLog.getTransFlag());
            seedDestructionPageRspDTO.setVectorTaskCode(seedStockDestructionLog.getVectorTaskCode());
            seedDestructionPageRspDTO.setExperimentNum(seedStockDestructionLog.getExperimentNum());
            seedDestructionPageRspDTO.setProjectCode(seedStockDestructionLog.getProjectCode());
            seedDestructionPageRspDTO.setFatherSingleNum(seedStockDestructionLog.getFatherSingleNum());
            seedDestructionPageRspDTO.setMatherSingleNum(seedStockDestructionLog.getMatherSingleNum());
            seedDestructionPageRspDTO.setPdImplementCode(seedStockDestructionLog.getPdImplementCode());
            seedDestructionPageRspDTO.setStockBeforeNumber(seedStockDestructionLog.getStockBeforeNumber());
            seedDestructionPageRspDTO.setStockAfterNumber(seedStockDestructionLog.getStockAfterNumber());
            seedDestructionPageRspDTOList.add(seedDestructionPageRspDTO);
        }
        pageInfo.setList(seedDestructionPageRspDTOList);
        pageInfo.setTotal(srcPageInfo.getTotal());
        return pageInfo;
    }


}
