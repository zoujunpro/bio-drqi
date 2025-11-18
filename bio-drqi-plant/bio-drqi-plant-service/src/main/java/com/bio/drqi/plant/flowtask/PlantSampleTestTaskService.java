package com.bio.drqi.plant.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.enums.PlantSourceCodeEnum;
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
import java.util.List;

@Service("plant_sample_test_task")
@Slf4j
public class PlantSampleTestTaskService extends AbstractPlantBaseTaskService {

    @Resource
    private OssService ossService;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private SeedProduceAddressDictMapper seedProduceAddressDictMapper;

    @Resource
    private PlantExperimentTbMapper plantExperimentTbMapper;


    @Resource
    private PlantExperimentDetailTbMapper plantExperimentDetailTbMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private PlantMultipleStockTbMapper plantMultipleStockTbMapper;

    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;


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
        if (SampleTestApplyTypeEnum.F.name().equals(plantExperimentTaskDTO.getApplyType())) {
            if (CollectionUtil.isEmpty(plantExperimentTaskDTO.getFirstSampleApplyList())) {
                throw new BusinessException("无取样数据");
            }
            for (PlantSampleTestTaskDTO.FirstSampleApply firstSampleApply : plantExperimentTaskDTO.getFirstSampleApplyList()) {
                ValidatorUtil.validator(firstSampleApply);
                BeanUtils.trimFiledSpace(firstSampleApply);
                if (PlantSourceCodeEnum.project.name().equals(firstSampleApply.getSourceCode())) {
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
            }
        }

        if (SampleTestApplyTypeEnum.R.name().equals(plantExperimentTaskDTO.getApplyType())) {
            if (CollectionUtil.isEmpty(plantExperimentTaskDTO.getRepeatSampleTestList())) {
                throw new BusinessException("无取样数据");
            }
            for (PlantSampleTestTaskDTO.RepeatSampleTest repeatSampleTest : plantExperimentTaskDTO.getRepeatSampleTestList()) {
                PlantSingleStockTb plantSingleStockTb = plantSingleStockTbMapper.selectOneByPlantCode(repeatSampleTest.getSampleCode());
                if(plantSingleStockTb==null){
                    throw new BusinessException("CER中无此种植编号苗信息："+repeatSampleTest.getSampleCode());
                }
                //todo 需要校验状态
            }
        }
        //生成种植数据



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
