package com.bio.drqi.ai.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.config.AiProperties;
import com.bio.drqi.ai.dto.plan.AiQueryFilterDTO;
import com.bio.drqi.ai.dto.plan.AiQueryOrderDTO;
import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.schema.AiDomainSchema;
import com.bio.drqi.ai.schema.AiFieldSchema;
import com.bio.drqi.ai.schema.AiJoinSchema;
import com.bio.drqi.ai.schema.AiMetricSchema;
import com.bio.drqi.ai.service.AiQueryRiskChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AiQueryRiskCheckerImpl implements AiQueryRiskChecker {

    private static final String QUERY_TYPE_DETAIL = "detail";

    @Resource
    private AiProperties aiProperties;

    @Override
    public void check(AiQueryPlanDTO plan, AiDomainSchema schema) {
        if (plan == null || schema == null) {
            throw new BusinessException("AI查询风险检查失败：查询计划为空");
        }
        checkSchemaSqlFragments(schema);
        rejectSensitiveFields(plan, schema);
        checkFilterValues(plan);
        normalizeLimit(plan);
        checkNoFilterDetail(plan);
    }

    private void checkSchemaSqlFragments(AiDomainSchema schema) {
        rejectDangerousSqlFragment("主表", schema.getTableName());
        rejectDangerousSqlFragment("主表别名", schema.getTableAlias());
        if (CollectionUtil.isNotEmpty(schema.getFields())) {
            for (AiFieldSchema field : schema.getFields().values()) {
                rejectDangerousSqlFragment("字段列：" + field.getField(), field.getColumn());
            }
        }
        if (CollectionUtil.isNotEmpty(schema.getDimensions())) {
            for (AiFieldSchema field : schema.getDimensions().values()) {
                rejectDangerousSqlFragment("维度列：" + field.getField(), field.getColumn());
            }
        }
        if (CollectionUtil.isNotEmpty(schema.getMetrics())) {
            for (AiMetricSchema metric : schema.getMetrics().values()) {
                rejectDangerousSqlFragment("指标表达式：" + metric.getMetric(), metric.getExpression());
            }
        }
        if (CollectionUtil.isNotEmpty(schema.getJoins())) {
            for (AiJoinSchema join : schema.getJoins().values()) {
                rejectDangerousSqlFragment("join表：" + join.getAlias(), join.getTableName());
                rejectDangerousSqlFragment("join别名：" + join.getAlias(), join.getAlias());
                rejectDangerousSqlFragment("join类型：" + join.getAlias(), join.getJoinType());
                rejectDangerousSqlFragment("join条件：" + join.getAlias(), join.getOnExpression());
            }
        }
    }

    private void rejectDangerousSqlFragment(String name, String fragment) {
        if (StrUtil.isBlank(fragment)) {
            return;
        }
        String normalized = fragment.toLowerCase();
        if (normalized.contains(";")
                || normalized.contains("--")
                || normalized.contains("/*")
                || normalized.contains("*/")
                || normalized.contains("#")
                || normalized.matches(".*\\b(insert|update|delete|drop|truncate|alter|create|replace|grant|revoke|call|exec)\\b.*")) {
            throw new BusinessException("AI查询配置包含高风险SQL片段，已拒绝执行：" + name);
        }
    }

    private void rejectSensitiveFields(AiQueryPlanDTO plan, AiDomainSchema schema) {
        if (CollectionUtil.isNotEmpty(plan.getSelectFields())) {
            for (String field : plan.getSelectFields()) {
                rejectSensitiveField(field, schema);
            }
        }
        if (CollectionUtil.isNotEmpty(plan.getDimensions())) {
            for (String field : plan.getDimensions()) {
                rejectSensitiveField(field, schema);
            }
        }
        if (CollectionUtil.isNotEmpty(plan.getFilters())) {
            for (AiQueryFilterDTO filter : plan.getFilters()) {
                rejectSensitiveField(filter.getField(), schema);
            }
        }
        if (CollectionUtil.isNotEmpty(plan.getOrderBy())) {
            for (AiQueryOrderDTO order : plan.getOrderBy()) {
                rejectSensitiveField(order.getField(), schema);
            }
        }
    }

    private void rejectSensitiveField(String field, AiDomainSchema schema) {
        if (StrUtil.isBlank(field)) {
            return;
        }
        AiFieldSchema fieldSchema = schema.getFields().get(field);
        if (fieldSchema == null) {
            fieldSchema = schema.getDimensions().get(field);
        }
        String label = fieldSchema == null ? null : fieldSchema.getLabel();
        String column = fieldSchema == null ? null : fieldSchema.getColumn();
        if (matchesAny(field, aiProperties.getExcludeFields())
                || matchesAny(label, aiProperties.getExcludeFields())
                || matchesAny(column, aiProperties.getExcludeFields())) {
            throw new BusinessException("字段涉及敏感信息，禁止AI查询：" + field);
        }
    }

    private void normalizeLimit(AiQueryPlanDTO plan) {
        AiProperties.Risk risk = aiProperties.getRisk();
        int maxLimit = QUERY_TYPE_DETAIL.equals(plan.getQueryType())
                ? valueOrDefault(risk.getDetailMaxLimit(), 200)
                : valueOrDefault(risk.getAggregateMaxLimit(), 500);
        if (plan.getLimit() == null || plan.getLimit() <= 0) {
            plan.setLimit(Math.min(maxLimit, 100));
            return;
        }
        if (plan.getLimit() > maxLimit) {
            log.info("AI查询limit超过风险阈值，已截断，domain={}，queryType={}，originalLimit={}，maxLimit={}",
                    plan.getDomain(), plan.getQueryType(), plan.getLimit(), maxLimit);
            plan.setLimit(maxLimit);
        }
    }

    private void checkNoFilterDetail(AiQueryPlanDTO plan) {
        if (!QUERY_TYPE_DETAIL.equals(plan.getQueryType()) || CollectionUtil.isNotEmpty(plan.getFilters())) {
            return;
        }
        AiProperties.Risk risk = aiProperties.getRisk();
        if (Boolean.TRUE.equals(risk.getRejectNoFilterDetail())) {
            throw new BusinessException("明细查询需要提供过滤条件，请补充项目、时间、编号等查询范围");
        }
        int maxLimit = valueOrDefault(risk.getNoFilterDetailMaxLimit(), 50);
        if (plan.getLimit() == null || plan.getLimit() > maxLimit) {
            log.info("AI无过滤明细查询已收紧limit，domain={}，originalLimit={}，maxLimit={}",
                    plan.getDomain(), plan.getLimit(), maxLimit);
            plan.setLimit(maxLimit);
        }
    }

    private void checkFilterValues(AiQueryPlanDTO plan) {
        if (CollectionUtil.isEmpty(plan.getFilters())) {
            return;
        }
        AiProperties.Risk risk = aiProperties.getRisk();
        int maxInValues = valueOrDefault(risk.getMaxInValues(), 100);
        int maxLikeLength = valueOrDefault(risk.getMaxLikeLength(), 50);
        int maxLastDays = valueOrDefault(risk.getMaxLastDays(), 366);
        int maxLastMonths = valueOrDefault(risk.getMaxLastMonths(), 24);

        for (AiQueryFilterDTO filter : plan.getFilters()) {
            if (filter == null || StrUtil.isBlank(filter.getOp())) {
                continue;
            }
            String op = filter.getOp();
            if ("in".equals(op)) {
                List<Object> values = toList(filter.getValue());
                if (values.size() > maxInValues) {
                    throw new BusinessException("过滤条件数量过多，请缩小查询范围：" + filter.getField());
                }
            } else if ("like".equals(op)) {
                String value = filter.getValue() == null ? "" : String.valueOf(filter.getValue()).trim();
                if (StrUtil.isBlank(value)) {
                    throw new BusinessException("模糊查询关键词不能为空：" + filter.getField());
                }
                if (value.length() > maxLikeLength) {
                    throw new BusinessException("模糊查询关键词过长，请缩短后再试：" + filter.getField());
                }
                filter.setValue(value);
            } else if ("last_days".equals(op)) {
                int days = parsePositiveInt(filter.getValue(), "最近天数必须是正整数：" + filter.getField());
                if (days > maxLastDays) {
                    throw new BusinessException("查询时间范围过大，请缩小到 " + maxLastDays + " 天以内");
                }
                filter.setValue(days);
            } else if ("last_months".equals(op)) {
                int months = parsePositiveInt(filter.getValue(), "最近月数必须是正整数：" + filter.getField());
                if (months > maxLastMonths) {
                    throw new BusinessException("查询时间范围过大，请缩小到 " + maxLastMonths + " 个月以内");
                }
                filter.setValue(months);
            }
        }
    }

    private boolean matchesAny(String value, List<String> patterns) {
        if (StrUtil.isBlank(value) || CollectionUtil.isEmpty(patterns)) {
            return false;
        }
        String normalized = value.replace("`", "");
        for (String pattern : patterns) {
            if (match(normalized, pattern)) {
                return true;
            }
        }
        return false;
    }

    private boolean match(String value, String pattern) {
        if (StrUtil.isBlank(pattern)) {
            return false;
        }
        String regex = pattern.replace(".", "\\.").replace("*", ".*");
        return value.matches("(?i).*" + regex + ".*");
    }

    private int valueOrDefault(Integer value, int defaultValue) {
        return value == null || value <= 0 ? defaultValue : value;
    }

    private int parsePositiveInt(Object value, String message) {
        if (value == null) {
            throw new BusinessException(message);
        }
        try {
            int number = Integer.parseInt(String.valueOf(value));
            if (number <= 0) {
                throw new BusinessException(message);
            }
            return number;
        } catch (NumberFormatException e) {
            throw new BusinessException(message);
        }
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
