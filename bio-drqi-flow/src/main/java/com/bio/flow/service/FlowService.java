package com.bio.flow.service;

import com.bio.flow.dto.ApproveDetailRspDTO;
import com.bio.flow.dto.ProcessDetailReqDTO;
import com.bio.flow.dto.ProcessDetailRspDTO;
import com.easyflow.engine.entity.FlowHisInstanceTb;
import com.easyflow.engine.model.SelfFlowActor;

import java.util.List;
import java.util.Map;

public interface FlowService {

    FlowHisInstanceTb start(String userName, Integer userId, Long processId, Map<String, Object> args, String remarks, List<SelfFlowActor> selfFlowActorList,String instanceName,String businessKey);

    FlowHisInstanceTb execute(String userName, Integer userId, Long instanceId, Map<String, Object> args, String remarks);

    FlowHisInstanceTb reject(String userName, Integer userId, Long instanceId, Map<String, Object> args, String remarks);

    FlowHisInstanceTb back(String userName, Integer userId, Long instanceId, Map<String, Object> args, String remarks);


    FlowHisInstanceTb revoke(String userName, Integer userId, Long instanceId,  String remarks);

    /**
     * 流程视图展示
     *
     * @param instanceId
     * @return
     */
    String instanceView(String instanceId);


    List<ProcessDetailRspDTO> processDetail(ProcessDetailReqDTO processDetailReqDTO);

    /**
     * 执行中和执行完毕审批详情
     *
     * @param instanceId
     * @return
     */
    ApproveDetailRspDTO approveDetail(String instanceId);


    List<String> queryCanApplyList(Long processId);

    Map<String, Object> getArgs(String str,String taskTypeCode);


    Map<Long,String>queryListFlowTaskByInstanceIds(List<Long> instanceIdList);
}
