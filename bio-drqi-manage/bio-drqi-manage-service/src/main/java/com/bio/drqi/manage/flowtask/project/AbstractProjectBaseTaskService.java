package com.bio.drqi.manage.flowtask.project;

import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.CerVectorStepLog;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.enums.ImplementationPlanTypeEnum;
import com.bio.drqi.mapper.CerVectorStepLogMapper;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import com.bio.flow.hander.DefaultBuildHtmlModelHandler;
import com.bio.flow.service.BaseTaskService;

import javax.annotation.Resource;
import java.util.Date;

public abstract class AbstractProjectBaseTaskService extends DefaultBuildHtmlModelHandler implements BaseTaskService {

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
        if (StringUtils.isEmpty(vectorTaskTb.getCurrentStepCode())) {
            vectorTaskTb.setCurrentStepCode(implementationPlanTypeEnum.name());
        } else {
            ImplementationPlanTypeEnum currentStep = ImplementationPlanTypeEnum.getImplementationPlanTypeEnum(vectorTaskTb.getCurrentStepCode());
            if (implementationPlanTypeEnum.order > currentStep.order) {
                vectorTaskTb.setCurrentStepCode(implementationPlanTypeEnum.name());
            }
        }
        cerVectorTaskTbMapper.updateById(vectorTaskTb);


        cerVectorStepLog = new CerVectorStepLog();
        cerVectorStepLog.setProjectId(vectorTaskTb.getProjectId());
        cerVectorStepLog.setVectorTaskId(vectorTaskId);
        cerVectorStepLog.setStepCode(implementationPlanTypeEnum.name());
        cerVectorStepLog.setCreateTime(new Date());
        cerVectorStepLog.setTaskNum(taskNum);
        cerVectorStepLogMapper.insert(cerVectorStepLog);
    }

}
