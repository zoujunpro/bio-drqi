package com.bio.drqi.ai.dto.workflow;

import com.bio.drqi.ai.dto.rsp.AiAnalysisRspDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AiWorkflowExecuteRspDTO {

    private Long executionId;

    private String executionNo;

    private String status;

    private Integer costMs;

    private AiAnalysisRspDTO result;

    private List<AiWorkflowStepDTO> steps = new ArrayList<AiWorkflowStepDTO>();
}
