package com.bio.flow.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.BioTaskConf;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.CerProjectTb;
import com.bio.drqi.mapper.BioTaskConfMapper;
import com.bio.drqi.mapper.BioTaskDtlTbMapper;
import com.bio.drqi.mapper.CerProjectTbMapper;
import com.bio.flow.dto.ApproveDetailRspDTO;
import com.bio.flow.dto.ProcessDetailReqDTO;
import com.bio.flow.dto.ProcessDetailRspDTO;
import com.easyflow.engine.FlowEngineService;
import com.easyflow.engine.core.FlowActor;
import com.easyflow.engine.entity.*;
import com.easyflow.engine.enums.TaskState;
import com.easyflow.engine.model.NodeModel;
import com.easyflow.engine.model.SelfFlowActor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FlowServiceImpl implements FlowService {

    private static final List<String> conditionType = Arrays.asList("seed_out_apply", "implementation_plan", "sample_and_test", "project_create", "bms_purchase_apply", "bms_product_out", "bms_product_input", "tc_sample_test_task_apply","tc_experiment_task_apply");


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
    public FlowHisInstanceTb back(String userName, Integer userId, Long instanceId, Map<String, Object> args, String remarks) {
        FlowActor flowActor = FlowActor.of(tenantId, String.valueOf(userId), userName);
        flowEngineService.executeBack(instanceId, flowActor, args, remarks);
        FlowHisInstanceTb flowHisInstanceTb = flowEngineService.getQueryService().getHistInstance(instanceId);
        return flowHisInstanceTb;
    }

    @Override
    public FlowHisInstanceTb revoke(String userName, Integer userId, Long instanceId, String remarks) {
        List<FlowHisCommitTb> flowHisCommitTbList = flowEngineService.getQueryService().getFlowCommitTbByInstanceId(instanceId);
        if (CollectionUtil.isNotEmpty(flowHisCommitTbList) && flowHisCommitTbList.stream().map(FlowEntity::getCreateId).distinct().count() > 1) {
            throw new BusinessException("已经执行且被其他人员审批，无法撤销");
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
        if (bioTaskConf == null) {
            throw new BusinessException("工作流已经变更，请刷新流程");
        }
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

    @Override
    public Map<Long, String> queryListFlowTaskByInstanceIds(List<Long> instanceIdList) {
        Map<Long, String> map = new HashMap<>();
        if (CollectionUtil.isNotEmpty(instanceIdList)) {
            List<FlowTaskTb> flowTaskTbList = flowEngineService.getQueryService().getActiveTaskByInstanceIds(instanceIdList);
            for (FlowTaskTb flowTaskTb : flowTaskTbList) {
                if(!map.containsKey(flowTaskTb.getInstanceId())){
                    map.put(flowTaskTb.getInstanceId(),String.valueOf(flowTaskTb.getTaskType()));
                }

            }
        }
        return map;
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
        result.setInstanceId(String.valueOf(flowHisInstanceTb.getId()));
        result.setStatus(flowHisInstanceTb.getInstanceState());

        Map<Long, List<FlowHisCommitTb>> flowHisCommitTbMapList = flowEngineService.getQueryService().getFlowCommitTbByInstanceId(Long.parseLong(instanceId)).stream().collect(Collectors.groupingBy(FlowHisCommitTb::getTaskId));
        List<NodeModel> nodeModelList = flowEngineService.findInstanceNodeModelList(Long.valueOf(instanceId), getArgs(bioTaskDtlTb.getTaskForm(), bioTaskDtlTb.getTaskTypeCode()));
        Map<String, NodeModel> nodeIdNodeModelMap = nodeModelList.stream().collect(Collectors.toMap(NodeModel::getNodeId, nodeModel -> nodeModel));
        List<FlowHisTaskTb> flowHisTaskTbList = flowEngineService.getQueryService().getHisTaskByInstanceId(Long.valueOf(instanceId)).stream().sorted(Comparator.comparingLong(FlowHisTaskTb::getId)).collect(Collectors.toList());
        for (FlowHisTaskTb flowHisTaskTb : flowHisTaskTbList) {
            List<ApproveDetailRspDTO.NodeUser> nodeUserList = findNoteUser(flowHisCommitTbMapList, flowHisTaskTb.getId());
            NodeModel nodeModel = nodeIdNodeModelMap.get(flowHisTaskTb.getTaskNodeId());
            ApproveDetailRspDTO.Model model = new ApproveDetailRspDTO.Model();
            model.setNodeId(nodeModel.getNodeId());
            model.setNodeName(nodeModel.getNodeName());
            model.setNodeType(nodeModel.getNodeType());
            model.setExamineMode(nodeModel.getExamineMode());
            model.setNodeStatus(flowHisTaskTb.getTaskState());
            model.setNodeUserList(nodeUserList);
            if (result.getModelList().size() == 0) {
                result.getModelList().add(model);
            } else {
                if (!flowHisTaskTb.getTaskNodeId().equals(result.getModelList().get(result.getModelList().size() - 1).getNodeId())) {
                    result.getModelList().add(model);
                } else {
                    //有执行记录代表已经执行过，先设定节点状态是执行完毕
                    result.getModelList().get(result.getModelList().size() - 1).setNodeStatus(flowHisTaskTb.getTaskState());
                    result.getModelList().get(result.getModelList().size() - 1).getNodeUserList().addAll(nodeUserList);
                }
            }


        }

        //当前正在执行节点
        List<FlowTaskTb> executeNodeList = flowEngineService.getQueryService().getTasksByInstanceId(Long.valueOf(instanceId));
        if (CollectionUtil.isNotEmpty(executeNodeList)) {
            FlowTaskTb flowTaskTb = executeNodeList.get(0);
            //判断当前执行节点是否已经被部分执行，如果不是则加入执行列表中,如果是则更改流程状态为执行中
            if (!flowTaskTb.getTaskNodeId().equals(result.getModelList().get(result.getModelList().size() - 1).getNodeId())) {
                ApproveDetailRspDTO.Model model = new ApproveDetailRspDTO.Model();
                NodeModel nodeModel = nodeIdNodeModelMap.get(flowTaskTb.getTaskNodeId());
                model.setNodeId(nodeModel.getNodeId());
                model.setNodeName(nodeModel.getNodeName());
                model.setNodeType(nodeModel.getNodeType());
                model.setExamineMode(nodeModel.getExamineMode());
                model.setNodeStatus(TaskState.active.getValue());
                result.getModelList().add(model);
            } else {
                result.getModelList().get(result.getModelList().size() - 1).setNodeStatus(TaskState.active.getValue());
            }
        }
        List<FlowTaskActorTb> flowTaskActorTbList = flowEngineService.getQueryService().getActiveTaskActorByInstanceId(Long.valueOf(instanceId));
        for (FlowTaskActorTb flowTaskActorTb : flowTaskActorTbList) {
            if (StringUtils.equals(flowTaskActorTb.getTaskNodeId(), result.getModelList().get(result.getModelList().size() - 1).getNodeId())) {
                ApproveDetailRspDTO.NodeUser nodeUser = new ApproveDetailRspDTO.NodeUser();
                nodeUser.setUserId(flowTaskActorTb.getActorId());
                nodeUser.setUsername(flowTaskActorTb.getActorName());
                result.getModelList().get(result.getModelList().size() - 1).getNodeUserList().add(nodeUser);
            }
        }
        return result;
    }

    private static List<ApproveDetailRspDTO.NodeUser> findNoteUser(Map<Long, List<FlowHisCommitTb>> flowHisCommitTbMapList, Long taskId) {
        List<ApproveDetailRspDTO.NodeUser> result = new ArrayList<>();
        List<FlowHisCommitTb> flowHisCommitTbList = flowHisCommitTbMapList.get(taskId);
        if (CollectionUtil.isNotEmpty(flowHisCommitTbList)) {
            for (FlowHisCommitTb flowHisCommitTb : flowHisCommitTbList) {
                ApproveDetailRspDTO.NodeUser nodeUser = new ApproveDetailRspDTO.NodeUser();
                nodeUser.setUserId(flowHisCommitTb.getCreateId());
                nodeUser.setUsername(flowHisCommitTb.getCreateName());
                nodeUser.setApproveResult(flowHisCommitTb.getCommitDesc());
                nodeUser.setApproveCode(flowHisCommitTb.getCommitType());
                nodeUser.setApproveTime(flowHisCommitTb.getCreateTime());
                nodeUser.setApproveRemark(flowHisCommitTb.getMessage());
                result.add(nodeUser);
            }
        }
        return result;
    }


    public ApproveDetailRspDTO approveDetailDemo(String instanceId) {
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
//        if (flowHisInstanceTb.getInstanceState() == InstanceState.revoke.getValue()) {
//            ApproveDetailRspDTO.Model model = result.getModelList().get(result.getModelList().size() - 1);
//            result.getModelList().remove(model);
//            result.getModelList().get(0).getNodeUserList().addAll(model.getNodeUserList());
//        }
        return result;
    }

    @Override
    public List<String> queryCanApplyList(Long processId) {
        return flowEngineService.findCanApplyList(processId);
    }
}
