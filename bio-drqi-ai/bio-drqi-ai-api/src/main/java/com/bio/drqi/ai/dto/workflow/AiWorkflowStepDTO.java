package com.bio.drqi.ai.dto.workflow;

import lombok.Data;

@Data
public class AiWorkflowStepDTO {

    private String nodeId;

    private String nodeType;

    private String nodeName;

    private String toolCode;

    private String status;

    private Integer costMs;

    private String summary;
}
