package com.bio.drqi.ai.service;

public interface AiAuditLogService {

    void log(String requestType, String question, String planJson, Integer rowCount, Long costMillis);
}
