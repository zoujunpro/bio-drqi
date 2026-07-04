package com.bio.drqi.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import com.bio.drqi.ai.dto.memory.AiConversationContextDTO;
import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.dto.rsp.AiAnalysisRspDTO;
import com.bio.drqi.ai.dto.rsp.AiTableColumnDTO;
import com.bio.drqi.ai.dto.rsp.AiTableDTO;
import com.bio.drqi.ai.service.AiConversationMemoryService;
import cn.hutool.json.JSONUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryAiConversationMemoryServiceImpl implements AiConversationMemoryService {

    private final Map<String, AiConversationContextDTO> contexts = new ConcurrentHashMap<>();

    @Override
    public AiConversationContextDTO getOrCreate(String conversationId) {
        String id = StrUtil.blankToDefault(conversationId, UUID.randomUUID().toString());
        return contexts.computeIfAbsent(id, key -> {
            AiConversationContextDTO context = new AiConversationContextDTO();
            context.setConversationId(key);
            return context;
        });
    }

    @Override
    public void saveUserMessage(String conversationId, String question) {
        getOrCreate(conversationId);
    }

    @Override
    public void updateAfterQuery(String conversationId, String question, AiQueryPlanDTO plan, AiAnalysisRspDTO response, Map<String, String> confirmedTerms) {
        AiConversationContextDTO context = getOrCreate(conversationId);
        context.setLastQueryPlan(plan);
        context.setCurrentDomain(plan == null ? null : plan.getDomain());
        context.setLastResultSummary(buildResultSummary(response));
        context.setLastResultSnapshot(buildResultSnapshot(response));
        if (confirmedTerms != null) {
            context.getConfirmedTerms().putAll(confirmedTerms);
        }
        context.setPendingClarification(null);
    }

    @Override
    public void updateAfterAnswer(String conversationId, String question, String answer) {
        AiConversationContextDTO context = getOrCreate(conversationId);
        context.setLastResultSummary(answer);
    }

    private String buildResultSummary(AiAnalysisRspDTO response) {
        if (response == null || response.getTables().isEmpty()) {
            return null;
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
}
