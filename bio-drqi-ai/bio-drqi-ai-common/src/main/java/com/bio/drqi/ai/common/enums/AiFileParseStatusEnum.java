package com.bio.drqi.ai.common.enums;

/**
 * AI 会话文件解析状态。
 */
public enum AiFileParseStatusEnum {

    WAITING("WAITING", "待解析"),

    PARSING("PARSING", "解析中"),

    SUCCESS("SUCCESS", "解析成功"),

    FAILED("FAILED", "解析失败"),

    UNSUPPORTED("UNSUPPORTED", "不支持");

    private final String code;

    private final String desc;

    AiFileParseStatusEnum(String code, String desc) {
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
