package com.bio.drqi.manage.service.seed.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.manage.base.PrintRspDTO;
import com.bio.drqi.enums.BioDictTypeEnum;
import com.bio.drqi.enums.GenerationEnum;
import com.bio.drqi.enums.SeedMaterialTypeEnum;
import com.bio.drqi.manage.print.SeedInPrintReqDTO;
import com.bio.drqi.manage.print.SeedOutPrintReqDTO;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.service.DictInnerService;
import com.bio.drqi.manage.service.seed.SeedPrintService;
import com.bio.drqi.mapper.*;
import com.bio.print.SeedInLabelPrintDTO;
import com.bio.print.SeedOutLabelPrintDTO;
import com.bio.print.api.PrintApi;
import com.bio.print.req.PrintDataReqDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class SeedPrintServiceImpl implements SeedPrintService {

    @Resource
    private SeedStockOutLogMapper seedStockOutLogMapper;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private SeedStockInLogMapper seedStockInLogMapper;

    @Resource
    private PrintApi printApi;

    @Resource
    private DictInnerService dictInnerService;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Override
    public List<PrintRspDTO> seedOutLabelPrint(SeedOutPrintReqDTO seedOutPrintReqDTO) {
        List<PrintRspDTO> printRspDTOList = new ArrayList<>();
        List<SeedOutLabelPrintDTO> defaultSeedOutLabelPrintDTOList = new ArrayList<>();
        List<SeedOutLabelPrintDTO> yellowSeedOutLabelPrintDTOList = new ArrayList<>();
        List<SeedOutLabelPrintDTO> blueSeedOutLabelPrintDTOList = new ArrayList<>();
        List<SeedOutPrintReqDTO.Content> contentList = seedOutPrintReqDTO.getContentList();
        for (SeedOutPrintReqDTO.Content content : contentList) {
            SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(content.getSeedNum());
            CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectOneBySpeciesCode(seedStockTb.getSpecies());
            CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedCodeAndSpeciesCode(seedStockTb.getBreedCode(), seedStockTb.getSpecies());
            SeedOutLabelPrintDTO seedOutLabelPrintDTO = new SeedOutLabelPrintDTO();
            seedOutLabelPrintDTO.setPrintNum(content.getPrintNum());
            seedOutLabelPrintDTO.setSeedNum(seedStockTb.getSeedNum());
            seedOutLabelPrintDTO.setSpeciesName(cerSpeciesConf.getSpeciesName());
            seedOutLabelPrintDTO.setBreedName(cerBreedDict.getBreedName());
            seedOutLabelPrintDTO.setOutNumber(content.getNum());
            seedOutLabelPrintDTO.setUnit(seedStockTb.getUnit());
            seedOutLabelPrintDTO.setHarvestTypeName(seedStockTb.getHarvestType());
            seedOutLabelPrintDTO.setProjectCode(seedStockTb.getProjectCode());
            if (StringUtils.isNotEmpty(seedStockTb.getHarvestType())) {
                seedOutLabelPrintDTO.setHarvestTypeName(dictInnerService.findByDictTypeAndDictValueCode(BioDictTypeEnum.HARVEST_TYPE, seedStockTb.getHarvestType()).getDictValueName());
            }
            if (content.getId() == null) {
                BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(content.getTaskNum());
                seedOutLabelPrintDTO.setOutTime(DateUtil.format(new Date(), DatePattern.NORM_DATE_FORMAT));
                seedOutLabelPrintDTO.setApplyUserName(bioTaskDtlTb.getApplyUserName());
                seedOutLabelPrintDTO.setTaskNum(bioTaskDtlTb.getTaskNum());
            } else {
                SeedStockOutLog seedStockOutLog = seedStockOutLogMapper.selectById(content.getId());
                seedOutLabelPrintDTO.setOutTime(DateUtil.format(seedStockOutLog.getCreateTime(), DatePattern.NORM_DATE_FORMAT));
                seedOutLabelPrintDTO.setApplyUserName(seedStockOutLog.getApplyUserName());
                seedOutLabelPrintDTO.setTaskNum(seedStockOutLog.getTaskNum());
            }
            if (SeedMaterialTypeEnum.getSeedMaterialTypeEnumByType(seedStockTb.getMaterialType()).equals(SeedMaterialTypeEnum.TYPE_1)) {
                yellowSeedOutLabelPrintDTOList.add(seedOutLabelPrintDTO);
            } else if (SeedMaterialTypeEnum.getSeedMaterialTypeEnumByType(seedStockTb.getMaterialType()).equals(SeedMaterialTypeEnum.TYPE_2)) {
                blueSeedOutLabelPrintDTOList.add(seedOutLabelPrintDTO);
            } else {
                defaultSeedOutLabelPrintDTOList.add(seedOutLabelPrintDTO);
            }
        }
        if (CollectionUtil.isNotEmpty(yellowSeedOutLabelPrintDTOList)) {
            PrintRspDTO printRspDTO = new PrintRspDTO();
            printRspDTO.setPrintName(SeedMaterialTypeEnum.TYPE_1.printName);
            printRspDTO.setPrintDataList(printDataSave("seed_out_label_print", yellowSeedOutLabelPrintDTOList));
            printRspDTOList.add(printRspDTO);
        }
        if (CollectionUtil.isNotEmpty(blueSeedOutLabelPrintDTOList)) {
            PrintRspDTO printRspDTO = new PrintRspDTO();
            printRspDTO.setPrintName(SeedMaterialTypeEnum.TYPE_2.printName);
            printRspDTO.setPrintDataList(printDataSave("seed_out_label_print", blueSeedOutLabelPrintDTOList));
            printRspDTOList.add(printRspDTO);
        }
        if (CollectionUtil.isNotEmpty(defaultSeedOutLabelPrintDTOList)) {
            PrintRspDTO printRspDTO = new PrintRspDTO();
            printRspDTO.setPrintName(SeedMaterialTypeEnum.TYPE_3.printName);
            printRspDTO.setPrintDataList(printDataSave("seed_out_label_print", defaultSeedOutLabelPrintDTOList));
            printRspDTOList.add(printRspDTO);
        }


        return printRspDTOList;
    }

    @Override
    public List<PrintRspDTO> seedInLabelPrint(SeedInPrintReqDTO seedInPrintReqDTO) {
        List<PrintRspDTO> printRspDTOList = new ArrayList<>();
        List<SeedInLabelPrintDTO> defaultSeedInLabelPrintDTOList = new ArrayList<>();
        List<SeedInLabelPrintDTO> yellowSeedInLabelPrintDTOList = new ArrayList<>();
        List<SeedInLabelPrintDTO> blueSeedInLabelPrintDTOList = new ArrayList<>();
        List<SeedInPrintReqDTO.Content> contentList = seedInPrintReqDTO.getContentList();
        for (SeedInPrintReqDTO.Content content : contentList) {

            SeedStockInLog seedStockInLog = null;
            if (content.getId() != null) {
                seedStockInLog = seedStockInLogMapper.selectById(content.getId());
            } else {
                seedStockInLog = seedStockInLogMapper.selectOneByUniqueCode(content.getUniqueCode());
            }

            SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedStockInLog.getSeedNum());
            CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectOneBySpeciesCode(seedStockTb.getSpecies());
            CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedCodeAndSpeciesCode(seedStockTb.getBreedCode(), seedStockTb.getSpecies());
            SeedInLabelPrintDTO seedInLabelPrintDTO = new SeedInLabelPrintDTO();
            seedInLabelPrintDTO.setPrintNum(content.getPrintNum());
            seedInLabelPrintDTO.setSeedNum(seedStockTb.getSeedNum());
            seedInLabelPrintDTO.setProjectCode(StringUtils.isEmpty(seedStockTb.getProjectCode()) ? "无" : seedStockTb.getProjectCode());
            seedInLabelPrintDTO.setSpeciesName(cerSpeciesConf.getSpeciesName());
            seedInLabelPrintDTO.setBreedName(cerBreedDict.getBreedName());
            seedInLabelPrintDTO.setPlantNum(StringUtils.isEmpty(seedStockTb.getPlantNum()) ? "N/A" : seedStockTb.getPlantNum());
            seedInLabelPrintDTO.setGeneration(GenerationEnum.getGenerationDesc(seedStockTb.getGeneration()));
            seedInLabelPrintDTO.setTaskNum(seedStockInLog.getTaskNum());
            if (StringUtils.isNotEmpty(seedStockTb.getHarvestType())) {
                seedInLabelPrintDTO.setHarvestTypeName(dictInnerService.findByDictTypeAndDictValueCode(BioDictTypeEnum.HARVEST_TYPE, seedStockTb.getHarvestType()).getDictValueName());
            }
            if (StringUtils.isNotEmpty(seedStockTb.getSourceType())) {
                seedInLabelPrintDTO.setSourceTypeName(dictInnerService.findByDictTypeAndDictValueCode(BioDictTypeEnum.SOURCE_CHANNEL, seedStockTb.getSourceType()).getDictValueName());
            }
            seedInLabelPrintDTO.setHarvestTime(StringUtils.isEmpty(seedStockTb.getHarvestTime()) ? "N/A" : seedStockTb.getHarvestTime());
            seedInLabelPrintDTO.setProductionLocationName(seedStockTb.getProductionLocationName());
            if (SeedMaterialTypeEnum.getSeedMaterialTypeEnumByType(seedStockTb.getMaterialType()).equals(SeedMaterialTypeEnum.TYPE_1)) {
                yellowSeedInLabelPrintDTOList.add(seedInLabelPrintDTO);
            } else if (SeedMaterialTypeEnum.getSeedMaterialTypeEnumByType(seedStockTb.getMaterialType()).equals(SeedMaterialTypeEnum.TYPE_2)) {
                blueSeedInLabelPrintDTOList.add(seedInLabelPrintDTO);
            } else {
                defaultSeedInLabelPrintDTOList.add(seedInLabelPrintDTO);
            }
        }

        if (CollectionUtil.isNotEmpty(yellowSeedInLabelPrintDTOList)) {
            PrintRspDTO printRspDTO = new PrintRspDTO();
            printRspDTO.setPrintName(SeedMaterialTypeEnum.TYPE_1.printName);
            printRspDTO.setPrintDataList(printDataSave("seed_in_label_print", yellowSeedInLabelPrintDTOList));
            printRspDTOList.add(printRspDTO);
        }
        if (CollectionUtil.isNotEmpty(blueSeedInLabelPrintDTOList)) {
            PrintRspDTO printRspDTO = new PrintRspDTO();
            printRspDTO.setPrintName(SeedMaterialTypeEnum.TYPE_2.printName);
            printRspDTO.setPrintDataList(printDataSave("seed_in_label_print", blueSeedInLabelPrintDTOList));
            printRspDTOList.add(printRspDTO);
        }
        if (CollectionUtil.isNotEmpty(defaultSeedInLabelPrintDTOList)) {
            PrintRspDTO printRspDTO = new PrintRspDTO();
            printRspDTO.setPrintName(SeedMaterialTypeEnum.TYPE_3.printName);
            printRspDTO.setPrintDataList(printDataSave("seed_in_label_print", defaultSeedInLabelPrintDTOList));
            printRspDTOList.add(printRspDTO);
        }
        return printRspDTOList;
    }

    private List<String> printDataSave(String printType, Object printData) {
        PrintDataReqDTO printDataReqDTO = new PrintDataReqDTO();
        printDataReqDTO.setPrintType(printType);
        printDataReqDTO.setPrintData(JSONUtil.toJsonStr(printData));
        ResponseResult<List<String>> responseResult = printApi.printDataSave(printDataReqDTO);
        if (responseResult.isError()) {
            throw new BusinessException(responseResult.getMessage());
        }
        return responseResult.getData();
    }
}
