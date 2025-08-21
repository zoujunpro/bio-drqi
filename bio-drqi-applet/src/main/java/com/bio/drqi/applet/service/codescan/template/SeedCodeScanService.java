package com.bio.drqi.applet.service.codescan.template;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.applet.dto.rsp.ScanCodeSeedRspDTO;
import com.bio.drqi.applet.service.codescan.AbstractBaseCodeScanService;
import com.bio.drqi.applet.service.codescan.dto.SeedUniqueCodeDTO;
import com.bio.drqi.domain.*;
import com.bio.drqi.common.enums.BioDictTypeEnum;
import com.bio.drqi.common.enums.GenerationEnum;
import com.bio.drqi.mapper.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 转化扫码
 */
@Service
public class SeedCodeScanService extends AbstractBaseCodeScanService<SeedUniqueCodeDTO, ScanCodeSeedRspDTO> {

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private CerSampleTestTbMapper cerSampleTestTbMapper;

    @Resource
    private BioDictMapper bioDictMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;


    @Override
    public SeedUniqueCodeDTO parseUniqueCode(String uniqueCode) {
        String[] uniqueCodeArr = uniqueCode.split("\\|");
        SeedUniqueCodeDTO seedUniqueCodeDTO = new SeedUniqueCodeDTO();
        seedUniqueCodeDTO.setSeedNum(uniqueCodeArr[1]);
        return seedUniqueCodeDTO;
    }


    @Override
    public ScanCodeSeedRspDTO dealCodeContent(SeedUniqueCodeDTO seedUniqueCodeDTO) {
        ScanCodeSeedRspDTO scanCodeSeedRspDTO = new ScanCodeSeedRspDTO();
        List<BioDict> bioDictList = bioDictMapper.selectAll();
        List<CerSpeciesConf> cerSpeciesConfList = cerSpeciesConfMapper.selectList(null);
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        Map<String, CerBreedDict> cerBreedDictMap = cerBreedDictList.stream().collect(Collectors.toMap(cerBreedDict -> cerBreedDict.getSpeciesCode() + ":" + cerBreedDict.getBreedCode(), cerBreedDict -> cerBreedDict));
        Map<String, CerSpeciesConf> cerSpeciesConfMap = cerSpeciesConfList.stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, cerSpeciesConf -> cerSpeciesConf));
        Map<String, BioDict> bioDictMap = bioDictList.stream().collect(Collectors.toMap(bioDict -> bioDict.getDictType() + ":" + bioDict.getDictValueCode(), bioDict -> bioDict));
        List<SeedStockTb> seedStockTbList = findSeed(seedUniqueCodeDTO.getSeedNum(), new ArrayList<SeedStockTb>());
        Collections.reverse(seedStockTbList);
        for (SeedStockTb seedStockTb : seedStockTbList) {
            ScanCodeSeedRspDTO.Seed seed = new ScanCodeSeedRspDTO.Seed();
            seed.setPlantNum(seedStockTb.getPlantNum());
            seed.setSeedNum(seedStockTb.getSeedNum());
            seed.setParentNum(seedStockTb.getParentNum());
            seed.setFartherInfo(seedStockTb.getFartherInfo());
            seed.setMatherInfo(seedStockTb.getMatherInfo());
            seed.setGeneration(GenerationEnum.getGenerationDesc(seedStockTb.getGeneration()));
            seed.setSpeciesCode(seedStockTb.getSpeciesCode());
            seed.setSpeciesName(cerSpeciesConfMap.get(seedStockTb.getSpeciesCode()).getSpeciesName());
            seed.setBreedCode(seedStockTb.getBreedCode());
            seed.setBreedName(cerBreedDictMap.get(seed.getSpeciesCode()+":"+seedStockTb.getBreedCode()).getBreedName());
            seed.setPollinationMethod(seedStockTb.getPollinationMethod());
            seed.setHarvestType(seedStockTb.getHarvestType());
            if (StringUtils.isNotEmpty(seed.getHarvestType())) {
                seed.setHarvestName(bioDictMap.get(BioDictTypeEnum.HARVEST_TYPE.name() + ":" + seedStockTb.getHarvestType()).getDictValueName());
            }
            seed.setHarvestTime(seedStockTb.getHarvestTime());
            seed.setSeedNumber(seedStockTb.getSeedNumber());
            seed.setUnit(seedStockTb.getUnit());
            seed.setSourceType(seedStockTb.getSourceType());
            if (StringUtils.isNotEmpty(seed.getSourceType())) {
                seed.setSourceTypeName(bioDictMap.get(BioDictTypeEnum.SOURCE_CHANNEL.name() + ":" + seedStockTb.getSourceType()).getDictValueName());
            }
          //  seed.setProductionLocationName(seedStockTb.getProductionLocationName());
            seed.setStockLocationNum(seedStockTb.getStockLocationNum());
            seed.setSubmitUserId(seedStockTb.getSubmitUserId());
            seed.setSubmitUserName(seedStockTb.getSubmitUserName());
            seed.setCreateTime(seedStockTb.getCreateTime());
            seed.setUpdateTime(seedStockTb.getUpdateTime());
            seed.setRemarks(seedStockTb.getRemarks());
            seed.setTotalNumber(seedStockTb.getTotalNumber());
            seed.setGeneticCharacter(seedStockTb.getGeneticCharacter());
            seed.setAliasName(seedStockTb.getAliasName());
            seed.setGeneType(seedStockTb.getGeneType());
             if(seedStockTb.getMaterialType()!=null){
                 seed.setMaterialType(seedStockTb.getMaterialType());
                 seed.setMaterialTypeName(bioDictMap.get(BioDictTypeEnum.MATERIAL_TYPE.name() + ":" + seedStockTb.getMaterialType()).getDictValueName());
             }


            scanCodeSeedRspDTO.getSeedList().add(seed);
        }
        if (CollectionUtil.isNotEmpty(seedStockTbList)) {
            SeedStockTb firstSeed = seedStockTbList.get(0);
            if (StringUtils.isNotEmpty(firstSeed.getProjectCode()) && StringUtils.isNotEmpty(firstSeed.getSampleCode())) {
                CerSampleTestTb cerSampleTestTb = cerSampleTestTbMapper.selectOneByUniqueCode(firstSeed.getProjectCode() + firstSeed.getSampleCode());
                if (cerSampleTestTb != null) {
                    CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(cerSampleTestTb.getProjectCode());
                    CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectOneBySubProjectCode(cerSampleTestTb.getSubProjectCode());
                    CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(cerSampleTestTb.getVectorTaskCode());
                    CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(cerSampleTestTb.getTransformCode(), cerVectorTaskTb.getVectorTaskCode());
                    scanCodeSeedRspDTO.setProjectCode(cerProjectTb.getProjectCode());
                    scanCodeSeedRspDTO.setProjectName(cerProjectTb.getProjectName());
                    scanCodeSeedRspDTO.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
                    scanCodeSeedRspDTO.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
                    scanCodeSeedRspDTO.setVectorTaskName(cerVectorTaskTb.getVectorTaskName());
                    scanCodeSeedRspDTO.setPlasmidNames(cerTransformTb.getPlasmidName());
                    scanCodeSeedRspDTO.setTransformCode(cerTransformTb.getTransformCode());
                }
            }

        }


        return scanCodeSeedRspDTO;
    }

    private List<SeedStockTb> findSeed(String seedNum, List<SeedStockTb> seedStockTbList) {
        if (seedNum == null) {
            return seedStockTbList;
        }
        SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedNum);
        if (seedStockTb != null) {
            seedStockTbList.add(seedStockTb);
            findSeed(seedStockTb.getParentNum(), seedStockTbList);
        } else {
            return seedStockTbList;
        }
        return seedStockTbList;
    }


}


