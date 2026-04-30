package com.bio.drqi.es.support;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Component
public class EsDocumentConverter {

    private final ObjectMapper objectMapper;

    public EsDocumentConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Map<String, Object>> toMapList(Collection<?> source) {
        List<Map<String, Object>> rows = new ArrayList<>();
        if (source == null || source.isEmpty()) {
            return rows;
        }
        for (Object item : source) {
            Map<String, Object> row = objectMapper.convertValue(item, new TypeReference<Map<String, Object>>() {
            });
            rows.add(row);
        }
        return rows;
    }
}
