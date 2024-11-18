package com.bio.cer.flow;

import lombok.Data;

import java.util.List;

@Data
public class ProcessDetailRspDTO {

    /**
     * 节点ID
     */
    private String nodeId;
    /**
     * 节点名称
     */
    private String nodeName;

    /**
     * 节点类型
     * <p>
     * 0，发起人
     * 1，审批人
     * 2，抄送人
     * 3，条件审批
     * 4，条件分支
     * 5，办理流程
     * </p>
     */
    private Integer nodeType;
    /**
     * 多人审批时审批方式
     * <p>
     * 1，按顺序依次审批
     * 2，会签 (可同时审批，每个人必须审批通过)
     * 3，或签 (有一人审批通过即可)
     * </p>
     */
    private Integer examineMode;

    /**
     * 设置审批人
     * <p>
     * 1发起人自己
     * 2发起热自选
     * 3角色
     * 4指定成员
     * 5上级
     * 6连续上级
     * 7部门负责人
     */
    private Integer setApprove;

    /**
     * 审批人信息
     */
    private List<NodeUser> nodeUserList;



    @Data
    public static class NodeUser {

        private String userId;

        private String username;

        public NodeUser(String userId, String username) {
            this.userId = userId;
            this.username = username;
        }
    }

}
