package com.bio.drqi.manage.controller;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.enums.GenerationEnum;
import com.bio.drqi.common.enums.SourceCodeEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/test5")
public class CleanSampleTestController {

    @Resource
    private CerSampleTestTbMapper cerSampleTestTbMapper;

    @Resource
    private BioSampleTestTbMapper bioSampleTestTbMapper;


    @Resource
    private CerPlantDtlTbMapper cerPlantDtlTbMapper;


    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;


    @Resource
    private CerConversionAndTransRefMapper cerConversionAndTransRefMapper;

    @Resource
    private PlantMultipleStockTbMapper plantMultipleStockTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;


    @Resource
    private CerSampleApplyTbMapper cerSampleApplyTbMapper;


    @Resource
    private BioSampleApplyTbMapper bioSampleApplyTbMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Transactional(rollbackFor = Exception.class)
    @GetMapping("cleanTransform")
    public ResponseResult<String> cleanTransform() {
        List<CerTransformTb> cerTransformTbList = cerTransformTbMapper.selectSelective(null);
        for (CerTransformTb cerTransformTb : cerTransformTbList) {
            log.info("cerTransformTb=" + JSONUtil.toJsonStr(cerTransformTb));
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(cerTransformTb.getVectorTaskCode());
            if (cerVectorTaskTb.getBreedCode().contains("|")) {
                CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedNameAndSpeciesCode(cerVectorTaskTb.getAcceptorMaterial(), cerVectorTaskTb.getSpeciesCode());
                if (cerBreedDict != null) {
                    cerTransformTb.setBreedCode(cerBreedDict.getBreedCode());
                } else {
                    cerTransformTb.setBreedCode(cerVectorTaskTb.getBreedCode().split("\\|")[0]);
                }
            } else {
                cerTransformTb.setBreedCode(cerVectorTaskTb.getBreedCode());
            }
            cerTransformTbMapper.updateById(cerTransformTb);
        }
        return ResponseResult.getSuccess("ok");
    }


    @Transactional(rollbackFor = Exception.class)
    @GetMapping("cleanBioSampleTestTb")
    public ResponseResult<String> cleanBioSampleTestTb() {
        List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectList(null);
        for (int i = 0; i < cerSampleTestTbList.size(); i++) {
            log.info("清洗取样数据第{}个，ID={}", i, cerSampleTestTbList.get(i).getId());
            CerSampleTestTb cerSampleTestTb = cerSampleTestTbList.get(i);
            CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(cerSampleTestTb.getTransformCode(), cerSampleTestTb.getVectorTaskCode());
            if (cerTransformTb == null) {
                continue;
                // throw new BusinessException("找不到转化信息");
            }
            BioSampleTestTb bioSampleTestTb = new BioSampleTestTb();
            bioSampleTestTb.setVectorTaskCode(cerSampleTestTb.getVectorTaskCode());
            bioSampleTestTb.setSampleCode(cerSampleTestTb.getSampleCode());
            bioSampleTestTb.setApplyTime(cerSampleTestTb.getApplyTime());
            bioSampleTestTb.setApplyUserId(cerSampleTestTb.getApplyUserId());
            bioSampleTestTb.setApplyUserName(cerSampleTestTb.getApplyUserName());
            bioSampleTestTb.setTestIdentifyPrimer(cerSampleTestTb.getTestIdentifyPrimer());
            bioSampleTestTb.setTestMethod(cerSampleTestTb.getTestMethod());
            bioSampleTestTb.setTestEditType(cerSampleTestTb.getTestEditType());
            bioSampleTestTb.setTestNoTransIdentityPrimer(cerSampleTestTb.getTestNoTransIdentityPrimer());
            bioSampleTestTb.setTestIsGeneModifyPositive(cerSampleTestTb.getTestIsGeneModifyPositive());
            bioSampleTestTb.setTestIfFixedPoint(cerSampleTestTb.getTestIfFixedPoint());
            bioSampleTestTb.setTestIfCopyInsert(cerSampleTestTb.getTestIfCopyInsert());
            bioSampleTestTb.setTestFixedPointType(cerSampleTestTb.getTestFixedPointType());
            bioSampleTestTb.setTestDonorResidueInfo(cerSampleTestTb.getTestDonorResidueInfo());
            bioSampleTestTb.setTestInsertionSite(cerSampleTestTb.getTestInsertionSite());
            bioSampleTestTb.setTestElisaResult(cerSampleTestTb.getTestElisaResult());
            bioSampleTestTb.setTestQbzrSeq(cerSampleTestTb.getTestQbzrSeq());
            bioSampleTestTb.setTestEditResidueInfo(cerSampleTestTb.getTestEditResidueInfo());
            bioSampleTestTb.setTestUserId(cerSampleTestTb.getTestUserId());
            bioSampleTestTb.setTestUserName(cerSampleTestTb.getTestUserName());
            bioSampleTestTb.setTestTime(cerSampleTestTb.getTestTime());
            bioSampleTestTb.setCheckUserName(cerSampleTestTb.getCheckUserName());
            bioSampleTestTb.setCheckUserId(cerSampleTestTb.getCheckUserId());
            bioSampleTestTb.setCheckResult(cerSampleTestTb.getCheckResult());
            bioSampleTestTb.setCreateTime(cerSampleTestTb.getCreateTime());
            bioSampleTestTb.setUpdateTime(cerSampleTestTb.getUpdateTime());
            bioSampleTestTb.setApplyNo(cerSampleTestTb.getApplyNo());
            bioSampleTestTb.setIdentifyPrimer(cerSampleTestTb.getIdentifyPrimer());
            bioSampleTestTb.setUniqueCode(StringUtils.isEmpty(cerSampleTestTb.getUniqueCode()) ? null : cerSampleTestTb.getSampleCode());
            bioSampleTestTb.setRemark(cerSampleTestTb.getRemark());
            bioSampleTestTb.setCloneSampleCode(cerSampleTestTb.getCloneSampleCode());
            bioSampleTestTb.setSourceCode(SourceCodeEnum.project.name());
            bioSampleTestTb.setGeneration(cerSampleTestTb.getSampleGeneration());
            bioSampleTestTb.setSpeciesCode(cerTransformTb.getSpeciesCode());
            bioSampleTestTb.setBreedCode(cerTransformTb.getBreedCode());
            bioSampleTestTb.setExperimentNum(null);
            bioSampleTestTb.setRegionNum(null);
            bioSampleTestTb.setSeedNum(null);
            bioSampleTestTb.setTransformCode(cerSampleTestTb.getTransformCode());
            try {
                bioSampleTestTbMapper.insert(bioSampleTestTb);
            } catch (Exception e) {
                bioSampleTestTb.setUniqueCode(null);
                bioSampleTestTbMapper.insert(bioSampleTestTb);
            }

        }
        return ResponseResult.getSuccess("ok");
    }

    @Transactional(rollbackFor = Exception.class)
    @GetMapping("cleanBioSampleApplyTb")
    public ResponseResult<String> cleanBioSampleApplyTb() {
        List<CerSampleApplyTb> cerSampleApplyTbList = cerSampleApplyTbMapper.selectSelective(null);
        for (CerSampleApplyTb cerSampleApplyTb : cerSampleApplyTbList) {
            log.info("cerSampleApplyTb={}" + JSONUtil.toJsonStr(cerSampleApplyTb));
            BioSampleApplyTb bioSampleApplyTb = new BioSampleApplyTb();
            bioSampleApplyTb.setApplyNo(cerSampleApplyTb.getApplyNo());
            bioSampleApplyTb.setApplyNumber(cerSampleApplyTb.getApplyNumber());
            bioSampleApplyTb.setApplyTime(cerSampleApplyTb.getApplyTime());
            bioSampleApplyTb.setApplyUserId(cerSampleApplyTb.getApplyUserId());
            bioSampleApplyTb.setApplyUserName(cerSampleApplyTb.getApplyUserName());
            bioSampleApplyTb.setApplyDesc(cerSampleApplyTb.getApplyDesc());
            bioSampleApplyTb.setApplyType(cerSampleApplyTb.getApplyType());
            bioSampleApplyTb.setIdentifyExcelUrl(cerSampleApplyTb.getIdentifyExcelUrl());
            bioSampleApplyTb.setCloneFlag(cerSampleApplyTb.getCloneFlag());
            bioSampleApplyTb.setLayoutFlag(cerSampleApplyTb.getLayoutFlag());
            bioSampleApplyTb.setVectorTaskCodes(cerSampleApplyTb.getVectorTaskCodes());
            bioSampleApplyTb.setSampleCodeRange(cerSampleApplyTb.getSampleCodeRange());
            bioSampleApplyTb.setTaskStatus(cerSampleApplyTb.getTaskStatus());
            bioSampleApplyTbMapper.insert(bioSampleApplyTb);
        }
        return ResponseResult.getSuccess("ok");
    }

    @Transactional(rollbackFor = Exception.class)
    @GetMapping("cleanPlantSingleStockTb")
    public ResponseResult<String> cleanPlantSingleStockTb() {
        List<CerPlantDtlTb> cerPlantDtlTbList = cerPlantDtlTbMapper.selectList(null);
        for (CerPlantDtlTb cerPlantDtlTb : cerPlantDtlTbList) {
            log.info("cerPlantDtlTb=" + JSONUtil.toJsonStr(cerPlantDtlTb));
            PlantSingleStockTb plantSingleStockTb = new PlantSingleStockTb();
            plantSingleStockTb.setPlantCode(cerPlantDtlTb.getPlantCode());
            plantSingleStockTb.setGeneration(cerPlantDtlTb.getGeneration());
            plantSingleStockTb.setPlantNumber(cerPlantDtlTb.getPlantNumber());
            plantSingleStockTb.setPlantDate(cerPlantDtlTb.getPlantDate());
            plantSingleStockTb.setSampleCode(cerPlantDtlTb.getSampleCode());
            plantSingleStockTb.setTransplantDate(cerPlantDtlTb.getTransplantDate());
            plantSingleStockTb.setVernalizationBeginDate(cerPlantDtlTb.getVernalizationBeginDate());
            plantSingleStockTb.setVernalizationEndDate(cerPlantDtlTb.getVernalizationEndDate());
            plantSingleStockTb.setPollinationMethod(cerPlantDtlTb.getPollinationMethod());
            plantSingleStockTb.setPlantStatus(cerPlantDtlTb.getPlantStatus());
            plantSingleStockTb.setPollinationDate(cerPlantDtlTb.getPollinationDate());
            plantSingleStockTb.setHarvestDate(cerPlantDtlTb.getHarvestDate());
            plantSingleStockTb.setHarvestType(cerPlantDtlTb.getEditType());
            plantSingleStockTb.setOtherField(cerPlantDtlTb.getOtherField());
            plantSingleStockTb.setEditType(cerPlantDtlTb.getEditType());
            plantSingleStockTb.setSpeciesCode(cerPlantDtlTb.getSpeciesCode());
            plantSingleStockTb.setCreateDate(cerPlantDtlTb.getCreateDate());
            plantSingleStockTb.setUpdateTime(cerPlantDtlTb.getUpdateTime());
            plantSingleStockTb.setCreateUserId(cerPlantDtlTb.getCreateUserId());
            plantSingleStockTb.setCreateUserName(cerPlantDtlTb.getCreateUserName());
            plantSingleStockTb.setTaskNum(cerPlantDtlTb.getTaskNum());
            plantSingleStockTb.setBreedCode(cerPlantDtlTb.getPlantCode());
            plantSingleStockTb.setSourceCode(SourceCodeEnum.project.name());
            plantSingleStockTb.setRemark("来源项目产生种子的数据清洗");
            plantSingleStockTb.setVectorTaskCode(cerPlantDtlTb.getVectorTaskCode());
            plantSingleStockTbMapper.insert(plantSingleStockTb);

        }
        return ResponseResult.getSuccess("ok");
    }

    @Transactional(rollbackFor = Exception.class)
    @GetMapping("cleanPlantMultipleStockTb")
    public ResponseResult<String> cleanPlantMultipleStockTb() {
        List<CerConversionAndTransRef> cerConversionAndTransRefList = cerConversionAndTransRefMapper.selectList(null);
        cerConversionAndTransRefList = cerConversionAndTransRefList.stream().filter(cerConversionAndTransRef -> StringUtils.isNotEmpty(cerConversionAndTransRef.getTransformCode()) && StringUtils.isEmpty(cerConversionAndTransRef.getSampleCode())).collect(Collectors.toList());
        for (CerConversionAndTransRef cerConversionAndTransRef : cerConversionAndTransRefList) {
            log.info("cerConversionAndTransRef=" + JSONUtil.toJsonStr(cerConversionAndTransRef));
            CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(cerConversionAndTransRef.getTransformCode(), cerConversionAndTransRef.getVectorTaskCode());
            if (cerTransformTb == null) {
                throw new BusinessException("找不到转化苗信息");
            }
            PlantMultipleStockTb plantMultipleStockTb = plantMultipleStockTbMapper.selectOneByVectorTaskCodeAndTransformCode(cerConversionAndTransRef.getVectorTaskCode(), cerConversionAndTransRef.getTransformCode());
            if (plantMultipleStockTb == null) {
                plantMultipleStockTb = new PlantMultipleStockTb();
                plantMultipleStockTb.setSeedNum(null);
                plantMultipleStockTb.setTransformCode(cerConversionAndTransRef.getTransformCode());
                plantMultipleStockTb.setGeneration(GenerationEnum.T0.code);
                plantMultipleStockTb.setPlantNumber(cerConversionAndTransRef.getTransNum());
                plantMultipleStockTb.setSourceCode(SourceCodeEnum.project.name());
                plantMultipleStockTb.setRemark("转化移苗数据");
                plantMultipleStockTb.setCreateTime(new Date());
                plantMultipleStockTb.setCreateUserId(cerConversionAndTransRef.getCreateUserId());
                plantMultipleStockTb.setCreateUserName(cerConversionAndTransRef.getCreateUserName());
                plantMultipleStockTb.setTaskNum(cerConversionAndTransRef.getTaskNum());
                plantMultipleStockTb.setSpeciesCode(cerTransformTb.getSpeciesCode());
                plantMultipleStockTb.setBreedCode(cerTransformTb.getBreedCode());
                plantMultipleStockTb.setSampleNumber(0);
                plantMultipleStockTb.setCurrentNumber(0);
                plantMultipleStockTb.setRegionNum(null);
                plantMultipleStockTb.setVectorTaskCode(cerTransformTb.getVectorTaskCode());
                plantMultipleStockTb.setPdImplementCode(null);
                plantMultipleStockTb.setPlantDate(DateUtil.format(cerConversionAndTransRef.getCreateTime(), DatePattern.NORM_DATE_PATTERN));
                plantMultipleStockTbMapper.insert(plantMultipleStockTb);
            }
        }
        return ResponseResult.getSuccess("okk");
    }


    @GetMapping("cleanPlantDataReport")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanPlantDataReport() {
        List<BioTaskDtlTb> bioTaskDtlTbList = bioTaskDtlTbMapper.selectAllByTaskTypeCode("cer_plant");
        for (BioTaskDtlTb bioTaskDtlTb:bioTaskDtlTbList){
            bioTaskDtlTb.setTaskNum("C"+bioTaskDtlTb.getTaskNum().substring(1));
            bioTaskDtlTb.setTaskTypeCode("plant_data_report");
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        }

        return ResponseResult.getSuccess("ok");
    }
}
