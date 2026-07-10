package com.bio.drqi.ai.common.enums;

/**
 * AI 消息来源。
 */
public enum AiMessageSourceEnum {

    /**
     * 普通用户和 AI 对话。
     */
    CONVERSATION("conversation", "普通会话"),

    /**
     * Dify 返回或回调消息。
     */
    DIFY("dify", "Dify"),

    /**
     * 工具调用消息。
     */
    TOOL("tool", "工具"),

    /**
     * 系统消息。
     */
    SYSTEM("system", "系统"),

    /**
     * 记忆生成、摘要或提取消息。
     */
    MEMORY("memory", "记忆");

    private final String code;

    private final String desc;

    AiMessageSourceEnum(String code, String desc) {
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
