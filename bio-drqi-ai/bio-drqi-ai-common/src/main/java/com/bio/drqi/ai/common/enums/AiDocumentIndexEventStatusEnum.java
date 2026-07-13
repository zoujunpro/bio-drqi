package com.bio.drqi.ai.common.enums;

/**
 * AI 文档索引事件状态。
 * 对应 MySQL 表 ai_document_index_event.status，用于追踪文档推送、删除、权限刷新是否成功。
 */
public enum AiDocumentIndexEventStatusEnum {

    /**
     * 处理中。
     */
    PROCESSING("PROCESSING", "处理中"),

    /**
     * 处理成功。
     */
    SUCCESS("SUCCESS", "处理成功"),

    /**
     * 处理失败。
     */
    FAILED("FAILED", "处理失败");

    private final String code;

    private final String desc;

    AiDocumentIndexEventStatusEnum(String code, String desc) {
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
