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
    private final Map<String, GlobalSearchDocumentBuilder> businessTableBuilderMap;
    private final Map<String, GlobalSearchDocumentBuilder> systemBusinessTableBuilderMap;
    private final Set<String> ensuredIndexSet = ConcurrentHashMap.newKeySet();

    public GlobalSearchSyncService(EsCommonService esCommonService,
                                   List<GlobalSearchDocumentBuilder> builders) {
        this.esCommonService = esCommonService;
        this.builderMap = builders == null ? Collections.emptyMap() : builders.stream()
                .collect(Collectors.toMap(this::builderKey, Function.identity(), (left, right) -> left));
        this.businessTableBuilderMap = builders == null ? Collections.emptyMap() : builders.stream()
                .collect(Collectors.toMap(this::businessTableBuilderKey, Function.identity(), (left, right) -> left));
        this.systemBusinessTableBuilderMap = builders == null ? Collections.emptyMap() : builders.stream()
                .collect(Collectors.toMap(this::systemBusinessTableBuilderKey, Function.identity(), (left, right) -> left));
        log.info("全局搜索同步初始化完成 builderCount={}, tables={}", builderMap.size(), builderMap.keySet());
    }

    public void upsert(String table, Map<String, Object> row) {
        GlobalSearchDocumentBuilder builder = resolveBuilder(table);
        upsert(builder, table, row);
    }

    public void upsert(String businessCode, String table, Map<String, Object> row) {
        GlobalSearchDocumentBuilder builder = resolveBuilder(businessCode, table);
        upsert(builder, table, row);
    }

    public void upsert(String systemCode, String businessCode, String table, Map<String, Object> row) {
        GlobalSearchDocumentBuilder builder = resolveBuilder(systemCode, businessCode, table);
        upsert(builder, table, row);
    }

    private void upsert(GlobalSearchDocumentBuilder builder, String table, Map<String, Object> row) {
        if (builder == null) {
            log.warn("全局搜索同步跳过，未配置 builder table={}", table);
            return;
        }
        if (row == null || row.isEmpty()) {
            log.debug("全局搜索同步跳过，数据为空 table={}", table);
            return;
        }
        Object id = row.get(ID_FIELD);
        if (id == null) {
            log.warn("全局搜索同步跳过，缺少 id table={}", table);
            return;
        }
        Map<String, Object> doc = builder.build(row);
        if (doc == null || doc.isEmpty()) {
            log.debug("全局搜索同步跳过，builder 返回空文档 table={}, id={}", table, id);
            return;
        }
        doc.putIfAbsent("system_code", normalize(builder.systemCode()));
        doc.putIfAbsent("business_code", normalize(builder.businessCode()));
        doc.putIfAbsent("table_name", normalize(builder.table()));
        doc.putIfAbsent("biz_id", String.valueOf(id));

        String index = resolveIndex(builder.systemCode());
        String docId = resolveDocId(builder, id);
        ensureIndex(index);
        esCommonService.upsert(index, docId, doc);
        log.info("全局搜索同步单条完成 index={}, table={}, id={}, docId={}", index, table, id, docId);
    }

    public void saveBatch(String table, Collection<Map<String, Object>> rows) {
        saveBatch(null, table, rows);
    }

    public void saveBatch(String businessCode, String table, Collection<Map<String, Object>> rows) {
        saveBatch(null, businessCode, table, rows);
    }

    public void saveBatch(String systemCode, String businessCode, String table, Collection<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) {
            log.info("全局搜索批量同步跳过，数据为空 table={}", table);
            return;
        }
        GlobalSearchDocumentBuilder builder = resolveBuilder(systemCode, businessCode, table);
        if (builder == null) {
            log.info("全局搜索批量同步跳过，未配置 builder table={}, rows={}", table, rows.size());
            return;
        }
        long start = System.currentTimeMillis();
        int success = 0;
        int skipped = 0;
        String index = resolveIndex(builder.systemCode());
        log.info("全局搜索批量同步开始 system={}, business={}, table={}, index={}, rows={}",
                builder.systemCode(), builder.businessCode(), table, index, rows.size());
        for (Map<String, Object> row : rows) {
            if (row == null || row.get(ID_FIELD) == null) {
                skipped++;
                continue;
            }
            upsert(builder, table, row);
            success++;
        }
        log.info("全局搜索批量同步完成 system={}, business={}, table={}, index={}, rows={}, success={}, skipped={}, costMs={}",
                builder.systemCode(), builder.businessCode(), table, index, rows.size(), success, skipped, System.currentTimeMillis() - start);
    }

    public void deleteByTable(String table) {
        deleteByTable(null, table);
    }

    public void deleteByTable(String businessCode, String table) {
        deleteByTable(null, businessCode, table);
    }

    public void deleteByTable(String systemCode, String businessCode, String table) {
        GlobalSearchDocumentBuilder builder = resolveBuilder(systemCode, businessCode, table);
        if (builder == null) {
            log.info("全局搜索按表清理跳过，未配置 builder table={}", table);
            return;
        }
        long start = System.currentTimeMillis();
        String index = resolveIndex(builder.systemCode());
        log.info("全局搜索按表清理开始 system={}, business={}, table={}, index={}",
                builder.systemCode(), builder.businessCode(), table, index);
        ensureIndex(index);
        esCommonService.deleteByQuery(index, tableFilterQuery(builder));
        log.info("全局搜索按表清理完成 system={}, business={}, table={}, index={}, costMs={}",
                builder.systemCode(), builder.businessCode(), table, index, System.currentTimeMillis() - start);
    }

    public void delete(String table, String id) {
        delete(null, table, id);
    }

    public void delete(String businessCode, String table, String id) {
        delete(null, businessCode, table, id);
    }

    public void delete(String systemCode, String businessCode, String table, String id) {
        GlobalSearchDocumentBuilder builder = resolveBuilder(systemCode, businessCode, table);
        if (builder == null) {
            log.warn("全局搜索删除跳过，未配置 builder table={}, id={}", table, id);
            return;
        }
        if (id == null || id.trim().isEmpty()) {
            log.warn("全局搜索删除跳过，id 为空 table={}", table);
            return;
        }
        String index = resolveIndex(builder.systemCode());
        String docId = resolveDocId(builder, id);
        esCommonService.delete(index, docId);
        log.info("全局搜索删除完成 system={}, business={}, table={}, index={}, id={}, docId={}",
                builder.systemCode(), builder.businessCode(), table, index, id, docId);
    }

    private GlobalSearchDocumentBuilder resolveBuilder(String table) {
        return builderMap.get(normalize(table));
    }

    private GlobalSearchDocumentBuilder resolveBuilder(String businessCode, String table) {
        return businessTableBuilderMap.get(normalize(businessCode) + ":" + normalize(table));
    }

    private GlobalSearchDocumentBuilder resolveBuilder(String systemCode, String businessCode, String table) {
        if (systemCode != null && businessCode != null) {
            return systemBusinessTableBuilderMap.get(normalize(systemCode) + ":" + normalize(businessCode) + ":" + normalize(table));
        }
        if (businessCode != null) {
            return resolveBuilder(businessCode, table);
        }
        return resolveBuilder(table);
    }

    private String builderKey(GlobalSearchDocumentBuilder builder) {
        return normalize(builder.table());
    }

    private String businessTableBuilderKey(GlobalSearchDocumentBuilder builder) {
        return normalize(builder.businessCode()) + ":" + normalize(builder.table());
    }

    private String systemBusinessTableBuilderKey(GlobalSearchDocumentBuilder builder) {
        return normalize(builder.systemCode()) + ":" + normalize(builder.businessCode()) + ":" + normalize(builder.table());
    }

    private String resolveIndex(String systemCode) {
        return normalize(systemCode) + INDEX_SUFFIX;
    }

    private String resolveDocId(GlobalSearchDocumentBuilder builder, Object id) {
        return normalize(builder.systemCode()) + "_" + normalize(builder.businessCode()) + "_" + normalize(builder.table()) + "_" + id;
    }

    private void ensureIndex(String index) {
        if (!ensuredIndexSet.add(index)) {
            return;
        }
        log.info("全局搜索索引检查开始 index={}", index);
        esCommonService.ensureIndex(index, buildGlobalSearchMapping());
        log.info("全局搜索索引检查完成 index={}", index);
    }

    private Map<String, Object> tableFilterQuery(GlobalSearchDocumentBuilder builder) {
        Map<String, Object> systemTerm = new LinkedHashMap<>();
        systemTerm.put("system_code", normalize(builder.systemCode()));

        Map<String, Object> businessTerm = new LinkedHashMap<>();
        businessTerm.put("business_code", normalize(builder.businessCode()));

        Map<String, Object> tableTerm = new LinkedHashMap<>();
        tableTerm.put("table_name", normalize(builder.table()));

        Map<String, Object> systemQuery = new LinkedHashMap<>();
        systemQuery.put("term", systemTerm);

        Map<String, Object> businessQuery = new LinkedHashMap<>();
        businessQuery.put("term", businessTerm);

        Map<String, Object> tableQuery = new LinkedHashMap<>();
        tableQuery.put("term", tableTerm);

        Map<String, Object> bool = new LinkedHashMap<>();
        bool.put("filter", java.util.Arrays.asList(systemQuery, businessQuery, tableQuery));

        Map<String, Object> query = new LinkedHashMap<>();
        query.put("bool", bool);
        return query;
    }

    private Map<String, Object> buildGlobalSearchMapping() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("system_code", keywordField());
        properties.put("business_code", keywordField());
        properties.put("table_name", keywordField());
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
