package com.bio.drqi.manage.flowtask.plant;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.common.enums.*;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.bio.drqi.manage.dto.plant.task.PlantSampleTestTaskDTO;
import com.bio.flow.dto.BioHtmlModelDTO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service("plant_sample_test_task")
@Slf4j
public class PlantSampleTestTaskService extends AbstractPlantBaseTaskService {

    private static final String oneTestType = "one";

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private PlantMultipleStockTbMapper plantMultipleStockTbMapper;

    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;

    @Resource
    private BioSampleCodePrefixTbMapper bioSampleCodePrefixTbMapper;

    @Resource
    private BioSampleApplyTbMapper bioSampleApplyTbMapper;

    @Resource
    private BioSampleTestTbMapper bioSampleTestTbMapper;

    @Resource
    private PlantApplyDetailTbMapper plantApplyDetailTbMapper;

    @Resource
    private PlantApplyTbMapper plantApplyTbMapper;

    @Resource
    private BioSampleTestHisTbMapper bioSampleTestHisTbMapper;

    @Resource
    private BioSampleLayoutTbMapper bioSampleLayoutTbMapper;

    @Resource
    private BioSampleTestTwoResultTbMapper bioSampleTestTwoResultTbMapper;

    @Resource
    private BioSampleTestTwoResultDetailTbMapper bioSampleTestTwoResultDetailTbMapper;

    @Resource
    private BioSampleTestOneResultTbMapper bioSampleTestOneResultTbMapper;

    @Resource
    private BioSampleTestResultFileTbMapper bioSampleTestResultFileTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;


    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        PlantSampleTestTaskDTO plantExperimentTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlantSampleTestTaskDTO.class);
        ValidatorUtil.validator(plantExperimentTaskDTO);
        BeanUtils.trimFiledSpace(plantExperimentTaskDTO);
        //校验内容
        CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectOneBySpeciesCode(plantExperimentTaskDTO.getSpeciesCode());
        if (cerSpeciesConf == null) {
            throw new BusinessException("物种找不到");
        }
        //取样备注上区分是单管还是孔板取样
        if("one".equals(plantExperimentTaskDTO.getTestType())){
            bioTaskDtlTb.setTaskDesc(bioTaskDtlTb.getTaskDesc()+"(单管取样)");
        }else {
            bioTaskDtlTb.setTaskDesc(bioTaskDtlTb.getTaskDesc()+"(96孔板取样)");
        }

        //初始化取样申请工单
        BioSampleApplyTb bioSampleApplyTb = initPlantSampleApplyTb(bioTaskDtlTb, plantExperimentTaskDTO);

        List<BioSampleTestTb> sampleTestTbList = new ArrayList<>();
        //首次取样
        if (SampleTestApplyTypeEnum.first.name().equals(plantExperimentTaskDTO.getApplyType())) {
            if (CollectionUtil.isEmpty(plantExperimentTaskDTO.getFirstSampleApplyList())) {
                throw new BusinessException("无取样数据");
            }
            for (PlantSampleTestTaskDTO.FirstSampleApply firstSampleApply : plantExperimentTaskDTO.getFirstSampleApplyList()) {
                //循环之前先清空上次的数据
                sampleTestTbList.clear();
                ValidatorUtil.validator(firstSampleApply);
                BeanUtils.trimFiledSpace(firstSampleApply);
                //获取临时苗库
                PlantMultipleStockTb plantMultipleStockTb = findPlantMultipleStockTb(firstSampleApply);
                //获取取样编号前缀
                String sampleCodePrefix = findSampleCodePrefix(firstSampleApply);
                List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllBySampleCodeLike(sampleCodePrefix);
                //获取当前库存中最大取样编号序号
                Integer maxSampleNumber = null;
                if (CollectionUtil.isNotEmpty(bioSampleTestTbList)) {
                    bioSampleTestTbList = bioSampleTestTbList.stream().filter(bioSampleTestTb -> !bioSampleTestTb.getSampleCode().contains("-") && bioSampleTestTb.getSampleCode().startsWith(sampleCodePrefix)).collect(Collectors.toList());
                    if(CollectionUtil.isNotEmpty(bioSampleTestTbList)){
                        maxSampleNumber = bioSampleTestTbList.stream().map(bioSampleTestTb -> Integer.valueOf(bioSampleTestTb.getSampleCode().substring(sampleCodePrefix.length()))).max(Integer::compare).get();
                    }
                }
                for (int i = 1; i <= firstSampleApply.getSampleNumber(); i++) {
                    maxSampleNumber = maxSampleNumber == null ? 1 : maxSampleNumber + 1;
                    String sampleCode = sampleCodePrefix + maxSampleNumber;
                    BioSampleTestTb plantSampleTestTb = BioSampleTestTb.ofFirst(plantMultipleStockTb, sampleCode, bioTaskDtlTb, CheckResultEnum.noCheck,TestResultEnum.noTest);
                    sampleTestTbList.add(plantSampleTestTb);
                }
                //首次取样更新临时苗取样数量
                plantMultipleStockTb.setSampleNumber(plantMultipleStockTb.getSampleNumber() + firstSampleApply.getSampleNumber());
                plantMultipleStockTb.setSampleNumber(plantMultipleStockTb.getPlantNumber() - firstSampleApply.getSampleNumber());
                plantMultipleStockTbMapper.updateById(plantMultipleStockTb);
                //插入数据
                try {
                    bioSampleTestTbMapper.insertBatch(sampleTestTbList);
                } catch (DuplicateKeyException e) {
                    log.error("取样申请异常", e);
                    throw new BusinessException("取样编号有重复");
                }

            }


        }
        //重复取样
        if (SampleTestApplyTypeEnum.repeat.name().equals(plantExperimentTaskDTO.getApplyType())) {
            if (CollectionUtil.isEmpty(plantExperimentTaskDTO.getRepeatSampleTestList())) {
                throw new BusinessException("无取样数据");
            }
            for (PlantSampleTestTaskDTO.RepeatSampleTest repeatSampleTest : plantExperimentTaskDTO.getRepeatSampleTestList()) {
                PlantSingleStockTb plantSingleStockTb = plantSingleStockTbMapper.selectOneByPlantCode(repeatSampleTest.getSampleCode());
                if (plantSingleStockTb == null) {
                    throw new BusinessException("CER中无此种植编号苗信息：" + repeatSampleTest.getSampleCode());
                }
                //校验苗状态
                if (!PlantStatusEnum.STATUS_1.code.equals(plantSingleStockTb.getPlantStatus()) && !PlantStatusEnum.STATUS_2.code.equals(plantSingleStockTb.getPlantStatus())) {
                    throw new BusinessException("只有正常或者异常苗方可进行取样");
                }
                BioSampleTestTb bioSampleTestTb = bioSampleTestTbMapper.selectOneBySampleCodeOrderByIdDesc(repeatSampleTest.getSampleCode());
                BioSampleTestTb plantSampleTestTb = BioSampleTestTb.ofRepeat(bioSampleTestTb, bioTaskDtlTb, CheckResultEnum.noCheck,TestResultEnum.noTest);
                sampleTestTbList.add(plantSampleTestTb);
            }
            try {
                bioSampleTestTbMapper.insertBatch(sampleTestTbList);
            } catch (DuplicateKeyException e) {
                log.error("取样申请异常", e);
                throw new BusinessException("取样编号有重复");
            }
        }

        //如果是首次取样，更新取样区间
        if (SampleTestApplyTypeEnum.first.name().equals(plantExperimentTaskDTO.getApplyType())) {
            StringBuffer sampleCodeRangeBuff = new StringBuffer();
            Map<String, List<BioSampleTestTb>> plantSampleTestTbMap = sampleTestTbList.stream().collect(Collectors.groupingBy(sampleTestTb -> sampleTestTb.getSampleCode().replaceAll("\\d", "")));
            plantSampleTestTbMap.forEach((sampleCodePrefix, sampleTestList) -> {
                sampleTestList = sampleTestList.stream().filter(sampleTest -> sampleTest.getSampleCode().startsWith(sampleCodePrefix)).sorted(Comparator.comparing(sampleTest -> Integer.valueOf(sampleTest.getSampleCode().substring(sampleCodePrefix.length())))).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(sampleTestList)) {
                    sampleCodeRangeBuff.append(sampleTestList.get(0).getSampleCode() + "-" + sampleTestList.get(sampleTestList.size() - 1).getSampleCode()).append(",");
                }
            });
            if (StringUtils.isNotEmpty(sampleCodeRangeBuff.toString())) {
                bioSampleApplyTb.setSampleCodeRange(sampleCodeRangeBuff.substring(0, sampleCodeRangeBuff.length() - 1));
                bioSampleApplyTb.setVectorTaskCodes(JSONUtil.toJsonStr(sampleTestTbList.stream().filter(plantSampleTestTb -> StringUtils.isNotEmpty(plantSampleTestTb.getVectorTaskCode())).map(BioSampleTestTb::getVectorTaskCode).distinct().collect(Collectors.toList())).replace("[", "").replace("]", "").replace("\"", ""));
            }
            bioSampleApplyTb.setApplyNumber(plantExperimentTaskDTO.getFirstSampleApplyList().stream().map(PlantSampleTestTaskDTO.FirstSampleApply::getSampleNumber).mapToInt(Integer::intValue).sum());
        } else {
            bioSampleApplyTb.setApplyNumber(plantExperimentTaskDTO.getRepeatSampleTestList().size());
        }
        //更新数据
        bioSampleApplyTbMapper.insert(bioSampleApplyTb);
    }


    @NotNull
    private static BioSampleApplyTb initPlantSampleApplyTb(BioTaskDtlTb bioTaskDtlTb, PlantSampleTestTaskDTO plantExperimentTaskDTO) {
        BioSampleApplyTb bioSampleApplyTb = new BioSampleApplyTb();
        bioSampleApplyTb.setApplyNo(bioTaskDtlTb.getTaskNum());
        bioSampleApplyTb.setApplyNumber(0);
        bioSampleApplyTb.setApplyTime(new Date());
        bioSampleApplyTb.setApplyUserId(SecurityContextHolder.getUserId());
        bioSampleApplyTb.setApplyUserName(SecurityContextHolder.getNickName());
        bioSampleApplyTb.setApplyType(plantExperimentTaskDTO.getApplyType());
        bioSampleApplyTb.setLayoutFlag(plantExperimentTaskDTO.getTestType());
        bioSampleApplyTb.setVectorTaskCodes(null);
        bioSampleApplyTb.setSampleCodeRange(null);
        return bioSampleApplyTb;
    }


    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            BioSampleApplyTb bioSampleApplyTb = bioSampleApplyTbMapper.selectOneByApplyNo(bioTaskDtlTb.getTaskNum());
            bioSampleTestHisTbMapper.deleteByApplyNo(bioTaskDtlTb.getTaskNum());
            bioSampleTestTbMapper.updateNoCheckDataByApplyNoAndCheckResult(CheckResultEnum.remove.name(), SecurityContextHolder.getUserId(), SecurityContextHolder.getNickName(), SecurityContextHolder.getUserId(), SecurityContextHolder.getNickName(), TestResultEnum.noResult.name(),bioSampleApplyTb.getApplyNo(), CheckResultEnum.noCheck.name());

            //首次CER取样需要生成种植编号
            if (SampleTestApplyTypeEnum.first.name().equals(bioSampleApplyTb.getApplyType())) {
                List<PlantSingleStockTb> plantSingleStockTbList = new ArrayList<>();
                List<BioSampleTestTb> plantSampleTestTbList = bioSampleTestTbMapper.selectAllByApplyNo(bioSampleApplyTb.getApplyNo()).stream().filter(plantSampleTestTb -> CheckResultEnum.stay.name().equals(plantSampleTestTb.getCheckResult())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(plantSampleTestTbList)) {
                    for (BioSampleTestTb bioSampleTestTb : plantSampleTestTbList) {
                        PlantMultipleStockTb plantMultipleStockTb = findPlantMultipleStockTb(bioSampleTestTb);
                        PlantSingleStockTb plantSingleStockTb = PlantSingleStockTb.of(bioSampleTestTb, PlantStatusEnum.STATUS_1, plantMultipleStockTb.getPlantDate(), bioSampleTestTb.getApplyNo(), bioSampleTestTb.getSourceCode(), "CER种植申请首次取样产出的苗");
                        plantSingleStockTb.setCreateUserId(plantMultipleStockTb.getCreateUserId());
                        plantSingleStockTb.setCreateUserName(plantMultipleStockTb.getCreateUserName());
                        plantSingleStockTbList.add(plantSingleStockTb);
                    }
                    plantSingleStockTbMapper.insertBatch(plantSingleStockTbList);
                }


            }

        }

    }

    private PlantMultipleStockTb findPlantMultipleStockTb(BioSampleTestTb bioSampleTestTb) {
        PlantMultipleStockTb plantMultipleStockTb = null;
        if (SourceCodeEnum.cer.name().equals(bioSampleTestTb.getSourceCode())) {
            plantMultipleStockTb = plantMultipleStockTbMapper.selectOneByRegionNumAndSeedNum(bioSampleTestTb.getRegionNum(), bioSampleTestTb.getSeedNum());
            if (plantMultipleStockTb == null) {
                throw new BusinessException("苗库中找不到小区编号为：" + bioSampleTestTb.getRegionNum() + "种子编号为：" + bioSampleTestTb.getSeedNum() + "的苗信息");
            }
        } else if (SourceCodeEnum.project.name().equals(bioSampleTestTb.getSourceCode())) {
            plantMultipleStockTb = plantMultipleStockTbMapper.selectOneByVectorTaskCodeAndTransformCode(bioSampleTestTb.getVectorTaskCode(), bioSampleTestTb.getTransformCode());
            if (plantMultipleStockTb == null) {
                throw new BusinessException("苗库中找不到实施方案编号号为：" + bioSampleTestTb.getVectorTaskCode() + "转化编号为：" + bioSampleTestTb.getTransformCode() + "的苗信息");
            }
        }
        return plantMultipleStockTb;
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        PlantSampleTestTaskDTO plantExperimentTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlantSampleTestTaskDTO.class);
        List<BioSampleTestTb> plantSampleTestTbList = bioSampleTestTbMapper.selectAllByApplyNo(bioTaskDtlTb.getTaskNum());
        if (CollectionUtil.isNotEmpty(plantSampleTestTbList)) {
            bioSampleTestHisTbMapper.insertBatch(BeanUtils.copyListProperties(plantSampleTestTbList, BioSampleTestHisTb.class));
        }
        bioSampleApplyTbMapper.deleteByApplyNo(bioTaskDtlTb.getTaskNum());
        bioSampleTestTbMapper.deleteByApplyNo(bioTaskDtlTb.getTaskNum());
        bioSampleLayoutTbMapper.deleteByApplyNo(bioTaskDtlTb.getTaskNum());
        List<BioSampleTestTwoResultTb> bioSampleSampleTwoResultTbList = bioSampleTestTwoResultTbMapper.selectAllByUploadNum(bioTaskDtlTb.getTaskNum());
        if (CollectionUtil.isNotEmpty(bioSampleSampleTwoResultTbList)) {
            bioSampleTestTwoResultTbMapper.deleteByUploadNum(bioTaskDtlTb.getTaskNum());
            bioSampleSampleTwoResultTbList.forEach(bioSampleSampleTwoResultTb -> {
                bioSampleTestTwoResultDetailTbMapper.deleteByApplyNoAndSampleCode(bioSampleSampleTwoResultTb.getApplyNo(), bioSampleSampleTwoResultTb.getSampleCode());
            });
        }

        plantSingleStockTbMapper.deleteByTaskNum(bioTaskDtlTb.getTaskNum());
        bioSampleTestOneResultTbMapper.deleteByUploadNum(bioTaskDtlTb.getTaskNum());
        bioSampleTestResultFileTbMapper.deleteByUploadNum(bioTaskDtlTb.getTaskNum());
        //首次取样回退取样苗信息
        if (SampleTestApplyTypeEnum.first.name().equals(plantExperimentTaskDTO.getApplyType())) {

            if (CollectionUtil.isEmpty(plantExperimentTaskDTO.getFirstSampleApplyList())) {
                throw new BusinessException("无取样数据");
            }
            for (PlantSampleTestTaskDTO.FirstSampleApply firstSampleApply : plantExperimentTaskDTO.getFirstSampleApplyList()) {
                //获取临时苗库
                PlantMultipleStockTb plantMultipleStockTb = findPlantMultipleStockTb(firstSampleApply);
                //回退取样数量
                plantMultipleStockTb.setSampleNumber(plantMultipleStockTb.getSampleNumber() - firstSampleApply.getSampleNumber());
                plantMultipleStockTb.setCurrentNumber(plantMultipleStockTb.getCurrentNumber()+firstSampleApply.getSampleNumber());
                plantMultipleStockTbMapper.updateById(plantMultipleStockTb);
            }
        }




    }

    private PlantMultipleStockTb findPlantMultipleStockTb(PlantSampleTestTaskDTO.FirstSampleApply firstSampleApply) {
        PlantMultipleStockTb plantMultipleStockTb = null;
        if (SourceCodeEnum.project.name().equals(firstSampleApply.getSourceCode())) {
            if (StringUtils.isEmpty(firstSampleApply.getVectorTaskCode())) {
                throw new BusinessException("实施方案编号缺失");
            }
            if (StringUtils.isEmpty(firstSampleApply.getTransformCode())) {
                throw new BusinessException("转化编号缺失");
            }
            plantMultipleStockTb = plantMultipleStockTbMapper.selectOneByVectorTaskCodeAndTransformCode(firstSampleApply.getVectorTaskCode(), firstSampleApply.getTransformCode());
            if (plantMultipleStockTb == null) {
                throw new BusinessException("苗库中不存在此转化编号的苗信息，转化编号：" + firstSampleApply.getTransformCode());
            }
            //如果是CER试验苗
        } else {
            if (StringUtils.isEmpty(firstSampleApply.getRegionNum())) {
                throw new BusinessException("小区编号缺失");
            }
            if (StringUtils.isEmpty(firstSampleApply.getSeedNum())) {
                throw new BusinessException("种子编号缺失");
            }
            plantMultipleStockTb = plantMultipleStockTbMapper.selectOneByRegionNumAndSeedNum(firstSampleApply.getRegionNum(), firstSampleApply.getSeedNum());
            if (plantMultipleStockTb == null) {
                throw new BusinessException("苗库中不存在此种子编号的临时苗信息，种子编号：" + firstSampleApply.getSeedNum() + "小区编号：" + firstSampleApply.getRegionNum());
            }
        }
        return plantMultipleStockTb;
    }

    private String findSampleCodePrefix(PlantSampleTestTaskDTO.FirstSampleApply firstSampleApply) {
        if (SourceCodeEnum.project.name().equals(firstSampleApply.getSourceCode())) {

            BioSampleCodePrefixTb bioSampleCodePrefixTb = bioSampleCodePrefixTbMapper.selectOneByVectorTaskCode(firstSampleApply.getVectorTaskCode());
            if (bioSampleCodePrefixTb == null) {
                throw new BusinessException("找不到实施方案的取样编号前缀设置：" + firstSampleApply.getVectorTaskCode());
            }
            return bioSampleCodePrefixTb.getSampleCodePrefix();
        } else if (SourceCodeEnum.cer.name().equals(firstSampleApply.getSourceCode())) {
            PlantApplyDetailTb plantApplyDetailTb = plantApplyDetailTbMapper.selectOneByRegionNumAndSeedNum(firstSampleApply.getRegionNum(), firstSampleApply.getSeedNum());
            if (plantApplyDetailTb == null) {
                throw new BusinessException("CER种植申请中找不到此种植记录：小区编号：" + firstSampleApply.getRegionNum() + "种子编号：" + firstSampleApply.getRegionNum());
            }
            PlantApplyTb plantApplyTb = plantApplyTbMapper.selectOneByPlantApplyNum(plantApplyDetailTb.getPlantApplyNum());
            if (plantApplyTb == null) {
                throw new BusinessException("种植申请找不到：" + plantApplyTb.getPlantApplyNum());
            }
            return plantApplyTb.getSampleCodePrefix();
        }
        log.error("数据异常，找不到取样编号前缀,当前数据信息：" + JSONUtil.toJsonStr(firstSampleApply));
        throw new BusinessException("数据异常，找不到取样编号前缀");
    }

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        PlantSampleTestTaskDTO dto = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlantSampleTestTaskDTO.class);
        if (dto == null) {
            return Collections.emptyList();
        }

        List<BioHtmlModelDTO.ModelSection> sections = new ArrayList<>();
        CerSpeciesConf speciesConf = StringUtils.isEmpty(dto.getSpeciesCode()) ? null : cerSpeciesConfMapper.selectOneBySpeciesCode(dto.getSpeciesCode());

        List<BioHtmlModelDTO.ModelField> applyFields = new ArrayList<>();
        applyFields.add(buildField("物种", speciesConf == null ? dto.getSpeciesCode() : speciesConf.getSpeciesName()));
        applyFields.add(buildField("取样类型", sampleApplyTypeName(dto.getApplyType())));
        applyFields.add(buildField("检测方式", testTypeName(dto.getTestType())));
        applyFields.add(buildField("重复取样文件", dto.getRepeatSampleApplyExcelUrl()));
        applyFields.add(buildField("检测数据文件", dto.getTestDataExcelUrl()));
        applyFields.add(buildField("引物模板文件", dto.getIdentifyPrimerTemplateExcelUrl()));
        applyFields.add(buildField("生信结果文件", dto.getBioInfoResultExcelUrl()));
        sections.add(buildFieldSection("申请信息", applyFields));

        if (CollectionUtil.isNotEmpty(dto.getFirstSampleApplyList())) {
            List<String> headers = Arrays.asList("来源", "CER试验编号", "小区编号", "种子编号", "实施方案编号", "转化编号", "取样数量");
            List<Map<String, Object>> rows = new ArrayList<>();
            for (PlantSampleTestTaskDTO.FirstSampleApply item : dto.getFirstSampleApplyList()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("来源", sourceCodeName(item.getSourceCode()));
                row.put("CER试验编号", item.getPlantExperimentNum());
                row.put("小区编号", item.getRegionNum());
                row.put("种子编号", item.getSeedNum());
                row.put("实施方案编号", item.getVectorTaskCode());
                row.put("转化编号", item.getTransformCode());
                row.put("取样数量", item.getSampleNumber());
                rows.add(row);
            }
            sections.add(buildTableSection("首次取样申请明细", headers, rows));
        }

        if (CollectionUtil.isNotEmpty(dto.getRepeatSampleTestList())) {
            List<String> headers = Arrays.asList("来源", "取样编号", "实施方案编号", "小区编号", "种子编号", "物种", "品种");
            List<Map<String, Object>> rows = new ArrayList<>();
            for (PlantSampleTestTaskDTO.RepeatSampleTest item : dto.getRepeatSampleTestList()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("来源", sourceCodeName(item.getSourceCode()));
                row.put("取样编号", item.getSampleCode());
                row.put("实施方案编号", item.getVectorTaskCode());
                row.put("小区编号", item.getRegionNum());
                row.put("种子编号", item.getSeedNum());
                row.put("物种", item.getSpeciesName());
                row.put("品种", item.getBreedName());
                rows.add(row);
            }
            sections.add(buildTableSection("重复取样申请明细", headers, rows));
        }

        List<BioSampleTestTb> sampleList = bioSampleTestTbMapper.selectAllByApplyNo(bioTaskDtlTb.getTaskNum());
        if (CollectionUtil.isNotEmpty(sampleList)) {
            Map<String, String> breedNameMap = cerBreedDictMapper.selectAll().stream()
                    .collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName, (left, right) -> left));
            Map<String, String> speciesNameMap = cerSpeciesConfMapper.selectAll().stream()
                    .collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName, (left, right) -> left));
            List<String> headers = Arrays.asList("来源", "取样编号", "实施方案编号", "转化编号", "小区编号", "种子编号", "物种", "品种", "代次", "检测结果", "审核结果", "检测人");
            List<Map<String, Object>> rows = new ArrayList<>();
            for (BioSampleTestTb item : sampleList) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("来源", sourceCodeName(item.getSourceCode()));
                row.put("取样编号", item.getSampleCode());
                row.put("实施方案编号", item.getVectorTaskCode());
                row.put("转化编号", item.getTransformCode());
                row.put("小区编号", item.getRegionNum());
                row.put("种子编号", item.getSeedNum());
                row.put("物种", speciesNameMap.getOrDefault(item.getSpeciesCode(), item.getSpeciesCode()));
                row.put("品种", breedNameMap.getOrDefault(item.getBreedCode(), item.getBreedCode()));
                row.put("代次", item.getGeneration());
                row.put("检测结果", testResultName(item.getTestResult()));
                row.put("审核结果", checkResultName(item.getCheckResult()));
                row.put("检测人", item.getTestUserName());
                rows.add(row);
            }
            sections.add(buildTableSection("取样信息明细", headers, rows));
        }

        return sections;
    }

    private String sampleApplyTypeName(String code) {
        if (SampleTestApplyTypeEnum.first.name().equals(code)) {
            return "首次取样";
        }
        if (SampleTestApplyTypeEnum.repeat.name().equals(code)) {
            return "重复取样";
        }
        return code;
    }

    private String testTypeName(String code) {
        if (oneTestType.equals(code)) {
            return "单管取样";
        }
        if ("more".equals(code)) {
            return "96孔板取样";
        }
        return code;
    }

    private String sourceCodeName(String code) {
        if (SourceCodeEnum.project.name().equals(code)) {
            return "项目";
        }
        if (SourceCodeEnum.cer.name().equals(code)) {
            return "CER";
        }
        if (SourceCodeEnum.field.name().equals(code)) {
            return "大田";
        }
        if (SourceCodeEnum.seed.name().equals(code)) {
            return "种子库";
        }
        return code;
    }

    private String testResultName(String code) {
        if (StringUtils.isEmpty(code)) {
            return "";
        }
        if (TestResultEnum.noTest.name().equals(code)) {
            return "未检测";
        }
        if (TestResultEnum.noResult.name().equals(code)) {
            return "无结果";
        }
        if (TestResultEnum.haveResult.name().equals(code)) {
            return "已有结果";
        }
        return code;
    }

    private String checkResultName(String code) {
        if (StringUtils.isEmpty(code)) {
            return "";
        }
        if (CheckResultEnum.stay.name().equals(code)) {
            return "保留";
        }
        if (CheckResultEnum.remove.name().equals(code)) {
            return "剔除";
        }
        if (CheckResultEnum.noCheck.name().equals(code)) {
            return "未审核";
        }
        return code;
    }
}
