package com.bio.drqi.manage.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.*;
import com.bio.drqi.manage.dto.project.VectorTaskAddDTO;
import com.bio.drqi.manage.service.DictInnerService;
import com.bio.drqi.manage.util.LetterUtil;
import com.bio.drqi.mapper.*;
import com.bio.print.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("test")
@Slf4j
public class TestCleanController {

    @Resource
    private BioPrintTransformTbMapper bioPrintTransformTbMapper;

    @Resource
    private BioPrintVectorTbMapper bioPrintVectorTbMapper;

    @Resource
    private BioPrintSampleTbMapper bioPrintSampleTbMapper;

    @Resource
    private BioPrintLayoutTbMapper bioPrintLayoutTbMapper;

    @Resource
    private BioSeedLabelTbMapper bioSeedLabelTbMapper;

    @Resource
    private BioPrintLabelInfoTbMapper bioPrintLabelInfoTbMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerSampleTestTbMapper cerSampleTestTbMapper;

    @Resource
    private SeedStockInLogMapper seedStockInLogMapper;

    @Resource
    private SeedStockOutLogMapper seedStockOutLogMapper;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private DictInnerService dictInnerService;

    @Resource
    private CerSampleCodePrefixTbMapper cerSampleCodePrefixTbMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private CerVectorTaskPlanLogMapper cerVectorTaskPlanLogMapper;

    @Resource
    private CerConversionAndTransRefMapper cerConversionAndTransRefMapper;


    @Resource
    private CerPlantDtlTbMapper cerPlantDtlTbMapper;

    @Resource
    private CerVectorStepLogMapper cerVectorStepLogMapper;


    @GetMapping("/cleanVectorStep")
    public ResponseResult<String> cleanVectorStep() {

        List<CerPlantDtlTb> cerPlantDtlTbList = cerPlantDtlTbMapper.selectSelective(null);
        for (CerPlantDtlTb cerPlantDtlTb : cerPlantDtlTbList) {
                CerVectorStepLog cerVectorStepLog = cerVectorStepLogMapper.selectOneByVectorTaskIdAndStepCode(cerPlantDtlTb.getVectorTaskId(), ImplementationPlanTypeEnum.cer_plant.name());
                if(cerVectorStepLog==null){

                    cerVectorStepLog=new CerVectorStepLog();
                    cerVectorStepLog.setProjectId(cerPlantDtlTb.getProjectId());
                    cerVectorStepLog.setVectorTaskId(cerPlantDtlTb.getVectorTaskId());
                    cerVectorStepLog.setStepCode(ImplementationPlanTypeEnum.cer_plant.name());
                    cerVectorStepLog.setCreateTime(new Date());
                    cerVectorStepLog.setTaskNum(cerPlantDtlTb.getTaskNum());
                    cerVectorStepLogMapper.insert(cerVectorStepLog);
                    log.info("插入step={}",cerVectorStepLog);
                }
        }
        return ResponseResult.getSuccess("ok");
    }


    @GetMapping("/cleanTrans")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanTrans() {
        List<CerConversionAndTransRef> cerConversionAndTransRefList = cerConversionAndTransRefMapper.selectList(null);
        for (CerConversionAndTransRef cerConversionAndTransRef : cerConversionAndTransRefList) {
            if (StringUtils.isNotEmpty(cerConversionAndTransRef.getSampleCode())) {
                CerPlantDtlTb cerPlantDtlTb = cerPlantDtlTbMapper.selectOneByPlantCodeAndVectorTaskCode(cerConversionAndTransRef.getSampleCode(), cerConversionAndTransRef.getVectorTaskCode());
                if (cerPlantDtlTb == null) {
                    CerSampleTestTb cerSampleTestTb = cerSampleTestTbMapper.selectOneByVectorTaskCodeAndSampleCodeFirst(cerConversionAndTransRef.getVectorTaskCode(), cerConversionAndTransRef.getSampleCode());
                    if (cerSampleTestTb != null) {
                        cerPlantDtlTb = CerPlantDtlTb.of(cerSampleTestTb, SecurityContextHolder.getUserId(), SecurityContextHolder.getNickName(), cerSampleTestTb.getApplyNo());
                        log.info("插入数据={}", cerPlantDtlTb);
                        cerPlantDtlTb.setPlantCode(cerSampleTestTb.getSampleCode());
                        cerPlantDtlTb.setPlantStatus(PlantStatusEnum.STATUS_1.code);
                        cerPlantDtlTbMapper.insert(cerPlantDtlTb);
                    }

                }
            } else if (StringUtils.isNotEmpty(cerConversionAndTransRef.getTransformCode())) {
                List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllByTransformCode(cerConversionAndTransRef.getTransformCode());
                for (CerSampleTestTb cerSampleTestTb : cerSampleTestTbList) {
                    CerPlantDtlTb cerPlantDtlTb = cerPlantDtlTbMapper.selectOneByPlantCodeAndVectorTaskCode(cerSampleTestTb.getSampleCode(), cerSampleTestTb.getVectorTaskCode());
                    if (cerPlantDtlTb == null) {
                        cerPlantDtlTb = CerPlantDtlTb.of(cerSampleTestTb, SecurityContextHolder.getUserId(), cerSampleTestTb.getApplyUserName(), cerSampleTestTb.getApplyNo());
                        log.info("插入数据={}", cerPlantDtlTb);
                        cerPlantDtlTb.setPlantCode(cerSampleTestTb.getSampleCode());
                        cerPlantDtlTb.setPlantStatus(PlantStatusEnum.STATUS_1.code);
                        cerPlantDtlTbMapper.insert(cerPlantDtlTb);
                    }

                }

            }
        }
        return ResponseResult.getSuccess("ok");
    }


    @GetMapping("cleanPrint")
    @Transactional(rollbackFor = Exception.class)
    public String cleanPrint() {
        log.info("清洗开始");
        //转化标签
        List<BioPrintTransformTb> bioPrintTransformTbList = bioPrintTransformTbMapper.selectList(null);
        for (BioPrintTransformTb bioPrintTransformTb : bioPrintTransformTbList) {
            log.info("转化标签清洗 bioPrintTransformTb={}", bioPrintTransformTb.getTransformCode());
            BioPrintLabelInfoTb bioPrintLabelInfoTb = bioPrintLabelInfoTbMapper.selectById(bioPrintTransformTb.getPrintId());
            if (bioPrintLabelInfoTb == null) {
                continue;
            }
            CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(bioPrintTransformTb.getTransformCode(), bioPrintTransformTb.getVectorTaskCode());
            if (cerTransformTb == null) {
                continue;
            }
            bioPrintLabelInfoTb.setUniqueCode(bioPrintTransformTb.getVectorTaskCode() + "|" + bioPrintTransformTb.getTransformCode());
            bioPrintLabelInfoTb.setPrintCode(StringUtils.padl(String.valueOf(bioPrintLabelInfoTb.getId()), 10, '0'));

            TransFormPrintData transFormPrintData = new TransFormPrintData();
            transFormPrintData.setProjectCode(cerTransformTb.getProjectCode());
            transFormPrintData.setVectorTaskCode(bioPrintTransformTb.getVectorTaskCode());
            transFormPrintData.setTransFormCode(bioPrintTransformTb.getTransformCode());
            transFormPrintData.setPlasmidName(cerTransformTb.getPlasmidName());
            transFormPrintData.setPrintNum(1);
            transFormPrintData.setUniqueCode(bioPrintLabelInfoTb.getUniqueCode());
            transFormPrintData.setPrintId(bioPrintLabelInfoTb.getPrintCode());
            transFormPrintData.setTaskNum(cerTransformTb.getTaskNum());

            bioPrintLabelInfoTb.setLabelText(JSONUtil.toJsonStr(transFormPrintData));
            bioPrintLabelInfoTbMapper.updateById(bioPrintLabelInfoTb);
        }

        //载体标签
        List<BioPrintVectorTb> bioPrintVectorTbList = bioPrintVectorTbMapper.selectList(null);
        for (BioPrintVectorTb bioPrintVectorTb : bioPrintVectorTbList) {
            log.info("载体标签清洗 bioPrintVectorTb={}", bioPrintVectorTb.getPlasmidName());
            BioPrintLabelInfoTb bioPrintLabelInfoTb = bioPrintLabelInfoTbMapper.selectById(bioPrintVectorTb.getPrintId());
            if (bioPrintLabelInfoTb == null) {
                continue;
            }
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(bioPrintVectorTb.getVectorTaskCode());
            if (cerVectorTaskTb == null) {
                continue;
            }
            VectorPrintData vectorPrintData = new VectorPrintData();
            vectorPrintData.setVectorTaskCode(bioPrintVectorTb.getVectorTaskCode());
            vectorPrintData.setPlasmidName(bioPrintVectorTb.getPlasmidName());
            vectorPrintData.setCapacity(bioPrintVectorTb.getCapacity());
            vectorPrintData.setConcentration(bioPrintVectorTb.getConcentration());
            vectorPrintData.setUniqueCode(vectorPrintData.getVectorTaskCode() + "|" + vectorPrintData.getPlasmidName());
            vectorPrintData.setPrintId(StringUtils.padl(String.valueOf(bioPrintLabelInfoTb.getId()), 10, '0'));
            vectorPrintData.setTaskNum(cerVectorTaskTb.getTaskNum());

            bioPrintLabelInfoTb.setUniqueCode(bioPrintVectorTb.getVectorTaskCode() + "|" + bioPrintVectorTb.getPlasmidName());
            bioPrintLabelInfoTb.setPrintCode(StringUtils.padl(String.valueOf(bioPrintLabelInfoTb.getId()), 10, '0'));
            bioPrintLabelInfoTb.setLabelText(JSONUtil.toJsonStr(vectorPrintData));
            bioPrintLabelInfoTbMapper.updateById(bioPrintLabelInfoTb);

        }
        //取样标签
        List<BioPrintSampleTb> bioPrintSampleTbList = bioPrintSampleTbMapper.selectList(null);
        for (BioPrintSampleTb bioPrintSampleTb : bioPrintSampleTbList) {
            log.info("取样标签清洗 bioPrintSampleTb={}", bioPrintSampleTb.getSampleCode());
            BioPrintLabelInfoTb bioPrintLabelInfoTb = bioPrintLabelInfoTbMapper.selectById(bioPrintSampleTb.getPrintId());
            if (bioPrintLabelInfoTb == null) {
                continue;
            }

            CerSampleTestTb cerSampleTestTb = cerSampleTestTbMapper.selectOneByVectorTaskCodeAndSampleCodeFirst(bioPrintSampleTb.getVectorTaskCode(), bioPrintSampleTb.getSampleCode());
            if (cerSampleTestTb == null) {
                continue;
            }

            SamplePrintData samplePrintData = new SamplePrintData();
            samplePrintData.setVectorTaskCode(bioPrintSampleTb.getVectorTaskCode());
            samplePrintData.setPlasmidName(bioPrintSampleTb.getPlasmidName());
            samplePrintData.setTransformCode(bioPrintSampleTb.getTransformCode());
            samplePrintData.setSampleCode(bioPrintSampleTb.getSampleCode());
            samplePrintData.setUniqueCode(bioPrintSampleTb.getVectorTaskCode() + "|" + bioPrintSampleTb.getSampleCode());
            samplePrintData.setPrintId(StringUtils.padl(String.valueOf(bioPrintLabelInfoTb.getId()), 10, '0'));
            samplePrintData.setTaskNum(cerSampleTestTb.getApplyNo());

            bioPrintLabelInfoTb.setLabelText(JSONUtil.toJsonStr(samplePrintData));
            bioPrintLabelInfoTb.setPrintCode(StringUtils.padl(String.valueOf(bioPrintLabelInfoTb.getId()), 10, '0'));
            bioPrintLabelInfoTb.setUniqueCode(bioPrintSampleTb.getVectorTaskCode() + "|" + bioPrintSampleTb.getSampleCode());
            bioPrintLabelInfoTbMapper.updateById(bioPrintLabelInfoTb);
        }


        //种子入库出库标签
        List<BioSeedLabelTb> bioSeedLabelTbList = bioSeedLabelTbMapper.selectList(null);
        for (BioSeedLabelTb bioSeedLabelTb : bioSeedLabelTbList) {
            log.info("种子入库出库标签清洗 bioSeedLabelTb={}", bioSeedLabelTb.getSeedNum());
            BioPrintLabelInfoTb bioPrintLabelInfoTb = bioPrintLabelInfoTbMapper.selectById(bioSeedLabelTb.getPrintId());
            if (bioPrintLabelInfoTb == null) {
                continue;
            }
            if ("seed_in_label_print".equals(bioSeedLabelTb.getSeedLabelType())) {
                SeedStockInLog seedStockInLog = seedStockInLogMapper.selectOneBySeedNum(bioSeedLabelTb.getSeedNum());
                if (seedStockInLog == null) {
                    continue;
                }
                SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedStockInLog.getSeedNum());
                CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectOneBySpeciesCode(seedStockTb.getSpecies());
                CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedCodeAndSpeciesCode(seedStockTb.getBreedCode(), seedStockTb.getSpecies());
                SeedInLabelPrintDTO seedInLabelPrintDTO = new SeedInLabelPrintDTO();
                seedInLabelPrintDTO.setPrintNum(1);
                seedInLabelPrintDTO.setSeedNum(seedStockInLog.getSeedNum());
                seedInLabelPrintDTO.setProjectCode(seedStockTb.getProjectCode());
                seedInLabelPrintDTO.setSpeciesName(cerSpeciesConf.getSpeciesName());
                seedInLabelPrintDTO.setBreedName(cerBreedDict.getBreedName());
                seedInLabelPrintDTO.setPlantNum(seedStockTb.getPlantNum());
                seedInLabelPrintDTO.setGeneration(GenerationEnum.getGenerationDesc(seedStockTb.getGeneration()));
                if (StringUtils.isNotEmpty(seedStockTb.getHarvestType())) {
                    seedInLabelPrintDTO.setHarvestTypeName(dictInnerService.findByDictTypeAndDictValueCode(BioDictTypeEnum.HARVEST_TYPE, seedStockTb.getHarvestType()).getDictValueName());
                }
                if (StringUtils.isNotEmpty(seedStockTb.getSourceType())) {
                    seedInLabelPrintDTO.setSourceTypeName(dictInnerService.findByDictTypeAndDictValueCode(BioDictTypeEnum.SOURCE_CHANNEL, seedStockTb.getSourceType()).getDictValueName());
                }
                seedInLabelPrintDTO.setProductionLocationName(seedStockTb.getProductionLocationName());
                seedInLabelPrintDTO.setHarvestTime(StringUtils.isEmpty(seedStockTb.getHarvestTime()) ? "N/A" : seedStockTb.getHarvestTime());
                seedInLabelPrintDTO.setUniqueCode(seedInLabelPrintDTO.getTaskNum() + "|" + seedInLabelPrintDTO.getSeedNum());
                seedInLabelPrintDTO.setPrintId(StringUtils.padl(String.valueOf(bioPrintLabelInfoTb.getId()), 10, '0'));
                seedInLabelPrintDTO.setTaskNum(seedStockInLog.getTaskNum());

                bioPrintLabelInfoTb.setLabelText(JSONUtil.toJsonStr(seedInLabelPrintDTO));
                bioPrintLabelInfoTb.setPrintCode(StringUtils.padl(String.valueOf(bioPrintLabelInfoTb.getId()), 10, '0'));
                bioPrintLabelInfoTb.setUniqueCode(seedInLabelPrintDTO.getTaskNum() + "|" + seedInLabelPrintDTO.getSeedNum());

            } else if ("seed_out_label_print".equals(bioSeedLabelTb.getSeedLabelType())) {
                List<SeedStockOutLog> seedStockOutLogList = seedStockOutLogMapper.selectAllBySeedNum(bioSeedLabelTb.getSeedNum());
                if (CollectionUtil.isEmpty(seedStockOutLogList)) {
                    continue;
                }
                SeedStockOutLog seedStockOutLog = seedStockOutLogList.get(0);
                SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedStockOutLog.getSeedNum());
                CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectOneBySpeciesCode(seedStockTb.getSpecies());
                CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedCodeAndSpeciesCode(seedStockTb.getBreedCode(), seedStockTb.getSpecies());
                SeedOutLabelPrintDTO seedOutLabelPrintDTO = new SeedOutLabelPrintDTO();
                seedOutLabelPrintDTO.setPrintNum(1);
                seedOutLabelPrintDTO.setSeedNum(seedStockOutLog.getSeedNum());
                seedOutLabelPrintDTO.setSpeciesName(cerSpeciesConf.getSpeciesName());
                seedOutLabelPrintDTO.setBreedName(cerBreedDict.getBreedName());
                seedOutLabelPrintDTO.setOutNumber(seedStockOutLog.getSeedNumber() + "");
                seedOutLabelPrintDTO.setUnit(seedStockTb.getUnit());
                BioDict bioDict = dictInnerService.findByDictTypeAndDictValueCode(BioDictTypeEnum.HARVEST_TYPE, seedStockTb.getHarvestType());
                if (bioDict != null) {
                    seedOutLabelPrintDTO.setHarvestTypeName(bioDict.getDictValueName());
                }
                seedOutLabelPrintDTO.setOutTime(DateUtil.format(seedStockOutLog.getCreateTime(), "yyyy-MM-dd"));
                seedOutLabelPrintDTO.setApplyUserName(seedStockOutLog.getApplyUserName());
                seedOutLabelPrintDTO.setProjectCode(seedStockTb.getProjectCode());
                seedOutLabelPrintDTO.setUniqueCode(seedStockOutLog.getTaskNum() + "|" + seedStockOutLog.getSeedNum());
                seedOutLabelPrintDTO.setPrintId(StringUtils.padl(String.valueOf(bioPrintLabelInfoTb.getId()), 10, '0'));
                seedOutLabelPrintDTO.setTaskNum(seedStockOutLog.getTaskNum());
                bioPrintLabelInfoTb.setLabelText(JSONUtil.toJsonStr(seedOutLabelPrintDTO));
                bioPrintLabelInfoTb.setPrintCode(StringUtils.padl(String.valueOf(bioPrintLabelInfoTb.getId()), 10, '0'));
                bioPrintLabelInfoTb.setUniqueCode(seedOutLabelPrintDTO.getTaskNum() + "|" + seedOutLabelPrintDTO.getSeedNum());
            }

            bioPrintLabelInfoTbMapper.updateById(bioPrintLabelInfoTb);
        }


        return "ok";
    }


    @GetMapping("/testBioSeedLabelTb")
    public String testBioSeedLabelTb() {
        List<BioPrintLabelInfoTb> bioPrintLabelInfoTbList = bioPrintLabelInfoTbMapper.searchAllByLabelType("seed_in_label_print");
        log.info("开始");
        for (BioPrintLabelInfoTb bioPrintLabelInfoTb : bioPrintLabelInfoTbList) {
            if (StringUtils.isEmpty(bioPrintLabelInfoTb.getLabelText())) {
                log.info("bioPrintLabelInfoTb={}", bioPrintLabelInfoTb.getId());
                if ("sample_small_label_print".equals(bioPrintLabelInfoTb.getLabelType())) {
                    String[] uniqueArr = bioPrintLabelInfoTb.getUniqueCode().split("\\|");
                    CerSampleTestTb cerSampleTestTb = cerSampleTestTbMapper.selectOneByVectorTaskCodeAndSampleCodeFirst(uniqueArr[0], uniqueArr[1]);
                    if (cerSampleTestTb != null) {
                        SamplePrintData samplePrintData = new SamplePrintData();
                        samplePrintData.setVectorTaskCode(cerSampleTestTb.getVectorTaskCode());
                        samplePrintData.setPlasmidName(cerSampleTestTb.getPlasmidName());
                        samplePrintData.setTransformCode(cerSampleTestTb.getTransformCode());
                        samplePrintData.setSampleCode(cerSampleTestTb.getSampleCode());
                        samplePrintData.setUniqueCode(cerSampleTestTb.getVectorTaskCode() + "|" + cerSampleTestTb.getSampleCode());
                        samplePrintData.setPrintId(StringUtils.padl(String.valueOf(bioPrintLabelInfoTb.getId()), 10, '0'));
                        samplePrintData.setTaskNum(cerSampleTestTb.getApplyNo());
                        bioPrintLabelInfoTb.setLabelText(JSONUtil.toJsonStr(samplePrintData));
                        bioPrintLabelInfoTb.setPrintCode(StringUtils.padl(String.valueOf(bioPrintLabelInfoTb.getId()), 10, '0'));
                        bioPrintLabelInfoTbMapper.updateById(bioPrintLabelInfoTb);
                    }

                } else if ("seed_out_label_print".equals(bioPrintLabelInfoTb.getLabelType())) {
                    String[] uniques = bioPrintLabelInfoTb.getUniqueCode().split("\\|");
                    List<SeedStockOutLog> seedStockOutLogList = seedStockOutLogMapper.selectAllBySeedNum(uniques[1]);
                    if (CollectionUtil.isEmpty(seedStockOutLogList) || seedStockOutLogList.size() == 1) {
                        continue;
                    }
                    SeedStockOutLog seedStockOutLog = seedStockOutLogList.get(1);
                    SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedStockOutLog.getSeedNum());
                    CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectOneBySpeciesCode(seedStockTb.getSpecies());
                    CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedCodeAndSpeciesCode(seedStockTb.getBreedCode(), seedStockTb.getSpecies());
                    SeedOutLabelPrintDTO seedOutLabelPrintDTO = new SeedOutLabelPrintDTO();
                    seedOutLabelPrintDTO.setPrintNum(1);
                    seedOutLabelPrintDTO.setSeedNum(seedStockOutLog.getSeedNum());
                    seedOutLabelPrintDTO.setSpeciesName(cerSpeciesConf.getSpeciesName());
                    seedOutLabelPrintDTO.setBreedName(cerBreedDict.getBreedName());
                    seedOutLabelPrintDTO.setOutNumber(seedStockOutLog.getSeedNumber() + "");
                    seedOutLabelPrintDTO.setUnit(seedStockTb.getUnit());
                    BioDict bioDict = dictInnerService.findByDictTypeAndDictValueCode(BioDictTypeEnum.HARVEST_TYPE, seedStockTb.getHarvestType());
                    if (bioDict != null) {
                        seedOutLabelPrintDTO.setHarvestTypeName(bioDict.getDictValueName());
                    }
                    seedOutLabelPrintDTO.setOutTime(DateUtil.format(seedStockOutLog.getCreateTime(), "yyyy-MM-dd"));
                    seedOutLabelPrintDTO.setApplyUserName(seedStockOutLog.getApplyUserName());
                    seedOutLabelPrintDTO.setProjectCode(seedStockTb.getProjectCode());
                    seedOutLabelPrintDTO.setUniqueCode(seedStockOutLog.getTaskNum() + "|" + seedStockOutLog.getSeedNum());
                    seedOutLabelPrintDTO.setPrintId(StringUtils.padl(String.valueOf(bioPrintLabelInfoTb.getId()), 10, '0'));
                    seedOutLabelPrintDTO.setTaskNum(seedStockOutLog.getTaskNum());
                    bioPrintLabelInfoTb.setLabelText(JSONUtil.toJsonStr(seedOutLabelPrintDTO));
                    bioPrintLabelInfoTb.setPrintCode(StringUtils.padl(String.valueOf(bioPrintLabelInfoTb.getId()), 10, '0'));
                    bioPrintLabelInfoTb.setUniqueCode(seedOutLabelPrintDTO.getTaskNum() + "|" + seedOutLabelPrintDTO.getSeedNum());
                    BioPrintLabelInfoTb bioPrintLabelInfoTb1 = bioPrintLabelInfoTbMapper.selectOneByUniqueCode(bioPrintLabelInfoTb.getUniqueCode());
                    if (bioPrintLabelInfoTb1 == null) {
                        bioPrintLabelInfoTbMapper.updateById(bioPrintLabelInfoTb);
                    } else {

                    }


                } else if ("seed_in_label_print".equals(bioPrintLabelInfoTb.getLabelType())) {
                    String[] uniques = bioPrintLabelInfoTb.getUniqueCode().split("\\|");
                    SeedStockInLog seedStockInLog = seedStockInLogMapper.selectOneBySeedNum(uniques[1]);
                    if (seedStockInLog == null) {
                        continue;
                    }
                    SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedStockInLog.getSeedNum());
                    CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectOneBySpeciesCode(seedStockTb.getSpecies());
                    CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedCodeAndSpeciesCode(seedStockTb.getBreedCode(), seedStockTb.getSpecies());
                    SeedInLabelPrintDTO seedInLabelPrintDTO = new SeedInLabelPrintDTO();
                    seedInLabelPrintDTO.setPrintNum(1);
                    seedInLabelPrintDTO.setSeedNum(seedStockInLog.getSeedNum());
                    seedInLabelPrintDTO.setProjectCode(seedStockTb.getProjectCode());
                    seedInLabelPrintDTO.setSpeciesName(cerSpeciesConf.getSpeciesName());
                    seedInLabelPrintDTO.setBreedName(cerBreedDict.getBreedName());
                    seedInLabelPrintDTO.setPlantNum(seedStockTb.getPlantNum());
                    seedInLabelPrintDTO.setGeneration(GenerationEnum.getGenerationDesc(seedStockTb.getGeneration()));
                    if (StringUtils.isNotEmpty(seedStockTb.getHarvestType())) {
                        seedInLabelPrintDTO.setHarvestTypeName(dictInnerService.findByDictTypeAndDictValueCode(BioDictTypeEnum.HARVEST_TYPE, seedStockTb.getHarvestType()).getDictValueName());
                    }
                    if (StringUtils.isNotEmpty(seedStockTb.getSourceType())) {
                        seedInLabelPrintDTO.setSourceTypeName(dictInnerService.findByDictTypeAndDictValueCode(BioDictTypeEnum.SOURCE_CHANNEL, seedStockTb.getSourceType()).getDictValueName());
                    }
                    seedInLabelPrintDTO.setProductionLocationName(seedStockTb.getProductionLocationName());
                    seedInLabelPrintDTO.setHarvestTime(StringUtils.isEmpty(seedStockTb.getHarvestTime()) ? "N/A" : seedStockTb.getHarvestTime());
                    seedInLabelPrintDTO.setUniqueCode(seedInLabelPrintDTO.getTaskNum() + "|" + seedInLabelPrintDTO.getSeedNum());
                    seedInLabelPrintDTO.setPrintId(StringUtils.padl(String.valueOf(bioPrintLabelInfoTb.getId()), 10, '0'));
                    seedInLabelPrintDTO.setTaskNum(seedStockInLog.getTaskNum());

                    bioPrintLabelInfoTb.setLabelText(JSONUtil.toJsonStr(seedInLabelPrintDTO));
                    bioPrintLabelInfoTb.setPrintCode(StringUtils.padl(String.valueOf(bioPrintLabelInfoTb.getId()), 10, '0'));
                    bioPrintLabelInfoTb.setUniqueCode(seedInLabelPrintDTO.getTaskNum() + "|" + seedInLabelPrintDTO.getSeedNum());
                    BioPrintLabelInfoTb bioPrintLabelInfoTb1 = bioPrintLabelInfoTbMapper.selectOneByUniqueCode(bioPrintLabelInfoTb.getUniqueCode());
                    if (bioPrintLabelInfoTb1 == null) {
                        bioPrintLabelInfoTbMapper.updateById(bioPrintLabelInfoTb);
                    } else {

                    }
                }
            }

        }
        log.info("结束");
        return "ok";
    }

    @GetMapping("cleanSampleCodePrefix")
    @Transactional(rollbackFor = Exception.class)
    public String cleanSampleCodePrefix() {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectList(null);
        for (CerVectorTaskTb cerVectorTaskTb : cerVectorTaskTbList) {
            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerVectorTaskTb.getTaskNum());
            log.info("bioTaskDtlTb={}", JSONUtil.toJsonStr(bioTaskDtlTb));
            VectorTaskAddDTO vectorTaskAddDTO = null;
            try {
                vectorTaskAddDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), VectorTaskAddDTO.class);
            } catch (Exception e) {
                continue;
            }
            CerSampleCodePrefixTb cerSampleCodePrefixTb = cerSampleCodePrefixTbMapper.selectOneByVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
            if (cerSampleCodePrefixTb == null) {
                //生成sampleCodePrefix
                String sampleCodePrefix = createSampleCode();
                cerSampleCodePrefixTb = new CerSampleCodePrefixTb();
                cerSampleCodePrefixTb.setSampleCodePrefix(sampleCodePrefix);
                cerSampleCodePrefixTb.setVectorTaskCode(vectorTaskAddDTO.getVectorTaskCode());
                cerSampleCodePrefixTb.setCreateTime(new Date());
                cerSampleCodePrefixTb.setCurrentIndex(100);
                try {
                    cerSampleCodePrefixTbMapper.insert(cerSampleCodePrefixTb);
                } catch (DuplicateKeyException e) {
                    throw new BusinessException("取样编号前缀重复：" + cerSampleCodePrefixTb.getSampleCodePrefix());
                }
            }
            vectorTaskAddDTO.setSampleCodePrefix(cerSampleCodePrefixTb.getSampleCodePrefix());
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(vectorTaskAddDTO));
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        }
        return "OK";
    }

    private String createSampleCode() {
        String sampleCodePrefix = LetterUtil.randomLetter(2);
        List<CerSampleCodePrefixTb> cerSampleCodePrefixTbList = cerSampleCodePrefixTbMapper.selectList(null);
        List<String> sampleCodePrefixList = cerSampleCodePrefixTbList.stream().map(CerSampleCodePrefixTb::getSampleCodePrefix).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(sampleCodePrefixList)) {
            return sampleCodePrefix;
        }
        while (sampleCodePrefixList.contains(sampleCodePrefix)) {
            sampleCodePrefix = LetterUtil.randomLetter(2);
        }
        return sampleCodePrefix;
    }


    @GetMapping("/cleanVectorTaskPlanTime")
    public String cleanVectorTaskPlanTime() {
        List<List<Object>> excelResultList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\实施方案时间进度20241220.xlsx");
        for (int i = 1; i < excelResultList.size(); i++) {
            String vectorTaskCode = excelResultList.get(i).get(0).toString();
            String username = excelResultList.get(i).get(1).toString();
            String planTypeDesc = excelResultList.get(i).get(2).toString();
            String startTime = excelResultList.get(i).get(3).toString();
            String endTime = excelResultList.get(i).get(4).toString();
            String flag = excelResultList.get(i).get(5).toString();
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
            if (cerVectorTaskTb == null) {
                System.out.println("********************************************=" + vectorTaskCode);
                continue;
            }
            String plantType = VectorTaskPlanEventTypeEnum.getCodeByDesc(planTypeDesc);
            if (plantType == null) {
                System.out.println("********************************************=" + planTypeDesc);
                continue;
            }


            CerVectorTaskPlanLog cerVectorTaskPlanLog = cerVectorTaskPlanLogMapper.selectOneByVectorTaskIdAndEventTypeAndUserName(cerVectorTaskTb.getId(), plantType, username);
            if ("N".equals(flag)) {
                cerVectorTaskPlanLogMapper.deleteById(cerVectorTaskPlanLog);
                System.out.println("**********************删除****" + cerVectorTaskPlanLog.getVectorTaskId() + ":" + cerVectorTaskPlanLog.getEventType());
            } else {
                if (cerVectorTaskPlanLog == null) {
                    cerVectorTaskPlanLog = new CerVectorTaskPlanLog();
                    cerVectorTaskPlanLog.setVectorTaskId(cerVectorTaskTb.getId());
                    cerVectorTaskPlanLog.setEventType(plantType);
                    cerVectorTaskPlanLog.setEstimatedStartTime(startTime);
                    cerVectorTaskPlanLog.setEstimatedEndTime(endTime);
                    cerVectorTaskPlanLog.setActualStartTime(null);
                    cerVectorTaskPlanLog.setActualEndTime(null);
                    cerVectorTaskPlanLog.setUserId(null);
                    cerVectorTaskPlanLog.setUserName(username);
                    cerVectorTaskPlanLog.setCreateTime(new Date());
                    cerVectorTaskPlanLog.setUpdateTime(new Date());
                    cerVectorTaskPlanLogMapper.insert(cerVectorTaskPlanLog);
                    System.out.println("**********************添加****" + cerVectorTaskPlanLog.getId() + ":" + cerVectorTaskPlanLog.getVectorTaskId() + ":" + cerVectorTaskPlanLog.getEventType());

                } else {
                    cerVectorTaskPlanLog.setEstimatedStartTime(startTime);
                    cerVectorTaskPlanLog.setEstimatedEndTime(endTime);
                    cerVectorTaskPlanLogMapper.updateById(cerVectorTaskPlanLog);
                }
            }


        }
        return "ok";

    }

    @Transactional(rollbackFor = Exception.class)
    @GetMapping("cleanSampleLabel")
    public void cleanSampleLabel() {
        List<BioPrintLabelInfoTb> largeBioPrintLabelInfoTbList = bioPrintLabelInfoTbMapper.searchAllByLabelType("sample_large_label_print");
        List<BioPrintLabelInfoTb> smallPrintLabelInfoTbList = bioPrintLabelInfoTbMapper.searchAllByLabelType("sample_small_label_print");
        largeBioPrintLabelInfoTbList.addAll(smallPrintLabelInfoTbList);
        for (BioPrintLabelInfoTb bioPrintLabelInfoTb : largeBioPrintLabelInfoTbList) {
            log.info("清洗unique=" + bioPrintLabelInfoTb.getUniqueCode());
            String uniqueCode = bioPrintLabelInfoTb.getUniqueCode();
            String[] uniqueCodeArr = uniqueCode.split("\\|");
            String sampleCode = uniqueCodeArr[uniqueCodeArr.length - 1];
            String vectorTaskCode = uniqueCodeArr[uniqueCodeArr.length - 2];
            CerSampleTestTb cerSampleTestTb = cerSampleTestTbMapper.selectOneByVectorTaskCodeAndSampleCodeFirst(vectorTaskCode, sampleCode);
            if (cerSampleTestTb == null) {
                continue;
            }
            if (uniqueCodeArr.length == 2) {
                uniqueCode = cerSampleTestTb.getApplyNo() + "|" + uniqueCode;
                bioPrintLabelInfoTb.setUniqueCode(uniqueCode);
            }
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(cerSampleTestTb.getVectorTaskCode());
            if (cerVectorTaskTb == null) {
                throw new BusinessException("数据异常，找不到实施方案信息：" + cerSampleTestTb.getVectorTaskCode());
            }
            SamplePrintData samplePrintData = new SamplePrintData();
            samplePrintData.setVectorTaskCode(cerSampleTestTb.getVectorTaskCode());
            samplePrintData.setPlasmidName(cerSampleTestTb.getPlasmidName());
            samplePrintData.setTransformCode(cerSampleTestTb.getTransformCode());
            samplePrintData.setSampleCode(cerSampleTestTb.getSampleCode());
            samplePrintData.setTaskNum(cerSampleTestTb.getApplyNo());
            samplePrintData.setBreedName(cerVectorTaskTb.getAcceptorMaterial());
            bioPrintLabelInfoTb.setLabelText(JSONUtil.toJsonStr(samplePrintData));
            bioPrintLabelInfoTbMapper.updateById(bioPrintLabelInfoTb);
        }


    }


}
