package com.bio.drqi.es.support.global;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public abstract class AbstractGlobalSearchDocumentBuilder implements GlobalSearchDocumentBuilder {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    protected String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    protected String join(Object... values) {
        StringBuilder builder = new StringBuilder();
        if (values == null) {
            return "";
        }
        for (Object value : values) {
            appendValue(builder, value);
        }
        return builder.toString().trim();
    }

    protected String join(Collection<?> values) {
        StringBuilder builder = new StringBuilder();
        if (values == null) {
            return "";
        }
        for (Object value : values) {
            appendValue(builder, value);
        }
        return builder.toString().trim();
    }

    protected String extractJsonValues(Object value) {
        String text = stringValue(value);
        if (text.isEmpty()) {
            return "";
        }
        try {
            JsonNode node = OBJECT_MAPPER.readTree(text);
            StringBuilder builder = new StringBuilder();
            appendJsonValues(builder, node);
            return builder.toString().trim();
        } catch (Exception e) {
            return text;
        }
    }

    private void appendValue(StringBuilder builder, Object value) {
        String text = stringValue(value);
        if (text.isEmpty()) {
            return;
        }
        if (builder.length() > 0) {
            builder.append(' ');
        }
        builder.append(text);
    }

    private void appendJsonValues(StringBuilder builder, JsonNode node) {
        if (node == null || node.isNull()) {
            return;
        }
        if (node.isValueNode()) {
            appendValue(builder, node.asText());
            return;
        }
        if (node.isArray()) {
            for (JsonNode item : node) {
                appendJsonValues(builder, item);
            }
            return;
        }
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                appendJsonValues(builder, fields.next().getValue());
            }
        }
    }
}
