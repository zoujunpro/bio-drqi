package com.bio.drqi.ai.common.enums;

/**
 * 业务意图处理类型。
 */
public enum AiIntentHandlerTypeEnum {

    /**
     * 通过工具或企业接口处理。
     */
    TOOL("TOOL", "工具调用"),

    /**
     * 通过企业知识库处理。
     */
    RAG("RAG", "知识库问答"),

    /**
     * 通过聊天附件处理。
     */
    FILE("FILE", "文件分析"),

    /**
     * 普通对话。
     */
    CHAT("CHAT", "普通对话"),

    /**
     * 复杂编排流程。
     */
    WORKFLOW("WORKFLOW", "工作流");

    private final String code;

    private final String desc;

    AiIntentHandlerTypeEnum(String code, String desc) {
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
