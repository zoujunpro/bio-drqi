package com.bio.drqi.ai.common.enums;

/**
 * AI 会话状态。
 */
public enum AiSessionStatusEnum {

    /**
     * 有效会话。
     */
    ACTIVE("ACTIVE", "有效"),

    /**
     * 已关闭会话。
     */
    CLOSED("CLOSED", "已关闭");

    private final String code;

    private final String desc;

    AiSessionStatusEnum(String code, String desc) {
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
