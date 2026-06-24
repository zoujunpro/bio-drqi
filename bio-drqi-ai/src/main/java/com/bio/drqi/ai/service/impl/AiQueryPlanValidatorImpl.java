package com.bio.drqi.ai.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.dto.plan.AiQueryFilterDTO;
import com.bio.drqi.ai.dto.plan.AiQueryOrderDTO;
import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.schema.AiDomainSchema;
import com.bio.drqi.ai.service.AiQueryPlanValidator;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
            throw new BusinessException("AI统计查询计划缺少统计指标");
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
            throw new BusinessException("AI明细查询计划缺少返回字段");
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
}
