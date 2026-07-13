package com.bio.drqi.ai.common.enums;

/**
 * 文档分块类型。
 */
public enum AiDocumentChunkTypeEnum {

    /**
     * 普通文本。
     */
    TEXT("TEXT", "普通文本"),

    /**
     * 表格内容。
     */
    TABLE("TABLE", "表格内容"),

    /**
     * 同一分块内同时包含文本和表格。
     */
    MIXED("MIXED", "混合内容");

    private final String code;

    private final String desc;

    AiDocumentChunkTypeEnum(String code, String desc) {
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
