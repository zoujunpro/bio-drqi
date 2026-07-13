package com.bio.drqi.ai.common.enums;

/**
 * AI 文档索引事件类型。
 * 对应 MySQL 表 ai_document_index_event.event_type。
 */
public enum AiDocumentIndexEventTypeEnum {

    /**
     * 写入或更新索引。文档系统解析正文后推送到 AI 服务，AI 服务重新生成分块和向量。
     */
    UPSERT("UPSERT", "写入或更新索引"),

    /**
     * 删除索引。文档系统删除、禁用或撤回文档时调用，AI 服务软删除对应索引。
     */
    DELETE("DELETE", "删除索引"),

    /**
     * 刷新权限。只替换权限快照，不重新分块、不重新生成向量。
     */
    PERMISSION_REFRESH("PERMISSION_REFRESH", "刷新权限");

    private final String code;

    private final String desc;

    AiDocumentIndexEventTypeEnum(String code, String desc) {
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
