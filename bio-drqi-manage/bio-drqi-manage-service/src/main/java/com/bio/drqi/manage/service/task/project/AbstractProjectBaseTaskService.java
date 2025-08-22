package com.bio.drqi.manage.service.task.project;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.bio.drqi.enums.ImplementationPlanTypeEnum;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.CerVectorStepLog;
import com.bio.drqi.domain.CerVectorTaskPlanLog;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.mapper.BioTaskDtlTbMapper;
import com.bio.drqi.mapper.CerVectorStepLogMapper;
import com.bio.drqi.mapper.CerVectorTaskPlanLogMapper;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import com.bio.flow.service.BaseTaskService;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

public abstract class AbstractProjectBaseTaskService implements BaseTaskService {

    @Resource
    protected CerVectorStepLogMapper cerVectorStepLogMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;


    public void logStep(Integer vectorTaskId, ImplementationPlanTypeEnum implementationPlanTypeEnum, String taskNum) {
        CerVectorStepLog cerVectorStepLog = cerVectorStepLogMapper.selectOneByVectorTaskIdAndStepCode(vectorTaskId, implementationPlanTypeEnum.name());
        if (cerVectorStepLog != null) {
            return;
        }
        CerVectorTaskTb vectorTaskTb = cerVectorTaskTbMapper.selectById(vectorTaskId);
        cerVectorStepLog = new CerVectorStepLog();
        cerVectorStepLog.setProjectId(vectorTaskTb.getProjectId());
        cerVectorStepLog.setVectorTaskId(vectorTaskId);
        cerVectorStepLog.setStepCode(implementationPlanTypeEnum.name());
        cerVectorStepLog.setCreateTime(new Date());
        cerVectorStepLog.setTaskNum(taskNum);
        cerVectorStepLogMapper.insert(cerVectorStepLog);
    }

}
