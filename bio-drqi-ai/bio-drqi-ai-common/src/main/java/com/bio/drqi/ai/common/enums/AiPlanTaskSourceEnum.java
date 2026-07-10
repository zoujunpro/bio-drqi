package com.bio.drqi.ai.common.enums;

/**
 * AI Planner 任务来源。
 */
public enum AiPlanTaskSourceEnum {

    /**
     * 简单任务自动生成。
     */
    SINGLE("SINGLE", "简单任务"),

    /**
     * 任务模板生成。
     */
    TEMPLATE("TEMPLATE", "任务模板"),

    /**
     * LLM 或 Dify 兜底生成。
     */
    LLM("LLM", "LLM兜底"),

    /**
     * 澄清任务。
     */
    CLARIFY("CLARIFY", "澄清任务");

    private final String code;

    private final String desc;

    AiPlanTaskSourceEnum(String code, String desc) {
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
