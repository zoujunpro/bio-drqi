package com.bio.drqi.ai.clarify;

/**
 * 意图澄清状态。
 */
public enum ClarificationState {

    /**
     * 意图足够明确，不需要追问。
     */
    RESOLVED,

    /**
     * 意图太模糊，需要用户选择业务方向。
     */
    NEED_CLARIFY,

    /**
     * 用户引用了“刚才/这些”，但当前会话没有可用上下文。
     */
    MISSING_CONTEXT,

    /**
     * 用户输入的是写操作或高风险操作，智能查询不执行。
     */
    REJECTED
}
