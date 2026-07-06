package com.bio.drqi.ai.exception;

public enum AiErrorCode {

    SUCCESS("SUCCESS", "成功"),
    AI_INTENT_UNKNOWN("AI_INTENT_UNKNOWN", "意图不明确"),
    AI_QUERY_RISK("AI_QUERY_RISK", "查询风险拦截"),
    AI_DB_ERROR("AI_DB_ERROR", "数据库查询失败"),
    AI_LLM_ERROR("AI_LLM_ERROR", "模型调用失败"),
    AI_RATE_LIMITED("AI_RATE_LIMITED", "请求过于频繁"),
    AI_CONFIG_ERROR("AI_CONFIG_ERROR", "AI配置错误"),
    AI_SYSTEM_ERROR("AI_SYSTEM_ERROR", "系统异常");

    private final String code;

    private final String message;

    AiErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
