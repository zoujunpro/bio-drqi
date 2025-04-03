package com.bio.flow.configuration;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.SystemDeptTb;
import com.bio.drqi.domain.SystemUserRoleRef;
import com.bio.drqi.domain.SystemUserTb;
import com.bio.drqi.mapper.SystemDeptTbMapper;
import com.bio.drqi.mapper.SystemUserRoleRefMapper;
import com.bio.drqi.mapper.SystemUserTbMapper;
import com.easyflow.engine.core.FlowActor;
import com.easyflow.engine.enums.NodeType;
import com.easyflow.engine.enums.SetApprove;
import com.easyflow.engine.enums.UseScope;
import com.easyflow.engine.handler.TaskActorProvider;
import com.easyflow.engine.model.NodeActor;
import com.easyflow.engine.model.NodeModel;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class EasyFlowConfiguration {


    private static final String tenantId = "1000";

    @Resource
    private SystemUserTbMapper systemUserTbMapper;

    @Resource
    private SystemUserRoleRefMapper systemUserRoleRefMapper;

    @Resource
    private SystemDeptTbMapper systemDeptTbMapper;

    @Bean
    public TaskActorProvider getTaskActorProvider() {
        return new TaskActorProvider() {
            @Override
            public List<FlowActor> getNodeActorUserList(NodeModel nodeModel, String applyUserId, NodeActor applyAdmin) {
                if (NodeType.start.getValue() == nodeModel.getNodeType()) {
                    /**
                     * 启动节点
                     */
                    if (nodeModel.getUseScope() == UseScope.people.getValue()) {
                        /**
                         * 指定人
                         */
                        List<SystemUserTb> systemUserTbList = systemUserTbMapper.selectBatchIds(nodeModel.getNodeActorList().stream().map(nodeActor -> Integer.valueOf(nodeActor.getId())).collect(Collectors.toList()));
                        return transSystemUserTbToFlowActor(systemUserTbList, null, null);
                    } else if (nodeModel.getUseScope() == UseScope.role.getValue()) {
                        /**
                         * 指定角色
                         */
                        return getFlowActorsByRole(nodeModel, applyAdmin);
                    } else if (nodeModel.getUseScope() == UseScope.dept.getValue()) {

                        /**
                         * 指定部门
                         */
                        List<SystemUserTb> systemUserTbList = systemUserTbMapper.selectAllByDeptIdIn(findAllDept(Integer.valueOf(nodeModel.getNodeActorList().get(0).getId())));
                        return transSystemUserTbToFlowActor(systemUserTbList, null, null);

                    } else if (nodeModel.getUseScope() == UseScope.all.getValue()) {
                        List<SystemUserTb> systemUserTbList = systemUserTbMapper.selectList(null);
                        return transSystemUserTbToFlowActor(systemUserTbList, null, null);
                    }
                } else if (NodeType.deal.getValue() == nodeModel.getNodeType()
                        || NodeType.approve.getValue() == nodeModel.getNodeType()) {
                    /**
                     * 审批或者执行节点
                     */
                    if (SetApprove.owner.getValue() == nodeModel.getSetApprove()) {
                        /**
                         * 发起人自己
                         */
                        SystemUserTb systemUserTb = systemUserTbMapper.selectById(Integer.valueOf(applyUserId));
                        return transSystemUserTbToFlowActor(Arrays.asList(systemUserTb), nodeModel, applyAdmin);
                    } else if (SetApprove.optional.getValue() == nodeModel.getSetApprove()) {
                        /**
                         *发起人自选
                         */
                        List<Integer> idList = nodeModel.getNodeActorList().stream().map(nodeActor -> Integer.valueOf(nodeActor.getId())).collect(Collectors.toList());
                        if (CollectionUtil.isNotEmpty(idList)) {
                            List<SystemUserTb> systemUserTbList = systemUserTbMapper.selectBatchIds(idList);
                            return transSystemUserTbToFlowActor(systemUserTbList, nodeModel, applyAdmin);
                        } else {
                            return new ArrayList<>();
                        }
                    } else if (SetApprove.role.getValue() == nodeModel.getSetApprove()) {
                        /**
                         * 指定角色
                         */
                        return getFlowActorsByRole(nodeModel, applyAdmin);
                    } else if (SetApprove.members.getValue() == nodeModel.getSetApprove()) {
                        /**
                         * 指定成员
                         */
                        List<SystemUserTb> systemUserTbList = new ArrayList<>();
                        for (NodeActor nodeActor : nodeModel.getNodeActorList()) {
                            SystemUserTb systemUserTb = systemUserTbMapper.selectById(Integer.valueOf(nodeActor.getId()));
                            systemUserTbList.add(systemUserTb);
                        }
                        return transSystemUserTbToFlowActor(systemUserTbList, nodeModel, applyAdmin);
                    } else if (SetApprove.superior.getValue() == nodeModel.getSetApprove()) {
                        /**
                         * 上级
                         */
                        SystemUserTb systemUserTb = systemUserTbMapper.selectById(Integer.valueOf(applyUserId));
                        if (systemUserTb == null || systemUserTb.getSuperiorId() == null) {
                            return new ArrayList<>();
                        }
                        systemUserTb = systemUserTbMapper.selectById(systemUserTb.getSuperiorId());
                        return transSystemUserTbToFlowActor(Arrays.asList(systemUserTb), nodeModel, applyAdmin);
                    } else if (SetApprove.continuous_superior.getValue() == nodeModel.getSetApprove()) {
                        /**
                         * 连续上级
                         */
                    } else if (SetApprove.manager.getValue() == nodeModel.getSetApprove()) {
                        /**
                         * 部门负责人
                         */
                        SystemUserTb systemUserTb = systemUserTbMapper.selectById(Integer.valueOf(applyUserId));
                        if (systemUserTb == null || systemUserTb.getDeptId() == null) {
                            return new ArrayList<>();
                        }
                        SystemDeptTb systemDeptTb = systemDeptTbMapper.selectById(systemUserTb.getDeptId());

                        SystemUserTb leaderSystemUserTb = systemUserTbMapper.selectDeptLeaderByDeptName(systemDeptTb.getDeptName());
                        return transSystemUserTbToFlowActor(Arrays.asList(leaderSystemUserTb), nodeModel, applyAdmin);
                    }
                } else if (NodeType.copy.getValue() == nodeModel.getNodeType()) {
                    if (CollectionUtil.isNotEmpty(nodeModel.getNodeActorList())) {
                        List<SystemUserTb> systemUserTbList = systemUserTbMapper.selectBatchIds(nodeModel.getNodeActorList().stream().map(nodeActor -> Integer.valueOf(nodeActor.getId())).collect(Collectors.toList()));
                        return transSystemUserTbToFlowActor(systemUserTbList, nodeModel, applyAdmin);
                    }
                }
                return new ArrayList<>();
            }

            private List<FlowActor> getFlowActorsByRole(NodeModel nodeModel, NodeActor applyAdmin) {
                List<SystemUserRoleRef> systemUserRoleRefList = systemUserRoleRefMapper.selectAllByRoleIdIn(nodeModel.getNodeActorList().stream().map(nodeActor -> Integer.valueOf(nodeActor.getId())).collect(Collectors.toList()));
                if (CollectionUtil.isEmpty(systemUserRoleRefList)) {
                    return transSystemUserTbToFlowActor(new ArrayList<>(), nodeModel, applyAdmin);
                }
                List<SystemUserTb> systemUserTbList = systemUserTbMapper.selectBatchIds(systemUserRoleRefList.stream().map(SystemUserRoleRef::getUserId).collect(Collectors.toList()));
                return transSystemUserTbToFlowActor(systemUserTbList, nodeModel, applyAdmin);
            }

            @Override
            public boolean isAllowed(NodeModel nodeModel, FlowActor flowActor) {
                if (NodeType.start.getValue() == nodeModel.getNodeType()) {
                    if (nodeModel.getUseScope() == UseScope.all.getValue()) {
                        return true;
                    } else if (nodeModel.getUseScope() == UseScope.no.getValue()) {
                        return false;
                    } else {
                        return isAllowed(getNodeActorUserList(nodeModel, flowActor.getCreateId(), null), flowActor);
                    }
                } else if (NodeType.deal.getValue() == nodeModel.getNodeType()
                        || NodeType.approve.getValue() == nodeModel.getNodeType()) {
                }
                return false;
            }

            private List<FlowActor> transSystemUserTbToFlowActor(List<SystemUserTb> systemUserTbList, NodeModel nodeModel, NodeActor applyAdmin) {
                List<FlowActor> flowActorList = new ArrayList<>();
                for (SystemUserTb systemUserTb : systemUserTbList) {
                    FlowActor flowActor = new FlowActor();
                    flowActor.setTenantId(tenantId);
                    flowActor.setCreateId(systemUserTb.getId().toString());
                    flowActor.setCreateName(systemUserTb.getNickname());
                    flowActor.setUrl(systemUserTb.getPortraitUrl());
                    flowActorList.add(flowActor);
                }
                if (CollectionUtil.isEmpty(flowActorList)) {
                    if (nodeModel.getEmptyApprovalDealType() == 2) {
                        flowActorList.add(FlowActor.of(tenantId, nodeModel.getEmptyApprovalActor()));
                    } else if (nodeModel.getEmptyApprovalDealType() == 3) {
                        flowActorList.add(FlowActor.of(tenantId, applyAdmin));
                    }
                }
                return flowActorList;
            }

            private boolean isAllowed(List<FlowActor> flowActorList, FlowActor currentFlowActor) {
                if (CollectionUtil.isEmpty(flowActorList)) {
                    return false;
                }
                for (FlowActor flowActor : flowActorList) {
                    if (StringUtils.equals(flowActor.getCreateId(), currentFlowActor.getCreateId())) {
                        return true;
                    }
                }
                return false;
            }
        };
    }


    private List<Integer> findAllDept(Integer id) {
        List<Integer> result = new ArrayList<>();
        SystemDeptTb systemDeptTb = systemDeptTbMapper.selectById(id);
        result.add(systemDeptTb.getId());
        findAllDeptChildren(systemDeptTb.getId(), result);
        return result;
    }

    private void findAllDeptChildren(Integer parentId, List<Integer> result) {
        List<SystemDeptTb> systemDeptTbList = systemDeptTbMapper.selectAllByParentId(parentId);
        if (CollectionUtil.isNotEmpty(systemDeptTbList)) {
            systemDeptTbList.forEach(deptTb -> {
                result.add(deptTb.getId());
                findAllDeptChildren(deptTb.getId(), result);
            });
        }

    }

    @Data
    public static class Dept {

        private Integer id;

        /**
         * 部门名称
         */
        private String deptName;

        /**
         * 部门编码
         */
        private String deptCode;


        private Integer parentId;

        /**
         * 子部门
         */
        private List<Dept> children = new ArrayList<>();

        /**
         * 创建时间
         */
        private Date createTime;

        /**
         * 更新时间
         */
        private Date updateTime;

        /**
         * 状态 Y启用 N禁用
         */
        private String status;

        public Dept(SystemDeptTb systemDeptTb) {
            this.id = systemDeptTb.getId();
            this.deptName = systemDeptTb.getDeptName();
            this.parentId = systemDeptTb.getParentId();
            this.createTime = systemDeptTb.getCreateTime();
            this.updateTime = systemDeptTb.getUpdateTime();
            this.status = systemDeptTb.getStatus();
        }
    }
}
