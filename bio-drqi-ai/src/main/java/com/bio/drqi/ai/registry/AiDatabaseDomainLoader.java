package com.bio.drqi.ai.registry;

import cn.hutool.core.util.StrUtil;
import com.bio.drqi.ai.config.AiProperties;
import com.bio.drqi.ai.schema.AiDomainSchema;
import com.bio.drqi.ai.schema.AiFieldSchema;
import com.bio.drqi.ai.schema.AiMetricSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
public class AiDatabaseDomainLoader {

    @Resource
    private ObjectProvider<DataSource> dataSourceProvider;

    @Resource
    private ObjectProvider<JdbcTemplate> jdbcTemplateProvider;

    @Resource
    private AiDomainRegistry aiDomainRegistry;

    @Resource
    private AiProperties aiProperties;

    @PostConstruct
    public void load() {
        DataSource dataSource = dataSourceProvider.getIfAvailable();
        if (dataSource == null) {
            log.warn("AI自动注册数据库业务域跳过：未创建DataSource");
            return;
        }
        try (Connection connection = dataSource.getConnection()) {
            String catalog = connection.getCatalog();
            DatabaseMetaData metaData = connection.getMetaData();
            int registeredCount = 0;
            try (ResultSet tables = metaData.getTables(catalog, null, "%", new String[]{"TABLE"})) {
                while (tables.next()) {
                    String tableName = tables.getString("TABLE_NAME");
                    if (!allowTable(tableName)) {
                        continue;
                    }
                    try {
                        String tableComment = StrUtil.blankToDefault(tables.getString("REMARKS"), tableName);
                        if (registerTableDomain(metaData, catalog, tableName, tableComment)) {
                            registeredCount++;
                        }
                    } catch (Exception e) {
                        log.warn("AI自动注册数据库业务域跳过表：{}，原因：{}", tableName, e.getMessage(), e);
                    }
                }
            }
            log.info("AI自动注册数据库业务域完成，catalog={}，registeredCount={}", catalog, registeredCount);
        } catch (Exception e) {
            log.warn("AI自动注册数据库业务域失败：{}", e.getMessage(), e);
        }
    }

    private boolean registerTableDomain(DatabaseMetaData metaData, String catalog, String tableName, String tableComment) throws Exception {
        String domain = tableName;
        if (aiDomainRegistry.contains(domain)) {
            return false;
        }

        AiDomainSchema schema = new AiDomainSchema();
        schema.setDomain(domain);
        schema.setName(tableComment);
        schema.setTableName(wrapColumn(tableName));

        Map<String, AiFieldSchema> fields = new LinkedHashMap<>();
        Map<String, AiFieldSchema> dimensions = new LinkedHashMap<>();
        try (ResultSet columns = metaData.getColumns(catalog, null, tableName, "%")) {
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                if (!allowField(columnName)) {
                    continue;
                }
                String columnComment = StrUtil.blankToDefault(columns.getString("REMARKS"), columnName);
                String fieldName = toCamel(columnName);
                String type = toAiType(columns.getInt("DATA_TYPE"));
                AiFieldSchema field = field(fieldName, columnComment, wrapColumn(columnName), type);
                applyDict(tableName, columnName, fieldName, field);
                fields.put(fieldName, field);

                if (isDimensionType(type)) {
                    dimensions.put(fieldName, field);
                }
                if ("date".equals(type)) {
                    dimensions.put(fieldName + "Month", field(fieldName + "Month", columnComment + "月份",
                            "date_format(" + wrapColumn(columnName) + ", '%Y-%m')", "date"));
                    dimensions.put(fieldName + "Day", field(fieldName + "Day", columnComment + "日期",
                            "date_format(" + wrapColumn(columnName) + ", '%Y-%m-%d')", "date"));
                }
            }
        }
        if (fields.isEmpty()) {
            return false;
        }

        schema.setFields(fields);
        schema.setDimensions(dimensions);
        schema.setMetrics(defaultMetrics());
        aiDomainRegistry.register(schema);
        return true;
    }

    private Map<String, AiMetricSchema> defaultMetrics() {
        Map<String, AiMetricSchema> metrics = new LinkedHashMap<>();
        AiMetricSchema totalCount = new AiMetricSchema();
        totalCount.setMetric("totalCount");
        totalCount.setLabel("总数量");
        totalCount.setType("sql");
        totalCount.setExpression("count(*)");
        metrics.put(totalCount.getMetric(), totalCount);
        return metrics;
    }

    private AiFieldSchema field(String field, String label, String column, String type) {
        AiFieldSchema schema = new AiFieldSchema();
        schema.setField(field);
        schema.setLabel(label);
        schema.setColumn(column);
        schema.setType(type);
        return schema;
    }

    private void applyDict(String tableName, String columnName, String fieldName, AiFieldSchema field) {
        AiProperties.DictField dictField = findDictField(tableName, columnName, fieldName);
        if (dictField == null || StrUtil.isBlank(dictField.getDictType())) {
            return;
        }
        Map<String, String> enumValues = loadDictValues(dictField);
        if (enumValues.isEmpty()) {
            return;
        }
        field.setType("enum");
        field.setEnumValues(enumValues);
    }

    private AiProperties.DictField findDictField(String tableName, String columnName, String fieldName) {
        if (aiProperties == null || aiProperties.getDictFields() == null) {
            return null;
        }
        for (AiProperties.DictField dictField : aiProperties.getDictFields()) {
            if (dictField == null) {
                continue;
            }
            boolean tableMatched = "*".equals(dictField.getTableName()) || tableName.equalsIgnoreCase(dictField.getTableName());
            boolean fieldMatched = columnName.equalsIgnoreCase(dictField.getFieldName()) || fieldName.equalsIgnoreCase(dictField.getFieldName());
            if (tableMatched && fieldMatched) {
                return dictField;
            }
        }
        return null;
    }

    private boolean allowTable(String tableName) {
        if (matchesAny(tableName, aiProperties.getExcludeTables())) {
            return false;
        }
        return aiProperties.getIncludeTables() == null || aiProperties.getIncludeTables().isEmpty()
                || matchesAny(tableName, aiProperties.getIncludeTables());
    }

    private boolean allowField(String columnName) {
        return !matchesAny(columnName, aiProperties.getExcludeFields());
    }

    private boolean matchesAny(String value, List<String> patterns) {
        if (patterns == null || patterns.isEmpty()) {
            return false;
        }
        for (String pattern : patterns) {
            if (match(value, pattern)) {
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
        return value.matches("(?i)" + regex);
    }

    private Map<String, String> loadDictValues(AiProperties.DictField dictField) {
        if ("bms_dict".equalsIgnoreCase(dictField.getDictSource())) {
            return loadBmsDictValues(dictField.getDictType());
        }
        return loadBioDictValues(dictField.getDictType());
    }

    private Map<String, String> loadBioDictValues(String dictType) {
        Map<String, String> map = new LinkedHashMap<>();
        JdbcTemplate jdbcTemplate = jdbcTemplateProvider.getIfAvailable();
        if (jdbcTemplate == null) {
            return map;
        }
        String sql = "select dict_value_code, dict_value_name from bio_dict where dict_type = ? and (dict_status is null or dict_status = '1') order by id";
        List<Map<String, Object>> rows;
        try {
            rows = jdbcTemplate.queryForList(sql, dictType);
        } catch (Exception e) {
            log.warn("AI加载bio_dict字典失败，dictType={}，原因：{}", dictType, e.getMessage());
            return map;
        }
        for (Map<String, Object> row : rows) {
            Object code = row.get("dict_value_code");
            Object name = row.get("dict_value_name");
            if (code != null && name != null) {
                map.put(String.valueOf(code), String.valueOf(name));
            }
        }
        return map;
    }

    private Map<String, String> loadBmsDictValues(String dictType) {
        Map<String, String> map = new LinkedHashMap<>();
        JdbcTemplate jdbcTemplate = jdbcTemplateProvider.getIfAvailable();
        if (jdbcTemplate == null) {
            return map;
        }
        String sql = "select dict_value_code, dict_value_name from bms_dict where dict_type_code = ? order by id";
        List<Map<String, Object>> rows;
        try {
            rows = jdbcTemplate.queryForList(sql, dictType);
        } catch (Exception e) {
            log.warn("AI加载bms_dict字典失败，dictType={}，原因：{}", dictType, e.getMessage());
            return map;
        }
        for (Map<String, Object> row : rows) {
            Object code = row.get("dict_value_code");
            Object name = row.get("dict_value_name");
            if (code != null && name != null) {
                map.put(String.valueOf(code), String.valueOf(name));
            }
        }
        return map;
    }

    private boolean isDimensionType(String type) {
        return "string".equals(type) || "date".equals(type) || "enum".equals(type);
    }

    private String toAiType(int sqlType) {
        switch (sqlType) {
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
            case Types.TIMESTAMP_WITH_TIMEZONE:
                return "date";
            case Types.INTEGER:
            case Types.BIGINT:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.NUMERIC:
            case Types.REAL:
            case Types.SMALLINT:
            case Types.TINYINT:
                return "number";
            default:
                return "string";
        }
    }

    private String toCamel(String value) {
        StringBuilder builder = new StringBuilder();
        boolean upper = false;
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (ch == '_') {
                upper = true;
                continue;
            }
            if (upper) {
                builder.append(Character.toUpperCase(ch));
                upper = false;
            } else {
                builder.append(Character.toLowerCase(ch));
            }
        }
        return builder.toString();
    }

    private String wrapColumn(String column) {
        return "`" + column + "`";
    }
}
