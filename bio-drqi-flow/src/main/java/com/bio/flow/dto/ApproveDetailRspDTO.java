package com.bio.flow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ApproveDetailRspDTO {

    private String instanceId;

    private Integer status;

    private List<Model> modelList=new ArrayList<>();

    @Data
    public static class Model {
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

        private Integer nodeStatus;

        /**
         * 审批人信息
         */
        private List<NodeUser> nodeUserList = new ArrayList<>();
    }


    @Data
    public static class NodeUser {

        private String userId;

        private String username;
        /**
         *
         */
        private String approveResult;

        private Integer approveCode;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date approveTime;

        private String approveRemark;

    }


}
