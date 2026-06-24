package com.bio.drqi.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

import com.bio.drqi.ai.service.AiAuditLogService;

@Slf4j
@Service
public class AiAuditLogServiceImpl implements AiAuditLogService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void log(String requestType, String question, String planJson, Integer rowCount, Long costMillis) {
        log.info("AI请求审计 type={}, rows={}, cost={}ms, question={}, plan={}",
                requestType, rowCount, costMillis, question, planJson);
        try {
            jdbcTemplate.update("insert into bio_request_log(request_time, request_param, request_method, request_desc) values (?, ?, ?, ?)",
                    new Date(), limit(question, 1800), "AI_" + requestType, limit(planJson, 1800));
        } catch (Exception e) {
            log.warn("AI请求审计写入bio_request_log失败", e);
        }
    }

    private String limit(String value, int maxLength) {
        if (StrUtil.isBlank(value) || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
