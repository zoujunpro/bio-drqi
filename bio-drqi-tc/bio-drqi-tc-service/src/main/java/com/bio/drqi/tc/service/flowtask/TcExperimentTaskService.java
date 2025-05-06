package com.bio.drqi.tc.service.flowtask;

import cn.hutool.json.JSONUtil;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.TcExperimentDesignTb;
import com.bio.drqi.domain.TcExperimentTb;
import com.bio.drqi.enums.BioTaskStatusEnum;
import com.bio.drqi.mapper.TcExperimentDesignTbMapper;
import com.bio.drqi.mapper.TcExperimentTbMapper;
import com.bio.drqi.tc.service.dto.TcExperimentTaskDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

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
            tcExperimentTb.setProjectCode(tcExperimentTaskDTO.getProjectCode());
            tcExperimentTb.setVectorTaskCode(tcExperimentTaskDTO.getVectorTaskCode());
            tcExperimentTb.setSpeciesCode(tcExperimentTaskDTO.getProjectCode());
            tcExperimentTb.setSpeciesName(tcExperimentTaskDTO.getProjectName());
            tcExperimentTb.setFileUrl(tcExperimentTaskDTO.getFileUrl());
            tcExperimentTb.setExperimentGoal(tcExperimentTaskDTO.getExperimentGoal());
            tcExperimentTb.setExperimentAddress(tcExperimentTaskDTO.getExperimentAddress());
            tcExperimentTb.setApplyUserName(bioTaskDtlTb.getApplyUserName());
            tcExperimentTb.setApplyUserId(bioTaskDtlTb.getApplyUserId());
            tcExperimentTb.setCreateTime(new Date());
            tcExperimentTb.setExperimentNum(bioTaskDtlTb.getTaskNum());
            tcExperimentTb.setTaskNum(bioTaskDtlTb.getTaskNum());
            tcExperimentTbMapper.insert(tcExperimentTb);


            TcExperimentDesignTb tcExperimentDesignTb=new TcExperimentDesignTb();
            tcExperimentDesignTbMapper.insert(tcExperimentDesignTb);


        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }
}
