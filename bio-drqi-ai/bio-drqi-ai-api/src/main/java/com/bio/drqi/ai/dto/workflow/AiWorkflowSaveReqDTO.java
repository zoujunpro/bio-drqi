package com.bio.drqi.ai.dto.workflow;

import lombok.Data;

@Data
public class AiWorkflowSaveReqDTO {

    private Long id;

    private String workflowCode;

    private String workflowName;

    private String description;

    private String category;

    private String dslJson;

    private Integer enabled;
}
