package com.bio.drqi.ai.common.enums;

/**
 * 语义配置状态。
 */
public enum AiSemanticStatusEnum {

    /**
     * 启用。
     */
    ACTIVE("ACTIVE", "启用"),

    /**
     * 停用。
     */
    DISABLED("DISABLED", "停用"),

    /**
     * 删除。
     */
    DELETED("DELETED", "删除");

    private final String code;

    private final String desc;

    AiSemanticStatusEnum(String code, String desc) {
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
