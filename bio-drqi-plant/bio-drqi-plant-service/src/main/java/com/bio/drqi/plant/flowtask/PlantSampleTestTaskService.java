package com.bio.drqi.plant.flowtask;

import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.bio.drqi.plant.dto.ExperimentExcelDTO;
import com.bio.drqi.plant.dto.task.ExperimentTaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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


    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        ExperimentTaskDTO experimentTaskDTO = BeanUtils.toBean(bioTaskDtlTb.getTaskForm(), ExperimentTaskDTO.class);
        ValidatorUtil.validator(experimentTaskDTO);
        //校验内容


        CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectOneBySpeciesCode(experimentTaskDTO.getSpeciesCode());
        if (cerSpeciesConf == null) {
            throw new BusinessException("物种找不到");
        }

        List<ExperimentExcelDTO> experimentExcelDTOList = getExperimentExcelDTOS(experimentTaskDTO);
        for (ExperimentExcelDTO experimentExcelDTO : experimentExcelDTOList) {
            BeanUtils.trimFiledSpace(experimentExcelDTO);
            ValidatorUtil.validator(experimentTaskDTO);
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
        }
    }


    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {

    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }

    @NotNull
    private List<ExperimentExcelDTO> getExperimentExcelDTOS(ExperimentTaskDTO experimentTaskDTO) {
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + experimentTaskDTO.getDesignUrl();
        try {
            ossService.downloadPath(tempFilePath, experimentTaskDTO.getDesignUrl());
        } catch (Exception e) {
            log.error("【CER试验申请】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        List<ExperimentExcelDTO> experimentExcelDTOList = ExcelUtil.readExcel(tempFilePath, ExperimentExcelDTO.class);
        return experimentExcelDTOList;
    }
}
