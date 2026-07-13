package com.bio.drqi.ai.common.constant;

/**
 * AI 文档索引配置常量。
 * 第一版先使用代码常量，后续如果要后台可配置，可以迁移到 Nacos 或系统配置表。
 */
public final class AiDocumentIndexConstant {

    /**
     * 单个分块目标 token 数。当前没有引入模型 tokenizer，工具类会用中英文混合文本做近似估算。
     */
    public static final int CHUNK_TARGET_TOKEN_COUNT = 700;

    /**
     * 单个分块最大 token 数。超过后会继续拆分，避免 embedding 入参过长。
     */
    public static final int CHUNK_MAX_TOKEN_COUNT = 900;

    /**
     * 相邻分块重叠 token 数。保留上下文，避免答案刚好跨两个分块时召回不完整。
     */
    public static final int CHUNK_OVERLAP_TOKEN_COUNT = 100;

    /**
     * 默认检索返回片段数。用户未指定 topK 时使用。
     */
    public static final int DEFAULT_TOP_K = 8;

    /**
     * 索引事件错误信息最大保存长度。避免异常堆栈或模型响应过长导致数据库字段溢出。
     */
    public static final int EVENT_ERROR_MESSAGE_MAX_LENGTH = 1000;

    private AiDocumentIndexConstant() {
    }
}
