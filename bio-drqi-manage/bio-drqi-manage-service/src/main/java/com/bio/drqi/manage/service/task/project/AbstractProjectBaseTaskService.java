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

    @Resource
    private CerVectorTaskPlanLogMapper cerVectorTaskPlanLogMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

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


    public void updateVectorTaskTimePlan(Integer vectorTaskId, ImplementationPlanTypeEnum implementationPlanTypeEnum) {
        List<CerVectorTaskPlanLog> cerVectorTaskPlanLogList = cerVectorTaskPlanLogMapper.selectAllByVectorTaskIdAndEventType(vectorTaskId, implementationPlanTypeEnum.name());
        if (CollectionUtil.isNotEmpty(cerVectorTaskPlanLogList)) {
            cerVectorTaskPlanLogList.forEach(cerVectorTaskPlanLog -> {
                cerVectorTaskPlanLog.setActualEndTime(DateUtil.format(new Date(), "yyyy-MM-dd"));
                cerVectorTaskPlanLog.setUpdateTime(new Date());
                cerVectorTaskPlanLog.setCreateTime(new Date());
                cerVectorTaskPlanLog.setActualStartTime(findStartTime(vectorTaskId, implementationPlanTypeEnum));
                cerVectorTaskPlanLogMapper.updateById(cerVectorTaskPlanLog);
            });
        }

    }

    private String findStartTime(Integer vectorTaskId, ImplementationPlanTypeEnum implementationPlanTypeEnum) {
        List<CerVectorTaskPlanLog> cerVectorTaskPlanLogList = cerVectorTaskPlanLogMapper.selectAllByVectorTaskIdOrderByIdAsc(vectorTaskId);
        for (int i = 0; i < cerVectorTaskPlanLogList.size(); i++) {
            if (implementationPlanTypeEnum.name().equals(cerVectorTaskPlanLogList.get(i).getEventType())) {
                if (i == 0) {
                    CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(vectorTaskId);
                    BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerVectorTaskTb.getTaskNum());
                    return DateUtil.format(bioTaskDtlTb.getUpdateTime(), "yyyy-MM-dd");
                }
                if (i > 0) {
                    return cerVectorTaskPlanLogList.get(i - 1).getActualEndTime();
                }

            }
        }
        return null;

    }


}
