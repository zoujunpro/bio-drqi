package com.bio.drqi.ai.common.enums;

/**
 * 意图匹配方式。
 */
public enum AiIntentMatchTypeEnum {

    /**
     * 未命中任何意图。
     */
    NONE("NONE", "未匹配"),

    /**
     * 基于编码、名称、描述、样例等规则匹配。
     */
    RULE("RULE", "规则匹配"),

    /**
     * 基于关键词匹配。
     */
    KEYWORD("KEYWORD", "关键词匹配"),

    /**
     * 基于向量相似度召回。
     */
    VECTOR("VECTOR", "向量匹配"),

    /**
     * 基于大模型判断。
     */
    LLM("LLM", "模型判断");

    private final String code;

    private final String desc;

    AiIntentMatchTypeEnum(String code, String desc) {
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
