package com.bio.flow.hander;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.mapper.BioTaskDtlTbMapper;
import com.easyflow.engine.core.FlowActor;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

public abstract class DefaultDuplicateCopyHandler {


    public abstract void doHandle(List<FlowActor> flowActorList, Long instanceId,String businessId);


}
