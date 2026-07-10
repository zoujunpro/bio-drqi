package com.bio.drqi.ai.common.enums;

/**
 * AI 长期记忆状态。
 */
public enum AiMemoryStatusEnum {

    /**
     * 有效记忆。
     */
    ACTIVE("ACTIVE", "有效"),

    /**
     * 已删除记忆。
     */
    DELETED("DELETED", "已删除"),

    /**
     * 已过期记忆。
     */
    EXPIRED("EXPIRED", "已过期");

    private final String code;

    private final String desc;

    AiMemoryStatusEnum(String code, String desc) {
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
