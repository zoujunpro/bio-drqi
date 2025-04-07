package com.bio.flow.hander;

import com.easyflow.engine.core.FlowActor;

import java.util.List;
import java.util.Map;

public abstract class DefaultDuplicateCopyHandler {
    public abstract void doHandle(List<FlowActor> flowActorList, Long instanceId);
}
