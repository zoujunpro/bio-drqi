package com.bio.drqi.es.service.impl;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bio.drqi.es.dto.req.TableSyncReqDTO;
import com.bio.drqi.es.service.EsCommonService;
import com.bio.drqi.es.service.EsSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "sync.es", name = "enabled", havingValue = "true")
public class EsSyncServiceImpl implements EsSyncService {

    private static final String DOMAIN_PACKAGE = "com.bio.drqi.domain";
    private static final String DOMAIN_PATTERN = "classpath*:com/bio/drqi/domain/*.class";
    private static final String idField="id";

    private final JdbcTemplate jdbcTemplate;
    private final EsCommonService esCommonService;
    private final AtomicReference<List<Class<?>>> domainClassCache = new AtomicReference<>();

    public EsSyncServiceImpl(JdbcTemplate jdbcTemplate, EsCommonService esCommonService) {
        this.jdbcTemplate = jdbcTemplate;
        this.esCommonService = esCommonService;
    }

    @Override
    public void syncTable(TableSyncReqDTO tableSyncReqDTO) {

        String[] schemaTable = splitSchemaTable(tableSyncReqDTO.getTableName());
        String schema = schemaTable[0];
        String table = schemaTable[1];
        String index = table.toLowerCase(Locale.ROOT);
        Class<?> entityClass = resolveEntityClass(table);
        if (entityClass == null) {
            throw new IllegalStateException("在包 " + DOMAIN_PACKAGE + " 下找不到表对应实体: " + table);
        }
        Map<String, Object> mapping = buildMappingByEntity(entityClass);
        int fieldCount = ((Map<?, ?>) mapping.get("properties")).size();
        if (fieldCount == 0) {
            throw new IllegalStateException("实体无可用字段: " + entityClass.getName());
        }
        esCommonService.recreateIndex(index, mapping);

        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM " + schema + "." + table);
        esCommonService.saveBatch(index, idField, rows);


    }

    private String[] splitSchemaTable(String value) {
        int idx = value.indexOf('.');
        if (idx > 0 && idx < value.length() - 1) {
            return new String[]{value.substring(0, idx), value.substring(idx + 1)};
        }
        String schema = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
        if (schema == null || schema.trim().isEmpty()) {
            throw new IllegalStateException("无法获取当前数据库名，请传 database.table");
        }
        return new String[]{schema, value};
    }

    private Map<String, Object> buildMappingByEntity(Class<?> entityClass) {
        Map<String, Object> properties = new LinkedHashMap<>();
        for (Field field : getAllFields(entityClass)) {
            if (shouldIgnoreField(field)) {
                continue;
            }
            String columnName = resolveColumnName(field);
            properties.put(columnName, toEsFieldMapping(field.getType()));
        }
        Map<String, Object> mapping = new LinkedHashMap<>();
        mapping.put("properties", properties);
        return mapping;
    }

    private Map<String, Object> toEsFieldMapping(Class<?> javaType) {
        Map<String, Object> field = new LinkedHashMap<>();
        if (String.class.equals(javaType)) {
            field.put("type", "keyword");
            field.put("ignore_above", 256);
            return field;
        }
        if (Integer.class.equals(javaType) || int.class.equals(javaType)
                || Short.class.equals(javaType) || short.class.equals(javaType)
                || Byte.class.equals(javaType) || byte.class.equals(javaType)) {
            field.put("type", "integer");
            return field;
        }
        if (Long.class.equals(javaType) || long.class.equals(javaType)) {
            field.put("type", "long");
            return field;
        }
        if (Float.class.equals(javaType) || float.class.equals(javaType)) {
            field.put("type", "float");
            return field;
        }
        if (Double.class.equals(javaType) || double.class.equals(javaType)
                || BigDecimal.class.equals(javaType)) {
            field.put("type", "double");
            return field;
        }
        if (Boolean.class.equals(javaType) || boolean.class.equals(javaType)) {
            field.put("type", "boolean");
            return field;
        }
        if (java.util.Date.class.equals(javaType) || java.sql.Date.class.equals(javaType)
                || LocalDateTime.class.equals(javaType) || LocalDate.class.equals(javaType)) {
            field.put("type", "date");
            field.put("format", "strict_date_optional_time||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis");
            return field;
        }
        field.put("type", "keyword");
        field.put("ignore_above", 256);
        return field;
    }

    private Class<?> resolveEntityClass(String table) {
        List<Class<?>> matched = new ArrayList<>();
        for (Class<?> domainClass : listDomainClasses()) {
            TableName tableName = domainClass.getAnnotation(TableName.class);
            if (tableName == null) {
                continue;
            }
            String value = tableName.value();
            if (value == null || value.trim().isEmpty()) {
                continue;
            }
            if (table.equalsIgnoreCase(value.trim())) {
                matched.add(domainClass);
            }
        }
        if (matched.isEmpty()) {
            return null;
        }
        if (matched.size() > 1) {
            throw new IllegalStateException("匹配到多个实体，请确认表名: " + table + "，匹配实体=" + matched);
        }
        return matched.get(0);
    }

    private List<Class<?>> listDomainClasses() {
        List<Class<?>> cached = domainClassCache.get();
        if (cached != null) {
            return cached;
        }
        synchronized (domainClassCache) {
            cached = domainClassCache.get();
            if (cached != null) {
                return cached;
            }
            List<Class<?>> classes = new ArrayList<>();
            try {
                PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
                CachingMetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);
                Resource[] resources = resolver.getResources(DOMAIN_PATTERN);
                for (Resource resource : resources) {
                    String className = metadataReaderFactory.getMetadataReader(resource).getClassMetadata().getClassName();
                    if (!className.startsWith(DOMAIN_PACKAGE)) {
                        continue;
                    }
                    Class<?> clazz = Class.forName(className);
                    classes.add(clazz);
                }
            } catch (Exception e) {
                throw new IllegalStateException("扫描 domain 实体失败", e);
            }
            domainClassCache.set(classes);
            return classes;
        }
    }

    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null && !Object.class.equals(current)) {
            fields.addAll(Arrays.asList(current.getDeclaredFields()));
            current = current.getSuperclass();
        }
        return fields;
    }

    private boolean shouldIgnoreField(Field field) {
        if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
            return true;
        }
        if ("serialVersionUID".equals(field.getName())) {
            return true;
        }
        TableField tableField = field.getAnnotation(TableField.class);
        return tableField != null && !tableField.exist();
    }

    private String resolveColumnName(Field field) {
        TableId tableId = field.getAnnotation(TableId.class);
        if (tableId != null && notEmpty(tableId.value())) {
            return tableId.value();
        }
        TableField tableField = field.getAnnotation(TableField.class);
        if (tableField != null && notEmpty(tableField.value())) {
            return tableField.value();
        }
        return camelToUnderscore(field.getName());
    }

    private String camelToUnderscore(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    builder.append('_');
                }
                builder.append(Character.toLowerCase(c));
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    private boolean notEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
