package com.bio.drqi.es.enums;

/**
 * ES 索引整体状态。
 */
public enum EsIndexStatusEnum {

    READY("READY", "正常"),

    BUILDING("BUILDING", "构建中"),

    WARNING("WARNING", "有差异"),

    FAILED("FAILED", "失败"),

    DISABLED("DISABLED", "停用");

    private final String code;

    private final String desc;

    EsIndexStatusEnum(String code, String desc) {
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
