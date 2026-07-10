package com.bio.drqi.ai.common.enums;

/**
 * AI 工具类型。
 */
public enum AiToolTypeEnum {

    /**
     * 包装企业后端 API。
     */
    API("API", "企业接口"),

    /**
     * 包装工作流。
     */
    WORKFLOW("WORKFLOW", "工作流"),

    /**
     * Dify 内部工具。
     */
    DIFY("DIFY", "Dify工具"),

    /**
     * MCP 工具。
     */
    MCP("MCP", "MCP工具"),

    /**
     * 本地 Java 工具。
     */
    LOCAL("LOCAL", "本地工具");

    private final String code;

    private final String desc;

    AiToolTypeEnum(String code, String desc) {
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
