package com.bio.drqi.manage.flowtask.plant;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.common.enums.PlantStatusEnum;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.PlantSingleStockTb;
import com.bio.drqi.manage.dto.plant.PlantDataReportDTO;
import com.bio.drqi.mapper.PlantSingleStockTbMapper;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("plant_data_report")
@Slf4j
public class PlanDataReportProcService extends AbstractPlantBaseTaskService {

    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        PlantDataReportDTO plantDataReportDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlantDataReportDTO.class);
        if (CollectionUtil.isEmpty(plantDataReportDTO.getContentList())) {
            throw new BusinessException("excel中没有数据");
        }
        for (PlantDataReportDTO.Content content : plantDataReportDTO.getContentList()) {
            PlantSingleStockTb plantSingleStockTb = plantSingleStockTbMapper.selectOneByPlantCode(content.getPlantCode());
            if (plantSingleStockTb == null) {
                throw new BusinessException("找不到此种植编号:" + content.getPlantCode());
            }
            if (StringUtil.isNotEmpty(content.getPlantStatus())) {
                if (PlantStatusEnum.getCodeByDesc(content.getPlantStatus()) == null) {
                    throw new BusinessException("植株状态异常：" + content.getPlantStatus());
                }
            }
        }
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            PlantDataReportDTO plantDataReportDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlantDataReportDTO.class);
            for (PlantDataReportDTO.Content content : plantDataReportDTO.getContentList()) {
                PlantSingleStockTb plantSingleStockTb = plantSingleStockTbMapper.selectOneByPlantCode(content.getPlantCode());
                if (plantSingleStockTb == null) {
                    throw new BusinessException("找不到此种植编号:" + content.getPlantCode());
                }
                plantSingleStockTb.setPollinationDate(content.getPollinationDate());
                plantSingleStockTb.setVernalizationEndDate(content.getVernalizationEndDate());
                plantSingleStockTb.setVernalizationBeginDate(content.getVernalizationBeginDate());
                plantSingleStockTb.setTransplantDate(content.getTransplantDate());
                plantSingleStockTb.setPlantDate(content.getPlantDate());
                plantSingleStockTb.setPollinationMethod(content.getPollinationMethod());
                plantSingleStockTb.setHarvestDate(content.getHarvestDate());
                if (StringUtil.isNotEmpty(content.getPlantStatus())) {
                    plantSingleStockTb.setPlantStatus(PlantStatusEnum.getCodeByDesc(content.getPlantStatus()));
                }
                plantSingleStockTbMapper.updateById(plantSingleStockTb);
            }
        }


    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }
}
