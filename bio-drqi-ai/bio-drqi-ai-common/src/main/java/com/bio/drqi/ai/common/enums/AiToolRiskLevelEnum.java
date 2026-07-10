package com.bio.drqi.ai.common.enums;

/**
 * AI 工具风险等级。
 */
public enum AiToolRiskLevelEnum {

    /**
     * 低风险，通常是只读查询。
     */
    LOW("LOW", "低风险"),

    /**
     * 中风险，可能涉及敏感数据或较大范围查询。
     */
    MEDIUM("MEDIUM", "中风险"),

    /**
     * 高风险，通常涉及写操作、删除、审批或敏感操作。
     */
    HIGH("HIGH", "高风险");

    private final String code;

    private final String desc;

    AiToolRiskLevelEnum(String code, String desc) {
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
