package com.bio.drqi.ai.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bio.drqi.ai.dao.domain.AiMemorySummary;
import com.bio.drqi.ai.dao.domain.AiMessage;
import com.bio.drqi.ai.dao.domain.AiMessageFile;
import com.bio.drqi.ai.dao.domain.AiSession;
import com.bio.drqi.ai.dao.domain.AiUserMemory;
import com.bio.drqi.ai.dao.mapper.AiMemorySummaryMapper;
import com.bio.drqi.ai.dao.mapper.AiMessageFileMapper;
import com.bio.drqi.ai.dao.mapper.AiMessageMapper;
import com.bio.drqi.ai.dao.mapper.AiSessionMapper;
import com.bio.drqi.ai.dao.mapper.AiUserMemoryMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * AI 记忆运行数据后台查看服务。
 */
@Service
public class AiMemoryAdminService {

    @Resource
    private AiSessionMapper aiSessionMapper;

    @Resource
    private AiMessageMapper aiMessageMapper;

    @Resource
    private AiMemorySummaryMapper aiMemorySummaryMapper;

    @Resource
    private AiUserMemoryMapper aiUserMemoryMapper;

    @Resource
    private AiMessageFileMapper aiMessageFileMapper;

    public IPage<AiSession> pageSessions(Map<String, Object> params) {
        QueryWrapper<AiSession> wrapper = new QueryWrapper<AiSession>();
        likeIfHasText(wrapper, "session_id", text(params, "sessionId"));
        likeIfHasText(wrapper, "user_id", text(params, "userId"));
        likeIfHasText(wrapper, "title", text(params, "keyword"));
        eqIfHasText(wrapper, "status", text(params, "status"));
        wrapper.orderByDesc("id");
        return aiSessionMapper.selectPage(page(params), wrapper);
    }

    public IPage<AiMessage> pageMessages(Map<String, Object> params) {
        QueryWrapper<AiMessage> wrapper = new QueryWrapper<AiMessage>();
        likeIfHasText(wrapper, "session_id", text(params, "sessionId"));
        likeIfHasText(wrapper, "user_id", text(params, "userId"));
        eqIfHasText(wrapper, "role", text(params, "role"));
        likeIfHasText(wrapper, "content", text(params, "keyword"));
        wrapper.orderByDesc("id");
        return aiMessageMapper.selectPage(page(params), wrapper);
    }

    public IPage<AiMemorySummary> pageSummaries(Map<String, Object> params) {
        QueryWrapper<AiMemorySummary> wrapper = new QueryWrapper<AiMemorySummary>();
        likeIfHasText(wrapper, "session_id", text(params, "sessionId"));
        likeIfHasText(wrapper, "user_id", text(params, "userId"));
        likeIfHasText(wrapper, "summary", text(params, "keyword"));
        wrapper.orderByDesc("id");
        return aiMemorySummaryMapper.selectPage(page(params), wrapper);
    }

    public IPage<AiUserMemory> pageUserMemories(Map<String, Object> params) {
        QueryWrapper<AiUserMemory> wrapper = new QueryWrapper<AiUserMemory>();
        likeIfHasText(wrapper, "user_id", text(params, "userId"));
        eqIfHasText(wrapper, "memory_type", text(params, "memoryType"));
        eqIfHasText(wrapper, "status", text(params, "status"));
        String keyword = text(params, "keyword");
        if (hasText(keyword)) {
            wrapper.and(item -> item.like("memory_key", keyword).or().like("memory_value", keyword));
        }
        wrapper.orderByDesc("id");
        return aiUserMemoryMapper.selectPage(page(params), wrapper);
    }

    public IPage<AiMessageFile> pageFiles(Map<String, Object> params) {
        QueryWrapper<AiMessageFile> wrapper = new QueryWrapper<AiMessageFile>();
        likeIfHasText(wrapper, "session_id", text(params, "sessionId"));
        likeIfHasText(wrapper, "user_id", text(params, "userId"));
        eqIfHasText(wrapper, "parse_status", text(params, "status"));
        likeIfHasText(wrapper, "file_name", text(params, "keyword"));
        wrapper.orderByDesc("id");
        return aiMessageFileMapper.selectPage(page(params), wrapper);
    }

    private <T> Page<T> page(Map<String, Object> params) {
        return new Page<T>(longValue(params, "pageNum", 1L), longValue(params, "pageSize", 20L));
    }

    private void likeIfHasText(QueryWrapper<?> wrapper, String column, String value) {
        if (hasText(value)) {
            wrapper.like(column, value);
        }
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
