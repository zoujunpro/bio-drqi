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

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    public abstract void doHandle(List<FlowActor> flowActorList, Long instanceId);

    public BioTaskDtlTb findBioTaskDtlTb(Long instanceId) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByInstanceId(instanceId);
        if (bioTaskDtlTb == null) {
            //发起直接执行完毕，此时未回填数据，需要找出当前人发起的最新的任务
            List<BioTaskDtlTb> bioTaskDtlTbList = bioTaskDtlTbMapper.selectAllByApplyUserIdAndInstanceIdIsNull(SecurityContextHolder.getUserId());
            if (CollectionUtil.isNotEmpty(bioTaskDtlTbList) && bioTaskDtlTbList.size() == 1) {
                bioTaskDtlTb = bioTaskDtlTbList.get(0);
            }
        }
        return bioTaskDtlTb;
    }
}
