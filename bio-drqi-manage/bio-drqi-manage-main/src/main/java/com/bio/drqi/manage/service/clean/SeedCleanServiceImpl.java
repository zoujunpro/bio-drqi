package com.bio.drqi.manage.service.clean;

import cn.hutool.json.JSONUtil;
import com.bio.cer.domain.BioDict;
import com.bio.cer.domain.CerBreedDict;
import com.bio.cer.domain.CerSpeciesConf;
import com.bio.cer.domain.SeedStockTb;
import com.bio.cer.mapper.BioDictMapper;
import com.bio.cer.mapper.CerBreedDictMapper;
import com.bio.cer.mapper.CerSpeciesConfMapper;
import com.bio.cer.mapper.SeedStockTbMapper;
import com.bio.cer.service.DictInnerService;
import com.bio.cer.service.seed.SeedStockInService;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SeedCleanServiceImpl implements SeedCleanService {

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private DictInnerService dictInnerService;

    @Resource
    private BioDictMapper dictMapper;

    @Resource
    private SeedStockInService seedStockInService;







    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cleanSeedAndInsert(String filePath) {
        List<CleanSeedDataDTO> cleanSeedDataDTOList = ExcelUtil.readExcel(filePath, CleanSeedDataDTO.class);
        List<CerSpeciesConf> cerSpeciesConfList = cerSpeciesConfMapper.selectList(null);
        Map<String, CerSpeciesConf> cerSpeciesConfMap = cerSpeciesConfList.stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesName, cerSpeciesConf -> cerSpeciesConf));
        List<BioDict> bioDictList = dictMapper.selectAll();
        Map<String, BioDict> bioDictMap = bioDictList.stream().collect(Collectors.toMap(BioDict::getDictValueName, dict -> dict));
        System.out.println("共有数据=" + cleanSeedDataDTOList.size());
        for (CleanSeedDataDTO cleanSeedDataDTO : cleanSeedDataDTOList) {
            log.info("当前清洗={}",cleanSeedDataDTO);
            SeedStockTb seedStockTb = new SeedStockTb();
            CerSpeciesConf cerSpeciesConf = cerSpeciesConfMap.get(cleanSeedDataDTO.getSpecies());
            if (cerSpeciesConf == null) {

                throw new BusinessException(cleanSeedDataDTO.getSpecies() + "物种不存在");
            }
            CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedNameAndSpeciesCode(StringUtils.isEmpty(cleanSeedDataDTO.getBreedName())?"未知":cleanSeedDataDTO.getBreedName(),cerSpeciesConf.getSpeciesCode());
            if (cerBreedDict == null) {
                cerBreedDict = new CerBreedDict();
                cerBreedDict.setBreedCode(StringUtils.isEmpty(cleanSeedDataDTO.getBreedName())?"NONE":cleanSeedDataDTO.getBreedName());
                cerBreedDict.setBreedName(StringUtils.isEmpty(cleanSeedDataDTO.getBreedName())?"未知":cleanSeedDataDTO.getBreedName());
                cerBreedDict.setSpeciesCode(cerSpeciesConf.getSpeciesCode());
                System.out.println("**********="+JSONUtil.toJsonStr(cerBreedDict));
                cerBreedDictMapper.insert(cerBreedDict);
            }
            seedStockTb.setBreedCode(cerBreedDict.getBreedCode());

            seedStockTb.setPlantNum(cleanSeedDataDTO.getPlantNum());
            seedStockTb.setProjectCode(cleanSeedDataDTO.getProjectCode());
            seedStockTb.setFartherInfo(cleanSeedDataDTO.getFatherInfo());
            seedStockTb.setMatherInfo(cleanSeedDataDTO.getMatherInfo());
            seedStockTb.setGeneration(cleanSeedDataDTO.getGeneration());
            seedStockTb.setSpecies(cerSpeciesConf.getSpeciesCode());

            seedStockTb.setPollinationMethod(bioDictMap.get(cleanSeedDataDTO.getPollinationMethod())!=null?bioDictMap.get(cleanSeedDataDTO.getPollinationMethod()).getDictValueCode():cleanSeedDataDTO.getPollinationMethod());
            seedStockTb.setSeedType(bioDictMap.get(cleanSeedDataDTO.getSeedType()) != null ? bioDictMap.get(cleanSeedDataDTO.getSeedType()).getDictValueCode() : cleanSeedDataDTO.getSeedType());
            seedStockTb.setHarvestType(bioDictMap.get(cleanSeedDataDTO.getHarvestType()) != null ? bioDictMap.get(cleanSeedDataDTO.getHarvestType()).getDictValueCode() : cleanSeedDataDTO.getHarvestType());
            seedStockTb.setHarvestTime(cleanSeedDataDTO.getHarvestTime());
            seedStockTb.setSeedNumber(new BigDecimal(StringUtils.isEmpty(cleanSeedDataDTO.getSeedNumber()) ? "0" : cleanSeedDataDTO.getSeedNumber()));
            seedStockTb.setUnit(cleanSeedDataDTO.getUnit());
            seedStockTb.setParentNum(cleanSeedDataDTO.getParentNum());
            seedStockTb.setSourceType(bioDictMap.get(cleanSeedDataDTO.getSource()) != null ? bioDictMap.get(cleanSeedDataDTO.getSource()).getDictValueCode() : cleanSeedDataDTO.getSource());
            seedStockTb.setProductionLocationName(cleanSeedDataDTO.getProductAddress());
            seedStockTb.setMaterialType(bioDictMap.get(cleanSeedDataDTO.getMaterialType()) != null ? bioDictMap.get(cleanSeedDataDTO.getMaterialType()).getDictValueCode() : cleanSeedDataDTO.getMaterialType());
            seedStockTb.setAliasName(cleanSeedDataDTO.getAliasName());
            seedStockTb.setGeneType(cleanSeedDataDTO.getGeneType());
            seedStockTb.setCreateTime(new Date());
            seedStockTb.setUpdateTime(new Date());
            System.out.println("cleanSeedDataDTO.getTotalNumber()=" + cleanSeedDataDTO.getTotalNumber());
            if (cleanSeedDataDTO.getTotalNumber().endsWith("g")) {
                cleanSeedDataDTO.setTotalNumber(cleanSeedDataDTO.getTotalNumber().replace("g", ""));
            }
            if ("-".equals(cleanSeedDataDTO.getTotalNumber())) {
                cleanSeedDataDTO.setTotalNumber("0");
            }
            seedStockTb.setTotalNumber(new BigDecimal(StringUtils.isEmpty(cleanSeedDataDTO.getTotalNumber()) ? "0" : cleanSeedDataDTO.getTotalNumber()));
            if(seedStockTb.getSeedNumber().compareTo(new BigDecimal(0))==0){
                seedStockTb.setSeedNumber(seedStockTb.getTotalNumber());
            }
            seedStockTbMapper.insert(seedStockTb);
            seedStockTb.setSeedNum(cerSpeciesConf.getNumPrefix() + StringUtils.padl(String.valueOf(seedStockTb.getId()), 8, '0'));
            seedStockTbMapper.updateById(seedStockTb);
        }
        List<SeedStockTb> seedStockTbList = seedStockTbMapper.selectList(null);
        Map<String, List<SeedStockTb>> mapList = seedStockTbList.stream().filter(seedStockTb -> StringUtils.isNotEmpty(seedStockTb.getParentNum())).collect(Collectors.groupingBy(SeedStockTb::getParentNum));
        mapList.forEach((parentNun, list) -> {
            SeedStockTb seedStockTb = seedStockTbMapper.selectOneByPlantNum(parentNun);
            if (seedStockTb != null) {
                list.forEach(stock -> {
                    seedStockTbMapper.updateParentNumById(seedStockTb.getSeedNum(), stock.getId());
                });
            } else {
                list.forEach(stock -> {
                    seedStockTbMapper.updateParentNumById(null, stock.getId());
                });
            }
        });

    }

    @Override
    public void cleanSeedIn(String filePath) {

    }
}
