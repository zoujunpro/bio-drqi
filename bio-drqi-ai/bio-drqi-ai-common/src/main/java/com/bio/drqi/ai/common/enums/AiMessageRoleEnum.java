package com.bio.drqi.ai.common.enums;

/**
 * AI 消息角色。
 */
public enum  AiMessageRoleEnum {

    USER("user", "用户"),

    ASSISTANT("assistant", "AI助手"),

    TOOL("tool", "工具"),

    SYSTEM("system", "系统");

    private final String code;

    private final String desc;

    AiMessageRoleEnum(String code, String desc) {
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
