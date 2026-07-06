package com.bio.drqi.ai.service;

import com.bio.drqi.ai.dto.audit.AiAuditLogDTO;

public interface AiAuditLogService {

    void log(String requestType, String question, String planJson, Integer rowCount, Long costMillis);

    void log(AiAuditLogDTO logDTO);
}
