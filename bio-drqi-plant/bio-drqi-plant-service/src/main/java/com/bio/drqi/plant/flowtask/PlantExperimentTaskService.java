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
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.common.enums.SampleGroupPergixEnum;
import com.bio.drqi.common.util.LetterUtil;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.bio.drqi.plant.dto.ExperimentExcelDTO;
import com.bio.drqi.plant.dto.task.PlantExperimentTaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service("plant_experiment_task")
@Slf4j
public class PlantExperimentTaskService extends AbstractPlantBaseTaskService {

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
    private PlantSampleCodePrefixTbMapper plantSampleCodePrefixTbMapper;

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
            SeedProduceAddressDict seedProduceAddressDict = seedProduceAddressDictMapper.selectOneByAddressName(experimentExcelDTO.getExperimentAddressName());
            if (seedProduceAddressDict == null) {
                throw new BusinessException("试验地点不正确");
            }
            if (!cerSpeciesConf.getSpeciesCode().equals(seedStockTb.getSpeciesCode())) {
                throw new BusinessException("所选种子物种不匹配");
            }
            List<PlantExperimentDetailTb> plantExperimentDetailTbList = plantExperimentDetailTbMapper.selectAllByRegionNum(experimentExcelDTO.getRegionNum());
            if (CollectionUtil.isNotEmpty(plantExperimentDetailTbList)) {
                throw new BusinessException("小区编号" + experimentExcelDTO.getRegionNum() + "已经存在其他试验中");
            }
            if (checkReginCodeAndSeedNumList.contains(experimentExcelDTO.getRegionNum() + experimentExcelDTO.getSeedNum())) {
                throw new BusinessException("CER试验小区" + experimentExcelDTO.getRegionNum() + "中存在重复种子编号" + experimentExcelDTO.getSeedNum());
            }
            checkReginCodeAndSeedNumList.add(experimentExcelDTO.getRegionNum() + experimentExcelDTO.getSeedNum());

        }
    }


    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            PlantExperimentTaskDTO plantExperimentTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlantExperimentTaskDTO.class);
            List<ExperimentExcelDTO> experimentExcelDTOList = getExperimentExcelDTOS(plantExperimentTaskDTO);
            PlantExperimentTb plantExperimentTb = new PlantExperimentTb();
            plantExperimentTb.setSpeciesCode(plantExperimentTaskDTO.getSpeciesCode());
            plantExperimentTb.setExperimentType(plantExperimentTaskDTO.getExperimentType());
            plantExperimentTb.setExperimentTarget(plantExperimentTaskDTO.getExperimentTarget());
            plantExperimentTb.setDesignUrl(plantExperimentTaskDTO.getDesignUrl());
            plantExperimentTb.setFileUrl(plantExperimentTaskDTO.getFileUrl());
            plantExperimentTb.setExperimentNum(bioTaskDtlTb.getTaskNum());
            plantExperimentTb.setCreateTime(new Date());
            plantExperimentTb.setCreateUserId(SecurityContextHolder.getUserId());
            plantExperimentTb.setCreateUserName(SecurityContextHolder.getNickName());
            plantExperimentTb.setSampleCodePrefix(createSampleCode());
            plantExperimentTb.setVectorTaskCodes(JSONUtil.toJsonStr(experimentExcelDTOList.stream().map(ExperimentExcelDTO::getVectorTaskCode).filter(vectorTaskCode -> StringUtils.isNotEmpty(vectorTaskCode)).collect(Collectors.toList())));
            plantExperimentTb.setPdNums(JSONUtil.toJsonStr(experimentExcelDTOList.stream().map(ExperimentExcelDTO::getPdNumber).filter(pdNumber -> StringUtils.isNotEmpty(pdNumber)).collect(Collectors.toList())));
            List<PlantExperimentDetailTb> plantExperimentDetailTbList = new ArrayList<>();
            for (ExperimentExcelDTO experimentExcelDTO : experimentExcelDTOList) {
                SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(experimentExcelDTO.getSeedNum());
                SeedProduceAddressDict seedProduceAddressDict = seedProduceAddressDictMapper.selectOneByAddressName(experimentExcelDTO.getExperimentAddressName());
                PlantExperimentDetailTb plantExperimentDetailTb = new PlantExperimentDetailTb();
                plantExperimentDetailTb.setPdNum(experimentExcelDTO.getPdNumber());
                plantExperimentDetailTb.setExperimentNum(plantExperimentTb.getExperimentNum());
                plantExperimentDetailTb.setRegionNum(experimentExcelDTO.getRegionNum());
                plantExperimentDetailTb.setVectorTaskCode(experimentExcelDTO.getVectorTaskCode());
                plantExperimentDetailTb.setSeedNum(experimentExcelDTO.getSeedNum());
                plantExperimentDetailTb.setPlantCode(seedStockTb.getPlantCode());
                plantExperimentDetailTb.setGenerationCode(seedStockTb.getGeneration());
                plantExperimentDetailTb.setSpeciesCode(seedStockTb.getSpeciesCode());
                plantExperimentDetailTb.setBreedCode(seedStockTb.getBreedCode());
                plantExperimentDetailTb.setPlantTime(experimentExcelDTO.getPlantTime());
                plantExperimentDetailTb.setPlantNumber(experimentExcelDTO.getPlantNumber());
                plantExperimentDetailTb.setPlantUnit("粒");
                plantExperimentDetailTb.setExperimentAddressCode(seedProduceAddressDict.getAddressCode());
                plantExperimentDetailTb.setRemarks(experimentExcelDTO.getRemark());
                plantExperimentDetailTb.setGeneType(seedStockTb.getGeneType());
                plantExperimentDetailTb.setCreateUserId(SecurityContextHolder.getUserId());
                plantExperimentDetailTb.setCreateUserName(SecurityContextHolder.getNickName());
                plantExperimentDetailTb.setCreateTime(new Date());
                plantExperimentDetailTbList.add(plantExperimentDetailTb);
            }
            List<PlantMultipleStockTb> plantMultipleStockTbList = plantExperimentDetailTbList.stream().map(plantExperimentDetailTb -> PlantMultipleStockTb.of(plantExperimentDetailTb)).collect(Collectors.toList());
            plantExperimentTbMapper.insert(plantExperimentTb);
            plantExperimentDetailTbMapper.insertBatch(plantExperimentDetailTbList);
            plantMultipleStockTbMapper.insertBatch(plantMultipleStockTbList);
            plantSampleCodePrefixTbMapper.insert(new PlantSampleCodePrefixTb(plantExperimentTb.getSampleCodePrefix()));

            plantExperimentTaskDTO.setSampleCodePrefix(plantExperimentTb.getSampleCodePrefix());
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(plantExperimentTaskDTO));
        }

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

    private String createSampleCode() {
        String maxSampleCodePrefix = plantExperimentTbMapper.selectMaxSampleCodePrefix();
        if (StringUtils.isEmpty(maxSampleCodePrefix)) {
            return "CAA";
        } else {
            return SampleGroupPergixEnum.C.name() + LetterUtil.nextLetterForInstantVerify(maxSampleCodePrefix.substring(1));
        }
    }
}
