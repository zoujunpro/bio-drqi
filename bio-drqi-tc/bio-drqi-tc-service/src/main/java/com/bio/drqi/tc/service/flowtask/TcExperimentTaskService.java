package com.bio.drqi.tc.service.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.TcExperimentDesignTb;
import com.bio.drqi.domain.TcExperimentTb;
import com.bio.drqi.enums.BioTaskStatusEnum;
import com.bio.drqi.mapper.TcExperimentDesignTbMapper;
import com.bio.drqi.mapper.TcExperimentTbMapper;
import com.bio.drqi.tc.service.dto.ExperimentDesignExcelDTO;
import com.bio.drqi.tc.service.dto.TcExperimentTaskDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service("tc_experiment_task_apply")
public class TcExperimentTaskService extends AbstractTcBaseTaskService {


    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;

    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        TcExperimentTaskDTO tcExperimentTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcExperimentTaskDTO.class);
        ValidatorUtil.validator(tcExperimentTaskDTO);
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        TcExperimentTaskDTO tcExperimentTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcExperimentTaskDTO.class);
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            TcExperimentTb tcExperimentTb = new TcExperimentTb();
            tcExperimentTb.setProjectCodes(JSONUtil.toJsonStr(tcExperimentTaskDTO.getProjectCodeList()));
            tcExperimentTb.setVectorTaskCodes(JSONUtil.toJsonStr(tcExperimentTaskDTO.getVectorTaskCodeList()));
            tcExperimentTb.setSpeciesCode(tcExperimentTaskDTO.getSpeciesCode());
            tcExperimentTb.setSpeciesName(tcExperimentTaskDTO.getSpeciesName());
            tcExperimentTb.setFileUrl(tcExperimentTaskDTO.getFileUrl());
            tcExperimentTb.setExperimentGoal(tcExperimentTaskDTO.getExperimentGoal());
            tcExperimentTb.setExperimentAddress(tcExperimentTaskDTO.getExperimentAddress());
            tcExperimentTb.setApplyUserName(bioTaskDtlTb.getApplyUserName());
            tcExperimentTb.setApplyUserId(bioTaskDtlTb.getApplyUserId());
            tcExperimentTb.setCreateTime(new Date());
            tcExperimentTb.setExperimentNum(bioTaskDtlTb.getTaskNum());
            tcExperimentTb.setTaskNum(bioTaskDtlTb.getTaskNum());
            tcExperimentTbMapper.insert(tcExperimentTb);

            if(StringUtils.isEmpty(tcExperimentTaskDTO.getExperimentDesignUrl())){
                throw new BusinessException("大田试验方案缺失");
            }
            List<ExperimentDesignExcelDTO> experimentDesignExcelDTOList = ExcelUtil.readExcel(tcExperimentTaskDTO.getExperimentDesignUrl(), ExperimentDesignExcelDTO.class);
            if(CollectionUtil.isEmpty(experimentDesignExcelDTOList)){
                throw new BusinessException("大田试验方案内容缺失");
            }
            List<TcExperimentDesignTb> tcExperimentDesignTbList = new ArrayList<TcExperimentDesignTb>();
            for (ExperimentDesignExcelDTO experimentDesignExcelDTO:experimentDesignExcelDTOList){
                TcExperimentDesignTb tcExperimentDesignTb = new TcExperimentDesignTb();
                tcExperimentDesignTb.setExperimentNum(experimentDesignExcelDTO.getExperimentNum());
                tcExperimentDesignTb.setRegionNum(experimentDesignExcelDTO.getRegionNum());
                tcExperimentDesignTb.setSeedNum(experimentDesignExcelDTO.getSeedNum());
                tcExperimentDesignTb.setProjectCode(experimentDesignExcelDTO.getProjectCode());
                tcExperimentDesignTb.setVectorTaskCode(experimentDesignExcelDTO.getVectorTaskCode());
                tcExperimentDesignTb.setSpeciesCode(tcExperimentTb.getSpeciesCode());
                tcExperimentDesignTb.setBreedName(experimentDesignExcelDTO.getBreedName());
                tcExperimentDesignTb.setTargetCharacter(experimentDesignExcelDTO.getTargetCharacter());
                tcExperimentDesignTb.setGenerationCode(experimentDesignExcelDTO.getGenerationCode());
                tcExperimentDesignTb.setTcGene(experimentDesignExcelDTO.getTcGene());
                tcExperimentDesignTb.setRegionArea(experimentDesignExcelDTO.getRegionArea());
                tcExperimentDesignTb.setAreaUnit(experimentDesignExcelDTO.getAreaUnit());
                tcExperimentDesignTb.setPlantSpace(experimentDesignExcelDTO.getPlantSpace());
                tcExperimentDesignTb.setRowNumber(experimentDesignExcelDTO.getRowNumber());
                tcExperimentDesignTb.setRowLength(experimentDesignExcelDTO.getRowLength());
                tcExperimentDesignTb.setRowSpace(experimentDesignExcelDTO.getRowSpace());
                tcExperimentDesignTb.setSeedingType(experimentDesignExcelDTO.getSeedingType());
                tcExperimentDesignTb.setSeedingNumber(experimentDesignExcelDTO.getSeedingNumber());
                tcExperimentDesignTb.setSeedingUnit(experimentDesignExcelDTO.getSeedingUnit());
                tcExperimentDesignTb.setSeedingTime(experimentDesignExcelDTO.getSeedingTime());
                tcExperimentDesignTb.setRemark(experimentDesignExcelDTO.getRemark());
                tcExperimentDesignTb.setTaskNum(bioTaskDtlTb.getTaskNum());
                tcExperimentDesignTb.setCreateUserId(bioTaskDtlTb.getApplyUserId());
                tcExperimentDesignTb.setCreateUserName(bioTaskDtlTb.getApplyUserName());
                tcExperimentDesignTb.setCreateTime(new Date());
                tcExperimentDesignTb.setEmergenceRate(experimentDesignExcelDTO.getEmergenceRate());
                tcExperimentDesignTb.setTransplantTime(experimentDesignExcelDTO.getTransplantTime());
                tcExperimentDesignTbList.add(tcExperimentDesignTb);
            }
            tcExperimentDesignTbMapper.insertBatch(tcExperimentDesignTbList);
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }
}
