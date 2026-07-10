package com.bio.drqi.ai.dto.planner;

import lombok.Data;

import java.io.Serializable;

/**
 * Planner 拆解后的任务。
 */
@Data
public class AiPlanTaskDTO implements Serializable {

    /**
     * 任务序号，从 1 开始。
     */
    private Integer taskNo;

    /**
     * 任务编码，例如 QUERY_PROJECT、QUERY_SAMPLE_COUNT。
     */
    private String taskCode;

    /**
     * 任务名称。
     */
    private String taskName;

    /**
     * 任务类型，例如 QUERY、ANALYSIS、MERGE、DIFY、TOOL、CLARIFY。
     */
    private String taskType;

    /**
     * 业务领域，例如 PROJECT、CER。
     */
    private String domain;

    /**
     * 业务对象，例如 PROJECT、SAMPLE、PLANT。
     */
    private String businessObject;

    /**
     * 意图编码。
     */
    private String intentCode;

    /**
     * 任务目标，例如工具编码、Dify 应用编码、澄清目标。
     */
    private String targetCode;

    /**
     * 入参 JSON。来自模板入参映射或语义结果初步组装。
     */
    private String inputJson;

    /**
     * 必填参数 JSON 数组，例如 ["projectId"]。
     */
    private String requiredParams;

    /**
     * 依赖任务序号 JSON 数组，例如 [1,2]。
     */
    private String dependsOn;

    /**
     * 来源：SINGLE/TEMPLATE/LLM/CLARIFY。
     */
    private String source;

    /**
     * 任务描述。
     */
    private String description;

    private static final long serialVersionUID = 1L;
}
