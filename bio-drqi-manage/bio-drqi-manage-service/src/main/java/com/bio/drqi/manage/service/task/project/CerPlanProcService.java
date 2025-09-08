package com.bio.drqi.manage.service.task.project;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.CerPlantDtlTb;
import com.bio.drqi.enums.CerPlantFixedFieldEnum;
import com.bio.drqi.manage.dto.project.CerPlantDTO;
import com.bio.drqi.mapper.CerPlantDtlTbMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

@Service("cer_plant")
@Slf4j
public class CerPlanProcService extends AbstractProjectBaseTaskService {

    @Resource
    private OssService ossService;

    @Resource
    private CerPlantDtlTbMapper cerPlantDtlTbMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        CerPlantDTO cerPlantDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), CerPlantDTO.class);
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + cerPlantDTO.getExcelUrl();
        try {
            ossService.downloadPath(tempFilePath, cerPlantDTO.getExcelUrl());
        } catch (Exception e) {
            log.error("【CER种植数据】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        List<CerPlantDTO.Content> contentList = ExcelUtil.readExcel(tempFilePath, CerPlantDTO.Content.class);
        if (CollectionUtil.isEmpty(contentList)) {
            throw new BusinessException("excel中没有数据");
        }
        for (CerPlantDTO.Content content : contentList) {
            CerPlantDtlTb cerPlantDtlTb = cerPlantDtlTbMapper.selectOneByPlantCode(content.getPlantCode());
            if (cerPlantDtlTb == null) {
                throw new BusinessException("找不到此种植编号");
            }
        }
        cerPlantDTO.setContentList(contentList);
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            CerPlantDTO cerPlantDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), CerPlantDTO.class);
            for (CerPlantDTO.Content content : cerPlantDTO.getContentList()) {
                CerPlantDtlTb cerPlantDtlTb = cerPlantDtlTbMapper.selectOneByPlantCode(content.getPlantCode());
                if (cerPlantDtlTb == null) {
                    throw new BusinessException("找不到此种植编号");
                }
                cerPlantDtlTb.setHarvestDate(content.getPlantDate());
                cerPlantDtlTb.setPollinationDate(content.getPollinationDate());
                cerPlantDtlTb.setVernalizationEndDate(content.getVernalizationEndDate());
                cerPlantDtlTb.setVernalizationBeginDate(content.getVernalizationBeginDate());
                cerPlantDtlTb.setTransplantDate(content.getTransplantDate());
                cerPlantDtlTb.setPlantDate(content.getPlantDate());
                cerPlantDtlTb.setPollinationMethod(content.getPollinationMethod());
                cerPlantDtlTb.setPlantStatus(content.getPlantStatus());
                cerPlantDtlTbMapper.updateById(cerPlantDtlTb);
            }
        }


    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }
}
