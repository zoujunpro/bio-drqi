package com.bio.drqi.ai.service.impl;

import com.bio.drqi.ai.dto.audit.AiAuditLogDTO;
import com.bio.drqi.ai.service.AiAuditLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NoopAiAuditLogServiceImpl implements AiAuditLogService {

    @Override
    public void log(String requestType, String question, String planJson, Integer rowCount, Long costMillis) {
        log.info("AI请求审计 type={}, rows={}, cost={}ms, question={}, plan={}",
                requestType, rowCount, costMillis, question, planJson);
    }

    @Override
    public void log(AiAuditLogDTO logDTO) {
        log.info("AI请求审计 type={}, rows={}, cost={}ms, question={}, plan={}",
                logDTO == null ? null : logDTO.getScenario(),
                logDTO == null ? null : logDTO.getRowCount(),
                logDTO == null ? null : logDTO.getCostMillis(),
                logDTO == null ? null : logDTO.getQuestion(),
                logDTO == null ? null : logDTO.getQueryPlan());
    }
}
