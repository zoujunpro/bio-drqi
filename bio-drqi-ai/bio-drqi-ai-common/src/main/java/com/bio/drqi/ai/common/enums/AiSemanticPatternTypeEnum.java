package com.bio.drqi.ai.common.enums;

/**
 * 语义规则模式类型。
 */
public enum AiSemanticPatternTypeEnum {

    /**
     * 指代词，例如 它、这个、刚才那个。
     */
    REFERENCE_WORD("REFERENCE_WORD", "指代词"),

    /**
     * 系统话术分类，例如 问候、感谢、帮助。
     */
    SYSTEM_CLASSIFY("SYSTEM_CLASSIFY", "系统话术分类"),

    /**
     * 意图关键词。
     */
    INTENT_KEYWORD("INTENT_KEYWORD", "意图关键词"),

    /**
     * 实体正则。
     */
    ENTITY_REGEX("ENTITY_REGEX", "实体正则"),

    /**
     * 时间规则。
     */
    TIME_RULE("TIME_RULE", "时间规则");

    private final String code;

    private final String desc;

    AiSemanticPatternTypeEnum(String code, String desc) {
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
