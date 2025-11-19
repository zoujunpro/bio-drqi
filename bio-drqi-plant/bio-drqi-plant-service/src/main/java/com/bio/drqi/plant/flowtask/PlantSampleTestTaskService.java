package com.bio.drqi.plant.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.enums.SourceCodeEnum;
import com.bio.drqi.common.enums.PlantStatusEnum;
import com.bio.drqi.common.enums.SampleTestApplyTypeEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.bio.drqi.plant.dto.ExperimentExcelDTO;
import com.bio.drqi.plant.dto.task.PlantExperimentTaskDTO;
import com.bio.drqi.plant.dto.task.PlantSampleTestTaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.Date;
import java.util.List;

@Service("plant_sample_test_task")
@Slf4j
public class PlantSampleTestTaskService extends AbstractPlantBaseTaskService {

    @Resource
    private OssService ossService;

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


        //首次取样
        if (SampleTestApplyTypeEnum.F.name().equals(plantExperimentTaskDTO.getApplyType())) {
            if (CollectionUtil.isEmpty(plantExperimentTaskDTO.getFirstSampleApplyList())) {
                throw new BusinessException("无取样数据");
            }
            for (PlantSampleTestTaskDTO.FirstSampleApply firstSampleApply : plantExperimentTaskDTO.getFirstSampleApplyList()) {
                ValidatorUtil.validator(firstSampleApply);
                BeanUtils.trimFiledSpace(firstSampleApply);
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
                }
                //找到取样编号前缀
                PlantSampleCodePrefixTb plantSampleCodePrefixTb = null;
                if (SourceCodeEnum.project.name().equals(firstSampleApply.getSourceCode())) {
                    //项目的取样数据占时不在这
                } else if (SourceCodeEnum.cer.name().equals(firstSampleApply.getSourceCode())) {
                    PlantExperimentTb plantExperimentTb = plantExperimentTbMapper.selectOneByExperimentNum(firstSampleApply.getPlantExperimentNum());
                    if (plantExperimentTb == null) {
                        throw new BusinessException("数据异常，找不到CER试验：" + firstSampleApply.getPlantExperimentNum());
                    }
                    if (StringUtils.isEmpty(plantExperimentTb.getSampleCodePrefix())) {
                        throw new BusinessException("CER试验" + plantExperimentTb.getSampleCodePrefix() + "找不到取样编号前缀");
                    }
                    plantSampleCodePrefixTb = plantSampleCodePrefixTbMapper.selectOneBySampleCodePrefix(plantExperimentTb.getSampleCodePrefix());
                    if (plantSampleCodePrefixTb == null) {
                        throw new BusinessException("CER试验" + plantExperimentTb.getSampleCodePrefix() + "找不到取样编号前缀");
                    }

                }
                for (int i = 1; i <= firstSampleApply.getSampleNumber(); i++) {
                    PlantSampleTestTb plantSampleTestTb = new PlantSampleTestTb();
                    plantSampleTestTb.setVectorTaskCode(firstSampleApply.getVectorTaskCode());
                    plantSampleTestTb.setSampleCode(plantSampleCodePrefixTb.getSampleCodePrefix() + (plantSampleCodePrefixTb.getCurrentIndex()+i));
                    plantSampleTestTb.setApplyTime(bioTaskDtlTb.getApplyTime());
                    plantSampleTestTb.setApplyUserId(bioTaskDtlTb.getApplyUserId());
                    plantSampleTestTb.setApplyUserName(bioTaskDtlTb.getApplyUserName());
                    plantSampleTestTb.setSourceCode(firstSampleApply.getSourceCode());
                    plantSampleTestTb.setTestIdentifyPrimer(null);
                    plantSampleTestTb.setTestMethod(null);
                    plantSampleTestTb.setTestEditType(null);
                    plantSampleTestTb.setTestNoTransIdentityPrimer(null);
                    plantSampleTestTb.setTestIsGeneModifyPositive(null);
                    plantSampleTestTb.setTestIfFixedPoint(null);
                    plantSampleTestTb.setTestIfCopyInsert(null);
                    plantSampleTestTb.setTestFixedPointType(null);
                    plantSampleTestTb.setTestDonorResidueInfo(null);
                    plantSampleTestTb.setTestInsertionSite(null);
                    plantSampleTestTb.setTestElisaResult(null);
                    plantSampleTestTb.setTestQbzrSeq(null);
                    plantSampleTestTb.setTestEditResidueInfo(null);
                    plantSampleTestTb.setTestUserId(null);
                    plantSampleTestTb.setTestUserName(null);
                    plantSampleTestTb.setTestTime(null);
                    plantSampleTestTb.setCheckUserName(null);
                    plantSampleTestTb.setCheckUserId(null);
                    plantSampleTestTb.setCheckResult(null);
                    plantSampleTestTb.setCreateTime(new Date());
                    plantSampleTestTb.setUpdateTime(new Date());
                    plantSampleTestTb.setApplyNo(bioTaskDtlTb.getTaskNum());
                    plantSampleTestTb.setIdentifyPrimer(null);
                    plantSampleTestTb.setUniqueCode(null);
                    plantSampleTestTb.setRemark(null);
                    plantSampleTestTb.setCloneSampleCode(null);
                    plantSampleTestTb.setTestOrgResult(null);
                }
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

                PlantSampleTestTb plantSampleTestTb = new PlantSampleTestTb();
                plantSampleTestTb.setVectorTaskCode(plantSingleStockTb.getVectorTaskCode());
                plantSampleTestTb.setSampleCode(plantSingleStockTb.getSampleCode());
                plantSampleTestTb.setApplyTime(bioTaskDtlTb.getApplyTime());
                plantSampleTestTb.setApplyUserId(bioTaskDtlTb.getApplyUserId());
                plantSampleTestTb.setApplyUserName(bioTaskDtlTb.getApplyUserName());
                plantSampleTestTb.setSourceCode(plantSingleStockTb.getSourceCode());
                plantSampleTestTb.setTestIdentifyPrimer(null);
                plantSampleTestTb.setTestMethod(null);
                plantSampleTestTb.setTestEditType(null);
                plantSampleTestTb.setTestNoTransIdentityPrimer(null);
                plantSampleTestTb.setTestIsGeneModifyPositive(null);
                plantSampleTestTb.setTestIfFixedPoint(null);
                plantSampleTestTb.setTestIfCopyInsert(null);
                plantSampleTestTb.setTestFixedPointType(null);
                plantSampleTestTb.setTestDonorResidueInfo(null);
                plantSampleTestTb.setTestInsertionSite(null);
                plantSampleTestTb.setTestElisaResult(null);
                plantSampleTestTb.setTestQbzrSeq(null);
                plantSampleTestTb.setTestEditResidueInfo(null);
                plantSampleTestTb.setTestUserId(null);
                plantSampleTestTb.setTestUserName(null);
                plantSampleTestTb.setTestTime(null);
                plantSampleTestTb.setCheckUserName(null);
                plantSampleTestTb.setCheckUserId(null);
                plantSampleTestTb.setCheckResult(null);
                plantSampleTestTb.setCreateTime(new Date());
                plantSampleTestTb.setUpdateTime(new Date());
                plantSampleTestTb.setApplyNo(bioTaskDtlTb.getTaskNum());
                plantSampleTestTb.setIdentifyPrimer(null);
                plantSampleTestTb.setUniqueCode(null);
                plantSampleTestTb.setRemark(null);
                plantSampleTestTb.setCloneSampleCode(null);
                plantSampleTestTb.setTestOrgResult(null);


            }
        }

    }


    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {

    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }

    @NotNull
    private List<ExperimentExcelDTO> getExperimentExcelDTOS(PlantExperimentTaskDTO plantExperimentTaskDTO) {
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + plantExperimentTaskDTO.getDesignUrl();
        try {
            ossService.downloadPath(tempFilePath, plantExperimentTaskDTO.getDesignUrl());
        } catch (Exception e) {
            log.error("【CER试验申请】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        List<ExperimentExcelDTO> experimentExcelDTOList = ExcelUtil.readExcel(tempFilePath, ExperimentExcelDTO.class);
        return experimentExcelDTOList;
    }
}
