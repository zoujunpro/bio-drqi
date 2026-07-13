package com.bio.drqi.ai.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bio.drqi.ai.dao.domain.AiQueryAuditLog;
import com.bio.drqi.ai.dao.mapper.AiQueryAuditLogMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * AI 查询审计后台查看服务。
 */
@Service
public class AiAuditAdminService {

    @Resource
    private AiQueryAuditLogMapper aiQueryAuditLogMapper;

    public IPage<AiQueryAuditLog> page(Map<String, Object> params) {
        QueryWrapper<AiQueryAuditLog> wrapper = new QueryWrapper<AiQueryAuditLog>();
        String keyword = text(params, "keyword");
        if (hasText(keyword)) {
            wrapper.and(item -> item.like("question", keyword).or().like("conversation_id", keyword));
        }
        eqIfHasText(wrapper, "scenario", text(params, "scenario"));
        eqIfHasText(wrapper, "intent", text(params, "intent"));
        eqIfHasText(wrapper, "domain", text(params, "domain"));
        eqIfHasText(wrapper, "success", text(params, "success"));
        wrapper.orderByDesc("id");
        return aiQueryAuditLogMapper.selectPage(pageInfo(params), wrapper);
    }

    private Page<AiQueryAuditLog> pageInfo(Map<String, Object> params) {
        return new Page<AiQueryAuditLog>(longValue(params, "pageNum", 1L), longValue(params, "pageSize", 20L));
    }

    private void eqIfHasText(QueryWrapper<?> wrapper, String column, String value) {
        if (hasText(value)) {
            wrapper.eq(column, value);
        }
    }

    private String text(Map<String, Object> params, String key) {
        if (params == null || params.get(key) == null) {
            return null;
        }
        String value = String.valueOf(params.get(key)).trim();
        return value.length() == 0 ? null : value;
    }

    private long longValue(Map<String, Object> params, String key, long defaultValue) {
        String value = text(params, key);
        if (!hasText(value)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ignored) {
            return defaultValue;
        }
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
