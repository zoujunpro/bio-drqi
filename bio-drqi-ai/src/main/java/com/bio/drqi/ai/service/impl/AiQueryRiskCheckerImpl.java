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
import com.bio.drqi.ai.service.AiQueryRiskChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
        rejectSensitiveFields(plan, schema);
        normalizeLimit(plan);
        checkNoFilterDetail(plan);
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
}
