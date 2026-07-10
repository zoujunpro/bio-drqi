package com.bio.drqi.ai.common.enums;

/**
 * AI 工具选择状态。
 */
public enum AiToolSelectionStatusEnum {

    /**
     * 已选择工具。
     */
    SELECTED("SELECTED", "已选择"),

    /**
     * 当前任务不需要外部工具。
     */
    SKIPPED("SKIPPED", "已跳过"),

    /**
     * 没有找到可用工具。
     */
    NO_TOOL("NO_TOOL", "无可用工具"),

    /**
     * 工具被规则拒绝。
     */
    REJECTED("REJECTED", "已拒绝");

    private final String code;

    private final String desc;

    AiToolSelectionStatusEnum(String code, String desc) {
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
