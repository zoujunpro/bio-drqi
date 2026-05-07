package com.bio.drqi.es.support.global;

import com.bio.drqi.es.service.EsCommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "bio.es", name = "enabled", havingValue = "true")
public class GlobalSearchSyncService {

    private static final String ID_FIELD = "id";
    private static final String INDEX_SUFFIX = "_global_search";

    private final EsCommonService esCommonService;
    private final Map<String, GlobalSearchDocumentBuilder> builderMap;
    private final Set<String> ensuredIndexSet = ConcurrentHashMap.newKeySet();

    public GlobalSearchSyncService(EsCommonService esCommonService,
                                   List<GlobalSearchDocumentBuilder> builders) {
        this.esCommonService = esCommonService;
        this.builderMap = builders == null ? Collections.emptyMap() : builders.stream()
                .collect(Collectors.toMap(this::builderKey, Function.identity(), (left, right) -> left));
    }

    public void upsert(String table, Map<String, Object> row) {
        GlobalSearchDocumentBuilder builder = resolveBuilder(table);
        if (builder == null || row == null || row.isEmpty()) {
            return;
        }
        Object id = row.get(ID_FIELD);
        if (id == null) {
            log.warn("全局搜索同步跳过，缺少 id table={}", table);
            return;
        }
        Map<String, Object> doc = builder.build(row);
        if (doc == null || doc.isEmpty()) {
            return;
        }
        doc.putIfAbsent("system_code", normalize(builder.systemCode()));
        doc.putIfAbsent("biz_type", normalize(builder.table()));
        doc.putIfAbsent("biz_id", String.valueOf(id));

        String index = resolveIndex(builder.systemCode());
        ensureIndex(index);
        esCommonService.upsert(index, resolveDocId(builder, id), doc);
    }

    public void saveBatch(String table, Collection<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        for (Map<String, Object> row : rows) {
            upsert(table, row);
        }
    }

    public void deleteByTable(String table) {
        GlobalSearchDocumentBuilder builder = resolveBuilder(table);
        if (builder == null) {
            return;
        }
        String index = resolveIndex(builder.systemCode());
        ensureIndex(index);
        esCommonService.deleteByQuery(index, tableFilterQuery(builder));
    }

    public void delete(String table, String id) {
        GlobalSearchDocumentBuilder builder = resolveBuilder(table);
        if (builder == null || id == null || id.trim().isEmpty()) {
            return;
        }
        esCommonService.delete(resolveIndex(builder.systemCode()), resolveDocId(builder, id));
    }

    private GlobalSearchDocumentBuilder resolveBuilder(String table) {
        return builderMap.get(normalize(table));
    }

    private String builderKey(GlobalSearchDocumentBuilder builder) {
        return normalize(builder.table());
    }

    private String resolveIndex(String systemCode) {
        return normalize(systemCode) + INDEX_SUFFIX;
    }

    private String resolveDocId(GlobalSearchDocumentBuilder builder, Object id) {
        return normalize(builder.systemCode()) + "_" + normalize(builder.table()) + "_" + id;
    }

    private void ensureIndex(String index) {
        if (!ensuredIndexSet.add(index)) {
            return;
        }
        esCommonService.ensureIndex(index, buildGlobalSearchMapping());
    }

    private Map<String, Object> tableFilterQuery(GlobalSearchDocumentBuilder builder) {
        Map<String, Object> systemTerm = new LinkedHashMap<>();
        systemTerm.put("system_code", normalize(builder.systemCode()));

        Map<String, Object> bizTypeTerm = new LinkedHashMap<>();
        bizTypeTerm.put("biz_type", normalize(builder.table()));

        Map<String, Object> systemQuery = new LinkedHashMap<>();
        systemQuery.put("term", systemTerm);

        Map<String, Object> bizTypeQuery = new LinkedHashMap<>();
        bizTypeQuery.put("term", bizTypeTerm);

        Map<String, Object> bool = new LinkedHashMap<>();
        bool.put("filter", java.util.Arrays.asList(systemQuery, bizTypeQuery));

        Map<String, Object> query = new LinkedHashMap<>();
        query.put("bool", bool);
        return query;
    }

    private Map<String, Object> buildGlobalSearchMapping() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("system_code", keywordField());
        properties.put("biz_type", keywordField());
        properties.put("biz_id", keywordField());
        properties.put("title", textWithKeywordField());
        properties.put("summary", textField());
        properties.put("search_content", textField());
        properties.put("route", keywordField());
        properties.put("display", objectField());
        properties.put("create_time", dateField());

        Map<String, Object> mapping = new LinkedHashMap<>();
        mapping.put("properties", properties);
        return mapping;
    }

    private Map<String, Object> keywordField() {
        Map<String, Object> field = new LinkedHashMap<>();
        field.put("type", "keyword");
        field.put("ignore_above", 256);
        return field;
    }

    private Map<String, Object> textField() {
        Map<String, Object> field = new LinkedHashMap<>();
        field.put("type", "text");
        field.put("analyzer", "ik_max_word");
        field.put("search_analyzer", "ik_smart");
        return field;
    }

    private Map<String, Object> textWithKeywordField() {
        Map<String, Object> field = textField();
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("keyword", keywordField());
        field.put("fields", fields);
        return field;
    }

    private Map<String, Object> objectField() {
        Map<String, Object> field = new LinkedHashMap<>();
        field.put("type", "object");
        field.put("enabled", false);
        return field;
    }

    private Map<String, Object> dateField() {
        Map<String, Object> field = new LinkedHashMap<>();
        field.put("type", "date");
        field.put("format", "strict_date_optional_time||yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis");
        return field;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
