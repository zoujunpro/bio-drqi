package com.bio.drqi.ai.dto.planner;

import lombok.Data;

import java.io.Serializable;

/**
 * AI 执行计划步骤。
 */
@Data
public class AiPlanStepDTO implements Serializable {

    /**
     * 步骤序号，从 1 开始。
     */
    private Integer stepNo;

    /**
     * 步骤类型：DIRECT_ANSWER/DIFY/TOOL/CLARIFY。
     */
    private String stepType;

    /**
     * 目标编码，例如工具编码、Dify 应用编码。
     */
    private String targetCode;

    /**
     * 工具编码。步骤类型为 TOOL 时使用。
     */
    private String toolCode;

    /**
     * 入参 JSON。第一版先保存字符串，后续可以升级为结构化对象。
     */
    private String inputJson;

    /**
     * 依赖步骤序号 JSON 数组，例如 [1,2]。为空表示当前步骤无强依赖。
     */
    private String dependsOn;

    /**
     * 输出变量名，供后续步骤引用。
     */
    private String outputKey;

    /**
     * 步骤说明。
     */
    private String description;

    private static final long serialVersionUID = 1L;
}
