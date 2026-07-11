package com.bio.drqi.ai.dto.chat;

/**
 * AI 聊天回答类型。
 */
public enum AiChatAnswerTypeEnum {

    TEXT("TEXT", "文本"),

    TABLE("TABLE", "表格"),

    FILE("FILE", "文件"),

    MIXED("MIXED", "混合结果"),

    ERROR("ERROR", "错误"),

    CLARIFY("CLARIFY", "澄清");

    private final String code;

    private final String desc;

    AiChatAnswerTypeEnum(String code, String desc) {
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
