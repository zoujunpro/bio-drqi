package com.bio.drqi.applet.service.codescan.template;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.applet.dto.rsp.ScanCodeSeedRspDTO;
import com.bio.drqi.applet.service.codescan.AbstractBaseCodeScanService;
import com.bio.drqi.applet.service.codescan.dto.SeedUniqueCodeDTO;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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
        List<SeedStockTb> seedStockTbList = findSeed(seedUniqueCodeDTO.getSeedNum(), new ArrayList<SeedStockTb>());
        Collections.reverse(seedStockTbList);
        for (SeedStockTb seedStockTb : seedStockTbList) {
            ScanCodeSeedRspDTO.Seed seed = new ScanCodeSeedRspDTO.Seed();
            seed.setPlantNum(seedStockTb.getPlantNum());
            seed.setSeedNum(seedStockTb.getSeedNum());
            seed.setParentNum(seedStockTb.getParentNum());
            seed.setFartherInfo(seedStockTb.getFartherInfo());
            seed.setMatherInfo(seedStockTb.getMatherInfo());
            seed.setGeneration(seedStockTb.getGeneration());
            seed.setSpeciesCode(seedStockTb.getSpecies());
            seed.setSpeciesName(null);
            seed.setBreedCode(seedStockTb.getBreedCode());
            seed.setBreedName(null);
            seed.setPollinationMethod(seedStockTb.getPollinationMethod());
            seed.setSeedType(seedStockTb.getSeedType());
            seed.setHarvestType(seedStockTb.getHarvestType());
            seed.setHarvestTime(seedStockTb.getHarvestTime());
            seed.setSeedNumber(seedStockTb.getSeedNumber());
            seed.setUnit(seedStockTb.getUnit());
            seed.setSourceType(seedStockTb.getSourceType());
            seed.setProductionLocationName(seedStockTb.getProductionLocationName());
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
            seed.setMaterialType(seedStockTb.getMaterialType());
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
                    scanCodeSeedRspDTO.setSubProjectName(cerSubProjectTb.getSubProjectName());
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


