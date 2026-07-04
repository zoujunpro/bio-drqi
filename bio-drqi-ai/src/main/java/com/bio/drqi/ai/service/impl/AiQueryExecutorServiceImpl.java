package com.bio.drqi.ai.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.config.AiProperties;
import com.bio.drqi.ai.dto.plan.AiQueryFilterDTO;
import com.bio.drqi.ai.dto.plan.AiQueryOrderDTO;
import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.dto.rsp.AiAnalysisRspDTO;
import com.bio.drqi.ai.dto.rsp.AiChartDTO;
import com.bio.drqi.ai.dto.rsp.AiTableColumnDTO;
import com.bio.drqi.ai.dto.rsp.AiTableDTO;
import com.bio.drqi.ai.schema.AiDomainSchema;
import com.bio.drqi.ai.schema.AiFieldSchema;
import com.bio.drqi.ai.schema.AiJoinSchema;
import com.bio.drqi.ai.schema.AiMetricSchema;
import com.bio.drqi.ai.service.AiQueryExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Primary
@Slf4j
public class AiQueryExecutorServiceImpl implements AiQueryExecutorService {

    private static final String QUERY_TYPE_DETAIL = "detail";

    @Resource
    private ObjectProvider<JdbcTemplate> jdbcTemplateProvider;

    @Resource
    private AiProperties aiProperties;

    @Override
    public AiAnalysisRspDTO execute(AiQueryPlanDTO plan, AiDomainSchema schema) {
        JdbcTemplate jdbcTemplate = jdbcTemplateProvider.getIfAvailable();
        if (jdbcTemplate == null) {
            throw new BusinessException("当前AI服务未启用数据库查询：未创建JdbcTemplate，请检查spring.datasource配置和MySQL驱动依赖");
        }
        applyQueryTimeout(jdbcTemplate);
        SqlBuildResult sqlBuildResult = buildSql(plan, schema);
        log.info("AI查询SQL生成完成，domain={}，queryType={}，sql={}，params={}",
                plan.getDomain(), plan.getQueryType(), sqlBuildResult.getSql(), sqlBuildResult.getParams());
        long queryStartTime = System.currentTimeMillis();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlBuildResult.getSql(), sqlBuildResult.getParams().toArray());
        log.info("AI数据库查询完成，cost={}ms，domain={}，rowCount={}",
                System.currentTimeMillis() - queryStartTime, plan.getDomain(), rows.size());
        rows = translateEnumValues(rows, plan, schema);

        AiTableDTO table = buildTable(plan, schema, rows);
        AiAnalysisRspDTO rspDTO = new AiAnalysisRspDTO();
        rspDTO.setExecutedSql(sqlBuildResult.getSql());
        rspDTO.setExecutedSqlParams(JSONUtil.toJsonStr(sqlBuildResult.getParams()));
        rspDTO.getTables().add(table);

        AiChartDTO chart = buildChart(plan, schema, rows);
        if (chart != null) {
            rspDTO.getCharts().add(chart);
        }
        return rspDTO;
    }

    private void applyQueryTimeout(JdbcTemplate jdbcTemplate) {
        Integer seconds = aiProperties.getRisk() == null ? null : aiProperties.getRisk().getQueryTimeoutSeconds();
        if (seconds != null && seconds > 0) {
            jdbcTemplate.setQueryTimeout(seconds);
        }
    }

    private SqlBuildResult buildSql(AiQueryPlanDTO plan, AiDomainSchema schema) {
        if (QUERY_TYPE_DETAIL.equals(plan.getQueryType())) {
            return buildDetailSql(plan, schema);
        }
        return buildAggregateSql(plan, schema);
    }

    private SqlBuildResult buildAggregateSql(AiQueryPlanDTO plan, AiDomainSchema schema) {
        SqlBuildResult result = new SqlBuildResult();
        StringBuilder sql = new StringBuilder();
        List<String> selectItems = new ArrayList<>();
        List<String> groupItems = new ArrayList<>();

        if (CollectionUtil.isNotEmpty(plan.getDimensions())) {
            for (String dimension : plan.getDimensions()) {
                AiFieldSchema fieldSchema = schema.getDimensions().get(dimension);
                selectItems.add(fieldSchema.getColumn() + " as " + wrapAlias(dimension));
                groupItems.add(fieldSchema.getColumn());
            }
        }
        for (String metric : plan.getMetrics()) {
            AiMetricSchema metricSchema = schema.getMetrics().get(metric);
            selectItems.add(metricSchema.getExpression() + " as " + wrapAlias(metric));
        }

        sql.append("select ").append(String.join(", ", selectItems));
        sql.append(buildFromSql(plan, schema));
        sql.append(" where 1 = 1");
        appendFilters(sql, result.getParams(), plan, schema);
        if (CollectionUtil.isNotEmpty(groupItems)) {
            sql.append(" group by ").append(String.join(", ", groupItems));
        }
        appendOrderBy(sql, plan);
        sql.append(" limit ?");
        result.getParams().add(plan.getLimit());
        result.setSql(sql.toString());
        return result;
    }

    private SqlBuildResult buildDetailSql(AiQueryPlanDTO plan, AiDomainSchema schema) {
        SqlBuildResult result = new SqlBuildResult();
        StringBuilder sql = new StringBuilder();
        List<String> selectItems = new ArrayList<>();
        for (String field : plan.getSelectFields()) {
            AiFieldSchema fieldSchema = schema.getFields().get(field);
            selectItems.add(fieldSchema.getColumn() + " as " + wrapAlias(field));
        }

        sql.append("select ").append(String.join(", ", selectItems));
        sql.append(buildFromSql(plan, schema));
        sql.append(" where 1 = 1");
        appendFilters(sql, result.getParams(), plan, schema);
        appendOrderBy(sql, plan);
        sql.append(" limit ?");
        result.getParams().add(plan.getLimit());
        result.setSql(sql.toString());
        return result;
    }

    private String buildFromSql(AiQueryPlanDTO plan, AiDomainSchema schema) {
        StringBuilder fromSql = new StringBuilder();
        fromSql.append(" from ").append(schema.getTableName());
        if (StrUtil.isNotBlank(schema.getTableAlias())) {
            fromSql.append(" ").append(schema.getTableAlias());
        }

        Set<String> requiredJoinAliases = collectRequiredJoinAliases(plan, schema);
        for (String alias : requiredJoinAliases) {
            AiJoinSchema joinSchema = schema.getJoins().get(alias);
            if (joinSchema == null) {
                throw new BusinessException("未配置的join别名：" + alias);
            }
            fromSql.append(" ").append(joinSchema.getJoinType())
                    .append(" ").append(joinSchema.getTableName())
                    .append(" ").append(joinSchema.getAlias())
                    .append(" on ").append(joinSchema.getOnExpression());
        }
        return fromSql.toString();
    }

    private Set<String> collectRequiredJoinAliases(AiQueryPlanDTO plan, AiDomainSchema schema) {
        Set<String> aliases = new HashSet<>();
        if (QUERY_TYPE_DETAIL.equals(plan.getQueryType())) {
            collectFieldJoinAliases(aliases, schema.getFields(), plan.getSelectFields());
        } else {
            collectFieldJoinAliases(aliases, schema.getDimensions(), plan.getDimensions());
        }
        if (CollectionUtil.isNotEmpty(plan.getFilters())) {
            for (AiQueryFilterDTO filter : plan.getFilters()) {
                AiFieldSchema fieldSchema = schema.getFields().get(filter.getField());
                if (fieldSchema != null) {
                    aliases.addAll(fieldSchema.getRequiredJoinAliases());
                }
            }
        }
        if (CollectionUtil.isNotEmpty(plan.getOrderBy())) {
            for (AiQueryOrderDTO order : plan.getOrderBy()) {
                AiFieldSchema fieldSchema = schema.getFields().get(order.getField());
                if (fieldSchema == null) {
                    fieldSchema = schema.getDimensions().get(order.getField());
                }
                if (fieldSchema != null) {
                    aliases.addAll(fieldSchema.getRequiredJoinAliases());
                }
            }
        }
        return aliases;
    }

    private void collectFieldJoinAliases(Set<String> aliases, Map<String, AiFieldSchema> fieldMap, List<String> fieldNames) {
        if (CollectionUtil.isEmpty(fieldNames)) {
            return;
        }
        for (String fieldName : fieldNames) {
            AiFieldSchema fieldSchema = fieldMap.get(fieldName);
            if (fieldSchema != null) {
                aliases.addAll(fieldSchema.getRequiredJoinAliases());
            }
        }
    }

    private void appendFilters(StringBuilder sql, List<Object> params, AiQueryPlanDTO plan, AiDomainSchema schema) {
        if (CollectionUtil.isEmpty(plan.getFilters())) {
            return;
        }
        for (AiQueryFilterDTO filter : plan.getFilters()) {
            AiFieldSchema fieldSchema = schema.getFields().get(filter.getField());
            String column = fieldSchema.getColumn();
            String op = filter.getOp();
            if ("eq".equals(op)) {
                sql.append(" and ").append(column).append(" = ?");
                params.add(filter.getValue());
            } else if ("in".equals(op)) {
                List<Object> values = toList(filter.getValue());
                if (CollectionUtil.isEmpty(values)) {
                    throw new BusinessException("in过滤值不能为空：" + filter.getField());
                }
                sql.append(" and ").append(column).append(" in (").append(placeholders(values.size())).append(")");
                params.addAll(values);
            } else if ("like".equals(op)) {
                sql.append(" and ").append(column).append(" like ?");
                params.add("%" + filter.getValue() + "%");
            } else if ("between".equals(op)) {
                List<Object> values = toList(filter.getValue());
                if (values.size() != 2) {
                    throw new BusinessException("between过滤值必须包含开始和结束：" + filter.getField());
                }
                sql.append(" and ").append(column).append(" between ? and ?");
                params.add(values.get(0));
                params.add(values.get(1));
            } else if ("gte".equals(op)) {
                sql.append(" and ").append(column).append(" >= ?");
                params.add(filter.getValue());
            } else if ("lte".equals(op)) {
                sql.append(" and ").append(column).append(" <= ?");
                params.add(filter.getValue());
            } else if ("last_days".equals(op)) {
                sql.append(" and ").append(column).append(" >= date_sub(curdate(), interval ? day)");
                params.add(filter.getValue());
            } else if ("last_months".equals(op)) {
                sql.append(" and ").append(column).append(" >= date_sub(curdate(), interval ? month)");
                params.add(filter.getValue());
            }
        }
    }

    private void appendOrderBy(StringBuilder sql, AiQueryPlanDTO plan) {
        if (CollectionUtil.isEmpty(plan.getOrderBy())) {
            return;
        }
        List<String> orderItems = new ArrayList<>();
        for (AiQueryOrderDTO order : plan.getOrderBy()) {
            orderItems.add(wrapAlias(order.getField()) + " " + order.getDirection());
        }
        sql.append(" order by ").append(String.join(", ", orderItems));
    }

    private AiTableDTO buildTable(AiQueryPlanDTO plan, AiDomainSchema schema, List<Map<String, Object>> rows) {
        AiTableDTO table = new AiTableDTO();
        table.setTitle(schema.getName());
        if (QUERY_TYPE_DETAIL.equals(plan.getQueryType())) {
            for (String field : plan.getSelectFields()) {
                AiFieldSchema fieldSchema = schema.getFields().get(field);
                table.getColumns().add(column(fieldSchema.getLabel(), field));
            }
            table.setData(rows);
            return table;
        }
        if (CollectionUtil.isNotEmpty(plan.getDimensions())) {
            for (String dimension : plan.getDimensions()) {
                AiFieldSchema fieldSchema = schema.getDimensions().get(dimension);
                table.getColumns().add(column(fieldSchema.getLabel(), dimension));
            }
        }
        for (String metric : plan.getMetrics()) {
            AiMetricSchema metricSchema = schema.getMetrics().get(metric);
            table.getColumns().add(column(metricSchema.getLabel(), metric));
        }
        table.setData(rows);
        return table;
    }

    private AiChartDTO buildChart(AiQueryPlanDTO plan, AiDomainSchema schema, List<Map<String, Object>> rows) {
        if (QUERY_TYPE_DETAIL.equals(plan.getQueryType())) {
            return null;
        }
        if (CollectionUtil.isEmpty(plan.getDimensions()) || CollectionUtil.isEmpty(rows)) {
            return null;
        }
        String chartType = StrUtil.blankToDefault(plan.getChartType(), "table");
        if ("table".equals(chartType)) {
            return null;
        }
        if ("auto".equals(chartType)) {
            chartType = rows.size() <= 8 ? "bar" : "line";
        }

        String firstDimension = plan.getDimensions().get(0);
        String firstMetric = plan.getMetrics().get(0);
        AiChartDTO chart = new AiChartDTO();
        chart.setType(chartType);
        chart.setTitle(schema.getName());
        if ("pie".equals(chartType)) {
            fillPieOption(chart, rows, firstDimension, firstMetric);
        } else {
            fillAxisOption(chart, rows, firstDimension, plan.getMetrics(), chartType);
        }
        return chart;
    }

    private void fillAxisOption(AiChartDTO chart, List<Map<String, Object>> rows, String xField, List<String> metrics, String chartType) {
        List<Object> xAxisData = new ArrayList<>();
        List<Map<String, Object>> series = new ArrayList<>();
        for (String metric : metrics) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", metric);
            item.put("type", chartType);
            item.put("data", new ArrayList<>());
            series.add(item);
        }
        for (Map<String, Object> row : rows) {
            xAxisData.add(row.get(xField));
            for (Map<String, Object> item : series) {
                List<Object> data = (List<Object>) item.get("data");
                data.add(row.get(item.get("name")));
            }
        }
        chart.getOption().put("xAxis", axis("category", xAxisData));
        chart.getOption().put("yAxis", axis("value", null));
        chart.getOption().put("series", series);
    }

    private void fillPieOption(AiChartDTO chart, List<Map<String, Object>> rows, String nameField, String valueField) {
        List<Map<String, Object>> data = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("name", row.get(nameField));
            item.put("value", row.get(valueField));
            data.add(item);
        }
        Map<String, Object> series = new LinkedHashMap<>();
        series.put("type", "pie");
        series.put("data", data);
        List<Map<String, Object>> seriesList = new ArrayList<>();
        seriesList.add(series);
        chart.getOption().put("series", seriesList);
    }

    private List<Map<String, Object>> translateEnumValues(List<Map<String, Object>> rows, AiQueryPlanDTO plan, AiDomainSchema schema) {
        List<String> fields = QUERY_TYPE_DETAIL.equals(plan.getQueryType()) ? plan.getSelectFields() : plan.getDimensions();
        if (CollectionUtil.isEmpty(fields)) {
            return rows;
        }
        for (String field : fields) {
            AiFieldSchema fieldSchema = QUERY_TYPE_DETAIL.equals(plan.getQueryType()) ? schema.getFields().get(field) : schema.getDimensions().get(field);
            if (fieldSchema == null || CollectionUtil.isEmpty(fieldSchema.getEnumValues())) {
                continue;
            }
            for (Map<String, Object> row : rows) {
                Object value = row.get(field);
                if (value != null && fieldSchema.getEnumValues().containsKey(String.valueOf(value))) {
                    row.put(field, fieldSchema.getEnumValues().get(String.valueOf(value)));
                }
            }
        }
        return rows;
    }

    private AiTableColumnDTO column(String title, String dataIndex) {
        AiTableColumnDTO column = new AiTableColumnDTO();
        column.setTitle(title);
        column.setDataIndex(dataIndex);
        return column;
    }

    private Map<String, Object> axis(String type, List<Object> data) {
        Map<String, Object> axis = new LinkedHashMap<>();
        axis.put("type", type);
        if (data != null) {
            axis.put("data", data);
        }
        return axis;
    }

    private String wrapAlias(String alias) {
        return "`" + alias + "`";
    }

    private String placeholders(int size) {
        List<String> placeholders = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            placeholders.add("?");
        }
        return String.join(", ", placeholders);
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

    private static class SqlBuildResult {
        private String sql;
        private final List<Object> params = new ArrayList<>();

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }

        public List<Object> getParams() {
            return params;
        }
    }
}
