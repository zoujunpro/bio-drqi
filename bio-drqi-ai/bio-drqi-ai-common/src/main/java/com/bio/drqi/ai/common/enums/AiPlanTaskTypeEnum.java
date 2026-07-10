package com.bio.drqi.ai.common.enums;

/**
 * AI Planner 任务类型。
 */
public enum AiPlanTaskTypeEnum {

    /**
     * 查询类任务，通常会调用业务查询工具。
     */
    QUERY("QUERY", "查询任务"),

    /**
     * 分析类任务，通常依赖前置查询结果做总结、风险判断或解释。
     */
    ANALYSIS("ANALYSIS", "分析任务"),

    /**
     * 合并类任务，用于聚合多个前置任务结果。
     */
    MERGE("MERGE", "合并任务"),

    /**
     * 直接工具任务。
     */
    TOOL("TOOL", "工具任务"),

    /**
     * Dify 或 LLM 任务。
     */
    DIFY("DIFY", "Dify或LLM任务"),

    /**
     * 澄清任务，表示当前信息不足，需要继续追问用户。
     */
    CLARIFY("CLARIFY", "澄清任务");

    private final String code;

    private final String desc;

    AiPlanTaskTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
