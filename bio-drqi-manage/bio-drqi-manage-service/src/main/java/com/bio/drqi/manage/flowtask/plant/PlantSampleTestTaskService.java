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
    private PlantExperimentDetailTbMapper plantExperimentDetailTbMapper;

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
                //如果是转化苗
                String generation = null;
                String speciesCode = null;
                String breedCode = null;
                BioSampleCodePrefixTb bioSampleCodePrefixTb = null;
                if (SourceCodeEnum.project.name().equals(firstSampleApply.getSourceCode())) {
                    if (StringUtils.isEmpty(firstSampleApply.getVectorTaskCode())) {
                        throw new BusinessException("实施方案编号缺失");
                    }
                    if (StringUtils.isEmpty(firstSampleApply.getTransformCode())) {
                        throw new BusinessException("转化编号缺失");
                    }
                    PlantMultipleStockTb plantMultipleStockTb = plantMultipleStockTbMapper.selectOneByVectorTaskCodeAndTransformCode(firstSampleApply.getVectorTaskCode(), firstSampleApply.getTransformCode());
                    if (plantMultipleStockTb == null) {
                        throw new BusinessException("苗库中不存在此转化编号的苗信息，转化编号：" + firstSampleApply.getTransformCode());
                    }
                    if (plantMultipleStockTb.getPlantNumber() < firstSampleApply.getSampleNumber()) {
                        throw new BusinessException("转化编号：" + firstSampleApply.getVectorTaskCode() + "的苗库存不足,当前库存苗数量:" + plantMultipleStockTb.getPlantNumber());
                    }
                    bioSampleCodePrefixTb = bioSampleCodePrefixTbMapper.selectOneByVectorTaskCode(firstSampleApply.getVectorTaskCode());
                    if (bioSampleCodePrefixTb == null) {
                        throw new BusinessException("实施方案编号未配置取样编号前缀：" + firstSampleApply.getVectorTaskCode());
                    }
                    generation = plantMultipleStockTb.getGeneration();
                    speciesCode = plantMultipleStockTb.getSpeciesCode();
                    breedCode = plantMultipleStockTb.getBreedCode();
                    //如果是CER试验苗
                } else {
                    PlantExperimentDetailTb plantExperimentDetailTb = plantExperimentDetailTbMapper.selectOneByRegionNumAndSeedNum(firstSampleApply.getRegionNum(), firstSampleApply.getSeedNum());
                    if (plantExperimentDetailTb == null) {
                        throw new BusinessException("CER试验中无此小区:" + firstSampleApply.getRegionNum() + "和种子编号：" + firstSampleApply.getSeedNum());
                    }
                    bioSampleCodePrefixTb = bioSampleCodePrefixTbMapper.selectOneByPlantExperimentCode(plantExperimentDetailTb.getExperimentNum());
                    if (bioSampleCodePrefixTb == null) {
                        throw new BusinessException("CER试验未配置取样编号前缀：" + firstSampleApply.getVectorTaskCode());
                    }
                    generation = plantExperimentDetailTb.getGenerationCode();
                    speciesCode = plantExperimentDetailTb.getSpeciesCode();
                    breedCode = plantExperimentDetailTb.getBreedCode();
                }
                //获取取样编号前缀
                String sampleCodePrefix = bioSampleCodePrefixTb.getSampleCodePrefix();
                List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllBySampleCodeLike(sampleCodePrefix);
                //获取当前库存中最大取样编号序号
                Integer maxSampleNumber = null;
                if (CollectionUtil.isNotEmpty(bioSampleTestTbList)) {
                    bioSampleTestTbList = bioSampleTestTbList.stream().filter(bioSampleTestTb -> !bioSampleTestTb.getSampleCode().contains("-") && bioSampleTestTb.getSampleCode().startsWith(sampleCodePrefix)).collect(Collectors.toList());
                    maxSampleNumber = bioSampleTestTbList.stream().map(bioSampleTestTb -> Integer.valueOf(bioSampleTestTb.getSampleCode().substring(sampleCodePrefix.length()))).max(Integer::compare).get();
                }
                for (int i = 1; i <= firstSampleApply.getSampleNumber(); i++) {
                    maxSampleNumber = maxSampleNumber == null ? 1 : maxSampleNumber + 1;
                    String sampleCode = sampleCodePrefix + maxSampleNumber;
                    BioSampleTestTb plantSampleTestTb = BioSampleTestTb.of(firstSampleApply.getSeedNum(), firstSampleApply.getRegionNum(), firstSampleApply.getPlantExperimentNum(), firstSampleApply.getVectorTaskCode(), generation, breedCode, speciesCode, sampleCode, bioTaskDtlTb, firstSampleApply.getSourceCode(), sampleCode);
                    sampleTestTbList.add(plantSampleTestTb);
                }
                //插入数据
                try {
                    bioSampleTestTbMapper.insertBatch(sampleTestTbList);
                } catch (DuplicateKeyException e) {
                    log.error("取样申请异常", e);
                    throw new BusinessException("取样编号有重复");
                }
                bioSampleApplyTb.setApplyNumber(firstSampleApply.getSampleNumber() + bioSampleApplyTb.getApplyNumber());
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
                BioSampleTestTb bioSampleTestTb = bioSampleTestTbMapper.selectOneBySampleCodeOrderByIdDesc(repeatSampleTest.getSampleCode());
                //校验苗状态
                if (!PlantStatusEnum.STATUS_1.code.equals(plantSingleStockTb.getPlantStatus()) && !PlantStatusEnum.STATUS_2.code.equals(plantSingleStockTb.getPlantStatus())) {
                    throw new BusinessException("只有正常或者异常苗方可进行取样");
                }
                BioSampleTestTb plantSampleTestTb = BioSampleTestTb.of(bioSampleTestTb.getSeedNum(), bioSampleTestTb.getRegionNum(), bioSampleTestTb.getExperimentNum(), repeatSampleTest.getVectorTaskCode(), plantSingleStockTb.getGeneration(), plantSingleStockTb.getBreedCode(), plantSingleStockTb.getSpeciesCode(), repeatSampleTest.getSampleCode(), bioTaskDtlTb, repeatSampleTest.getSourceCode(), null);
                sampleTestTbList.add(plantSampleTestTb);
            }
            bioSampleApplyTb.setApplyNumber(plantExperimentTaskDTO.getRepeatSampleTestList().size());

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
            Map<String, List<BioSampleTestTb>> plantSampleTestTbMap = sampleTestTbList.stream().collect(Collectors.groupingBy(sampleTestTb -> sampleTestTb.getSampleCode().replaceAll("\\\\d", "")));
            plantSampleTestTbMap.forEach((sampleCodePrefix, sampleTestList) -> {
                sampleTestList = sampleTestList.stream().filter(sampleTest -> sampleTest.getSampleCode().startsWith(sampleCodePrefix)).sorted(Comparator.comparing(sampleTest -> Integer.valueOf(sampleTest.getSampleCode().substring(2)))).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(sampleTestList)) {
                    sampleCodeRangeBuff.append(sampleTestList.get(0).getSampleCode() + "-" + sampleTestList.get(sampleTestList.size() - 1).getSampleCode()).append(",");
                }
            });
            if (StringUtils.isNotEmpty(sampleCodeRangeBuff.toString())) {
                bioSampleApplyTb.setSampleCodeRange(sampleCodeRangeBuff.substring(0, sampleCodeRangeBuff.length() - 1));
                bioSampleApplyTb.setVectorTaskCodes(JSONUtil.toJsonStr(sampleTestTbList.stream().filter(plantSampleTestTb -> StringUtils.isNotEmpty(plantSampleTestTb.getVectorTaskCode())).map(BioSampleTestTb::getVectorTaskCode).collect(Collectors.toList())).replace("[", "").replace("]", "").replace("\"", ""));
            }
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
        bioSampleApplyTb.setApplyDesc(bioTaskDtlTb.getTaskDesc());
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
            bioSampleApplyTbMapper.deleteByApplyNo(bioTaskDtlTb.getTaskNum());
            bioSampleTestTbMapper.updateCheckResultByApplyNoAndCheckResultIsNull(CheckResultEnum.remove.name(), bioSampleApplyTb.getApplyNo());

            //首次取样，且已经发生过移苗
            if (SampleTestApplyTypeEnum.first.name().equals(bioSampleApplyTb.getApplyType())) {
                List<PlantSingleStockTb> plantSingleStockTbList = new ArrayList<>();
                List<BioSampleTestTb> plantSampleTestTbList = bioSampleTestTbMapper.selectAllByApplyNo(bioSampleApplyTb.getApplyNo()).stream().filter(plantSampleTestTb -> CheckResultEnum.stay.name().equals(plantSampleTestTb.getCheckResult())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(plantSampleTestTbList)) {
                    for (BioSampleTestTb plantSampleTestTb : plantSampleTestTbList) {
                        PlantSingleStockTb plantSingleStockTb = PlantSingleStockTb.of(plantSampleTestTb, PlantStatusEnum.STATUS_1);
                        plantSingleStockTb.setPlantStatus(PlantStatusEnum.STATUS_1.code);
                        //plantSingleStockTb.setPlantDate(DateUtil.format(cerConversionAndTransRefList.get(0).getCreateTime(), DatePattern.NORM_DATE_PATTERN));
                        plantSingleStockTbList.add(plantSingleStockTb);
                    }
                }
                plantSingleStockTbMapper.insertBatch(plantSingleStockTbList);

            }

        }

    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        List<BioSampleTestTb> plantSampleTestTbList = bioSampleTestTbMapper.selectAllByApplyNo(bioTaskDtlTb.getTaskNum());
        if (CollectionUtil.isNotEmpty(plantSampleTestTbList)) {
            bioSampleTestHisTbMapper.insertBatch(BeanUtils.copyListProperties(plantSampleTestTbList, BioHisSampleTestTb.class));
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
    }
}
