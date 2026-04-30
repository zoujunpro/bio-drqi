package com.bio.drqi.es.support;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class EsDocumentConverter {

    public List<Map<String, Object>> toMapList(Collection<?> source) {
        List<Map<String, Object>> rows = new ArrayList<>();
        if (source == null || source.isEmpty()) {
            return rows;
        }
        for (Object item : source) {
            rows.add(toMap(item));
        }
        return rows;
    }

    private Map<String, Object> toMap(Object source) {
        Map<String, Object> row = new LinkedHashMap<>();
        for (Field field : getAllFields(source.getClass())) {
            if (shouldIgnoreField(field)) {
                continue;
            }
            field.setAccessible(true);
            try {
                row.put(resolveColumnName(field), field.get(source));
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("读取字段失败：" + field.getName(), e);
            }
        }
        return row;
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
