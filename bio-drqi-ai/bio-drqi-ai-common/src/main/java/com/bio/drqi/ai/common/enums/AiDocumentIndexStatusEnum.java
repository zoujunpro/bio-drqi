package com.bio.drqi.ai.common.enums;

/**
 * AI 文档索引状态。
 * 对应 MySQL 表 ai_document_index.status 和 PG 表 ai_document_chunk_vector.status。
 */
public enum AiDocumentIndexStatusEnum {

    /**
     * 可检索。只有 READY 状态的文档元数据和向量分块会参与 RAG 检索。
     */
    READY("READY", "可检索"),

    /**
     * 已删除。删除采用软删除，保留元数据和事件记录，便于追溯。
     */
    DELETED("DELETED", "已删除"),

    /**
     * 未知状态。用于权限刷新等操作找不到文档索引时的响应展示，不建议入库。
     */
    UNKNOWN("UNKNOWN", "未知");

    private final String code;

    private final String desc;

    AiDocumentIndexStatusEnum(String code, String desc) {
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
