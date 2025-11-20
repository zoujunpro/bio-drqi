package com.bio.drqi.plant.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.enums.*;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.bio.drqi.plant.dto.ExperimentExcelDTO;
import com.bio.drqi.plant.dto.task.PlantExperimentTaskDTO;
import com.bio.drqi.plant.dto.task.PlantSampleTestTaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
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
    private PlantSampleCodePrefixTbMapper plantSampleCodePrefixTbMapper;

    @Resource
    private PlantExperimentTbMapper plantExperimentTbMapper;

    @Resource
    private PlantSampleApplyTbMapper plantSampleApplyTbMapper;

    @Resource
    private PlantSampleTestTbMapper plantSampleTestTbMapper;

    @Resource
    private PlantExperimentDetailTbMapper plantExperimentDetailTbMapper;


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
        PlantSampleApplyTb plantSampleApplyTb = initPlantSampleApplyTb(bioTaskDtlTb, plantExperimentTaskDTO);

        List<PlantSampleTestTb> sampleTestTbList = new ArrayList<>();
        //首次取样
        if (SampleTestApplyTypeEnum.F.name().equals(plantExperimentTaskDTO.getApplyType())) {
            if (CollectionUtil.isEmpty(plantExperimentTaskDTO.getFirstSampleApplyList())) {
                throw new BusinessException("无取样数据");
            }
            for (PlantSampleTestTaskDTO.FirstSampleApply firstSampleApply : plantExperimentTaskDTO.getFirstSampleApplyList()) {
                ValidatorUtil.validator(firstSampleApply);
                BeanUtils.trimFiledSpace(firstSampleApply);
                //如果是转化苗
                String generation = null;
                String speciesCode = null;
                String breedCode = null;
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
                    generation = plantMultipleStockTb.getGeneration();
                    speciesCode = plantMultipleStockTb.getSpeciesCode();
                    breedCode = plantMultipleStockTb.getBreedCode();
                    //如果是CER试验苗
                } else {
                    PlantExperimentDetailTb plantExperimentDetailTb = plantExperimentDetailTbMapper.selectOneByRegionNumAndSeedNum(firstSampleApply.getRegionNum(), firstSampleApply.getSeedNum());
                    if (plantExperimentDetailTb == null) {
                        throw new BusinessException("CER试验中无此小区:" + firstSampleApply.getRegionNum() + "和种子编号：" + firstSampleApply.getSeedNum());
                    }
                    generation = plantExperimentDetailTb.getGenerationCode();
                }
                //找到取样编号前缀
                PlantSampleCodePrefixTb plantSampleCodePrefixTb = getPlantSampleCodePrefixTb(firstSampleApply);
                for (int i = 1; i <= firstSampleApply.getSampleNumber(); i++) {
                    String sampleCode = plantSampleCodePrefixTb.getSampleCodePrefix() + (plantSampleCodePrefixTb.getCurrentIndex() + i - 1);
                    PlantSampleTestTb plantSampleTestTb = PlantSampleTestTb.of(firstSampleApply.getVectorTaskCode(), generation, breedCode, speciesCode, sampleCode, bioTaskDtlTb, firstSampleApply.getSourceCode(), sampleCode);
                    sampleTestTbList.add(plantSampleTestTb);
                }
                plantSampleApplyTb.setApplyNumber(firstSampleApply.getSampleNumber() + plantSampleApplyTb.getApplyNumber());
                plantSampleCodePrefixTb.setCurrentIndex(plantSampleCodePrefixTb.getCurrentIndex() + firstSampleApply.getSampleNumber());
                plantSampleCodePrefixTbMapper.updateById(plantSampleCodePrefixTb);
            }

        }
        if (SampleTestApplyTypeEnum.R.name().equals(plantExperimentTaskDTO.getApplyType())) {
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
                PlantSampleTestTb plantSampleTestTb = PlantSampleTestTb.of(repeatSampleTest.getVectorTaskCode(), plantSingleStockTb.getGeneration(), plantSingleStockTb.getBreedCode(), plantSingleStockTb.getSpeciesCode(), repeatSampleTest.getSampleCode(), bioTaskDtlTb, repeatSampleTest.getSourceCode(), null);
                sampleTestTbList.add(plantSampleTestTb);
            }
            plantSampleApplyTb.setApplyNumber(plantExperimentTaskDTO.getRepeatSampleTestList().size());
        }

        //如果是首次取样，更新取样区间
        if (SampleTestApplyTypeEnum.F.name().equals(plantExperimentTaskDTO.getApplyType())) {
            StringBuffer sampleCodeRangeBuff = new StringBuffer();
            Map<String, List<PlantSampleTestTb>> plantSampleTestTbMap = sampleTestTbList.stream().collect(Collectors.groupingBy(sampleTestTb -> sampleTestTb.getSampleCode().replaceAll("\\\\d", "")));
            plantSampleTestTbMap.forEach((sampleCodePrefix, sampleTestList) -> {
                sampleTestList = sampleTestList.stream().filter(sampleTest -> sampleTest.getSampleCode().startsWith(sampleCodePrefix)).sorted(Comparator.comparing(sampleTest -> Integer.valueOf(sampleTest.getSampleCode().substring(2)))).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(sampleTestList)) {
                    sampleCodeRangeBuff.append(sampleTestList.get(0).getSampleCode() + "-" + sampleTestList.get(sampleTestList.size() - 1).getSampleCode()).append(",");
                }
            });
            if (StringUtils.isNotEmpty(sampleCodeRangeBuff.toString())) {
                plantSampleApplyTb.setSampleCodeRange(sampleCodeRangeBuff.substring(0, sampleCodeRangeBuff.length() - 1));
                plantSampleApplyTb.setVectorTaskCodes(JSONUtil.toJsonStr(sampleTestTbList.stream().filter(plantSampleTestTb -> StringUtils.isNotEmpty(plantSampleTestTb.getVectorTaskCode())).map(PlantSampleTestTb::getVectorTaskCode).collect(Collectors.toList())).replace("[", "").replace("]", "").replace("\"", ""));
            }
        }
        //更新数据
        plantSampleApplyTbMapper.insert(plantSampleApplyTb);
        try {
            plantSampleTestTbMapper.insertBatch(sampleTestTbList);
        } catch (DuplicateKeyException e) {
            log.error("取样申请异常", e);
            throw new BusinessException("取样编号有重复");
        }
    }

    @Nullable
    private PlantSampleCodePrefixTb getPlantSampleCodePrefixTb(PlantSampleTestTaskDTO.FirstSampleApply firstSampleApply) {
        PlantSampleCodePrefixTb plantSampleCodePrefixTb = null;
        if (SourceCodeEnum.project.name().equals(firstSampleApply.getSourceCode())) {
            //项目的取样数据占时不在这
        } else if (SourceCodeEnum.cer.name().equals(firstSampleApply.getSourceCode())) {
            PlantExperimentTb plantExperimentTb = plantExperimentTbMapper.selectOneByExperimentNum(firstSampleApply.getPlantExperimentNum());
            if (plantExperimentTb == null) {
                throw new BusinessException("数据异常，找不到CER试验：" + firstSampleApply.getPlantExperimentNum());
            }
            if (StringUtils.isEmpty(plantExperimentTb.getSampleCodePrefix())) {
                throw new BusinessException("CER试验" + plantExperimentTb.getSampleCodePrefix() + "未配置取样编号前缀");
            }
            plantSampleCodePrefixTb = plantSampleCodePrefixTbMapper.selectOneBySampleCodePrefix(plantExperimentTb.getSampleCodePrefix());
            if (plantSampleCodePrefixTb == null) {
                throw new BusinessException("CER试验" + plantExperimentTb.getSampleCodePrefix() + "找不到取样编号前缀");
            }

        }
        return plantSampleCodePrefixTb;
    }

    @NotNull
    private static PlantSampleApplyTb initPlantSampleApplyTb(BioTaskDtlTb bioTaskDtlTb, PlantSampleTestTaskDTO plantExperimentTaskDTO) {
        PlantSampleApplyTb plantSampleApplyTb = new PlantSampleApplyTb();
        plantSampleApplyTb.setApplyNo(bioTaskDtlTb.getTaskNum());
        plantSampleApplyTb.setApplyNumber(0);
        plantSampleApplyTb.setApplyTime(new Date());
        plantSampleApplyTb.setApplyUserId(SecurityContextHolder.getUserId());
        plantSampleApplyTb.setApplyUserName(SecurityContextHolder.getNickName());
        plantSampleApplyTb.setApplyDesc(bioTaskDtlTb.getTaskDesc());
        plantSampleApplyTb.setApplyType(plantExperimentTaskDTO.getApplyType());
        plantSampleApplyTb.setIdentifyExcelUrl(null);
        plantSampleApplyTb.setOneTestExcelUrl(null);
        plantSampleApplyTb.setNgsExcelUrl(null);
        plantSampleApplyTb.setLayoutFlag(plantExperimentTaskDTO.getTestType());
        plantSampleApplyTb.setVectorTaskCodes(null);
        plantSampleApplyTb.setSampleCodeRange(null);
        return plantSampleApplyTb;
    }


    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            PlantSampleApplyTb plantSampleApplyTb = plantSampleApplyTbMapper.selectOneByApplyNo(bioTaskDtlTb.getTaskNum());

            plantSampleTestTbMapper.updateCheckResultByApplyNoAndCheckResultIsNull(CheckResultEnum.remove.name(), plantSampleApplyTb.getApplyNo());

            //首次取样，且已经发生过移苗
            if (SampleTestApplyTypeEnum.F.name().equals(plantSampleApplyTb.getApplyType())) {
                List<PlantSingleStockTb> plantSingleStockTbList = new ArrayList<>();
                List<PlantSampleTestTb> plantSampleTestTbList = plantSampleTestTbMapper.selectAllByApplyNo(plantSampleApplyTb.getApplyNo()).stream().filter(plantSampleTestTb -> CheckResultEnum.stay.name().equals(plantSampleTestTb.getCheckResult())).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(plantSampleTestTbList)) {
                    for (PlantSampleTestTb plantSampleTestTb : plantSampleTestTbList) {
                        PlantSingleStockTb plantSingleStockTb = PlantSingleStockTb.of(plantSampleTestTb);
                        plantSingleStockTbList.add(plantSingleStockTb);
                    }

                }
                plantSingleStockTbMapper.insertBatch(plantSingleStockTbList);

            }

        }

    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        //todo
    }
}
