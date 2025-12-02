package com.bio.drqi.manage.flowtask.plant;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.common.enums.SampleGroupPergixEnum;
import com.bio.drqi.common.enums.SourceCodeEnum;
import com.bio.drqi.common.util.LetterUtil;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.dto.plant.ExperimentExcelDTO;
import com.bio.drqi.mapper.*;
import com.bio.drqi.manage.dto.plant.task.PlantExperimentTaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service("plant_apply_task")
@Slf4j
public class PlantApplyTaskService extends AbstractPlantBaseTaskService {

    @Resource
    private OssService ossService;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private PlantApplyTbMapper plantApplyTbMapper;


    @Resource
    private PlantApplyDetailTbMapper plantApplyDetailTbMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private PlantMultipleStockTbMapper plantMultipleStockTbMapper;

    @Resource
    private BioSampleCodePrefixTbMapper bioSampleCodePrefixTbMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        PlantExperimentTaskDTO plantExperimentTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlantExperimentTaskDTO.class);
        ValidatorUtil.validator(plantExperimentTaskDTO);
        //校验内容
        CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectOneBySpeciesCode(plantExperimentTaskDTO.getSpeciesCode());
        if (cerSpeciesConf == null) {
            throw new BusinessException("物种找不到");
        }
        List<ExperimentExcelDTO> experimentExcelDTOList = getExperimentExcelDTOS(plantExperimentTaskDTO);
        List<String> checkReginCodeAndSeedNumList = new ArrayList<>();
        for (ExperimentExcelDTO experimentExcelDTO : experimentExcelDTOList) {
            BeanUtils.trimFiledSpace(experimentExcelDTO);
            ValidatorUtil.validator(plantExperimentTaskDTO);
            SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(experimentExcelDTO.getSeedNum());
            if (seedStockTb == null) {
                throw new BusinessException("上传数据在种子库中找不到材料信息" + experimentExcelDTO.getSeedNum());
            }
            if (!cerSpeciesConf.getSpeciesCode().equals(seedStockTb.getSpeciesCode())) {
                throw new BusinessException("所选种子物种不匹配");
            }
            List<PlantApplyDetailTb> plantExperimentDetailTbList = plantApplyDetailTbMapper.selectAllByRegionNum(experimentExcelDTO.getRegionNum());
            if (CollectionUtil.isNotEmpty(plantExperimentDetailTbList)) {
                throw new BusinessException("小区编号" + experimentExcelDTO.getRegionNum() + "已经存在其他试验中");
            }
            if (checkReginCodeAndSeedNumList.contains(experimentExcelDTO.getRegionNum() + experimentExcelDTO.getSeedNum())) {
                throw new BusinessException("CER试验小区" + experimentExcelDTO.getRegionNum() + "中存在重复种子编号" + experimentExcelDTO.getSeedNum());
            }
            checkReginCodeAndSeedNumList.add(experimentExcelDTO.getRegionNum() + experimentExcelDTO.getSeedNum());
        }
        plantExperimentTaskDTO.setVectorTaskCodeList(experimentExcelDTOList.stream().map(ExperimentExcelDTO::getVectorTaskCode).filter(vectorTaskCode -> StringUtils.isNotEmpty(vectorTaskCode)).distinct().collect(Collectors.toList()));
        plantExperimentTaskDTO.setPdImplementCodeList(experimentExcelDTOList.stream().map(ExperimentExcelDTO::getPdImplementCode).filter(pdImplementCode -> StringUtils.isNotEmpty(pdImplementCode)).distinct().collect(Collectors.toList()));
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(plantExperimentTaskDTO));
    }


    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            PlantExperimentTaskDTO plantExperimentTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlantExperimentTaskDTO.class);
            List<ExperimentExcelDTO> experimentExcelDTOList = getExperimentExcelDTOS(plantExperimentTaskDTO);
            PlantApplyTb plantApplyTb = new PlantApplyTb();
            plantApplyTb.setSpeciesCode(plantExperimentTaskDTO.getSpeciesCode());
            plantApplyTb.setExperimentType(JSONUtil.toJsonStr(plantExperimentTaskDTO.getExperimentType()));
            plantApplyTb.setPlantTarget(plantExperimentTaskDTO.getPlantTarget());
            plantApplyTb.setPlantDetailUrl(plantExperimentTaskDTO.getPlantDetailUrl());
            plantApplyTb.setFileUrl(plantExperimentTaskDTO.getFileUrl());
            plantApplyTb.setPlantApplyNum(bioTaskDtlTb.getTaskNum());
            plantApplyTb.setCreateTime(new Date());
            plantApplyTb.setCreateUserId(SecurityContextHolder.getUserId());
            plantApplyTb.setCreateUserName(SecurityContextHolder.getNickName());
            plantApplyTb.setExperimentAddressCode(plantExperimentTaskDTO.getExperimentAddressCode());
            plantApplyTb.setSampleCodePrefix(createSampleCode());
            plantApplyTb.setVectorTaskCodes(JSONUtil.toJsonStr(experimentExcelDTOList.stream().map(ExperimentExcelDTO::getVectorTaskCode).filter(vectorTaskCode -> StringUtils.isNotEmpty(vectorTaskCode)).distinct().collect(Collectors.toList())));
            plantApplyTb.setPdImplementCodes(JSONUtil.toJsonStr(experimentExcelDTOList.stream().map(ExperimentExcelDTO::getPdImplementCode).filter(pdImplementCode -> StringUtils.isNotEmpty(pdImplementCode)).distinct().collect(Collectors.toList())));
            List<PlantApplyDetailTb> plantExperimentDetailTbList = new ArrayList<>();
            for (ExperimentExcelDTO experimentExcelDTO : experimentExcelDTOList) {
                SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(experimentExcelDTO.getSeedNum());
                PlantApplyDetailTb plantApplyDetailTb = new PlantApplyDetailTb();
                plantApplyDetailTb.setPdImplementCode(experimentExcelDTO.getPdImplementCode());
                plantApplyDetailTb.setPlantApplyNum(plantApplyTb.getPlantApplyNum());
                plantApplyDetailTb.setRegionNum(experimentExcelDTO.getRegionNum());
                plantApplyDetailTb.setVectorTaskCode(experimentExcelDTO.getVectorTaskCode());
                plantApplyDetailTb.setSeedNum(experimentExcelDTO.getSeedNum());
                plantApplyDetailTb.setPlantCode(seedStockTb.getPlantCode());
                plantApplyDetailTb.setGenerationCode(seedStockTb.getGeneration());
                plantApplyDetailTb.setSpeciesCode(seedStockTb.getSpeciesCode());
                plantApplyDetailTb.setBreedCode(seedStockTb.getBreedCode());
                plantApplyDetailTb.setPlantTime(experimentExcelDTO.getPlantTime());
                plantApplyDetailTb.setPlantNumber(experimentExcelDTO.getPlantNumber());
                plantApplyDetailTb.setPlantUnit("粒");
                plantApplyDetailTb.setRemarks(experimentExcelDTO.getRemark());
                plantApplyDetailTb.setGeneType(seedStockTb.getGeneType());
                plantApplyDetailTb.setCreateUserId(SecurityContextHolder.getUserId());
                plantApplyDetailTb.setCreateUserName(SecurityContextHolder.getNickName());
                plantApplyDetailTb.setCreateTime(new Date());
                plantExperimentDetailTbList.add(plantApplyDetailTb);
            }
            List<PlantMultipleStockTb> plantMultipleStockTbList = plantExperimentDetailTbList.stream().map(plantExperimentDetailTb -> PlantMultipleStockTb.of(plantExperimentDetailTb, bioTaskDtlTb, SourceCodeEnum.cer)).collect(Collectors.toList());
            plantApplyTbMapper.insert(plantApplyTb);
            plantApplyDetailTbMapper.insertBatch(plantExperimentDetailTbList);
            plantMultipleStockTbMapper.insertBatch(plantMultipleStockTbList);
            bioSampleCodePrefixTbMapper.insert(new BioSampleCodePrefixTb(plantApplyTb.getSampleCodePrefix(),null, plantApplyTb.getPlantApplyNum()));
            plantExperimentTaskDTO.setSampleCodePrefix(plantApplyTb.getSampleCodePrefix());
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(plantExperimentTaskDTO));
        }

    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }

    @NotNull
    private List<ExperimentExcelDTO> getExperimentExcelDTOS(PlantExperimentTaskDTO plantExperimentTaskDTO) {
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + plantExperimentTaskDTO.getPlantDetailUrl();
        try {
            ossService.downloadPath(tempFilePath, plantExperimentTaskDTO.getPlantDetailUrl());
        } catch (Exception e) {
            log.error("【CER试验申请】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        List<ExperimentExcelDTO> experimentExcelDTOList = ExcelUtil.readExcel(tempFilePath, ExperimentExcelDTO.class);
        return experimentExcelDTOList;
    }

    private String createSampleCode() {
        String maxSampleCodePrefix = plantApplyTbMapper.selectMaxSampleCodePrefix();
        if (StringUtils.isEmpty(maxSampleCodePrefix)) {
            return "CAA";
        } else {
            return SampleGroupPergixEnum.C.name() + LetterUtil.nextLetterForInstantVerify(maxSampleCodePrefix.substring(1));
        }
    }
}
