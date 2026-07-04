package com.bio.drqi.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.ai.config.AiProperties;
import com.bio.drqi.ai.dto.memory.AiConversationContextDTO;
import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.dto.rsp.AiAnalysisRspDTO;
import com.bio.drqi.ai.dto.rsp.AiTableColumnDTO;
import com.bio.drqi.ai.dto.rsp.AiTableDTO;
import com.bio.drqi.ai.service.AiConversationMemoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Primary
@Slf4j
public class JdbcAiConversationMemoryServiceImpl implements AiConversationMemoryService {

    @Resource
    private ObjectProvider<JdbcTemplate> jdbcTemplateProvider;

    @Resource
    private AiProperties aiProperties;

    @Resource
    private InMemoryAiConversationMemoryServiceImpl inMemoryAiConversationMemoryService;

    @Override
    public AiConversationContextDTO getOrCreate(String conversationId) {
        // conversationId 是一次连续 AI 对话的唯一标识。
        // 如果前端没有传，说明用户开启了新对话，这里生成一个新的 id。
        // 如果前端传了，说明用户在追问，例如“把刚才的数据整理一下”，这里要按原 id 找回上一次上下文。
        String id = StrUtil.blankToDefault(conversationId, UUID.randomUUID().toString());
        JdbcTemplate jdbcTemplate = jdbcTemplateProvider.getIfAvailable();
        if (jdbcTemplate == null) {
            // 没有可用 JDBC 时，降级到内存记忆；服务重启后内存记忆会丢失。
            return inMemoryAiConversationMemoryService.getOrCreate(id);
        }
        try {
            // 确保 ai_conversation 主记录存在，用于标记这是一轮有效会话。
            ensureConversation(jdbcTemplate, id);
            // 从 ai_query_context 恢复结构化上下文，包括上一次业务域、查询计划、术语确认、结果摘要/快照。
            AiConversationContextDTO context = loadContext(jdbcTemplate, id);
            if (context != null) {
                return context;
            }
            // 没有上下文记录时初始化一条空记录，后续查询完成后会更新具体内容。
            ensureQueryContext(jdbcTemplate, id, new AiConversationContextDTO());
        } catch (Exception e) {
            log.warn("AI会话记忆读取数据库失败，降级为内存记忆，conversationId={}", id, e);
            return inMemoryAiConversationMemoryService.getOrCreate(id);
        }
        // 返回一个新的空上下文，供当前请求继续处理；conversationId 会随响应返回给前端。
        AiConversationContextDTO context = new AiConversationContextDTO();
        context.setConversationId(id);
        return context;
    }

    @Override
    public void saveUserMessage(String conversationId, String question) {
        saveMessage(conversationId, "user", question, null, null);
        inMemoryAiConversationMemoryService.saveUserMessage(conversationId, question);
    }

    @Override
    public void updateAfterQuery(String conversationId, String question, AiQueryPlanDTO plan, AiAnalysisRspDTO response, Map<String, String> confirmedTerms) {
        AiConversationContextDTO context = inMemoryAiConversationMemoryService.getOrCreate(conversationId);
        inMemoryAiConversationMemoryService.updateAfterQuery(conversationId, question, plan, response, confirmedTerms);
        context = inMemoryAiConversationMemoryService.getOrCreate(conversationId);
        String summary = buildResultSummary(response);
        String snapshot = buildResultSnapshot(response);
        context.setLastResultSummary(summary);
        context.setLastResultSnapshot(snapshot);
        saveMessage(conversationId, "assistant", summary, null, plan == null ? null : plan.getDomain());
        saveContext(context);
    }

    @Override
    public void updateAfterAnswer(String conversationId, String question, String answer) {
        inMemoryAiConversationMemoryService.updateAfterAnswer(conversationId, question, answer);
        saveMessage(conversationId, "assistant", answer, null, null);
        AiConversationContextDTO context = inMemoryAiConversationMemoryService.getOrCreate(conversationId);
        context.setLastResultSummary(answer);
        saveContext(context);
    }

    private AiConversationContextDTO loadContext(JdbcTemplate jdbcTemplate, String conversationId) {
        List<AiConversationContextDTO> contexts = jdbcTemplate.query(
                "select conversation_id, current_domain, last_query_plan, confirmed_terms, pending_clarification, last_result_summary, last_result_snapshot from ai_query_context where conversation_id = ?",
                new Object[]{conversationId},
                (rs, rowNum) -> mapContext(rs));
        return contexts.isEmpty() ? null : contexts.get(0);
    }

    private AiConversationContextDTO mapContext(ResultSet rs) throws SQLException {
        AiConversationContextDTO context = new AiConversationContextDTO();
        context.setConversationId(rs.getString("conversation_id"));
        context.setCurrentDomain(rs.getString("current_domain"));
        String lastQueryPlan = rs.getString("last_query_plan");
        if (StrUtil.isNotBlank(lastQueryPlan)) {
            context.setLastQueryPlan(JSONUtil.toBean(lastQueryPlan, AiQueryPlanDTO.class));
        }
        String confirmedTerms = rs.getString("confirmed_terms");
        if (StrUtil.isNotBlank(confirmedTerms) && JSONUtil.isTypeJSON(confirmedTerms)) {
            JSONObject jsonObject = JSONUtil.parseObj(confirmedTerms);
            for (String key : jsonObject.keySet()) {
                context.getConfirmedTerms().put(key, jsonObject.getStr(key));
            }
        }
        context.setPendingClarification(rs.getString("pending_clarification"));
        context.setLastResultSummary(rs.getString("last_result_summary"));
        context.setLastResultSnapshot(readOptionalString(rs, "last_result_snapshot"));
        inMemoryAiConversationMemoryService.updateAfterQuery(context.getConversationId(), null,
                context.getLastQueryPlan(), null, context.getConfirmedTerms());
        if (StrUtil.isNotBlank(context.getLastResultSummary())) {
            inMemoryAiConversationMemoryService.updateAfterAnswer(context.getConversationId(), null, context.getLastResultSummary());
        }
        return context;
    }

    private void saveContext(AiConversationContextDTO context) {
        JdbcTemplate jdbcTemplate = jdbcTemplateProvider.getIfAvailable();
        if (jdbcTemplate == null || context == null || StrUtil.isBlank(context.getConversationId())) {
            return;
        }
        try {
            ensureConversation(jdbcTemplate, context.getConversationId());
            jdbcTemplate.update("insert into ai_query_context(conversation_id, current_domain, last_query_plan, confirmed_terms, pending_clarification, last_result_summary, last_result_snapshot, expire_time) values (?, ?, ?, ?, ?, ?, ?, ?) on duplicate key update current_domain = values(current_domain), last_query_plan = values(last_query_plan), confirmed_terms = values(confirmed_terms), pending_clarification = values(pending_clarification), last_result_summary = values(last_result_summary), last_result_snapshot = values(last_result_snapshot), expire_time = values(expire_time)",
                    context.getConversationId(),
                    context.getCurrentDomain(),
                    context.getLastQueryPlan() == null ? null : JSONUtil.toJsonStr(context.getLastQueryPlan()),
                    JSONUtil.toJsonStr(context.getConfirmedTerms()),
                    context.getPendingClarification(),
                    limit(context.getLastResultSummary(), 500),
                    limit(context.getLastResultSnapshot(), 60000),
                    expireTime());
        } catch (Exception e) {
            log.warn("AI会话上下文写入数据库失败，conversationId={}", context.getConversationId(), e);
        }
    }

    private void saveMessage(String conversationId, String role, String content, String intent, String domain) {
        if (StrUtil.isBlank(conversationId) || StrUtil.isBlank(content)) {
            return;
        }
        JdbcTemplate jdbcTemplate = jdbcTemplateProvider.getIfAvailable();
        if (jdbcTemplate == null) {
            return;
        }
        try {
            ensureConversation(jdbcTemplate, conversationId);
            jdbcTemplate.update("insert into ai_message(conversation_id, role, content, intent, domain) values (?, ?, ?, ?, ?)",
                    conversationId, role, limit(content, 16000), intent, domain);
        } catch (Exception e) {
            log.warn("AI会话消息写入数据库失败，conversationId={}，role={}", conversationId, role, e);
        }
    }

    private void ensureConversation(JdbcTemplate jdbcTemplate, String conversationId) {
        jdbcTemplate.update("insert into ai_conversation(conversation_id, expire_time) values (?, ?) on duplicate key update expire_time = values(expire_time)",
                conversationId, expireTime());
    }

    private void ensureQueryContext(JdbcTemplate jdbcTemplate, String conversationId, AiConversationContextDTO context) {
        jdbcTemplate.update("insert into ai_query_context(conversation_id, current_domain, last_query_plan, confirmed_terms, pending_clarification, last_result_summary, last_result_snapshot, expire_time) values (?, ?, ?, ?, ?, ?, ?, ?) on duplicate key update expire_time = values(expire_time)",
                conversationId,
                context.getCurrentDomain(),
                context.getLastQueryPlan() == null ? null : JSONUtil.toJsonStr(context.getLastQueryPlan()),
                JSONUtil.toJsonStr(context.getConfirmedTerms()),
                context.getPendingClarification(),
                limit(context.getLastResultSummary(), 500),
                limit(context.getLastResultSnapshot(), 60000),
                expireTime());
    }

    private Timestamp expireTime() {
        int retentionHours = aiProperties.getMemory() == null || aiProperties.getMemory().getRetentionHours() == null
                ? 24 : aiProperties.getMemory().getRetentionHours();
        return new Timestamp(new Date().getTime() + retentionHours * 60L * 60L * 1000L);
    }

    private String buildResultSummary(AiAnalysisRspDTO response) {
        if (response == null || response.getTables().isEmpty()) {
            return "已完成，本次没有返回表格数据。";
        }
        int rowCount = 0;
        for (AiTableDTO table : response.getTables()) {
            rowCount += table.getData().size();
        }
        return "返回表格数=" + response.getTables().size() + "，总行数=" + rowCount;
    }

    private String buildResultSnapshot(AiAnalysisRspDTO response) {
        if (response == null || response.getTables().isEmpty()) {
            return null;
        }
        List<Map<String, Object>> tables = new ArrayList<>();
        for (AiTableDTO table : response.getTables()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("title", table.getTitle());
            List<Map<String, String>> columns = new ArrayList<>();
            for (AiTableColumnDTO column : table.getColumns()) {
                Map<String, String> columnMap = new LinkedHashMap<>();
                columnMap.put("title", column.getTitle());
                columnMap.put("dataIndex", column.getDataIndex());
                columns.add(columnMap);
            }
            item.put("columns", columns);
            item.put("rows", table.getData().size() > 50 ? table.getData().subList(0, 50) : table.getData());
            item.put("totalRows", table.getData().size());
            tables.add(item);
        }
        return JSONUtil.toJsonStr(tables);
    }

    private String readOptionalString(ResultSet rs, String column) {
        try {
            return rs.getString(column);
        } catch (SQLException e) {
            return null;
        }
    }

    private String limit(String value, int maxLength) {
        if (StrUtil.isBlank(value) || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}
