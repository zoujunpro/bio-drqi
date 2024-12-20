package com.bio.drqi.manage.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.flow.ApproveDetailRspDTO;
import com.bio.drqi.flow.ProcessDetailReqDTO;
import com.bio.drqi.flow.ProcessDetailRspDTO;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.BioTaskConf;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.CerProjectTb;
import com.bio.drqi.mapper.BioTaskConfMapper;
import com.bio.drqi.mapper.BioTaskDtlTbMapper;
import com.bio.drqi.mapper.CerProjectTbMapper;
import com.easyflow.engine.FlowEngineService;
import com.easyflow.engine.core.FlowActor;
import com.easyflow.engine.entity.*;
import com.easyflow.engine.enums.InstanceState;
import com.easyflow.engine.enums.TaskState;
import com.easyflow.engine.model.NodeModel;
import com.easyflow.engine.model.SelfFlowActor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlowServiceImpl implements FlowService {

    private static final List<String> conditionType = Arrays.asList("seed_out_apply", "implementation_plan","sample_and_test");

    private static final String tenantId = "1000";

    @Resource
    private FlowEngineService flowEngineService;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;


    @Resource
    private BioTaskConfMapper bioTaskConfMapper;


    @Override
    public FlowHisInstanceTb start(String userName, Integer userId, Long processId, Map<String, Object> args, String remarks, List<SelfFlowActor> selfFlowActorList, String instanceName) {
        FlowActor flowActor = FlowActor.of(tenantId, String.valueOf(userId), userName);
        if (processId == null) {
            throw new BusinessException("流程未配置");
        }
        Optional<FlowInstanceTb> flowInstanceTbOptional = flowEngineService.startInstanceByProcessId(processId, flowActor, args, () -> selfFlowActorList, instanceName);
        if (!flowInstanceTbOptional.isPresent()) {
            throw new BusinessException("启动流程失败");
        }
        FlowHisInstanceTb flowHisInstanceTb = flowEngineService.getQueryService().getHistInstance(flowInstanceTbOptional.get().getId());
        return flowHisInstanceTb;
    }

    @Override
    public FlowHisInstanceTb execute(String userName, Integer userId, Long instanceId, Map<String, Object> args, String remarks) {
        FlowActor flowActor = FlowActor.of(tenantId, String.valueOf(userId), userName);
        flowEngineService.executeTask(instanceId, flowActor, args, remarks);
        FlowHisInstanceTb flowHisInstanceTb = flowEngineService.getQueryService().getHistInstance(instanceId);
        return flowHisInstanceTb;
    }

    @Override
    public FlowHisInstanceTb reject(String userName, Integer userId, Long instanceId, Map<String, Object> args, String remarks) {
        FlowActor flowActor = FlowActor.of(tenantId, String.valueOf(userId), userName);
        flowEngineService.rejectTask(instanceId, flowActor, args, remarks);
        FlowHisInstanceTb flowHisInstanceTb = flowEngineService.getQueryService().getHistInstance(instanceId);
        return flowHisInstanceTb;
    }

    @Override
    public FlowHisInstanceTb revoke(String userName, Integer userId, Long instanceId, String remarks) {
        List<FlowHisCommitTb> flowHisCommitTbList = flowEngineService.getQueryService().getFlowCommitTbByInstanceId(instanceId);
        if (CollectionUtil.isNotEmpty(flowHisCommitTbList) && flowHisCommitTbList.size() > 1) {
            throw new BusinessException("已经执行的流程不能撤销");
        }
        FlowActor flowActor = FlowActor.of(tenantId, String.valueOf(userId), userName);
        flowEngineService.getRuntimeService().revoke(instanceId, flowActor, remarks);
        FlowHisInstanceTb flowHisInstanceTb = flowEngineService.getQueryService().getHistInstance(instanceId);
        return flowHisInstanceTb;
    }

    @Override
    public String instanceView(String instanceId) {
        FlowExtInstanceTb flowExtInstanceTb = flowEngineService.getQueryService().getFlowExtInstanceByInstanceId(Long.parseLong(instanceId));
        return flowExtInstanceTb.getModelContent();
    }

    @Override
    public List<ProcessDetailRspDTO> processDetail(ProcessDetailReqDTO processDetailReqDTO) {
        List<ProcessDetailRspDTO> result = new ArrayList<>();
        BioTaskConf bioTaskConf = bioTaskConfMapper.selectOneByProcessId(Long.parseLong(processDetailReqDTO.getProcessId()));
        FlowActor flowActor = FlowActor.of(tenantId, String.valueOf(SecurityContextHolder.getUserId()), SecurityContextHolder.getNickName());
        List<NodeModel> nodeModelList = flowEngineService.findProcessNodeModelList(Long.valueOf(processDetailReqDTO.getProcessId()), getArgs(processDetailReqDTO.getFormObject(), bioTaskConf.getTaskTypeCode()), flowActor);
        if (CollectionUtil.isNotEmpty(nodeModelList)) {
            for (NodeModel nodeModel : nodeModelList) {
                ProcessDetailRspDTO processDetailRspDTO = new ProcessDetailRspDTO();
                processDetailRspDTO.setNodeId(nodeModel.getNodeId());
                processDetailRspDTO.setNodeName(nodeModel.getNodeName());
                processDetailRspDTO.setNodeType(nodeModel.getNodeType());
                processDetailRspDTO.setExamineMode(nodeModel.getExamineMode());
                processDetailRspDTO.setSetApprove(nodeModel.getSetApprove());
                List<ProcessDetailRspDTO.NodeUser> nodeUserList = nodeModel.getNodeActorList().stream().map(nodeActor -> new ProcessDetailRspDTO.NodeUser(nodeActor.getId(), nodeActor.getName())).collect(Collectors.toList());
                processDetailRspDTO.setNodeUserList(nodeUserList);
                result.add(processDetailRspDTO);
            }
        }
        return result;
    }


    public Map<String, Object> getArgs(String str, String taskTypeCode) {

        //过滤大字节
        if (conditionType.contains(taskTypeCode)) {
            Map<String, Object> args = new HashMap<>();
            if (!isJSONObject(str)) {
                List<Object> list = JSONUtil.toList(str, Object.class);
                if (CollectionUtil.isEmpty(list)) {
                    return args;
                }
                args = JSONUtil.toBean(JSONUtil.toJsonStr(list.get(0)), Map.class);
                if (Objects.nonNull(args.get("projectId"))) {
                    CerProjectTb cerProjectTb = cerProjectTbMapper.selectById(Integer.valueOf(String.valueOf(args.get("projectId"))));
                    args.put("species", cerProjectTb.getSpecies());
                }
                return args;
            } else {
                args = JSONUtil.toBean(str, Map.class);
                if (Objects.nonNull(args.get("applyFrom"))) {
                    return (Map<String, Object>) args.get("applyFrom");
                }
                return args;
            }
        }
        return null;


    }

    private boolean isJSONObject(String str) {
        try {
            Object object = JSONUtil.toBean(str, Object.class);
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    @Override
    public ApproveDetailRspDTO approveDetail(String instanceId) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByInstanceId(Long.valueOf(instanceId));
        ApproveDetailRspDTO result = new ApproveDetailRspDTO();
        FlowHisInstanceTb flowHisInstanceTb = flowEngineService.getQueryService().getHistInstance(Long.valueOf(instanceId));
        List<NodeModel> nodeModelList = flowEngineService.findInstanceNodeModelList(Long.valueOf(instanceId), getArgs(bioTaskDtlTb.getTaskForm(), bioTaskDtlTb.getTaskTypeCode()));
        Map<String, List<FlowHisTaskTb>> flowHisTaskTbMap = flowEngineService.getQueryService().getHisTaskByInstanceId(Long.valueOf(instanceId)).stream().collect(Collectors.groupingBy(FlowHisTaskTb::getTaskNodeId));
        Map<String, List<FlowHisCommitTb>> flowHisCommitTbMapList = flowEngineService.getQueryService().getFlowCommitTbByInstanceId(Long.parseLong(instanceId)).stream().collect(Collectors.groupingBy(FlowHisCommitTb::getTaskNodeId));
        List<FlowTaskActorTb> flowTaskActorTbList = flowEngineService.getQueryService().getActiveTaskActorByInstanceId(Long.valueOf(instanceId));
        result.setInstanceId(String.valueOf(flowHisInstanceTb.getId()));
        result.setStatus(flowHisInstanceTb.getInstanceState());
        if (CollectionUtil.isNotEmpty(nodeModelList)) {
            for (NodeModel nodeModel : nodeModelList) {
                ApproveDetailRspDTO.Model model = new ApproveDetailRspDTO.Model();
                model.setNodeId(nodeModel.getNodeId());
                model.setNodeName(nodeModel.getNodeName());
                model.setNodeType(nodeModel.getNodeType());
                model.setExamineMode(nodeModel.getExamineMode());

                List<FlowHisCommitTb> flowHisCommitTbList = flowHisCommitTbMapList.get(nodeModel.getNodeId());
                if (CollectionUtil.isNotEmpty(flowHisCommitTbList)) {
                    for (FlowHisCommitTb flowHisCommitTb : flowHisCommitTbList) {
                        ApproveDetailRspDTO.NodeUser nodeUser = new ApproveDetailRspDTO.NodeUser();
                        nodeUser.setUserId(flowHisCommitTb.getCreateId());
                        nodeUser.setUsername(flowHisCommitTb.getCreateName());
                        nodeUser.setApproveResult(flowHisCommitTb.getCommitDesc());
                        nodeUser.setApproveCode(flowHisCommitTb.getCommitType());
                        nodeUser.setApproveTime(flowHisCommitTb.getCreateTime());
                        nodeUser.setApproveRemark(flowHisCommitTb.getMessage());
                        model.getNodeUserList().add(nodeUser);
                        //有执行记录代表已经执行过，先设定节点状态是执行完毕
                        if (model.getNodeStatus() == null) {
                            List<FlowHisTaskTb> flowHisTaskTbList = flowHisTaskTbMap.get(nodeModel.getNodeId());
                            FlowHisTaskTb flowHisTaskTb = flowHisTaskTbList.stream().max(Comparator.comparing(FlowHisTaskTb::getTaskState)).get();
                            model.setNodeStatus(flowHisTaskTb.getTaskState());
                        }
                    }
                }

                for (FlowTaskActorTb flowTaskActorTb : flowTaskActorTbList) {
                    if (StringUtils.equals(flowTaskActorTb.getTaskNodeId(), nodeModel.getNodeId())) {
                        ApproveDetailRspDTO.NodeUser nodeUser = new ApproveDetailRspDTO.NodeUser();
                        nodeUser.setUserId(flowTaskActorTb.getActorId());
                        nodeUser.setUsername(flowTaskActorTb.getActorName());
                        model.getNodeUserList().add(nodeUser);

                        //运行任务表中有任务，代表这个节点是执行中
                        model.setNodeStatus(TaskState.active.getValue());
                    }
                }
                if (CollectionUtil.isNotEmpty(model.getNodeUserList())) {
                    result.getModelList().add(model);
                }

            }
        }
        if (flowHisInstanceTb.getInstanceState() == InstanceState.revoke.getValue()) {
            ApproveDetailRspDTO.Model model = result.getModelList().get(result.getModelList().size() - 1);
            result.getModelList().remove(model);
            result.getModelList().get(0).getNodeUserList().addAll(model.getNodeUserList());
        }
        return result;
    }

    @Override
    public List<String> queryCanApplyList(Long processId) {
        return flowEngineService.findCanApplyList(processId);
    }
}
