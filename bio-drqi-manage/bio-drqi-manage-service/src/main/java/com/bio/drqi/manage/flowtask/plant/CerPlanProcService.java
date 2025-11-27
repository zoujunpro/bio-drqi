package com.bio.drqi.manage.flowtask.plant;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.common.enums.PlantStatusEnum;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.CerPlantDtlTb;
import com.bio.drqi.domain.PlantSingleStockTb;
import com.bio.drqi.manage.dto.project.CerPlantDTO;
import com.bio.drqi.manage.flowtask.project.AbstractProjectBaseTaskService;
import com.bio.drqi.mapper.BioTaskDtlTbMapper;
import com.bio.drqi.mapper.CerPlantDtlTbMapper;
import com.bio.drqi.mapper.PlantSingleStockTbMapper;
import com.github.pagehelper.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("cer_plant")
@Slf4j
public class CerPlanProcService extends AbstractPlantBaseTaskService {

    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        CerPlantDTO cerPlantDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), CerPlantDTO.class);
        if (CollectionUtil.isEmpty(cerPlantDTO.getContentList())) {
            throw new BusinessException("excel中没有数据");
        }
        for (CerPlantDTO.Content content : cerPlantDTO.getContentList()) {
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
            CerPlantDTO cerPlantDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), CerPlantDTO.class);
            for (CerPlantDTO.Content content : cerPlantDTO.getContentList()) {
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
