package com.bio.drqi.manage.service.project.impl;


import com.bio.drqi.timePlan.VectorTaskTimePlanAddReqDTO;
import com.bio.drqi.timePlan.VectorTaskTimePlanListRspDTO;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.domain.CerVectorTaskPlanLog;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.manage.service.project.CerImplementationTimePlanService;
import com.bio.drqi.mapper.CerVectorTaskPlanLogMapper;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class CerImplementationTimePlanServiceImpl implements CerImplementationTimePlanService {

    @Resource
    private CerVectorTaskPlanLogMapper cerVectorTaskPlanLogMapper;

    @Resource
    private CerVectorTaskTbMapper vectorTaskTbMapper;

    @Override
    public VectorTaskTimePlanListRspDTO list(String vectorTaskCode) {
        VectorTaskTimePlanListRspDTO vectorTaskTimePlanListRspDTO=new VectorTaskTimePlanListRspDTO();
        CerVectorTaskTb cerVectorTaskTb = vectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
        if(cerVectorTaskTb!=null){
            List<CerVectorTaskPlanLog> cerVectorTaskPlanLogList = cerVectorTaskPlanLogMapper.selectAllByVectorTaskIdOrderByIdAsc(cerVectorTaskTb.getId());
            List<VectorTaskTimePlanListRspDTO.Content> contentList= BeanUtils.copyToList(cerVectorTaskPlanLogList,VectorTaskTimePlanListRspDTO.Content.class);
            vectorTaskTimePlanListRspDTO.setContentList(contentList);
            return vectorTaskTimePlanListRspDTO.buildOverTimeFlag().countEstimatedTotalDay();
        }else {
            return new VectorTaskTimePlanListRspDTO();
        }

    }

    @Override
    public void add(VectorTaskTimePlanAddReqDTO vectorTaskTimePlanAddReqDTO) {
        CerVectorTaskTb cerVectorTaskTb = vectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskTimePlanAddReqDTO.getVectorTaskCode());
        CerVectorTaskPlanLog cerVectorTaskPlanLog=new CerVectorTaskPlanLog();
        cerVectorTaskPlanLog.setVectorTaskId(cerVectorTaskTb.getId());
        cerVectorTaskPlanLog.setEventType(vectorTaskTimePlanAddReqDTO.getEventType());
        cerVectorTaskPlanLog.setEstimatedStartTime(vectorTaskTimePlanAddReqDTO.getEstimatedStartTime());
        cerVectorTaskPlanLog.setEstimatedEndTime(vectorTaskTimePlanAddReqDTO.getEstimatedEndTime());
        cerVectorTaskPlanLog.setUserId(vectorTaskTimePlanAddReqDTO.getUserId());
        cerVectorTaskPlanLog.setUserName(vectorTaskTimePlanAddReqDTO.getUserName());
        cerVectorTaskPlanLog.setCreateTime(new Date());
        cerVectorTaskPlanLogMapper.insert(cerVectorTaskPlanLog);
    }
}
