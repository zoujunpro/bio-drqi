package com.bio.drqi.manage.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.uuid.IdUtils;
import com.bio.drqi.common.enums.*;
import com.bio.drqi.contents.CerProjectContents;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.dto.project.VectorTaskAddDTO;
import com.bio.drqi.manage.dto.seed.SeedInStoreDTO;
import com.bio.drqi.manage.flowtask.plant.PlantSampleTestTaskService;
import com.bio.drqi.manage.service.common.SeedPlantService;
import com.bio.drqi.mapper.*;
import com.bio.drqi.tc.service.dto.TcSampleTestTaskDTO;
import com.bio.print.PlantApplyPrintDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
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
    private CerProjectTbMapper cerProjectTbMapper;

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
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private BioDictMapper bioDictMapper;

    @Resource
    private SeedProduceAddressDictMapper seedProduceAddressDictMapper;


    @Resource
    private SeedPlantService seedPlantService;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private SeedStockInLogMapper seedStockInLogMapper;

    @Resource
    private SeedStockOutLogMapper seedStockOutLogMapper;

    @Resource
    private SeedStockDestructionLogMapper seedStockDestructionLogMapper;

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
    private BioSampleTestOneResultTbMapper bioSampleTestOneResultTbMapper;

    @Resource
    private BioSampleTestTwoResultTbMapper bioSampleTestTwoResultTbMapper;

    @Resource
    private BioSampleTestTwoResultDetailTbMapper bioSampleTestTwoResultDetailTbMapper;


    @Resource
    private TcPollinationSingleNumTbMapper tcPollinationSingleNumTbMapper;

    @Resource
    private SeedQualityCheckDtlTbMapper seedQualityCheckDtlTbMapper;

    @Resource
    private SeedQualityCheckConfigMapper seedQualityCheckConfigMapper;


    @GetMapping("cleanSeedStockPlantNum")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSeedStockPlantNum() {
        long start = System.currentTimeMillis();
        log.info("cleanSeedStockPlantNum#开始清洗种子库种植编号");
        List<SeedStockTb> seedStockTbList = seedStockTbMapper.selectSelective(null);
        if (CollectionUtil.isEmpty(seedStockTbList)) {
            log.info("cleanSeedStockPlantNum#无种子库数据需要清洗");
            return ResponseResult.getSuccess("无种子库数据需要清洗");
        }
        int updateCount = 0;
        int skipCount = 0;
        int total = seedStockTbList.size();
        log.info("cleanSeedStockPlantNum#种子库数据加载完成，数量={}", total);
        for (int i = 0; i < total; i++) {
            SeedStockTb seedStockTb = seedStockTbList.get(i);
            String plantCode = seedStockTb.getPlantCode();
            if (StringUtils.isEmpty(plantCode)) {
                skipCount++;
                continue;
            }
            List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllBySampleCode(plantCode);
            if (CollectionUtil.isEmpty(bioSampleTestTbList)) {
                seedStockTb.setRemarks(appendPlantCodeToRemarks(seedStockTb.getRemarks(), plantCode));
                seedStockTb.setPlantCode(null);
                seedStockTb.setUpdateTime(new Date());
                seedStockTbMapper.updateById(seedStockTb);
                updateCount++;
                log.info("cleanSeedStockPlantNum#种植编号不在取样列表中，已清空种植编号并追加备注，seedStockId={}，seedNum={}，plantCode={}",
                        seedStockTb.getId(), seedStockTb.getSeedNum(), plantCode);
            } else {
                skipCount++;
            }
            if ((i + 1) % 500 == 0 || i + 1 == total) {
                log.info("cleanSeedStockPlantNum#清洗进度 {}/{}，更新={}，跳过={}", i + 1, total, updateCount, skipCount);
            }
        }
        log.info("cleanSeedStockPlantNum#清洗完成，耗时={}ms，更新={}，跳过={}",
                System.currentTimeMillis() - start, updateCount, skipCount);
        return ResponseResult.getSuccess("清洗完成，更新=" + updateCount + "，跳过=" + skipCount);
    }

    private String appendPlantCodeToRemarks(String remarks, String plantCode) {
        String appendText = plantCode;
        if (StringUtils.isEmpty(remarks)) {
            return appendText;
        }
        if (remarks.contains(appendText)) {
            return remarks;
        }
        return remarks + "；" + appendText;
    }


    @GetMapping("cleanSeedStockProjectInfo")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSeedStockProjectInfo() {
        long start = System.currentTimeMillis();
        log.info("cleanSeedStockProjectInfo#开始清洗seed_stock_tb项目字段");
        List<SeedStockTb> seedStockTbList = seedStockTbMapper.selectList(null).stream()
                .filter(seedStockTb -> StringUtils.isNotEmpty(seedStockTb.getVectorTaskCode()))
                .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(seedStockTbList)) {
            return ResponseResult.getSuccess("无实施方案编号不为空的种子库数据需要清洗");
        }

        Map<String, CerVectorTaskTb> vectorTaskMap = cerVectorTaskTbMapper.selectList(null).stream()
                .filter(cerVectorTaskTb -> StringUtils.isNotEmpty(cerVectorTaskTb.getVectorTaskCode()))
                .collect(Collectors.toMap(CerVectorTaskTb::getVectorTaskCode, cerVectorTaskTb -> cerVectorTaskTb, (left, right) -> left));
        Map<String, CerProjectTb> projectMap = cerProjectTbMapper.selectList(null).stream()
                .filter(cerProjectTb -> StringUtils.isNotEmpty(cerProjectTb.getProjectCode()))
                .collect(Collectors.toMap(CerProjectTb::getProjectCode, cerProjectTb -> cerProjectTb, (left, right) -> left));

        int updateCount = 0;
        int vectorTaskMissCount = 0;
        int projectMissCount = 0;
        int noChangeCount = 0;
        int total = seedStockTbList.size();
        log.info("cleanSeedStockProjectInfo#数据加载完成，种子数={}，实施方案数={}，项目数={}", total, vectorTaskMap.size(), projectMap.size());
        for (int i = 0; i < total; i++) {
            SeedStockTb seedStockTb = seedStockTbList.get(i);
            CerVectorTaskTb cerVectorTaskTb = vectorTaskMap.get(seedStockTb.getVectorTaskCode());
            if (cerVectorTaskTb == null || StringUtils.isEmpty(cerVectorTaskTb.getProjectCode())) {
                vectorTaskMissCount++;
                continue;
            }
            CerProjectTb cerProjectTb = projectMap.get(cerVectorTaskTb.getProjectCode());
            if (cerProjectTb == null) {
                projectMissCount++;
                continue;
            }
            String projectCode = cerProjectTb.getProjectCode();
            String projectName = cerProjectTb.getProjectName();
            if (Objects.equals(projectCode, seedStockTb.getProjectCode()) && Objects.equals(projectName, seedStockTb.getTargetCharacter())) {
                noChangeCount++;
                continue;
            }

            SeedStockTb updateSeedStockTb = new SeedStockTb();
            updateSeedStockTb.setId(seedStockTb.getId());
            updateSeedStockTb.setProjectCode(projectCode);
            updateSeedStockTb.setTargetCharacter(projectName);
            updateSeedStockTb.setUpdateTime(new Date());
            seedStockTbMapper.updateById(updateSeedStockTb);
            updateCount++;

            if ((i + 1) % 500 == 0 || i + 1 == total) {
                log.info("cleanSeedStockProjectInfo#清洗进度 {}/{}，更新={}，实施方案缺失={}，项目缺失={}，无需更新={}",
                        i + 1, total, updateCount, vectorTaskMissCount, projectMissCount, noChangeCount);
            }
        }

        log.info("cleanSeedStockProjectInfo#清洗完成，耗时={}ms，更新={}，实施方案缺失={}，项目缺失={}，无需更新={}",
                System.currentTimeMillis() - start, updateCount, vectorTaskMissCount, projectMissCount, noChangeCount);
        return ResponseResult.getSuccess("清洗完成，更新：" + updateCount
                + "，实施方案缺失：" + vectorTaskMissCount
                + "，项目缺失：" + projectMissCount
                + "，无需更新：" + noChangeCount);
    }

    @GetMapping("cleanBioSampleTestTime")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanBioSampleTestTime() {
        long start = System.currentTimeMillis();
        log.info("cleanBioSampleTestTime#开始清洗bio_sample_test_tb时间字段");
        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectList(null);
        if (CollectionUtil.isEmpty(bioSampleTestTbList)) {
            return ResponseResult.getSuccess("无取样检测数据需要清洗");
        }

        log.info("cleanBioSampleTestTime#取样检测数据加载完成，数量={}", bioSampleTestTbList.size());
        Map<String, BioTaskDtlTb> taskMap = bioTaskDtlTbMapper.selectList(null).stream()
                .filter(bioTaskDtlTb -> StringUtils.isNotEmpty(bioTaskDtlTb.getTaskNum()))
                .collect(Collectors.toMap(BioTaskDtlTb::getTaskNum, bioTaskDtlTb -> bioTaskDtlTb, (left, right) -> left));
        Map<String, BioSampleApplyTb> sampleApplyMap = bioSampleApplyTbMapper.selectList(null).stream()
                .filter(bioSampleApplyTb -> StringUtils.isNotEmpty(bioSampleApplyTb.getApplyNo()))
                .collect(Collectors.toMap(BioSampleApplyTb::getApplyNo, bioSampleApplyTb -> bioSampleApplyTb, (left, right) -> left));
        List<BioSampleTestOneResultTb> oneResultList = bioSampleTestOneResultTbMapper.selectList(null);
        Map<String, List<BioSampleTestOneResultTb>> oneResultApplySampleMap = oneResultList.stream()
                .filter(oneResult -> StringUtils.isNotEmpty(oneResult.getTaskNum()) && StringUtils.isNotEmpty(oneResult.getSampleCode()))
                .collect(Collectors.groupingBy(oneResult -> sampleTimeKey(oneResult.getTaskNum(), oneResult.getSampleCode())));
        Map<String, List<BioSampleTestOneResultTb>> oneResultSampleMap = oneResultList.stream()
                .filter(oneResult -> StringUtils.isNotEmpty(oneResult.getSampleCode()))
                .collect(Collectors.groupingBy(BioSampleTestOneResultTb::getSampleCode));
        oneResultApplySampleMap.values().forEach(this::sortOneResultDesc);
        oneResultSampleMap.values().forEach(this::sortOneResultDesc);

        List<BioSampleTestTwoResultTb> twoResultList = bioSampleTestTwoResultTbMapper.selectList(null);
        Map<String, List<BioSampleTestTwoResultTb>> twoResultApplySampleMap = twoResultList.stream()
                .filter(twoResult -> StringUtils.isNotEmpty(twoResult.getApplyNo()) && StringUtils.isNotEmpty(twoResult.getSampleCode()))
                .collect(Collectors.groupingBy(twoResult -> sampleTimeKey(twoResult.getApplyNo(), twoResult.getSampleCode())));
        Map<String, List<BioSampleTestTwoResultTb>> twoResultSampleMap = twoResultList.stream()
                .filter(twoResult -> StringUtils.isNotEmpty(twoResult.getSampleCode()))
                .collect(Collectors.groupingBy(BioSampleTestTwoResultTb::getSampleCode));
        twoResultApplySampleMap.values().forEach(this::sortTwoResultDesc);
        twoResultSampleMap.values().forEach(this::sortTwoResultDesc);
        log.info("cleanBioSampleTestTime#关联数据加载完成，工单数={}，申请数={}，一代结果数={}，二代结果数={}",
                taskMap.size(), sampleApplyMap.size(), oneResultList.size(), twoResultList.size());

        int updateCount = 0;
        int testTimeByOneCount = 0;
        int testTimeByTwoCount = 0;
        int testTimeByCreateCount = 0;
        int checkTimeCount = 0;
        int noTestTimeUnfinishedCount = 0;
        int skipCount = 0;
        int total = bioSampleTestTbList.size();
        for (int i = 0; i < total; i++) {
            BioSampleTestTb bioSampleTestTb = bioSampleTestTbList.get(i);
            if (StringUtils.isEmpty(bioSampleTestTb.getApplyNo())) {
                skipCount++;
                continue;
            }

            BioTaskDtlTb bioTaskDtlTb = taskMap.get(bioSampleTestTb.getApplyNo());
            BioSampleApplyTb bioSampleApplyTb = sampleApplyMap.get(bioSampleTestTb.getApplyNo());
            boolean taskFinished = bioTaskDtlTb != null && BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus());
            String testType = bioSampleApplyTb == null ? null : bioSampleApplyTb.getLayoutFlag();

            BioSampleTestTb updateBioSampleTestTb = new BioSampleTestTb();
            updateBioSampleTestTb.setId(bioSampleTestTb.getId());
            boolean needUpdate = false;

            if (StringUtils.isEmpty(bioSampleTestTb.getTestTime())) {
                TestTimeResult testTimeResult = findSampleTestTime(bioSampleTestTb, testType, oneResultApplySampleMap, oneResultSampleMap, twoResultApplySampleMap, twoResultSampleMap);
                if (testTimeResult != null && StringUtils.isNotEmpty(testTimeResult.getTestTime())) {
                    updateBioSampleTestTb.setTestTime(testTimeResult.getTestTime());
                    needUpdate = true;
                    if ("one".equals(testTimeResult.getSourceType())) {
                        testTimeByOneCount++;
                    } else if ("more".equals(testTimeResult.getSourceType())) {
                        testTimeByTwoCount++;
                    }
                } else if (taskFinished && bioSampleTestTb.getCreateTime() != null) {
                    updateBioSampleTestTb.setTestTime(formatDateTime(bioSampleTestTb.getCreateTime()));
                    needUpdate = true;
                    testTimeByCreateCount++;
                } else if (!taskFinished) {
                    noTestTimeUnfinishedCount++;
                }
            }

            if (taskFinished && bioTaskDtlTb.getUpdateTime() != null) {
                String checkTime = formatDateTime(bioTaskDtlTb.getUpdateTime());
                if (!checkTime.equals(bioSampleTestTb.getCheckTime())) {
                    updateBioSampleTestTb.setCheckTime(checkTime);
                    needUpdate = true;
                    checkTimeCount++;
                }
            }

            if (needUpdate) {
                bioSampleTestTbMapper.updateById(updateBioSampleTestTb);
                updateCount++;
            }
            if ((i + 1) % 500 == 0 || i + 1 == total) {
                log.info("cleanBioSampleTestTime#清洗进度 {}/{}，已更新={}，补test_time一代={}，二代={}，创建时间={}，未完成且无检测时间={}，补check_time={}，跳过={}",
                        i + 1, total, updateCount, testTimeByOneCount, testTimeByTwoCount, testTimeByCreateCount, noTestTimeUnfinishedCount, checkTimeCount, skipCount);
            }
        }
        log.info("cleanBioSampleTestTime#清洗完成，耗时={}ms，更新={}，一代={}，二代={}，创建时间={}，未完成且无检测时间={}，check_time={}，跳过={}",
                System.currentTimeMillis() - start, updateCount, testTimeByOneCount, testTimeByTwoCount, testTimeByCreateCount, noTestTimeUnfinishedCount, checkTimeCount, skipCount);
        return ResponseResult.getSuccess("清洗完成，更新：" + updateCount
                + "，一代测序补test_time：" + testTimeByOneCount
                + "，二代测序补test_time：" + testTimeByTwoCount
                + "，创建时间补test_time：" + testTimeByCreateCount
                + "，未完成且无检测时间不更新：" + noTestTimeUnfinishedCount
                + "，补check_time：" + checkTimeCount
                + "，跳过：" + skipCount);
    }

    private TestTimeResult findSampleTestTime(BioSampleTestTb bioSampleTestTb, String testType,
                                              Map<String, List<BioSampleTestOneResultTb>> oneResultApplySampleMap,
                                              Map<String, List<BioSampleTestOneResultTb>> oneResultSampleMap,
                                              Map<String, List<BioSampleTestTwoResultTb>> twoResultApplySampleMap,
                                              Map<String, List<BioSampleTestTwoResultTb>> twoResultSampleMap) {
        if ("one".equals(testType)) {
            TestTimeResult testTimeResult = findOneSampleTestTime(bioSampleTestTb, oneResultApplySampleMap, oneResultSampleMap);
            return testTimeResult != null ? testTimeResult : findTwoSampleTestTime(bioSampleTestTb, twoResultApplySampleMap, twoResultSampleMap);
        }
        if ("more".equals(testType)) {
            TestTimeResult testTimeResult = findTwoSampleTestTime(bioSampleTestTb, twoResultApplySampleMap, twoResultSampleMap);
            return testTimeResult != null ? testTimeResult : findOneSampleTestTime(bioSampleTestTb, oneResultApplySampleMap, oneResultSampleMap);
        }
        TestTimeResult testTimeResult = findOneSampleTestTime(bioSampleTestTb, oneResultApplySampleMap, oneResultSampleMap);
        return testTimeResult != null ? testTimeResult : findTwoSampleTestTime(bioSampleTestTb, twoResultApplySampleMap, twoResultSampleMap);
    }

    private TestTimeResult findOneSampleTestTime(BioSampleTestTb bioSampleTestTb,
                                                 Map<String, List<BioSampleTestOneResultTb>> oneResultApplySampleMap,
                                                 Map<String, List<BioSampleTestOneResultTb>> oneResultSampleMap) {
        List<BioSampleTestOneResultTb> oneResultList = oneResultApplySampleMap.get(sampleTimeKey(bioSampleTestTb.getApplyNo(), bioSampleTestTb.getSampleCode()));
        if (CollectionUtil.isEmpty(oneResultList)) {
            oneResultList = oneResultSampleMap.get(bioSampleTestTb.getSampleCode());
        }
        if (CollectionUtil.isEmpty(oneResultList)) {
            return null;
        }
        for (BioSampleTestOneResultTb oneResult : oneResultList) {
            if (StringUtils.isNotEmpty(oneResult.getTestTime())) {
                return new TestTimeResult(oneResult.getTestTime(), "one");
            }
        }
        return oneResultList.get(0).getCreateTime() == null ? null : new TestTimeResult(formatDateTime(oneResultList.get(0).getCreateTime()), "one");
    }

    private TestTimeResult findTwoSampleTestTime(BioSampleTestTb bioSampleTestTb,
                                                 Map<String, List<BioSampleTestTwoResultTb>> twoResultApplySampleMap,
                                                 Map<String, List<BioSampleTestTwoResultTb>> twoResultSampleMap) {
        List<BioSampleTestTwoResultTb> twoResultList = twoResultApplySampleMap.get(sampleTimeKey(bioSampleTestTb.getApplyNo(), bioSampleTestTb.getSampleCode()));
        if (CollectionUtil.isEmpty(twoResultList)) {
            twoResultList = twoResultSampleMap.get(bioSampleTestTb.getSampleCode());
        }
        if (CollectionUtil.isEmpty(twoResultList) || twoResultList.get(0).getCreateTime() == null) {
            return null;
        }
        return new TestTimeResult(formatDateTime(twoResultList.get(0).getCreateTime()), "more");
    }

    private String sampleTimeKey(String applyNo, String sampleCode) {
        return applyNo + "|" + sampleCode;
    }

    private void sortOneResultDesc(List<BioSampleTestOneResultTb> oneResultList) {
        oneResultList.sort(Comparator.comparing(BioSampleTestOneResultTb::getId, Comparator.nullsLast(Integer::compareTo)).reversed());
    }

    private void sortTwoResultDesc(List<BioSampleTestTwoResultTb> twoResultList) {
        twoResultList.sort(Comparator.comparing(BioSampleTestTwoResultTb::getId, Comparator.nullsLast(Integer::compareTo)).reversed());
    }

    private String formatDateTime(Date date) {
        return DateUtil.format(date, DatePattern.NORM_DATETIME_PATTERN);
    }

    @Data
    private static class TestTimeResult {
        private final String testTime;
        private final String sourceType;
    }


    @GetMapping("cleanPlasmidSpecificPrimers")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanPlasmidSpecificPrimers() {
        CerVectorTb query = new CerVectorTb();
        List<CerVectorTb> cerVectorTbList = cerVectorTbMapper.selectSelective(query);
        if (CollectionUtil.isEmpty(cerVectorTbList)) {
            return ResponseResult.getSuccess("无质粒数据需要清洗");
        }

        Map<String, List<CerVectorTb>> vectorMap = cerVectorTbList.stream()
                .filter(cerVectorTb -> StringUtils.isEmpty(trimToNull(cerVectorTb.getPlasmidSpecificPrimers())))
                .filter(cerVectorTb -> StringUtils.isNotEmpty(trimToNull(cerVectorTb.getTaskNum())))
                .collect(Collectors.groupingBy(cerVectorTb -> trimToNull(cerVectorTb.getTaskNum())));
        if (vectorMap.isEmpty()) {
            return ResponseResult.getSuccess("无缺失质粒特异性引物的数据需要清洗");
        }

        int updateCount = 0;
        int skipCount = 0;
        int taskMissCount = 0;
        int formErrorCount = 0;
        for (Map.Entry<String, List<CerVectorTb>> entry : vectorMap.entrySet()) {
            String taskNum = entry.getKey();
            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(taskNum);
            if (bioTaskDtlTb == null || StringUtils.isEmpty(trimToNull(bioTaskDtlTb.getTaskForm()))) {
                taskMissCount += entry.getValue().size();
                log.warn("cleanPlasmidSpecificPrimers#任务或任务表单不存在，taskNum={}", taskNum);
                continue;
            }

            VectorTaskAddDTO vectorTaskAddDTO;
            try {
                vectorTaskAddDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), VectorTaskAddDTO.class);
            } catch (Exception e) {
                formErrorCount += entry.getValue().size();
                log.warn("cleanPlasmidSpecificPrimers#任务表单解析失败，taskNum={}", taskNum, e);
                continue;
            }

            List<VectorTaskAddDTO.Vector> vectorList = vectorTaskAddDTO.getVectorList();
            if (CollectionUtil.isEmpty(vectorList)) {
                skipCount += entry.getValue().size();
                log.warn("cleanPlasmidSpecificPrimers#任务表单缺少vectorList，taskNum={}", taskNum);
                continue;
            }

            Map<String, String> primerMap = vectorList.stream()
                    .filter(vector -> StringUtils.isNotEmpty(trimToNull(vector.getPlasmidName())))
                    .filter(vector -> StringUtils.isNotEmpty(trimToNull(vector.getPlasmidSpecificPrimers())))
                    .collect(Collectors.toMap(
                            vector -> normalizePlasmidName(vector.getPlasmidName()),
                            vector -> trimToNull(vector.getPlasmidSpecificPrimers()),
                            (left, right) -> left
                    ));
            String singlePrimer = primerMap.size() == 1 ? primerMap.values().iterator().next() : null;

            for (CerVectorTb cerVectorTb : entry.getValue()) {
                String primer = primerMap.get(normalizePlasmidName(cerVectorTb.getPlasmidName()));
                if (StringUtils.isEmpty(primer) && entry.getValue().size() == 1) {
                    primer = singlePrimer;
                }
                if (StringUtils.isEmpty(primer)) {
                    skipCount++;
                    log.warn("cleanPlasmidSpecificPrimers#未匹配到质粒特异性引物，taskNum={}，plasmidName={}", taskNum, cerVectorTb.getPlasmidName());
                    continue;
                }

                CerVectorTb update = new CerVectorTb();
                update.setId(cerVectorTb.getId());
                update.setPlasmidSpecificPrimers(primer);
                cerVectorTbMapper.updateById(update);
                updateCount++;
            }
        }
        return ResponseResult.getSuccess("清洗完成，更新：" + updateCount + "，跳过：" + skipCount + "，任务缺失：" + taskMissCount + "，表单异常：" + formErrorCount);
    }

    private String normalizePlasmidName(String plasmidName) {
        String value = trimToNull(plasmidName);
        return value == null ? null : value.replaceAll("\\s+", "").toUpperCase();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimValue = value.trim();
        return trimValue.length() == 0 ? null : trimValue;
    }

    @GetMapping("cleanPlasmidQualityInspectionType")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanPlasmidQualityInspectionType() {
        long start = System.currentTimeMillis();
        log.info("cleanPlasmidQualityInspectionType#开始清洗质粒质检质检类型");
        List<CerPlasmidQualityTb> plasmidQualityTbList = cerPlasmidQualityTbMapper.selectList(null);
        if (CollectionUtil.isEmpty(plasmidQualityTbList)) {
            return ResponseResult.getSuccess("无质粒质检数据需要清洗");
        }

        int updateCount = 0;
        int skipCount = 0;
        int noChangeCount = 0;
        int total = plasmidQualityTbList.size();
        for (int i = 0; i < total; i++) {
            CerPlasmidQualityTb plasmidQualityTb = plasmidQualityTbList.get(i);
            String qualityInspectionType = trimToNull(plasmidQualityTb.getQualityInspectionType());
            String normalizedType = normalizePlasmidQualityInspectionType(qualityInspectionType);
            if (StringUtils.isEmpty(normalizedType)) {
                skipCount++;
                continue;
            }
            if (Objects.equals(qualityInspectionType, normalizedType)) {
                noChangeCount++;
                continue;
            }

            CerPlasmidQualityTb update = new CerPlasmidQualityTb();
            update.setId(plasmidQualityTb.getId());
            update.setQualityInspectionType(normalizedType);
            cerPlasmidQualityTbMapper.updateById(update);
            updateCount++;

            if ((i + 1) % 500 == 0 || i + 1 == total) {
                log.info("cleanPlasmidQualityInspectionType#清洗进度 {}/{}，更新={}，无需更新={}，跳过={}",
                        i + 1, total, updateCount, noChangeCount, skipCount);
            }
        }

        log.info("cleanPlasmidQualityInspectionType#清洗完成，耗时={}ms，更新={}，无需更新={}，跳过={}",
                System.currentTimeMillis() - start, updateCount, noChangeCount, skipCount);
        return ResponseResult.getSuccess("清洗完成，更新：" + updateCount
                + "，无需更新：" + noChangeCount
                + "，跳过：" + skipCount);
    }

    private String normalizePlasmidQualityInspectionType(String qualityInspectionType) {
        String type = trimToNull(qualityInspectionType);
        if (StringUtils.isEmpty(type)) {
            return null;
        }
        if (type.startsWith("[")) {
            try {
                List<String> typeList = JSONUtil.toList(type, String.class);
                if (CollectionUtil.isEmpty(typeList)) {
                    return null;
                }
                type = trimToNull(typeList.get(0));
            } catch (Exception e) {
                log.warn("normalizePlasmidQualityInspectionType#质检类型解析失败，qualityInspectionType={}", qualityInspectionType, e);
                return null;
            }
        }
        if ("1".equals(type) || "质粒制备".equals(type)) {
            return "1";
        }
        if ("2".equals(type) || "农杆菌检测".equals(type) || "农杆菌转化".equals(type)) {
            return "2";
        }
        return null;
    }

    @GetMapping("cleanSeedQualityCheckResult20260430")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSeedQualityCheckResult20260430() {
        List<SeedQualityCheckDtlTb> seedQualityCheckDtlTbList = seedQualityCheckDtlTbMapper.selectSelective(null);
        if (CollectionUtil.isEmpty(seedQualityCheckDtlTbList)) {
            return ResponseResult.getSuccess("无日常检测明细需要清洗");
        }

        List<SeedQualityCheckConfig> seedQualityCheckConfigList = seedQualityCheckConfigMapper.selectAllOrderByIdDesc();
        Map<String, String> fieldDescMap = seedQualityCheckConfigList.stream()
                .collect(Collectors.toMap(SeedQualityCheckConfig::getFieldCode, SeedQualityCheckConfig::getFieldName, (left, right) -> left));

        Map<String, LinkedHashMap<String, SeedStockTb.CheckResultContent>> seedCheckResultMap = new LinkedHashMap<>();
        for (SeedQualityCheckDtlTb seedQualityCheckDtlTb : seedQualityCheckDtlTbList) {
            if (StringUtils.isEmpty(seedQualityCheckDtlTb.getSeedNum()) || StringUtils.isEmpty(seedQualityCheckDtlTb.getCheckResult())) {
                continue;
            }
            Map<String, Object> checkResultMap = JSONUtil.toBean(seedQualityCheckDtlTb.getCheckResult(), Map.class);
            if (checkResultMap == null || checkResultMap.isEmpty()) {
                continue;
            }
            LinkedHashMap<String, SeedStockTb.CheckResultContent> currentSeedCheckResultMap =
                    seedCheckResultMap.computeIfAbsent(seedQualityCheckDtlTb.getSeedNum(), seedNum -> new LinkedHashMap<>());
            checkResultMap.forEach((fieldCode, value) -> {
                if (value == null || currentSeedCheckResultMap.containsKey(fieldCode)) {
                    return;
                }
                SeedStockTb.CheckResultContent checkResultContent = new SeedStockTb.CheckResultContent();
                checkResultContent.setType(fieldCode);
                checkResultContent.setDesc(fieldDescMap.getOrDefault(fieldCode, fieldCode));
                checkResultContent.setValue(value);
                checkResultContent.setUserId(null);
                checkResultContent.setUserName(seedQualityCheckDtlTb.getCreateUser());
                checkResultContent.setTime(seedQualityCheckDtlTb.getCreateTime() == null ? null : DateUtil.format(seedQualityCheckDtlTb.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN));
                currentSeedCheckResultMap.put(fieldCode, checkResultContent);
            });
        }

        int updateCount = 0;
        int skipCount = 0;
        for (Map.Entry<String, LinkedHashMap<String, SeedStockTb.CheckResultContent>> entry : seedCheckResultMap.entrySet()) {
            if (entry.getValue().isEmpty()) {
                skipCount++;
                continue;
            }
            SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(entry.getKey());
            if (seedStockTb == null) {
                skipCount++;
                log.warn("cleanSeedQualityCheckResult20260430#种子不存在，seedNum={}", entry.getKey());
                continue;
            }
            seedStockTb.setCheckResult(JSONUtil.toJsonStr(new ArrayList<>(entry.getValue().values())));
            seedStockTbMapper.updateById(seedStockTb);
            updateCount++;
        }
        return ResponseResult.getSuccess("清洗完成，更新种子数：" + updateCount + "，跳过：" + skipCount);
    }


    @GetMapping("cleanTcSampleApply20260316")
    public ResponseResult<Seed> cleanTcSampleApply20260316() {
        List<TcSampleTestApplyTb> tcSampleTestApplyTbList = tcSampleTestApplyTbMapper.selectSelective(null);
        for (TcSampleTestApplyTb tcSampleTestApplyTb : tcSampleTestApplyTbList) {
            BioSampleApplyTb bioSampleApplyTb = bioSampleApplyTbMapper.selectOneByApplyNo(tcSampleTestApplyTb.getSampleApplyNum());
            if (bioSampleApplyTb == null) {
                continue;
            }
            List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByApplyNo(bioSampleApplyTb.getApplyNo());

            if (SampleTestApplyTypeEnum.first.name().equals(bioSampleApplyTb.getApplyType())) {
                StringBuffer sampleCodeRangeBuff = new StringBuffer();
                Map<String, List<BioSampleTestTb>> plantSampleTestTbMap = bioSampleTestTbList.stream().collect(Collectors.groupingBy(bioSampleTestTb -> bioSampleTestTb.getSampleCode().replaceAll("\\d", "")));
                plantSampleTestTbMap.forEach((sampleCodePrefix, sampleTestList) -> {
                    sampleTestList = sampleTestList.stream().filter(sampleTest -> sampleTest.getSampleCode().startsWith(sampleCodePrefix)).sorted(Comparator.comparing(sampleTest -> Integer.valueOf(sampleTest.getSampleCode().substring(sampleCodePrefix.length())))).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(sampleTestList)) {
                        sampleCodeRangeBuff.append(sampleTestList.get(0).getSampleCode() + "-" + sampleTestList.get(sampleTestList.size() - 1).getSampleCode()).append(",");
                    }
                });
                if (StringUtils.isNotEmpty(sampleCodeRangeBuff.toString())) {
                    bioSampleApplyTb.setSampleCodeRange(sampleCodeRangeBuff.substring(0, sampleCodeRangeBuff.length() - 1));
                    bioSampleApplyTb.setVectorTaskCodes(JSONUtil.toJsonStr(bioSampleTestTbList.stream().filter(tcSampleTestTb -> StringUtils.isNotEmpty(tcSampleTestTb.getVectorTaskCode())).map(BioSampleTestTb::getVectorTaskCode).distinct().collect(Collectors.toList())).replace("[", "").replace("]", "").replace("\"", ""));
                }
            }
            bioSampleApplyTb.setApplyNumber(bioSampleTestTbList.size());
            bioSampleApplyTbMapper.updateById(bioSampleApplyTb);
        }
        return ResponseResult.getSuccess("ok");
    }


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
            List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectAllBySampleApplyNum(tcSampleTestApplyTb.getSampleApplyNum());
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
                //如果是首次取样，更新取样区间
                if (SampleTestApplyTypeEnum.first.name().equals(tcSampleTestTaskDTO.getApplyType())) {
                    StringBuffer sampleCodeRangeBuff = new StringBuffer();
                    Map<String, List<TcSampleTestTb>> plantSampleTestTbMap = tcSampleTestTbList.stream().collect(Collectors.groupingBy(tcSampleTestTb -> tcSampleTestTb.getSampleCode().replaceAll("\\d", "")));
                    plantSampleTestTbMap.forEach((sampleCodePrefix, sampleTestList) -> {
                        sampleTestList = sampleTestList.stream().filter(sampleTest -> sampleTest.getSampleCode().startsWith(sampleCodePrefix)).sorted(Comparator.comparing(sampleTest -> Integer.valueOf(sampleTest.getSampleCode().substring(sampleCodePrefix.length())))).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(sampleTestList)) {
                            sampleCodeRangeBuff.append(sampleTestList.get(0).getSampleCode() + "-" + sampleTestList.get(sampleTestList.size() - 1).getSampleCode()).append(",");
                        }
                    });
                    if (StringUtils.isNotEmpty(sampleCodeRangeBuff.toString())) {
                        bioSampleApplyTb.setSampleCodeRange(sampleCodeRangeBuff.substring(0, sampleCodeRangeBuff.length() - 1));
                        bioSampleApplyTb.setVectorTaskCodes(JSONUtil.toJsonStr(tcSampleTestTbList.stream().filter(tcSampleTestTb -> StringUtils.isNotEmpty(tcSampleTestTb.getVectorTaskCode())).map(TcSampleTestTb::getVectorTaskCode).distinct().collect(Collectors.toList())).replace("[", "").replace("]", "").replace("\"", ""));
                    }
                    bioSampleApplyTb.setApplyNumber(tcSampleTestTaskDTO.getFirstSampleApplyList().stream().map(TcSampleTestTaskDTO.FirstSampleApply::getSampleNum).mapToInt(Integer::intValue).sum());
                } else {
                    bioSampleApplyTb.setApplyNumber(tcSampleTestTaskDTO.getRepeatSampleApplyList().size());
                }
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
            TcSampleTestApplyTb tcSampleTestApplyTb = tcSampleTestApplyTbMapper.selectOneByTaskNum(tcSampleTestTb.getTaskNum());
            BioSampleTestTb bioSampleTestTb = new BioSampleTestTb();
            bioSampleTestTb.setVectorTaskCode(tcSampleTestTb.getVectorTaskCode());
            bioSampleTestTb.setSampleCode(tcSampleTestTb.getSampleCode());
            bioSampleTestTb.setApplyTime(tcSampleTestApplyTb.getCreateTime());
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
            bioSampleTestTb.setTcSampleCode(tcSampleTestTb.getTcSampleCode());
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

    @GetMapping("/cleanSeedStockIn20260420")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSeedStockIn20260420() {
        String excelPath = "/Users/zoujun/Downloads/定点转基因回交转育-海南-yxq-V1-2026-04-23.xlsx";
        String taskNum = "S0007592";
        Date operateDate = new Date();
        List<SeedInStockCleanExcelDTO> excelList = ExcelUtil.readExcel(excelPath, SeedInStockCleanExcelDTO.class);
        if (CollectionUtil.isEmpty(excelList)) {
            throw new BusinessException("Excel无数据");
        }

        List<BioDict> bioDictList = bioDictMapper.selectAll();
        List<CerSpeciesConf> cerSpeciesConfList = cerSpeciesConfMapper.selectList(null);
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        List<SeedProduceAddressDict> seedProduceAddressDictList = seedProduceAddressDictMapper.selectAll();
        Map<String, String> seedProduceAddressDictMap = seedProduceAddressDictList.stream().collect(Collectors.toMap(SeedProduceAddressDict::getAddressName, SeedProduceAddressDict::getAddressCode));
        Map<String, CerBreedDict> cerBreedDictMap = cerBreedDictList.stream().collect(Collectors.toMap(cerBreedDict -> cerBreedDict.getSpeciesCode() + ":" + cerBreedDict.getBreedName(), cerBreedDict -> cerBreedDict));
        Map<String, CerSpeciesConf> cerSpeciesConfMap = cerSpeciesConfList.stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesName, cerSpeciesConf -> cerSpeciesConf));
        Map<String, CerSpeciesConf> cerSpeciesConfCodeMap = cerSpeciesConfList.stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, cerSpeciesConf -> cerSpeciesConf));
        Map<String, BioDict> bioDictMap = bioDictList.stream().collect(Collectors.toMap(bioDict -> bioDict.getDictType() + ":" + bioDict.getDictValueName(), bioDict -> bioDict));

        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(taskNum);
        if (bioTaskDtlTb == null) {
            throw new BusinessException("找不到指定工单：" + taskNum);
        }
        bioTaskDtlTb.setTaskStatus(BioTaskStatusEnum.TASK_STATUS_2.status);

        SeedInStoreDTO seedInStoreDTO = new SeedInStoreDTO();
        SeedInStoreDTO.ApplyForm applyForm = new SeedInStoreDTO.ApplyForm();
        seedInStoreDTO.setApplyForm(applyForm);
        SeedInStoreDTO.ExecuteForm executeForm = new SeedInStoreDTO.ExecuteForm();
        executeForm.setExcelUrl(excelPath);
        List<SeedInStoreDTO.ExecuteFormContent> executeFormContentList = new ArrayList<>();

        for (SeedInStockCleanExcelDTO excelDTO : excelList) {
            if (StringUtils.isEmpty(excelDTO.getSpeciesName()) && StringUtils.isEmpty(excelDTO.getBreedName())) {
                continue;
            }
            log.info("cleanSeedStockIn20260420#excelDTO={}", JSONUtil.toJsonStr(excelDTO));
            fillSeedInStockExcelCode(excelDTO, bioDictMap, cerSpeciesConfMap, cerBreedDictMap, seedProduceAddressDictMap);

            SeedStockTb seedStockTb = buildSeedStockForClean(excelDTO, operateDate);
            seedStockTbMapper.insert(seedStockTb);
            CerSpeciesConf cerSpeciesConf = cerSpeciesConfCodeMap.get(excelDTO.getSpeciesCode());
            if (cerSpeciesConf == null) {
                throw new BusinessException("找不到物种配置：" + excelDTO.getSpeciesCode());
            }
            seedStockTb.setSeedNum(cerSpeciesConf.getNumPrefix() + StringUtils.padl(String.valueOf(seedStockTb.getId()), 8, '0'));
            seedStockTbMapper.updateById(seedStockTb);

            SeedInStoreDTO.ExecuteFormContent content = buildExecuteFormContent(excelDTO, seedStockTb);
            executeFormContentList.add(content);

            SeedStockInLog seedStockInLog = seedStockInLogMapper.selectOneBySeedNum(seedStockTb.getSeedNum());
            if (seedStockInLog == null) {
                seedStockInLog = new SeedStockInLog();
                seedStockInLog.setSeedNum(seedStockTb.getSeedNum());
                seedStockInLog.setRemarks(seedStockTb.getRemarks());
                seedStockInLog.setUnit(seedStockTb.getUnit());
                seedStockInLog.setSeedNumber(seedStockTb.getSeedNumber());
                seedStockInLog.setSourceType(seedStockTb.getSourceType());
                seedStockInLog.setTaskNum(taskNum);
                seedStockInLog.setApplyUserId(bioTaskDtlTb.getApplyUserId());
                seedStockInLog.setApplyUserName(bioTaskDtlTb.getApplyUserName());
                seedStockInLog.setCreateTime(operateDate);
                seedStockInLog.setUniqueCode(content.getUniqueCode());
                fillSeedStockInLogSnapshot(seedStockInLog, seedStockTb);
                seedStockInLogMapper.insert(seedStockInLog);
            } else {
                seedStockInLog.setRemarks(seedStockTb.getRemarks());
                seedStockInLog.setUnit(seedStockTb.getUnit());
                seedStockInLog.setSeedNumber(seedStockTb.getSeedNumber());
                seedStockInLog.setSourceType(seedStockTb.getSourceType());
                seedStockInLog.setTaskNum(taskNum);
                seedStockInLog.setApplyUserId(bioTaskDtlTb.getApplyUserId());
                seedStockInLog.setApplyUserName(bioTaskDtlTb.getApplyUserName());
                seedStockInLog.setCreateTime(operateDate);
                seedStockInLog.setUniqueCode(content.getUniqueCode());
                fillSeedStockInLogSnapshot(seedStockInLog, seedStockTb);
                seedStockInLogMapper.updateById(seedStockInLog);
            }
        }
        executeForm.setExecuteFormContentList(executeFormContentList);
        seedInStoreDTO.setExecuteForm(executeForm);
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(seedInStoreDTO));

        bioTaskDtlTb.setUpdateTime(operateDate);
        bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        return ResponseResult.getSuccess(taskNum);
    }

    @GetMapping("/cleanSeedStockAuditSnapshot")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSeedStockAuditSnapshot() {
        long start = System.currentTimeMillis();
        log.info("cleanSeedStockAuditSnapshot#开始回填种子出入库审计快照字段");

        List<SeedStockTb> seedStockTbList = seedStockTbMapper.selectSelective(null);
        Map<String, SeedStockTb> seedStockMap = seedStockTbList.stream()
                .filter(seedStockTb -> StringUtils.isNotEmpty(seedStockTb.getSeedNum()))
                .collect(Collectors.toMap(SeedStockTb::getSeedNum, seedStockTb -> seedStockTb, (a, b) -> a));
        log.info("cleanSeedStockAuditSnapshot#加载种子库存完成，总数={}，有效种子编号数={}", seedStockTbList.size(), seedStockMap.size());

        int inUpdateCount = 0;
        int inSkipCount = 0;
        List<SeedStockInLog> seedStockInLogList = seedStockInLogMapper.selectList(null);
        log.info("cleanSeedStockAuditSnapshot#开始回填入库快照，入库记录总数={}", seedStockInLogList.size());
        for (int i = 0; i < seedStockInLogList.size(); i++) {
            SeedStockInLog seedStockInLog = seedStockInLogList.get(i);
            SeedStockTb seedStockTb = seedStockMap.get(seedStockInLog.getSeedNum());
            if (seedStockTb == null) {
                inSkipCount++;
                log.warn("cleanSeedStockAuditSnapshot#入库记录找不到库存，inLogId={}，seedNum={}", seedStockInLog.getId(), seedStockInLog.getSeedNum());
            } else {
                fillSeedStockInLogSnapshot(seedStockInLog, seedStockTb);
                seedStockInLogMapper.updateById(seedStockInLog);
                inUpdateCount++;
            }
            if ((i + 1) % 500 == 0 || i + 1 == seedStockInLogList.size()) {
                log.info("cleanSeedStockAuditSnapshot#入库快照回填进度 {}/{}，更新={}，跳过={}", i + 1, seedStockInLogList.size(), inUpdateCount, inSkipCount);
            }
        }

        int outUpdateCount = 0;
        int outSkipCount = 0;
        List<SeedStockOutLog> seedStockOutLogList = seedStockOutLogMapper.selectList(null);
        Map<String, List<SeedStockOutLog>> outLogMap = seedStockOutLogList.stream()
                .filter(seedStockOutLog -> StringUtils.isNotEmpty(seedStockOutLog.getSeedNum()))
                .collect(Collectors.groupingBy(SeedStockOutLog::getSeedNum));
        int outEmptySeedNumCount = seedStockOutLogList.size() - outLogMap.values().stream().mapToInt(List::size).sum();
        log.info("cleanSeedStockAuditSnapshot#开始回填出库快照，出库记录总数={}，空种子编号记录数={}，种子分组数={}",
                seedStockOutLogList.size(), outEmptySeedNumCount, outLogMap.size());
        int outGroupIndex = 0;
        for (Map.Entry<String, List<SeedStockOutLog>> entry : outLogMap.entrySet()) {
            outGroupIndex++;
            SeedStockTb seedStockTb = seedStockMap.get(entry.getKey());
            if (seedStockTb == null) {
                outSkipCount += entry.getValue().size();
                log.warn("cleanSeedStockAuditSnapshot#出库记录找不到库存，seedNum={}，outLogCount={}", entry.getKey(), entry.getValue().size());
            } else {
                List<SeedStockOutLog> outLogs = entry.getValue();
                outLogs.sort(Comparator
                        .comparing(SeedStockOutLog::getCreateTime, Comparator.nullsLast(Date::compareTo))
                        .thenComparing(SeedStockOutLog::getId, Comparator.nullsLast(Integer::compareTo)));

                BigDecimal stockNumber = seedStockTb.getTotalNumber();
                if (stockNumber == null) {
                    stockNumber = seedStockTb.getSeedNumber() == null ? BigDecimal.ZERO : seedStockTb.getSeedNumber();
                    for (SeedStockOutLog outLog : outLogs) {
                        if (outLog.getSeedNumber() != null) {
                            stockNumber = stockNumber.add(outLog.getSeedNumber());
                        }
                    }
                    log.info("cleanSeedStockAuditSnapshot#出库回放使用当前库存反推初始库存，seedNum={}，currentSeedNumber={}，outLogCount={}，initialStock={}",
                            entry.getKey(), seedStockTb.getSeedNumber(), outLogs.size(), stockNumber);
                }

                for (SeedStockOutLog outLog : outLogs) {
                    BigDecimal outNumber = outLog.getSeedNumber() == null ? BigDecimal.ZERO : outLog.getSeedNumber();
                    BigDecimal stockBeforeNumber = stockNumber;
                    BigDecimal stockAfterNumber = stockBeforeNumber.subtract(outNumber);
                    fillSeedStockOutLogSnapshot(outLog, seedStockTb, stockBeforeNumber, stockAfterNumber);
                    seedStockOutLogMapper.updateById(outLog);
                    stockNumber = stockAfterNumber;
                    outUpdateCount++;
                }
            }
            if (outGroupIndex % 500 == 0 || outGroupIndex == outLogMap.size()) {
                log.info("cleanSeedStockAuditSnapshot#出库快照回填分组进度 {}/{}，更新={}，跳过={}", outGroupIndex, outLogMap.size(), outUpdateCount, outSkipCount);
            }
        }

        outSkipCount += outEmptySeedNumCount;
        Map<String, SeedStockOutLog> seedStockOutLogBySeedAndTaskMap = seedStockOutLogList.stream()
                .filter(seedStockOutLog -> StringUtils.isNotEmpty(seedStockOutLog.getSeedNum()) && StringUtils.isNotEmpty(seedStockOutLog.getTaskNum()))
                .collect(Collectors.toMap(seedStockOutLog -> buildSeedTaskKey(seedStockOutLog.getSeedNum(), seedStockOutLog.getTaskNum()), seedStockOutLog -> seedStockOutLog, (a, b) -> a));
        log.info("cleanSeedStockAuditSnapshot#出库快照索引构建完成，seedNum+taskNum索引数={}", seedStockOutLogBySeedAndTaskMap.size());

        int destructionUpdateCount = 0;
        int destructionSkipCount = 0;
        List<SeedStockDestructionLog> seedStockDestructionLogList = seedStockDestructionLogMapper.selectList(null);
        Map<String, List<SeedStockDestructionLog>> destructionLogMap = seedStockDestructionLogList.stream()
                .filter(seedStockDestructionLog -> StringUtils.isNotEmpty(seedStockDestructionLog.getSeedNum()))
                .collect(Collectors.groupingBy(SeedStockDestructionLog::getSeedNum));
        int destructionEmptySeedNumCount = seedStockDestructionLogList.size() - destructionLogMap.values().stream().mapToInt(List::size).sum();
        log.info("cleanSeedStockAuditSnapshot#开始回填销毁快照，销毁记录总数={}，空种子编号记录数={}，种子分组数={}",
                seedStockDestructionLogList.size(), destructionEmptySeedNumCount, destructionLogMap.size());
        int destructionGroupIndex = 0;
        for (Map.Entry<String, List<SeedStockDestructionLog>> entry : destructionLogMap.entrySet()) {
            destructionGroupIndex++;
            SeedStockTb seedStockTb = seedStockMap.get(entry.getKey());
            if (seedStockTb == null) {
                destructionSkipCount += entry.getValue().size();
                log.warn("cleanSeedStockAuditSnapshot#销毁记录找不到库存，seedNum={}，destructionLogCount={}", entry.getKey(), entry.getValue().size());
            } else {
                List<SeedStockDestructionLog> destructionLogs = entry.getValue();
                destructionLogs.sort(Comparator
                        .comparing(SeedStockDestructionLog::getDestructionTime, Comparator.nullsLast(Date::compareTo))
                        .thenComparing(SeedStockDestructionLog::getId, Comparator.nullsLast(Integer::compareTo)));

                BigDecimal stockNumber = seedStockTb.getTotalNumber();
                if (stockNumber == null) {
                    stockNumber = seedStockTb.getSeedNumber() == null ? BigDecimal.ZERO : seedStockTb.getSeedNumber();
                    for (SeedStockDestructionLog destructionLog : destructionLogs) {
                        if (destructionLog.getSeedNumber() != null) {
                            stockNumber = stockNumber.add(destructionLog.getSeedNumber());
                        }
                    }
                    log.info("cleanSeedStockAuditSnapshot#销毁回放使用当前库存反推初始库存，seedNum={}，currentSeedNumber={}，destructionLogCount={}，initialStock={}",
                            entry.getKey(), seedStockTb.getSeedNumber(), destructionLogs.size(), stockNumber);
                }

                for (SeedStockDestructionLog destructionLog : destructionLogs) {
                    BigDecimal destructionNumber = destructionLog.getSeedNumber() == null ? BigDecimal.ZERO : destructionLog.getSeedNumber();
                    SeedStockOutLog seedStockOutLog = seedStockOutLogBySeedAndTaskMap.get(buildSeedTaskKey(destructionLog.getSeedNum(), destructionLog.getTaskNum()));
                    BigDecimal stockBeforeNumber = seedStockOutLog != null && seedStockOutLog.getStockBeforeNumber() != null ? seedStockOutLog.getStockBeforeNumber() : stockNumber;
                    BigDecimal stockAfterNumber = seedStockOutLog != null && seedStockOutLog.getStockAfterNumber() != null ? seedStockOutLog.getStockAfterNumber() : stockBeforeNumber.subtract(destructionNumber);
                    if (seedStockOutLog == null) {
                        log.info("cleanSeedStockAuditSnapshot#销毁记录未匹配到出库流水，使用销毁记录回放库存，destructionLogId={}，seedNum={}，taskNum={}",
                                destructionLog.getId(), destructionLog.getSeedNum(), destructionLog.getTaskNum());
                    }
                    fillSeedStockDestructionLogSnapshot(destructionLog, seedStockTb, stockBeforeNumber, stockAfterNumber);
                    seedStockDestructionLogMapper.updateById(destructionLog);
                    stockNumber = stockAfterNumber;
                    destructionUpdateCount++;
                }
            }
            if (destructionGroupIndex % 500 == 0 || destructionGroupIndex == destructionLogMap.size()) {
                log.info("cleanSeedStockAuditSnapshot#销毁快照回填分组进度 {}/{}，更新={}，跳过={}", destructionGroupIndex, destructionLogMap.size(), destructionUpdateCount, destructionSkipCount);
            }
        }

        destructionSkipCount += destructionEmptySeedNumCount;
        log.info("cleanSeedStockAuditSnapshot#完成，入库更新={}，入库跳过={}，出库更新={}，出库跳过={}，销毁更新={}，销毁跳过={}，耗时={}ms",
                inUpdateCount, inSkipCount, outUpdateCount, outSkipCount, destructionUpdateCount, destructionSkipCount, System.currentTimeMillis() - start);
        return ResponseResult.getSuccess("入库更新=" + inUpdateCount + "，入库跳过=" + inSkipCount
                + "，出库更新=" + outUpdateCount + "，出库跳过=" + outSkipCount
                + "，销毁更新=" + destructionUpdateCount + "，销毁跳过=" + destructionSkipCount);
    }

    private String buildSeedTaskKey(String seedNum, String taskNum) {
        return seedNum + "#" + taskNum;
    }

    private void fillSeedStockInLogSnapshot(SeedStockInLog seedStockInLog, SeedStockTb seedStockTb) {
        seedStockInLog.setPlantCode(seedStockTb.getPlantCode());
        seedStockInLog.setParentNum(seedStockTb.getParentNum());
        seedStockInLog.setFatherInfo(seedStockTb.getFatherInfo());
        seedStockInLog.setMatherInfo(seedStockTb.getMatherInfo());
        seedStockInLog.setGeneration(seedStockTb.getGeneration());
        seedStockInLog.setSpeciesCode(seedStockTb.getSpeciesCode());
        seedStockInLog.setBreedCode(seedStockTb.getBreedCode());
        seedStockInLog.setPollinationMethod(seedStockTb.getPollinationMethod());
        seedStockInLog.setHarvestType(seedStockTb.getHarvestType());
        seedStockInLog.setHarvestTime(seedStockTb.getHarvestTime());
        seedStockInLog.setProductionLocationCode(seedStockTb.getProductionLocationCode());
        seedStockInLog.setStockLocationNum(seedStockTb.getStockLocationNum());
        seedStockInLog.setTotalNumber(seedStockTb.getTotalNumber());
        seedStockInLog.setTargetCharacter(seedStockTb.getTargetCharacter());
        seedStockInLog.setAliasName(seedStockTb.getAliasName());
        seedStockInLog.setGeneType(seedStockTb.getGeneType());
        seedStockInLog.setMaterialType(seedStockTb.getMaterialType());
        seedStockInLog.setMatherSeedNum(seedStockTb.getMatherSeedNum());
        seedStockInLog.setFatherSeedNum(seedStockTb.getFatherSeedNum());
        seedStockInLog.setMatherRegionNum(seedStockTb.getMatherRegionNum());
        seedStockInLog.setFatherRegionNum(seedStockTb.getFatherRegionNum());
        seedStockInLog.setGenealogy(seedStockTb.getGenealogy());
        seedStockInLog.setGeneSeparateFlag(seedStockTb.getGeneSeparateFlag());
        seedStockInLog.setTransFlag(seedStockTb.getTransFlag());
        seedStockInLog.setVectorTaskCode(seedStockTb.getVectorTaskCode());
        seedStockInLog.setExperimentNum(seedStockTb.getExperimentNum());
        seedStockInLog.setProjectCode(seedStockTb.getProjectCode());
        seedStockInLog.setFatherSingleNum(seedStockTb.getFatherSingleNum());
        seedStockInLog.setMatherSingleNum(seedStockTb.getMatherSingleNum());
        seedStockInLog.setPdImplementCode(seedStockTb.getPdImplementCode());
    }

    private void fillSeedStockOutLogSnapshot(SeedStockOutLog seedStockOutLog, SeedStockTb seedStockTb, BigDecimal stockBeforeNumber, BigDecimal stockAfterNumber) {
        seedStockOutLog.setPlantCode(seedStockTb.getPlantCode());
        seedStockOutLog.setParentNum(seedStockTb.getParentNum());
        seedStockOutLog.setFatherInfo(seedStockTb.getFatherInfo());
        seedStockOutLog.setMatherInfo(seedStockTb.getMatherInfo());
        seedStockOutLog.setGeneration(seedStockTb.getGeneration());
        seedStockOutLog.setSpeciesCode(seedStockTb.getSpeciesCode());
        seedStockOutLog.setBreedCode(seedStockTb.getBreedCode());
        seedStockOutLog.setPollinationMethod(seedStockTb.getPollinationMethod());
        seedStockOutLog.setHarvestType(seedStockTb.getHarvestType());
        seedStockOutLog.setHarvestTime(seedStockTb.getHarvestTime());
        seedStockOutLog.setSourceType(seedStockTb.getSourceType());
        seedStockOutLog.setProductionLocationCode(seedStockTb.getProductionLocationCode());
        seedStockOutLog.setStockLocationNum(seedStockTb.getStockLocationNum());
        seedStockOutLog.setTotalNumber(seedStockTb.getTotalNumber());
        seedStockOutLog.setTargetCharacter(seedStockTb.getTargetCharacter());
        seedStockOutLog.setAliasName(seedStockTb.getAliasName());
        seedStockOutLog.setGeneType(seedStockTb.getGeneType());
        seedStockOutLog.setMaterialType(seedStockTb.getMaterialType());
        seedStockOutLog.setMatherSeedNum(seedStockTb.getMatherSeedNum());
        seedStockOutLog.setFatherSeedNum(seedStockTb.getFatherSeedNum());
        seedStockOutLog.setMatherRegionNum(seedStockTb.getMatherRegionNum());
        seedStockOutLog.setFatherRegionNum(seedStockTb.getFatherRegionNum());
        seedStockOutLog.setGenealogy(seedStockTb.getGenealogy());
        seedStockOutLog.setGeneSeparateFlag(seedStockTb.getGeneSeparateFlag());
        seedStockOutLog.setTransFlag(seedStockTb.getTransFlag());
        seedStockOutLog.setVectorTaskCode(seedStockTb.getVectorTaskCode());
        seedStockOutLog.setExperimentNum(seedStockTb.getExperimentNum());
        seedStockOutLog.setProjectCode(seedStockTb.getProjectCode());
        seedStockOutLog.setFatherSingleNum(seedStockTb.getFatherSingleNum());
        seedStockOutLog.setMatherSingleNum(seedStockTb.getMatherSingleNum());
        seedStockOutLog.setPdImplementCode(seedStockTb.getPdImplementCode());
        seedStockOutLog.setStockBeforeNumber(stockBeforeNumber);
        seedStockOutLog.setStockAfterNumber(stockAfterNumber);
    }

    private void fillSeedStockDestructionLogSnapshot(SeedStockDestructionLog seedStockDestructionLog, SeedStockTb seedStockTb, BigDecimal stockBeforeNumber, BigDecimal stockAfterNumber) {
        seedStockDestructionLog.setPlantCode(seedStockTb.getPlantCode());
        seedStockDestructionLog.setParentNum(seedStockTb.getParentNum());
        seedStockDestructionLog.setFatherInfo(seedStockTb.getFatherInfo());
        seedStockDestructionLog.setMatherInfo(seedStockTb.getMatherInfo());
        seedStockDestructionLog.setGeneration(seedStockTb.getGeneration());
        seedStockDestructionLog.setSpeciesCode(seedStockTb.getSpeciesCode());
        seedStockDestructionLog.setBreedCode(seedStockTb.getBreedCode());
        seedStockDestructionLog.setPollinationMethod(seedStockTb.getPollinationMethod());
        seedStockDestructionLog.setHarvestType(seedStockTb.getHarvestType());
        seedStockDestructionLog.setHarvestTime(seedStockTb.getHarvestTime());
        seedStockDestructionLog.setSourceType(seedStockTb.getSourceType());
        seedStockDestructionLog.setProductionLocationCode(seedStockTb.getProductionLocationCode());
        seedStockDestructionLog.setStockLocationNum(seedStockTb.getStockLocationNum());
        seedStockDestructionLog.setTotalNumber(seedStockTb.getTotalNumber());
        seedStockDestructionLog.setTargetCharacter(seedStockTb.getTargetCharacter());
        seedStockDestructionLog.setAliasName(seedStockTb.getAliasName());
        seedStockDestructionLog.setGeneType(seedStockTb.getGeneType());
        seedStockDestructionLog.setMaterialType(seedStockTb.getMaterialType());
        seedStockDestructionLog.setMatherSeedNum(seedStockTb.getMatherSeedNum());
        seedStockDestructionLog.setFatherSeedNum(seedStockTb.getFatherSeedNum());
        seedStockDestructionLog.setMatherRegionNum(seedStockTb.getMatherRegionNum());
        seedStockDestructionLog.setFatherRegionNum(seedStockTb.getFatherRegionNum());
        seedStockDestructionLog.setGenealogy(seedStockTb.getGenealogy());
        seedStockDestructionLog.setGeneSeparateFlag(seedStockTb.getGeneSeparateFlag());
        seedStockDestructionLog.setTransFlag(seedStockTb.getTransFlag());
        seedStockDestructionLog.setVectorTaskCode(seedStockTb.getVectorTaskCode());
        seedStockDestructionLog.setExperimentNum(seedStockTb.getExperimentNum());
        seedStockDestructionLog.setProjectCode(seedStockTb.getProjectCode());
        seedStockDestructionLog.setFatherSingleNum(seedStockTb.getFatherSingleNum());
        seedStockDestructionLog.setMatherSingleNum(seedStockTb.getMatherSingleNum());
        seedStockDestructionLog.setPdImplementCode(seedStockTb.getPdImplementCode());
        seedStockDestructionLog.setStockBeforeNumber(stockBeforeNumber);
        seedStockDestructionLog.setStockAfterNumber(stockAfterNumber);
    }

    private void fillSeedInStockExcelCode(SeedInStockCleanExcelDTO excelDTO,
                                          Map<String, BioDict> bioDictMap,
                                          Map<String, CerSpeciesConf> cerSpeciesConfMap,
                                          Map<String, CerBreedDict> cerBreedDictMap,
                                          Map<String, String> seedProduceAddressDictMap) {
        SeedSourceEnum seedSourceEnum = SeedSourceEnum.getByName(excelDTO.getSource());
        if (seedSourceEnum == null) {
            throw new BusinessException("种子来源填写错误：" + excelDTO.getSource());
        }
        excelDTO.setSource(seedSourceEnum.code);

        if (StringUtils.isNotEmpty(excelDTO.getHarvestTypeName())) {
            BioDict harvestTypeBioDict = bioDictMap.get(BioDictTypeEnum.HARVEST_TYPE + ":" + excelDTO.getHarvestTypeName());
            if (harvestTypeBioDict == null) {
                throw new BusinessException("收获方式填写错误：" + excelDTO.getHarvestTypeName());
            }
            excelDTO.setHarvestType(harvestTypeBioDict.getDictValueCode());
        }

        if (StringUtils.isNotEmpty(excelDTO.getPollinationMethodName())) {
            BioDict pollinationMethodBioDict = bioDictMap.get(BioDictTypeEnum.POLLINATE_TYPE + ":" + excelDTO.getPollinationMethodName());
            if (pollinationMethodBioDict == null) {
                throw new BusinessException("授粉方式填写错误：" + excelDTO.getPollinationMethodName());
            }
            excelDTO.setPollinationMethod(pollinationMethodBioDict.getDictValueCode());
        }

        CerSpeciesConf cerSpeciesConf = cerSpeciesConfMap.get(excelDTO.getSpeciesName());
        if (cerSpeciesConf == null) {
            throw new BusinessException("物种填写错误：" + excelDTO.getSpeciesName());
        }
        excelDTO.setSpeciesCode(cerSpeciesConf.getSpeciesCode());

        CerBreedDict cerBreedDict = cerBreedDictMap.get(cerSpeciesConf.getSpeciesCode() + ":" + excelDTO.getBreedName());
        if (cerBreedDict == null) {
            throw new BusinessException("品种填写错误：" + excelDTO.getBreedName());
        }
        excelDTO.setBreedCode(cerBreedDict.getBreedCode());

        BioDict materialTypeBioDict = bioDictMap.get(BioDictTypeEnum.MATERIAL_TYPE + ":" + excelDTO.getMaterialTypeName());
        if (materialTypeBioDict == null) {
            throw new BusinessException("材料类型填写错误：" + excelDTO.getMaterialTypeName());
        }
        excelDTO.setMaterialType(materialTypeBioDict.getDictValueCode());

        GenerationEnum generationEnum = GenerationEnum.getGeneration(excelDTO.getGeneration());
        if (generationEnum == null) {
            throw new BusinessException("代次填写错误：" + excelDTO.getGeneration());
        }
        excelDTO.setGeneration(generationEnum.code);

        if (StringUtils.isNotEmpty(excelDTO.getProductionLocationName())) {
            String productionLocationCode = seedProduceAddressDictMap.get(excelDTO.getProductionLocationName());
            if (productionLocationCode == null) {
                throw new BusinessException("生产地址填写错误：" + excelDTO.getProductionLocationName());
            }
            excelDTO.setProductionLocationCode(productionLocationCode);
        }
    }

    private SeedStockTb buildSeedStockForClean(SeedInStockCleanExcelDTO excelDTO, Date operateDate) {
        SeedStockTb seedStockTb = new SeedStockTb();
        seedStockTb.setPlantCode(excelDTO.getPlantCode());
        seedStockTb.setGeneration(excelDTO.getGeneration());
        seedStockTb.setSpeciesCode(excelDTO.getSpeciesCode());
        seedStockTb.setBreedCode(excelDTO.getBreedCode());
        seedStockTb.setPollinationMethod(excelDTO.getPollinationMethod());
        seedStockTb.setHarvestType(excelDTO.getHarvestType());
        seedStockTb.setHarvestTime(excelDTO.getHarvestTime());
        seedStockTb.setSeedNumber(excelDTO.getSeedNumber());
        seedStockTb.setTotalNumber(excelDTO.getSeedNumber());
        seedStockTb.setUnit(excelDTO.getUnit());
        seedStockTb.setSourceType(excelDTO.getSource());
        seedStockTb.setProductionLocationCode(excelDTO.getProductionLocationCode());
        seedStockTb.setSubmitUserId(SecurityContextHolder.getUserId());
        seedStockTb.setSubmitUserName(StringUtils.isEmpty(SecurityContextHolder.getNickName()) ? "数据清洗" : SecurityContextHolder.getNickName());
        seedStockTb.setCreateTime(operateDate);
        seedStockTb.setUpdateTime(operateDate);
        seedStockTb.setRemarks(excelDTO.getRemarks());
        seedStockTb.setAliasName(excelDTO.getAliasName());
        seedStockTb.setMaterialType(excelDTO.getMaterialType());
        seedStockTb.setExperimentNum(excelDTO.getExperimentNum());
        seedStockTb.setVectorTaskCode(excelDTO.getVectorTaskCode());
        seedStockTb.setMatherSeedNum(excelDTO.getMatherSeedNum());
        seedStockTb.setFatherSeedNum(excelDTO.getFatherSeedNum());
        seedStockTb.setFatherRegionNum(excelDTO.getFatherRegionNum());
        seedStockTb.setMatherRegionNum(excelDTO.getMatherRegionNum());
        seedStockTb.setFatherSingleNum(excelDTO.getFatherSingleNum());
        seedStockTb.setMatherSingleNum(excelDTO.getMatherSingleNum());
        seedStockTb.setFatherInfo(excelDTO.getFatherInfo());
        seedStockTb.setMatherInfo(excelDTO.getMatherInfo());
        if (StringUtils.isNotEmpty(excelDTO.getVectorTaskCode())) {
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(excelDTO.getVectorTaskCode());
            if (cerVectorTaskTb != null) {
                seedStockTb.setProjectCode(cerVectorTaskTb.getProjectCode());
                CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(cerVectorTaskTb.getProjectCode());
                if (cerProjectTb != null) {
                    seedStockTb.setTargetCharacter(cerProjectTb.getProjectName());
                }
            }
        }
        return seedStockTb;
    }

    private SeedInStoreDTO.ExecuteFormContent buildExecuteFormContent(SeedInStockCleanExcelDTO excelDTO, SeedStockTb seedStockTb) {
        SeedInStoreDTO.ExecuteFormContent content = new SeedInStoreDTO.ExecuteFormContent();
        content.setSeedNum(seedStockTb.getSeedNum());
        content.setSource(excelDTO.getSource());
        content.setPlantCode(excelDTO.getPlantCode());
        content.setVectorTaskCode(excelDTO.getVectorTaskCode());
        content.setFatherInfo(excelDTO.getFatherInfo());
        content.setMatherInfo(excelDTO.getMatherInfo());
        content.setMatherSeedNum(excelDTO.getMatherSeedNum());
        content.setFatherSeedNum(excelDTO.getFatherSeedNum());
        content.setGeneration(excelDTO.getGeneration());
        content.setSpeciesCode(excelDTO.getSpeciesCode());
        content.setSpeciesName(excelDTO.getSpeciesName());
        content.setBreedCode(excelDTO.getBreedCode());
        content.setBreedName(excelDTO.getBreedName());
        content.setPollinationMethod(excelDTO.getPollinationMethod());
        content.setHarvestType(excelDTO.getHarvestType());
        content.setHarvestTime(excelDTO.getHarvestTime());
        content.setSeedNumber(excelDTO.getSeedNumber());
        content.setUnit(excelDTO.getUnit());
        content.setProductionLocationName(excelDTO.getProductionLocationName());
        content.setProductionLocationCode(excelDTO.getProductionLocationCode());
        content.setTargetCharacter(seedStockTb.getTargetCharacter());
        content.setRemarks(excelDTO.getRemarks());
        content.setAliasName(excelDTO.getAliasName());
        content.setMaterialType(excelDTO.getMaterialType());
        content.setStoreFlag(CerProjectContents.Y);
        content.setUniqueCode(IdUtils.simpleUUID());
        content.setMatherRegionNum(excelDTO.getMatherRegionNum());
        content.setFatherRegionNum(excelDTO.getFatherRegionNum());
        content.setExperimentNum(excelDTO.getExperimentNum());
        content.setFatherSingleNum(excelDTO.getFatherSingleNum());
        content.setMatherSingleNum(excelDTO.getMatherSingleNum());
        return content;
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

    @Data
    public static class SeedInStockCleanExcelDTO {

        @ExcelProperty("种子来源")
        private String source;

        @ExcelProperty("代次")
        private String generation;

        @ExcelProperty("种植编号")
        private String plantCode;

        @ExcelProperty("实施方案编号")
        private String vectorTaskCode;

        @ExcelProperty("材料类型")
        private String materialTypeName;

        @ExcelProperty("试验方案编号")
        private String experimentNum;

        @ExcelProperty("父本小区编号")
        private String fatherRegionNum;

        @ExcelProperty("母本小区编号")
        private String matherRegionNum;

        @ExcelProperty("父本单株编号")
        private String fatherSingleNum;

        @ExcelProperty("母本单株编号")
        private String matherSingleNum;

        @ExcelProperty("生产地点")
        private String productionLocationName;

        @ExcelProperty("母本信息")
        private String matherInfo;

        @ExcelProperty("父本信息")
        private String fatherInfo;

        @ExcelProperty("母本种子编号")
        private String matherSeedNum;

        @ExcelProperty("父本种子编号")
        private String fatherSeedNum;

        @ExcelProperty("作物")
        private String speciesName;

        @ExcelProperty("品种")
        private String breedName;

        @ExcelProperty("收获方式")
        private String harvestTypeName;

        @ExcelProperty("收获时间")
        private String harvestTime;

        @ExcelProperty("授粉方式")
        private String pollinationMethodName;

        @ExcelProperty("数量")
        private java.math.BigDecimal seedNumber;

        @ExcelProperty("计量单位")
        private String unit;

        @ExcelProperty("别名")
        private String aliasName;

        @ExcelProperty("备注")
        private String remarks;

        private String harvestType;

        private String pollinationMethod;

        private String speciesCode;

        private String breedCode;

        private String productionLocationCode;

        private String materialType;
    }


}
