package com.bio.drqi.ai.common.enums;

/**
 * 语义分类。
 */
public enum AiSemanticCategoryEnum {

    /**
     * 问候语。
     */
    GREETING("GREETING", "问候语"),

    /**
     * 告别语。
     */
    GOODBYE("GOODBYE", "告别语"),

    /**
     * 感谢语。
     */
    THANKS("THANKS", "感谢语"),

    /**
     * 使用帮助。
     */
    HELP("HELP", "使用帮助"),

    /**
     * 普通闲聊。
     */
    CHITCHAT("CHITCHAT", "普通闲聊"),

    /**
     * 业务问题。
     */
    BUSINESS("BUSINESS", "业务问题"),

    /**
     * 知识库问答。
     */
    KNOWLEDGE_QUERY("KNOWLEDGE_QUERY", "知识库问答"),

    /**
     * 文件分析请求。
     */
    FILE_ANALYSIS("FILE_ANALYSIS", "文件分析"),

    /**
     * 确认或同意。
     */
    CONFIRMATION("CONFIRMATION", "确认"),

    /**
     * 否定或拒绝。
     */
    REJECTION("REJECTION", "否定"),

    /**
     * 纠正上一轮信息。
     */
    CORRECTION("CORRECTION", "纠错"),

    /**
     * 追问或承接上一轮上下文。
     */
    FOLLOW_UP("FOLLOW_UP", "追问"),

    /**
     * 澄清问题。
     */
    CLARIFICATION("CLARIFICATION", "澄清"),

    /**
     * 敏感或高风险请求。
     */
    SENSITIVE("SENSITIVE", "敏感请求"),

    /**
     * 未知分类。
     */
    UNKNOWN("UNKNOWN", "未知分类");

    private final String code;

    private final String desc;

    AiSemanticCategoryEnum(String code, String desc) {
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
