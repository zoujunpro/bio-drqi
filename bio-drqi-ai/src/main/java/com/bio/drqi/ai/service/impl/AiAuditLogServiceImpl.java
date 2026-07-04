package com.bio.drqi.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import com.bio.drqi.ai.entity.AiQueryAuditLog;
import com.bio.drqi.ai.mapper.AiQueryAuditLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import com.bio.drqi.ai.dto.audit.AiAuditLogDTO;
import com.bio.drqi.ai.service.AiAuditLogService;

@Slf4j
@Service
@Primary
public class AiAuditLogServiceImpl implements AiAuditLogService {

    @Resource
    private AiQueryAuditLogMapper aiQueryAuditLogMapper;

    @Override
    public void log(String requestType, String question, String planJson, Integer rowCount, Long costMillis) {
        AiAuditLogDTO logDTO = new AiAuditLogDTO();
        logDTO.setScenario(requestType);
        logDTO.setQuestion(question);
        logDTO.setQueryPlan(planJson);
        logDTO.setRowCount(rowCount);
        logDTO.setCostMillis(costMillis);
        log(logDTO);
    }

    @Override
    public void log(AiAuditLogDTO logDTO) {
        if (logDTO == null) {
            return;
        }
        log.info("AI请求审计 type={}, rows={}, cost={}ms, question={}, plan={}",
                logDTO.getScenario(), logDTO.getRowCount(), logDTO.getCostMillis(), logDTO.getQuestion(), logDTO.getQueryPlan());
        insertAiAuditLog(logDTO);
    }

    private void insertAiAuditLog(AiAuditLogDTO logDTO) {
        try {
            aiQueryAuditLogMapper.insert(toEntity(logDTO));
        } catch (Exception e) {
            log.warn("AI请求审计写入ai_query_audit_log失败", e);
        }
    }

    private AiQueryAuditLog toEntity(AiAuditLogDTO logDTO) {
        AiQueryAuditLog entity = new AiQueryAuditLog();
        entity.setTraceId(limit(logDTO.getTraceId(), 64));
        entity.setConversationId(limit(logDTO.getConversationId(), 64));
        entity.setUserId(limit(logDTO.getUserId(), 64));
        entity.setScenario(limit(logDTO.getScenario(), 32));
        entity.setQuestion(limit(logDTO.getQuestion(), 4000));
        entity.setIntent(limit(logDTO.getIntent(), 32));
        entity.setDomain(limit(logDTO.getDomain(), 64));
        entity.setQueryPlan(limit(logDTO.getQueryPlan(), 16000));
        entity.setSqlText(limit(joinSqlAndParams(logDTO), 4000));
        entity.setRowCount(logDTO.getRowCount() == null ? 0 : logDTO.getRowCount());
        entity.setCostMs(logDTO.getCostMillis() == null ? 0L : logDTO.getCostMillis());
        entity.setSuccess(Boolean.FALSE.equals(logDTO.getSuccess()) ? 0 : 1);
        entity.setErrorMessage(limit(logDTO.getErrorMessage(), 1000));
        return entity;
    }

    private String limit(String value, int maxLength) {
        if (StrUtil.isBlank(value) || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private String joinSqlAndParams(AiAuditLogDTO logDTO) {
        if (StrUtil.isBlank(logDTO.getSqlParams())) {
            return logDTO.getSqlText();
        }
        return logDTO.getSqlText() + "\nparams=" + logDTO.getSqlParams();
    }
}
