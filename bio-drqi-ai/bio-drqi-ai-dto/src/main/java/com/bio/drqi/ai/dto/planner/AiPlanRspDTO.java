package com.bio.drqi.ai.dto.planner;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * AI Planner 响应。
 */
@Data
public class AiPlanRspDTO implements Serializable {

    /**
     * 计划类型：DIRECT_ANSWER/DIFY/TOOL/MIXED/CLARIFY/UNKNOWN。
     */
    private String planType;

    /**
     * 是否可以直接执行。
     */
    private Boolean executable;

    /**
     * 不可执行或需要澄清时的原因。
     */
    private String reason;

    /**
     * 澄清问题。
     */
    private String clarifyQuestion;

    /**
     * 执行步骤。
     */
    private List<AiPlanStepDTO> steps = new ArrayList<AiPlanStepDTO>();

    private static final long serialVersionUID = 1L;
}
