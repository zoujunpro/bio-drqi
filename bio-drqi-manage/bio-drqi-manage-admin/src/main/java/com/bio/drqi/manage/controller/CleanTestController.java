package com.bio.drqi.manage.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.common.enums.SourceCodeEnum;
import com.bio.drqi.common.enums.TestResultEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.SeedSourceEnum;
import com.bio.drqi.manage.dto.project.VectorTaskAddDTO;
import com.bio.drqi.manage.dto.seed.SeedInStoreDTO;
import com.bio.drqi.manage.flowtask.plant.PlantSampleTestTaskService;
import com.bio.drqi.manage.sample.req.ApproveSampleResultReqDTO;
import com.bio.drqi.manage.service.bio.BioSampleTestService;
import com.bio.drqi.manage.service.common.SeedPlantService;
import com.bio.drqi.mapper.*;
import com.bio.drqi.tc.service.dto.TcSampleTestTaskDTO;
import com.bio.print.PlantApplyPrintDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.Jar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/test1")
@Slf4j
public class CleanTestController {

    @Resource
    private CerVectorTbMapper cerVectorTbMapper;

    @Resource
    private CerPlasmidQualityTbMapper cerPlasmidQualityTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private BioSampleTestService bioSampleTestService;

    @Resource
    private BioSampleTestTbMapper bioSampleTestTbMapper;

    @Resource
    private PlantSampleTestTaskService plantSampleTestTaskService;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private BioPrintLabelInfoTbMapper bioPrintLabelInfoTbMapper;

    @Resource
    private PlantApplyDetailTbMapper plantApplyDetailTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;


    @Resource
    private SeedPlantService seedPlantService;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private SeedStockInLogMapper seedStockInLogMapper;

    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;

    @Resource
    private PlantMultipleStockTbMapper plantMultipleStockTbMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;
    @Autowired
    private CerConversionAndTransTbMapper cerConversionAndTransTbMapper;
    @Autowired
    private CerConversionAndTransRefMapper cerConversionAndTransRefMapper;

    @Resource
    private TcSampleTestApplyTbMapper tcSampleTestApplyTbMapper;

    @Resource
    private TcSampleTestTbMapper tcSampleTestTbMapper;

    @Resource
    private TcSampleLayoutTbMapper tcSampleLayoutTbMapper;

    @Resource
    private TcSampleTestBioInfoResultTbMapper tcSampleTestBioInfoResultTbMapper;

    @Resource
    private TcSampleTestBioResultRefMapper tcSampleTestBioResultRefMapper;

    @Resource
    private BioSampleLayoutTbMapper bioSampleLayoutTbMapper;

    @Resource
    private BioSampleApplyTbMapper bioSampleApplyTbMapper;

    @Resource
    private BioSampleTestTwoResultTbMapper bioSampleTestTwoResultTbMapper;

    @Resource
    private BioSampleTestTwoResultDetailTbMapper bioSampleTestTwoResultDetailTbMapper;


    @Resource
    private TcPollinationSingleNumTbMapper tcPollinationSingleNumTbMapper;


    @GetMapping("cleanTcSampleData20260316")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanTcSampleData20260316() {
        //取样检测的孔板迁移
        List<TcSampleLayoutTb> tcSampleLayoutTbList = tcSampleLayoutTbMapper.selectList(null);
        for (TcSampleLayoutTb tcSampleLayoutTb : tcSampleLayoutTbList) {
            log.info("cleanTcSampleData20260316#迁移TcSampleLayoutTb=" + JSONUtil.toJsonStr(tcSampleLayoutTb));
            BioSampleLayoutTb bioSampleLayoutTb = bioSampleLayoutTbMapper.selectOneByApplyNo(tcSampleLayoutTb.getApplyNo());
            if (bioSampleLayoutTb != null) {
                bioSampleLayoutTb = new BioSampleLayoutTb();
                bioSampleLayoutTb.setApplyNo(tcSampleLayoutTb.getApplyNo());
                bioSampleLayoutTb.setSingleContent(tcSampleLayoutTb.getSingleContent());
                bioSampleLayoutTb.setPlateContent(tcSampleLayoutTb.getPlateContent());
                bioSampleLayoutTb.setCreateTime(tcSampleLayoutTb.getCreateTime());
                bioSampleLayoutTbMapper.insert(bioSampleLayoutTb);
            }
        }
        //迁移田测取样检测申请表
        List<TcSampleTestApplyTb> tcSampleTestApplyTbs = tcSampleTestApplyTbMapper.selectSelective(null);
        for (TcSampleTestApplyTb tcSampleTestApplyTb : tcSampleTestApplyTbs) {
            log.info("cleanTcSampleData20260316#迁移tcSampleTestApplyTb=" + JSONUtil.toJsonStr(tcSampleTestApplyTb));
            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(tcSampleTestApplyTb.getTaskNum());
            if (bioTaskDtlTb == null) {
                continue;
            }
            TcSampleTestTaskDTO tcSampleTestTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcSampleTestTaskDTO.class);
            if (tcSampleTestTaskDTO == null) {
                continue;
            }
            BioSampleApplyTb bioSampleApplyTb = bioSampleApplyTbMapper.selectOneByApplyNo(bioTaskDtlTb.getTaskNum());
            if (tcSampleTestTaskDTO != null && bioSampleApplyTb == null) {
                bioSampleApplyTb = new BioSampleApplyTb();
                bioSampleApplyTb.setApplyNo(tcSampleTestApplyTb.getSampleApplyNum());
                bioSampleApplyTb.setApplyTime(tcSampleTestApplyTb.getCreateTime());
                bioSampleApplyTb.setApplyUserId(tcSampleTestApplyTb.getCreateUserId());
                bioSampleApplyTb.setApplyUserName(tcSampleTestApplyTb.getCreateUserName());
                bioSampleApplyTb.setApplyDesc(null);
                bioSampleApplyTb.setApplyType(tcSampleTestApplyTb.getApplyType());
                bioSampleApplyTb.setIdentifyExcelUrl(tcSampleTestApplyTb.getIdentifyPrimerExcelUrl());
                bioSampleApplyTb.setSampleOrganize(tcSampleTestApplyTb.getSampleOrganize());
                bioSampleApplyTb.setCloneFlag("N");
                bioSampleApplyTb.setLayoutFlag(tcSampleTestTaskDTO.getTestType());
                bioSampleApplyTb.setVectorTaskCodes(null);
                bioSampleApplyTb.setSampleCodeRange(null);
                bioSampleApplyTbMapper.insert(bioSampleApplyTb);
            }
        }
        //迁移取样表
        List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectSelective(null);
        for (TcSampleTestTb tcSampleTestTb : tcSampleTestTbList) {
            log.info("cleanTcSampleData20260316#迁移tcSampleTestTb=" + JSONUtil.toJsonStr(tcSampleTestTb));
            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(tcSampleTestTb.getSampleApplyNum());
            if (bioTaskDtlTb == null) {
                continue;
            }
            BioSampleTestTb bioSampleTestTb = new BioSampleTestTb();
            bioSampleTestTb.setVectorTaskCode(tcSampleTestTb.getVectorTaskCode());
            bioSampleTestTb.setSampleCode(tcSampleTestTb.getSampleCode());
            bioSampleTestTb.setApplyTime(DateUtil.parse(tcSampleTestTb.getSampleTime(), "yyyy-MM-dd"));
            bioSampleTestTb.setApplyUserId(bioTaskDtlTb.getApplyUserId());
            bioSampleTestTb.setApplyUserName(bioTaskDtlTb.getApplyUserName());
            bioSampleTestTb.setTestIdentifyPrimer(tcSampleTestTb.getTestIdentifyPrimer());
            bioSampleTestTb.setTestMethod(tcSampleTestTb.getTestMethod());
            bioSampleTestTb.setTestEditType(tcSampleTestTb.getTestEditType());
            bioSampleTestTb.setTestNoTransIdentityPrimer(tcSampleTestTb.getTestNoTransIdentityPrimer());
            bioSampleTestTb.setTestIsGeneModifyPositive(tcSampleTestTb.getTestIsGeneModifyPositive());
            bioSampleTestTb.setTestIfFixedPoint(tcSampleTestTb.getTestIfFixedPoint());
            bioSampleTestTb.setTestIfCopyInsert(tcSampleTestTb.getTestIfCopyInsert());
            bioSampleTestTb.setTestFixedPointType(tcSampleTestTb.getTestFixedPointType());
            bioSampleTestTb.setTestDonorResidueInfo(tcSampleTestTb.getTestDonorResidueInfo());
            bioSampleTestTb.setTestInsertionSite(tcSampleTestTb.getTestInsertionSite());
            bioSampleTestTb.setTestElisaResult(tcSampleTestTb.getTestElisaResult());
            bioSampleTestTb.setTestQbzrSeq(tcSampleTestTb.getTestQbzrSeq());
            bioSampleTestTb.setTestEditResidueInfo(tcSampleTestTb.getTestEditResidueInfo());
            bioSampleTestTb.setTestUserId(tcSampleTestTb.getTestUserId());
            bioSampleTestTb.setTestUserName(tcSampleTestTb.getTestUserName());
            bioSampleTestTb.setTestTime(tcSampleTestTb.getTestTime());
            bioSampleTestTb.setCheckUserName(null);
            bioSampleTestTb.setCheckUserId(null);
            bioSampleTestTb.setCheckResult(tcSampleTestTb.getCheckResult());
            bioSampleTestTb.setCreateTime(bioTaskDtlTb.getCreateTime());
            bioSampleTestTb.setUpdateTime(bioTaskDtlTb.getCreateTime());
            bioSampleTestTb.setApplyNo(tcSampleTestTb.getSampleApplyNum());
            bioSampleTestTb.setIdentifyPrimer(tcSampleTestTb.getIdentifyPrimer());
            bioSampleTestTb.setUniqueCode(StringUtils.isEmpty(tcSampleTestTb.getUniqueCode()) ? null : tcSampleTestTb.getSampleCode());
            bioSampleTestTb.setRemark(null);
            bioSampleTestTb.setCloneSampleCode(null);
            bioSampleTestTb.setSourceCode(SourceCodeEnum.field.name());
            bioSampleTestTb.setTestOrgResult(tcSampleTestTb.getTestOrgResult());
            bioSampleTestTb.setGeneration(tcSampleTestTb.getGenerationCode());
            bioSampleTestTb.setSpeciesCode(tcSampleTestTb.getSpeciesCode());
            bioSampleTestTb.setBreedCode(tcSampleTestTb.getBreedCode());
            bioSampleTestTb.setExperimentNum(tcSampleTestTb.getExperimentNum());
            bioSampleTestTb.setRegionNum(tcSampleTestTb.getRegionNum());
            bioSampleTestTb.setSeedNum(tcSampleTestTb.getSeedNum());
            bioSampleTestTb.setTransformCode(null);
            bioSampleTestTb.setTestResult(TestResultEnum.noTest.name());
            List<TcSampleTestBioInfoResultTb> tcSampleTestBioInfoResultTbList = tcSampleTestBioInfoResultTbMapper.selectAllByApplyNoAndSampleCode(bioSampleTestTb.getApplyNo(), bioSampleTestTb.getSampleCode());
            if (CollectionUtil.isNotEmpty(tcSampleTestBioInfoResultTbList) || bioSampleTestTb.ifHaveTestResult()) {
                bioSampleTestTb.setTestResult(TestResultEnum.haveResult.name());
            } else {
                if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
                    bioSampleTestTb.setTestResult(TestResultEnum.noResult.name());
                }
            }
            bioSampleTestTbMapper.insert(bioSampleTestTb);
        }
        //迁移二代测序
        List<TcSampleTestBioResultRef> tcSampleTestBioResultRefList = tcSampleTestBioResultRefMapper.selectList(null);
        for (TcSampleTestBioResultRef tcSampleTestBioResultRef : tcSampleTestBioResultRefList) {
            log.info("cleanTcSampleData20260316#迁移TcSampleTestBioResultRef=" + JSONUtil.toJsonStr(tcSampleTestBioResultRef));
            List<TcSampleTestBioInfoResultTb> bioInfoResultTbList = tcSampleTestBioInfoResultTbMapper.selectAllByApplyNoAndSampleCode(tcSampleTestBioResultRef.getApplyNo(), tcSampleTestBioResultRef.getSampleCode());
            bioInfoResultTbList = bioInfoResultTbList.stream().filter(tcSampleTestBioInfoResultTb -> "checked".equals(tcSampleTestBioInfoResultTb.getConfirmStatus())).collect(Collectors.toList());
            BioSampleTestTwoResultTb bioSampleTestTwoResultTb = new BioSampleTestTwoResultTb();
            bioSampleTestTwoResultTb.setApplyNo(tcSampleTestBioResultRef.getApplyNo());
            bioSampleTestTwoResultTb.setSampleCode(tcSampleTestBioResultRef.getSampleCode());
            bioSampleTestTwoResultTb.setSampleId(tcSampleTestBioResultRef.getSampleId());
            bioSampleTestTwoResultTb.setRunId(tcSampleTestBioResultRef.getRunId());
            bioSampleTestTwoResultTb.setCreateTime(tcSampleTestBioResultRef.getCreateTime());
            bioSampleTestTwoResultTb.setUploadNum(null);
            bioSampleTestTwoResultTb.setTestChannel(SourceCodeEnum.field.name());
            if (CollectionUtil.isNotEmpty(bioInfoResultTbList)) {
                bioSampleTestTwoResultTb.setSynResult("Y");
            }
            bioSampleTestTwoResultTbMapper.insert(bioSampleTestTwoResultTb);
            for (TcSampleTestBioInfoResultTb tcSampleTestBioInfoResultTb : bioInfoResultTbList) {
                BioSampleTestTwoResultDetailTb bioSampleTestTwoResultDetailTb = new BioSampleTestTwoResultDetailTb();
                bioSampleTestTwoResultDetailTb.setApplyNo(tcSampleTestBioInfoResultTb.getApplyNo());
                bioSampleTestTwoResultDetailTb.setSampleCode(tcSampleTestBioInfoResultTb.getSampleCode());
                bioSampleTestTwoResultDetailTb.setSampleId(tcSampleTestBioInfoResultTb.getSampleId());
                bioSampleTestTwoResultDetailTb.setUniqueDbCode(tcSampleTestBioInfoResultTb.getUniqueDbCode());
                bioSampleTestTwoResultDetailTb.setRunId(tcSampleTestBioInfoResultTb.getRunId());
                bioSampleTestTwoResultDetailTb.setHapId(tcSampleTestBioInfoResultTb.getHapId());
                bioSampleTestTwoResultDetailTb.setVarType(tcSampleTestBioInfoResultTb.getVarType());
                bioSampleTestTwoResultDetailTb.setMutate(tcSampleTestBioInfoResultTb.getMutate());
                bioSampleTestTwoResultDetailTb.setRatio(tcSampleTestBioInfoResultTb.getRatio());
                bioSampleTestTwoResultDetailTb.setCreateTime(tcSampleTestBioInfoResultTb.getCreateTime());
                bioSampleTestTwoResultDetailTb.setConfirmStatus(tcSampleTestBioInfoResultTb.getConfirmStatus());
                bioSampleTestTwoResultDetailTb.setResultKey(tcSampleTestBioInfoResultTb.getResultKey());
                bioSampleTestTwoResultDetailTb.setMatchFlag(tcSampleTestBioInfoResultTb.getMatchFlag());
                bioSampleTestTwoResultDetailTb.setTwoResultId(tcSampleTestBioResultRef.getId());
                bioSampleTestTwoResultDetailTbMapper.insert(bioSampleTestTwoResultDetailTb);

            }


        }
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("cleanTcPollinationSingleNumTb")
    public ResponseResult<String> cleanTcPollinationSingleNumTb() {
        List<TcPollinationSingleNumTb> tcPollinationSingleNumTbList = tcPollinationSingleNumTbMapper.selectList(null);
        for (TcPollinationSingleNumTb tcPollinationSingleNumTb : tcPollinationSingleNumTbList) {
            List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectAllByTcSampleCode(tcPollinationSingleNumTb.getTcSingleNumber());
            List<TcSampleTestTb> tc = tcSampleTestTbList.stream().filter(tcSampleTestTb -> tcSampleTestTb.getUniqueCode() != null).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(tc)) {
                tcPollinationSingleNumTb.setSampleCode(tc.get(0).getSampleCode());
                tcPollinationSingleNumTb.setSampleApplyNum(tc.get(0).getSampleApplyNum());
                tcPollinationSingleNumTbMapper.updateById(tcPollinationSingleNumTb);
            } else if (CollectionUtil.isNotEmpty(tcSampleTestTbList)) {
                tcPollinationSingleNumTb.setSampleCode(tcSampleTestTbList.get(0).getSampleCode());
                tcPollinationSingleNumTb.setSampleApplyNum(tcSampleTestTbList.get(0).getSampleApplyNum());
                tcPollinationSingleNumTbMapper.updateById(tcPollinationSingleNumTb);
            }
        }
        return ResponseResult.getSuccess("ok");

    }


    @GetMapping("cleanSampleTestResult")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSampleTestResult() {
        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectSelective(null);
        bioSampleTestTbList = bioSampleTestTbList.stream().filter(bioSampleTestTb -> bioSampleTestTb.getTestUserId() != null).collect(Collectors.toList());
        for (BioSampleTestTb bioSampleTestTb : bioSampleTestTbList) {
            log.info("清洗数据：" + bioSampleTestTb.getSampleCode());
            if (bioSampleTestTb.ifHaveTestResult()) {
                bioSampleTestTb.setTestResult(TestResultEnum.haveResult.name());
                bioSampleTestTbMapper.updateById(bioSampleTestTb);
            } else {
                List<CerConversionAndTransRef> cerConversionAndTransRefList = cerConversionAndTransRefMapper.selectAllBySampleCode(bioSampleTestTb.getSampleCode());
                if (CollectionUtil.isNotEmpty(cerConversionAndTransRefList)) {
                    bioSampleTestTb.setTestResult(TestResultEnum.haveResult.name());
                    bioSampleTestTbMapper.updateById(bioSampleTestTb);
                }
            }

        }
        return ResponseResult.getSuccess("ok");

    }


    @GetMapping("cleanTrans20260227")
    public ResponseResult<String> cleanTrans20260227() {
        List<CerConversionAndTransTb> cerConversionAndTransTbList = cerConversionAndTransTbMapper.selectSelective(null);
        for (CerConversionAndTransTb cerConversionAndTransTb : cerConversionAndTransTbList) {
            if (StringUtils.isNotEmpty(cerConversionAndTransTb.getTaskNum())) {
                BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerConversionAndTransTb.getTaskNum());
                if (bioTaskDtlTb != null) {
                    cerConversionAndTransTb.setCreateUserId(bioTaskDtlTb.getApplyUserId());
                    cerConversionAndTransTb.setCreateUserName(bioTaskDtlTb.getApplyUserName());
                    cerConversionAndTransTbMapper.updateById(cerConversionAndTransTb);

                    List<CerConversionAndTransRef> cerConversionAndTransRefList = cerConversionAndTransRefMapper.selectAllByConversionAndTransId(cerConversionAndTransTb.getId());
                    if (CollectionUtil.isNotEmpty(cerConversionAndTransRefList)) {
                        cerConversionAndTransRefList.forEach(cerConversionAndTransRef -> {
                            cerConversionAndTransRef.setCreateUserId(cerConversionAndTransTb.getCreateUserId());
                            cerConversionAndTransRef.setCreateUserName(cerConversionAndTransTb.getCreateUserName());
                            cerConversionAndTransRefMapper.updateById(cerConversionAndTransRef);
                        });
                    }
                }

            }
        }
        return ResponseResult.getSuccess("成功");
    }


    @GetMapping("cleanVectorTaskCode")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanVectorTaskCode() {
        List<TransForm> seedList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\转化信息1.xlsx", TransForm.class);
        for (TransForm transForm : seedList) {
            log.info("transForm=" + JSONUtil.toJsonStr(transForm));
            CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(transForm.transform_code, transForm.vector_task_code);
            CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedNameAndSpeciesCode(transForm.getAcceptorMaterial(), cerTransformTb.getSpeciesCode());
            cerTransformTb.setBreedCode(cerBreedDict.getBreedCode());
            cerTransformTbMapper.updateById(cerTransformTb);
            List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByTransformCodeAndVectorTaskCode(cerTransformTb.getTransformCode(), cerTransformTb.getVectorTaskCode());
            for (BioSampleTestTb bioSampleTestTb : bioSampleTestTbList) {
                bioSampleTestTb.setBreedCode(cerBreedDict.getBreedCode());
                bioSampleTestTbMapper.updateById(bioSampleTestTb);

                PlantSingleStockTb plantSingleStockTb = plantSingleStockTbMapper.selectOneByPlantCode(bioSampleTestTb.getSampleCode());
                if (plantSingleStockTb != null) {
                    plantSingleStockTb.setBreedCode(cerBreedDict.getBreedCode());
                    plantSingleStockTbMapper.updateById(plantSingleStockTb);

                    List<SeedStockTb> seedStockTbList = seedStockTbMapper.selectAllByPlantCode(plantSingleStockTb.getPlantCode());
                    if (CollectionUtil.isNotEmpty(seedStockTbList)) {
                        seedStockTbList.forEach(seedStockTb -> {
                            seedStockTb.setBreedCode(cerBreedDict.getBreedCode());
                            seedStockTbMapper.updateById(seedStockTb);
                        });
                    }
                }
            }
            List<PlantMultipleStockTb> plantMultipleStockTbList = plantMultipleStockTbMapper.selectAllByVectorTaskCode(cerTransformTb.getVectorTaskCode());
            if (CollectionUtil.isNotEmpty(plantMultipleStockTbList)) {
                plantMultipleStockTbList.forEach(plantMultipleStockTb -> {
                    plantMultipleStockTb.setBreedCode(cerBreedDict.getBreedCode());
                    plantMultipleStockTbMapper.updateById(plantMultipleStockTb);
                });
            }
        }
        return ResponseResult.getSuccess("ok");
    }

    @Data
    public static class TransForm {

        @ExcelProperty("id")
        private String id;

        @ExcelProperty("transform_code")
        private String transform_code;

        @ExcelProperty("受体材料")
        private String acceptorMaterial;

        @ExcelProperty("vector_task_code")
        private String vector_task_code;

    }


    @GetMapping("/cleanSeed")
    public ResponseResult<String> cleanSeed() {
        List<Seed> seedList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\外来种.xlsx", Seed.class);
        for (Seed seed : seedList) {
            List<SeedStockInLog> seedStockInLogList = seedStockInLogMapper.selectList(null);
            Map<String, List<SeedStockInLog>> listMap = seedStockInLogList.stream().collect(Collectors.groupingBy(SeedStockInLog::getTaskNum));
            listMap.forEach((taskNum, list) -> {
                BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(taskNum);
                SeedInStoreDTO seedInStoreDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedInStoreDTO.class);
                Map<String, SeedInStoreDTO.ExecuteFormContent> contentMap = seedInStoreDTO.getExecuteForm().getExecuteFormContentList().stream().collect(Collectors.toMap(SeedInStoreDTO.ExecuteFormContent::getUniqueCode, executeFormContent -> executeFormContent));
                list.forEach(seedStockInLog -> {
                    log.info("seedStockInLog={}" + JSONUtil.toJsonStr(seedStockInLog));
                    SeedInStoreDTO.ExecuteFormContent content = contentMap.get(seedStockInLog.getUniqueCode());
                    SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedStockInLog.getSeedNum());
                    seedStockTb.setGeneration(content.getGeneration());
                    seedStockTb.setSourceType(content.getSource());
                    seedStockTbMapper.updateById(seedStockTb);
                });


            });
        }
        return ResponseResult.getSuccess("ok");
    }

    @Data
    public static class Seed {

        @ExcelProperty("种子编号")
        private String seedNum;


    }

    @GetMapping("/cleanTransFlag")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanTransFlag() {
        List<CerVectorTb> cerVectorTbList = cerVectorTbMapper.selectSelective(null);
        Map<String, List<CerVectorTb>> map = cerVectorTbList.stream().filter(cerVectorTb -> StringUtils.isNotEmpty(cerVectorTb.getTaskNum())).collect(Collectors.groupingBy(CerVectorTb::getTaskNum));
        map.forEach((taskNum, list) -> {
            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(taskNum);
            VectorTaskAddDTO vectorTaskAddDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), VectorTaskAddDTO.class);
            list.forEach(cerVectorTb -> {
                cerVectorTb.setTransFlag(StringUtils.isEmpty(vectorTaskAddDTO.getTransFlag()) ? "N" : vectorTaskAddDTO.getTransFlag());
                cerVectorTbMapper.updateById(cerVectorTb);
            });
        });
        return ResponseResult.getSuccess("ok");
    }


    @GetMapping("/cleanPlantSeed")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanPlantSeed() {
        List<SeedStockTb> seedStockTbList4 = seedStockTbMapper.selectSelective(SeedStockTb.builder().sourceType("333").build());
        for (SeedStockTb seedStockTb : seedStockTbList4) {
            log.info("大田seedStockTb=" + JSONUtil.toJsonStr(seedStockTb));
            if (StringUtils.isEmpty(seedStockTb.getExperimentNum())) {
                continue;
            }
            seedPlantService.seedInStockAddRefPlant(seedStockTb);
        }

        List<SeedStockTb> seedStockTbList1 = seedStockTbMapper.selectSelective(SeedStockTb.builder().sourceType(SeedSourceEnum.CODE_1.code).build());
        for (SeedStockTb seedStockTb : seedStockTbList1) {
            log.info("CER seedStockTb=" + JSONUtil.toJsonStr(seedStockTb));
            if (StringUtils.isEmpty(seedStockTb.getPlantCode())) {
                continue;
            }
            PlantSingleStockTb plantSingleStockTb = plantSingleStockTbMapper.selectOneByPlantCode(seedStockTb.getPlantCode());
            if (plantSingleStockTb == null) {
                continue;
            }

            seedPlantService.seedInStockAddRefPlant(seedStockTb);
        }
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanPrintPlant_apply_label_print")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanPrintPlant_apply_label_print() {
        List<BioPrintLabelInfoTb> plant_label_cer_printList = bioPrintLabelInfoTbMapper.searchAllByLabelType("plant_apply_label_print");
        for (BioPrintLabelInfoTb bioPrintLabelInfoTb : plant_label_cer_printList) {
            String[] uniqueCodeArr = bioPrintLabelInfoTb.getUniqueCode().split("\\|");
            PlantApplyDetailTb plantApplyDetailTb = plantApplyDetailTbMapper.selectOneByRegionNumAndSeedNum(uniqueCodeArr[0], uniqueCodeArr[1]);
            PlantApplyPrintDTO plantApplyPrintDTO = new PlantApplyPrintDTO();
            plantApplyPrintDTO.setRegionNum(plantApplyDetailTb.getRegionNum());
            CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedCode(plantApplyDetailTb.getBreedCode());
            plantApplyPrintDTO.setSeedNum(plantApplyDetailTb.getSeedNum());
            plantApplyPrintDTO.setBreedName(cerBreedDict.getBreedName());
            plantApplyPrintDTO.setTaskNum(plantApplyDetailTb.getPlantApplyNum());
            plantApplyPrintDTO.setPrintNumber(1);
            bioPrintLabelInfoTb.setLabelText(JSONUtil.toJsonStr(plantApplyPrintDTO));
            bioPrintLabelInfoTbMapper.updateById(bioPrintLabelInfoTb);


        }


        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanPrint")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanPrint() {
        List<BioPrintLabelInfoTb> plant_label_cer_printList = bioPrintLabelInfoTbMapper.searchAllByLabelType("plant_label_cer_print");
        for (BioPrintLabelInfoTb bioPrintLabelInfoTb : plant_label_cer_printList) {
            String[] str = bioPrintLabelInfoTb.getUniqueCode().split("\\|");
            if (str.length == 3) {
                bioPrintLabelInfoTb.setUniqueCode(str[2]);
                bioPrintLabelInfoTbMapper.updateById(bioPrintLabelInfoTb);
            }
        }
        List<BioPrintLabelInfoTb> plant_label_project_printList = bioPrintLabelInfoTbMapper.searchAllByLabelType("plant_label_project_print");
        for (BioPrintLabelInfoTb bioPrintLabelInfoTb : plant_label_project_printList) {
            String[] str = bioPrintLabelInfoTb.getUniqueCode().split("\\|");
            if (str.length == 2) {
                bioPrintLabelInfoTb.setUniqueCode(str[1]);
                bioPrintLabelInfoTbMapper.updateById(bioPrintLabelInfoTb);
            }
        }
        List<BioPrintLabelInfoTb> sample_label_large_project_printList = bioPrintLabelInfoTbMapper.searchAllByLabelType("sample_label_large_project_print");
        for (BioPrintLabelInfoTb bioPrintLabelInfoTb : sample_label_large_project_printList) {
            String[] str = bioPrintLabelInfoTb.getUniqueCode().split("\\|");
            if (str.length == 3) {
                bioPrintLabelInfoTb.setUniqueCode(str[0] + "|" + str[2]);
                bioPrintLabelInfoTbMapper.updateById(bioPrintLabelInfoTb);
            }
        }

        List<BioPrintLabelInfoTb> sample_label_small_project_printList = bioPrintLabelInfoTbMapper.searchAllByLabelType("sample_label_small_project_print");
        for (BioPrintLabelInfoTb bioPrintLabelInfoTb : sample_label_small_project_printList) {
            String[] str = bioPrintLabelInfoTb.getUniqueCode().split("\\|");
            if (str.length == 3) {
                bioPrintLabelInfoTb.setUniqueCode(str[0] + "|" + str[2]);
                bioPrintLabelInfoTbMapper.updateById(bioPrintLabelInfoTb);
            }
        }


        return null;
    }


    @GetMapping("/cleanPlasmid")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanPlasmid() {

        List<Plasmid> plasmidList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\上线\\质粒信息.xlsx", Plasmid.class);
        for (Plasmid plasmid : plasmidList) {
            log.info("plasmid=" + JSONUtil.toJsonStr(plasmid));
            if (StringUtils.isEmpty(plasmid.plasmidName)) {
                continue;
            }
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(plasmid.vectorTaskCode);

            if (cerVectorTaskTb == null) {
                throw new BusinessException("数据异常，找不到实施方案");
            }
            CerVectorTb cerVectorTb = cerVectorTbMapper.selectOneByPlasmidNameAndVectorTaskId(plasmid.plasmidName, cerVectorTaskTb.getId());
            if (cerVectorTb == null) {
                throw new BusinessException("找不到质粒信息");
            }
            cerVectorTb.setBacterialResistance(plasmid.bacterialResistance);
            cerVectorTb.setPlasmidSpecificPrimers(plasmid.plasmidSpecificPrimers);
            cerVectorTb.setCopyNumber(plasmid.copyNumber);
            cerVectorTb.setSelectionMarker(plasmid.selectionMarker);
            cerVectorTbMapper.updateById(cerVectorTb);
        }

        return ResponseResult.getSuccess("ok");

    }

    @GetMapping("/cleanPlasmidCheck")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanPlasmidCheck() {

        List<Plasmid> plasmidList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\上线\\质粒信息.xlsx", Plasmid.class);
        Map<String, List<Plasmid>> map = plasmidList.stream().filter(plasmid -> StringUtils.isNotEmpty(plasmid.plasmidName)).collect(Collectors.groupingBy(Plasmid::getVectorTaskCode));
        map.forEach((vectorTaskCode, plasmids) -> {
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
            List<CerPlasmidQualityTb> cerPlasmidQualityTbList = cerPlasmidQualityTbMapper.selectAllByVectorTaskId(cerVectorTaskTb.getId());
            cerPlasmidQualityTbMapper.deleteByVectorTaskId(cerVectorTaskTb.getId());
            if (CollectionUtil.isEmpty(cerPlasmidQualityTbList)) {
                throw new BusinessException("找不到质检信息");
            }
            for (Plasmid plasmid : plasmids) {
                CerPlasmidQualityTb cerPlasmidQualityTb = new CerPlasmidQualityTb();
                cerPlasmidQualityTb.setSubProjectId(cerVectorTaskTb.getSubProjectId());
                cerPlasmidQualityTb.setProjectId(cerVectorTaskTb.getProjectId());
                cerPlasmidQualityTb.setVectorTaskId(cerVectorTaskTb.getId());
                cerPlasmidQualityTb.setPlasmidName(plasmid.getPlasmidName());
                cerPlasmidQualityTb.setQualityInspectionNumber(plasmid.getPlasmidName());
                cerPlasmidQualityTb.setQualityInspectionResult("pass");
                cerPlasmidQualityTb.setAgrobacteriumInformation(plasmid.getAgrobacteriumInformation());
                cerPlasmidQualityTb.setCreateUserName("张立肖");
                cerPlasmidQualityTb.setCreateUserId(24);
                cerPlasmidQualityTb.setUpdateTime(new Date());
                cerPlasmidQualityTb.setCreateTime(new Date());
                if (StringUtils.isNotEmpty(plasmid.agrobacteriumInformation)) {
                    cerPlasmidQualityTb.setQualityInspectionType(JSONUtil.toJsonStr(Arrays.asList("2")));
                } else {
                    cerPlasmidQualityTb.setQualityInspectionType(JSONUtil.toJsonStr(Arrays.asList("1")));
                }

                cerPlasmidQualityTb.setAgrobacteriumResistance(plasmid.getAgrobacteriumResistance());
                cerPlasmidQualityTb.setPlasmidConcentration(plasmid.getPlasmidConcentration());
                cerPlasmidQualityTb.setExtractionKit(plasmid.getExtractionKit());
                cerPlasmidQualityTb.setTaskStatus(cerPlasmidQualityTbList.get(0).getTaskStatus());
                cerPlasmidQualityTb.setTaskNum(cerPlasmidQualityTbList.get(0).getTaskNum());
                cerPlasmidQualityTb.setFileUrls(JSONUtil.toJsonStr(cerPlasmidQualityTbList.get(0).getFileUrls()));
                cerPlasmidQualityTb.setProjectCode(cerVectorTaskTb.getProjectCode());
                cerPlasmidQualityTb.setSubProjectCode(cerVectorTaskTb.getSubProjectCode());
                cerPlasmidQualityTb.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
                cerPlasmidQualityTb.setRemark(cerPlasmidQualityTbList.get(0).getRemark());
                cerPlasmidQualityTbMapper.insert(cerPlasmidQualityTb);
            }

        });


        return ResponseResult.getSuccess("ok");

    }

    @Data
    public static class Plasmid {

        /**
         * 质粒名称
         */
        @ExcelProperty("质粒名称")
        private String plasmidName;

        /**
         * 细菌抗性
         */
        @ExcelProperty("细菌抗性")
        private String bacterialResistance;

        /**
         * 质粒特异性引物
         */
        @ExcelProperty("质粒特异性引物")
        private String plasmidSpecificPrimers;

        /**
         * 拷贝数
         */
        @ExcelProperty("拷贝数")
        private String copyNumber;
        /**
         * 植物筛选标记
         */
        @ExcelProperty("植物筛选标记")
        private String selectionMarker;

        /**
         * 农杆菌信息
         */
        @ExcelProperty("质检农杆菌信息")
        private String agrobacteriumInformation;


        @ExcelProperty("质检农杆菌抗性")
        private String agrobacteriumResistance;

        /**
         * 质粒浓度
         */
        @ExcelProperty("质检质粒浓度")
        private String plasmidConcentration;

        /**
         * 提取试剂盒
         */
        @ExcelProperty("质检提取试剂盒")
        private String extractionKit;


        @ExcelProperty("实施方案编号")
        private String vectorTaskCode;
    }
}
