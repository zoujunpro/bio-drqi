package com.bio.drqi.step.rsp;

import lombok.Data;

@Data
public class QueryStepRspDTO {
    /**执行节点编码*/
    private String flowStepCode;

    /**执行节点状态  0未开始 1执行中，2审批中，3已执行*/
    private String stepStatus;

    /**拒绝原因*/
    private String reason;

    private String flowStepName;
}
