package com.bio.drqi.ai.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.ai.dto.memory.AiConversationContextDTO;
import com.bio.drqi.ai.dto.plan.AiQueryFilterDTO;
import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.schema.AiDomainSchema;
import com.bio.drqi.ai.schema.AiFieldSchema;
import com.bio.drqi.ai.service.AiContextFilterEnhancer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class AiContextFilterEnhancerImpl implements AiContextFilterEnhancer {

    private static final List<String> REFERENCE_WORDS = Arrays.asList(
            "这些", "上述", "上面", "刚才", "刚刚", "前面", "它们", "他们", "该项目", "这些项目", "这些数据"
    );

    private static final List<String> PROJECT_FIELDS = Arrays.asList(
            "projectCode", "projectId", "projectNum", "projectNo", "projectName", "project"
    );

    @Override
    public void enhance(String question, AiConversationContextDTO context, AiQueryPlanDTO plan, AiDomainSchema schema) {
        if (!shouldEnhance(question, context, plan, schema)) {
            return;
        }
        ContextValues contextValues = extractContextValues(question, context.getLastResultSnapshot(), schema);
        if (contextValues == null || CollectionUtil.isEmpty(contextValues.getValues())) {
            log.info("AI上下文指代未提取到可用过滤值，domain={}，question={}", plan.getDomain(), question);
            return;
        }
        if (hasFilter(plan, contextValues.getTargetField())) {
            return;
        }
        AiQueryFilterDTO filter = new AiQueryFilterDTO();
        filter.setField(contextValues.getTargetField());
        filter.setOp("in");
        filter.setValue(new ArrayList<>(contextValues.getValues()));
        plan.getFilters().add(filter);
        log.info("AI上下文指代已注入过滤条件，domain={}，field={}，size={}，question={}",
                plan.getDomain(), filter.getField(), contextValues.getValues().size(), question);
    }

    private boolean shouldEnhance(String question, AiConversationContextDTO context, AiQueryPlanDTO plan, AiDomainSchema schema) {
        return StrUtil.isNotBlank(question)
                && context != null
                && StrUtil.isNotBlank(context.getLastResultSnapshot())
                && plan != null
                && schema != null
                && containsAny(question, REFERENCE_WORDS);
    }

    private ContextValues extractContextValues(String question, String snapshot, AiDomainSchema targetSchema) {
        try {
            JSONArray tables = JSONUtil.parseArray(snapshot);
            List<String> targetCandidates = targetFieldCandidates(question);
            String targetField = findTargetField(targetSchema, targetCandidates);
            if (StrUtil.isBlank(targetField)) {
                return null;
            }
            Set<Object> values = new LinkedHashSet<>();
            for (Object tableObject : tables) {
                JSONObject table = JSONUtil.parseObj(tableObject);
                JSONArray columns = table.getJSONArray("columns");
                JSONArray rows = table.getJSONArray("rows");
                if (columns == null || rows == null) {
                    continue;
                }
                List<String> sourceFields = findSourceFields(columns, targetCandidates);
                if (sourceFields.isEmpty()) {
                    continue;
                }
                for (Object rowObject : rows) {
                    JSONObject row = JSONUtil.parseObj(rowObject);
                    for (String sourceField : sourceFields) {
                        Object value = row.get(sourceField);
                        if (value != null && StrUtil.isNotBlank(String.valueOf(value))) {
                            values.add(value);
                        }
                        if (values.size() >= 100) {
                            break;
                        }
                    }
                    if (values.size() >= 100) {
                        break;
                    }
                }
            }
            if (values.isEmpty()) {
                return null;
            }
            return new ContextValues(targetField, values);
        } catch (Exception e) {
            log.warn("AI上下文指代解析失败，snapshot={}", limit(snapshot, 1000), e);
            return null;
        }
    }

    private List<String> targetFieldCandidates(String question) {
        if (question.contains("项目")) {
            return PROJECT_FIELDS;
        }
        return PROJECT_FIELDS;
    }

    private String findTargetField(AiDomainSchema schema, List<String> candidates) {
        for (String candidate : candidates) {
            if (schema.getFields().containsKey(candidate)) {
                return candidate;
            }
        }
        for (Map.Entry<String, AiFieldSchema> entry : schema.getFields().entrySet()) {
            String field = entry.getKey();
            String label = entry.getValue() == null ? null : entry.getValue().getLabel();
            for (String candidate : candidates) {
                if (field.toLowerCase().contains(candidate.toLowerCase())
                        || (StrUtil.isNotBlank(label) && label.contains("项目"))) {
                    return field;
                }
            }
        }
        return null;
    }

    private List<String> findSourceFields(JSONArray columns, List<String> candidates) {
        List<String> result = new ArrayList<>();
        for (Object columnObject : columns) {
            JSONObject column = JSONUtil.parseObj(columnObject);
            String dataIndex = column.getStr("dataIndex");
            String title = column.getStr("title");
            if (StrUtil.isBlank(dataIndex)) {
                continue;
            }
            for (String candidate : candidates) {
                if (dataIndex.equalsIgnoreCase(candidate)
                        || dataIndex.toLowerCase().contains(candidate.toLowerCase())
                        || (StrUtil.isNotBlank(title) && title.contains("项目"))) {
                    result.add(dataIndex);
                    break;
                }
            }
        }
        return result;
    }

    private boolean hasFilter(AiQueryPlanDTO plan, String field) {
        if (CollectionUtil.isEmpty(plan.getFilters())) {
            return false;
        }
        for (AiQueryFilterDTO filter : plan.getFilters()) {
            if (filter != null && field.equals(filter.getField())) {
                return true;
            }
        }
        return false;
    }

    private boolean containsAny(String text, List<String> words) {
        for (String word : words) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;
    }

    private String limit(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength);
    }

    private static class ContextValues {
        private final String targetField;
        private final Set<Object> values;

        private ContextValues(String targetField, Set<Object> values) {
            this.targetField = targetField;
            this.values = values;
        }

        private String getTargetField() {
            return targetField;
        }

        private Set<Object> getValues() {
            return values;
        }
    }
}
