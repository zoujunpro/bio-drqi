package com.bio.drqi.manage.listener;

import com.easyflow.engine.core.FlowActor;
import com.easyflow.engine.handler.SeedDuplicateCopyHandler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class DefaultDuplicateCopyHandler implements SeedDuplicateCopyHandler {

    @Resource
    private CerSeedTaskListener cerSeedTaskListener;

    @Resource
    private CerProjectTaskListener cerProjectTaskListener;

    @Override
    public void handle(List<FlowActor> flowActorList, Long instanceId) {

        cerProjectTaskListener.handle(flowActorList,instanceId);

        cerSeedTaskListener.handle(flowActorList,instanceId);
    }
}
