package com.bio.drqi.applet.service.codescan.template;

import cn.hutool.core.bean.BeanUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.applet.dto.rsp.ScanCodeSeedRspDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeTransformRspDTO;
import com.bio.drqi.applet.service.codescan.AbstractBaseCodeScanService;
import com.bio.drqi.applet.service.codescan.dto.SeedUniqueCodeDTO;
import com.bio.drqi.applet.service.codescan.dto.TransformUniqueCodeDTO;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    private CerVectorGroupTbMapper cerVectorGroupTbMapper;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;


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
        for (SeedStockTb seedStockTb:seedStockTbList){
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

        return scanCodeSeedRspDTO;
    }

    private List<SeedStockTb> findSeed(String seedNum, List<SeedStockTb> seedStockTbList) {
        if (seedNum == null) {
            return seedStockTbList;
        }
        SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedNum);
        if (seedStockTb != null) {
            seedStockTbList.add(seedStockTb);
            findSeed(seedNum, seedStockTbList);
        } else {
            return seedStockTbList;
        }
        return seedStockTbList;
    }


}


