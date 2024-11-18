package com.bio.drqi.manage.freemarker.dto;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.bio.cer.flow.ApproveDetailRspDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class HtmlGenerateDTO<T> {
    private String printTime;
    private String printUser;
    private String taskDesc;
    private String taskNum;
    private String taskType;
    private String applyName;
    private String applyDate;
    private String deptName;
    private String approveResult;
    private T contentData;

    private List<Node> nodeList = new ArrayList<>();


    @Data
    public static class Node {
        private String nodeName;
        private List<NodeContent> nodeContentList = new ArrayList<>();
    }

    @Data
    public static class NodeContent {
        private String actorUserName;
        private String actorTime;
        private String eventType;
    }

    public HtmlGenerateDTO buildNodeList(ApproveDetailRspDTO approveDetailRspDTO) {
        List<ApproveDetailRspDTO.Model> modelList = approveDetailRspDTO.getModelList();
        for (ApproveDetailRspDTO.Model model : modelList) {
            Node node = new Node();
            node.setNodeName(model.getNodeName());
            for (ApproveDetailRspDTO.NodeUser nodeUser : model.getNodeUserList()) {
                NodeContent nodeContent = new NodeContent();
                nodeContent.setActorUserName(nodeUser.getUsername());
                nodeContent.setActorTime(DateUtil.format(nodeUser.getApproveTime(), DatePattern.NORM_DATETIME_PATTERN));
                nodeContent.setEventType(nodeUser.getApproveResult());
                node.getNodeContentList().add(nodeContent);
            }
            this.getNodeList().add(node);
        }
        return this;
    }
}
