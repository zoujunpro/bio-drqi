package com.bio.drqi.ai.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.dto.plan.AiQueryFilterDTO;
import com.bio.drqi.ai.dto.plan.AiQueryOrderDTO;
import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.schema.AiDomainSchema;
import com.bio.drqi.ai.schema.AiFieldSchema;
import com.bio.drqi.ai.service.AiQueryPlanValidator;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AiQueryPlanValidatorImpl implements AiQueryPlanValidator {

    private static final String QUERY_TYPE_AGGREGATE = "aggregate";
    private static final String QUERY_TYPE_DETAIL = "detail";

    /**
     * 过滤操作白名单。后续接 SQL 执行器时，只允许这些操作被转换成查询条件。
     */
    private static final Set<String> SUPPORT_OPS = new HashSet<>(Arrays.asList(
            "eq", "in", "like", "between", "gte", "lte", "last_days", "last_months"
    ));
    /**
     * 前端当前支持的展示类型。
     */
    private static final Set<String> SUPPORT_CHART_TYPES = new HashSet<>(Arrays.asList(
            "table", "bar", "line", "pie", "auto"
    ));

    @Override
    public void validate(AiQueryPlanDTO plan, AiDomainSchema schema) {
        // domain 必须来自业务域注册表，不能让模型随意指定表。
        if (plan == null || StrUtil.isBlank(plan.getDomain())) {
            throw new BusinessException("AI查询计划缺少业务域");
        }
        if (!schema.getDomain().equals(plan.getDomain())) {
            throw new BusinessException("AI查询计划业务域不匹配");
        }
        if (StrUtil.isBlank(plan.getQueryType())) {
            plan.setQueryType(CollectionUtil.isEmpty(plan.getMetrics()) ? QUERY_TYPE_DETAIL : QUERY_TYPE_AGGREGATE);
        }
        if (!QUERY_TYPE_AGGREGATE.equals(plan.getQueryType()) && !QUERY_TYPE_DETAIL.equals(plan.getQueryType())) {
            throw new BusinessException("不支持的查询类型：" + plan.getQueryType());
        }
        if (QUERY_TYPE_AGGREGATE.equals(plan.getQueryType())) {
            validateAggregatePlan(plan, schema);
        } else {
            validateDetailPlan(plan, schema);
        }
        // 过滤、排序都只允许使用后端 schema 中声明过的名称。
        if (CollectionUtil.isNotEmpty(plan.getFilters())) {
            for (AiQueryFilterDTO filter : plan.getFilters()) {
                validateFilter(filter, schema);
            }
        }
        if (CollectionUtil.isNotEmpty(plan.getOrderBy())) {
            for (AiQueryOrderDTO order : plan.getOrderBy()) {
                validateOrder(order, schema, plan);
            }
        }
        if (StrUtil.isNotBlank(plan.getChartType()) && !SUPPORT_CHART_TYPES.contains(plan.getChartType())) {
            throw new BusinessException("不支持的图表类型：" + plan.getChartType());
        }
        // limit 由后端兜底和截断，避免模型生成超大查询。
        if (plan.getLimit() == null || plan.getLimit() <= 0) {
            plan.setLimit(100);
        }
        if (plan.getLimit() > 500) {
            plan.setLimit(500);
        }
    }

    private void validateAggregatePlan(AiQueryPlanDTO plan, AiDomainSchema schema) {
        if (CollectionUtil.isEmpty(plan.getMetrics())) {
            if (schema.getMetrics().containsKey("totalCount")) {
                plan.getMetrics().add("totalCount");
            } else {
                throw new BusinessException("AI统计查询计划缺少统计指标");
            }
        }
        for (String metric : plan.getMetrics()) {
            if (!schema.getMetrics().containsKey(metric)) {
                throw new BusinessException("不支持的统计指标：" + metric);
            }
        }
        if (CollectionUtil.isNotEmpty(plan.getDimensions())) {
            for (String dimension : plan.getDimensions()) {
                if (!schema.getDimensions().containsKey(dimension)) {
                    throw new BusinessException("不支持的分组字段：" + dimension);
                }
            }
        }
    }

    private void validateDetailPlan(AiQueryPlanDTO plan, AiDomainSchema schema) {
        if (CollectionUtil.isEmpty(plan.getSelectFields())) {
            plan.setSelectFields(schema.getFields().keySet().stream().limit(8).collect(Collectors.toList()));
        }
        for (String field : plan.getSelectFields()) {
            if (!schema.getFields().containsKey(field)) {
                throw new BusinessException("不支持的明细字段：" + field);
            }
        }
    }

    private void validateFilter(AiQueryFilterDTO filter, AiDomainSchema schema) {
        if (filter == null || StrUtil.isBlank(filter.getField())) {
            throw new BusinessException("过滤条件字段不能为空");
        }
        if (!schema.getFields().containsKey(filter.getField())) {
            throw new BusinessException("不支持的过滤字段：" + filter.getField());
        }
        if (!SUPPORT_OPS.contains(filter.getOp())) {
            throw new BusinessException("不支持的过滤操作：" + filter.getOp());
        }
        normalizeEnumFilterValue(filter, schema.getFields().get(filter.getField()));
    }

    private void validateOrder(AiQueryOrderDTO order, AiDomainSchema schema, AiQueryPlanDTO plan) {
        if (order == null || StrUtil.isBlank(order.getField())) {
            throw new BusinessException("排序字段不能为空");
        }
        boolean selectedMetric = CollectionUtil.isNotEmpty(plan.getMetrics()) && plan.getMetrics().contains(order.getField());
        boolean selectedDimension = CollectionUtil.isNotEmpty(plan.getDimensions()) && plan.getDimensions().contains(order.getField());
        boolean selectedField = CollectionUtil.isNotEmpty(plan.getSelectFields()) && plan.getSelectFields().contains(order.getField());
        if (!selectedMetric && !selectedDimension && !selectedField) {
            throw new BusinessException("不支持的排序字段：" + order.getField());
        }
        if (!"asc".equalsIgnoreCase(order.getDirection()) && !"desc".equalsIgnoreCase(order.getDirection())) {
            throw new BusinessException("不支持的排序方向：" + order.getDirection());
        }
    }

    private void normalizeEnumFilterValue(AiQueryFilterDTO filter, AiFieldSchema fieldSchema) {
        if (fieldSchema == null || CollectionUtil.isEmpty(fieldSchema.getEnumValues()) || filter.getValue() == null) {
            return;
        }
        if ("in".equals(filter.getOp())) {
            List<Object> values = toList(filter.getValue());
            List<Object> normalized = new ArrayList<>();
            for (Object value : values) {
                normalized.add(normalizeEnumValue(value, fieldSchema.getEnumValues()));
            }
            filter.setValue(normalized);
            return;
        }
        filter.setValue(normalizeEnumValue(filter.getValue(), fieldSchema.getEnumValues()));
    }

    private Object normalizeEnumValue(Object value, Map<String, String> enumValues) {
        if (value == null) {
            return null;
        }
        String text = String.valueOf(value);
        if (enumValues.containsKey(text)) {
            return text;
        }
        for (Map.Entry<String, String> entry : enumValues.entrySet()) {
            if (text.equalsIgnoreCase(entry.getValue())) {
                return entry.getKey();
            }
        }
        return value;
    }

    private List<Object> toList(Object value) {
        List<Object> values = new ArrayList<>();
        if (value == null) {
            return values;
        }
        if (value instanceof Collection) {
            values.addAll((Collection<?>) value);
            return values;
        }
        if (value.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(value); i++) {
                values.add(Array.get(value, i));
            }
            return values;
        }
        values.add(value);
        return values;
    }
}
