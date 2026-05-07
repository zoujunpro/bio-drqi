package com.bio.drqi.es.support;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bio.drqi.common.annotation.EsFieldMapping;
import com.bio.drqi.common.enums.EsFieldTypeEnum;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class EsMappingBuilder {

    public Map<String, Object> buildMappingByEntity(Class<?> entityClass) {
        Map<String, Object> properties = new LinkedHashMap<>();
        for (Field field : getAllFields(entityClass)) {
            if (shouldIgnoreField(field)) {
                continue;
            }
            String columnName = resolveColumnName(field);
            properties.put(columnName, toEsFieldMapping(field));
        }
        Map<String, Object> mapping = new LinkedHashMap<>();
        mapping.put("properties", properties);
        return mapping;
    }

    private Map<String, Object> toEsFieldMapping(Field field) {
        EsFieldMapping annotation = field.getAnnotation(EsFieldMapping.class);
        if (annotation != null && annotation.type() != EsFieldTypeEnum.AUTO) {
            return toAnnotatedEsFieldMapping(annotation);
        }
        return toDefaultEsFieldMapping(field.getType());
    }

    private Map<String, Object> toAnnotatedEsFieldMapping(EsFieldMapping annotation) {
        Map<String, Object> field = new LinkedHashMap<>();
        field.put("type", annotation.type().getType());
        field.put("index", annotation.index());
        if (annotation.ignoreAbove() > 0) {
            field.put("ignore_above", annotation.ignoreAbove());
        }
        if (notEmpty(annotation.analyzer())) {
            field.put("analyzer", annotation.analyzer());
        }
        if (notEmpty(annotation.searchAnalyzer())) {
            field.put("search_analyzer", annotation.searchAnalyzer());
        }
        if (notEmpty(annotation.format())) {
            field.put("format", annotation.format());
        }
        return field;
    }

    private Map<String, Object> toDefaultEsFieldMapping(Class<?> javaType) {
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
