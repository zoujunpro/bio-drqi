package com.bio.drqi.ai.dto.workflow;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class AiWorkflowExecuteReqDTO {

    private Long workflowId;

    private String workflowCode;

    private Map<String, Object> input = new LinkedHashMap<String, Object>();
}
