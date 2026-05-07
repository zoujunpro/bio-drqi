package com.bio.drqi.es.handler;

import com.bio.drqi.es.dto.CanalMessage;
import com.bio.drqi.es.service.EsCommonService;
import com.bio.drqi.es.support.DomainEntityResolver;
import com.bio.drqi.es.support.EsDocumentConverter;
import com.bio.drqi.es.support.EsMappingBuilder;
import com.bio.drqi.es.support.global.GlobalSearchSyncService;
import com.bio.drqi.es.support.MapperTableQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "bio.es", name = "enabled", havingValue = "true")
public class CanalMessageHandler {

    private static final String DEFAULT_ID_FIELD = "id";
    private final EsCommonService esCommonService;
    private final MapperTableQueryService mapperTableQueryService;
    private final DomainEntityResolver domainEntityResolver;
    private final EsMappingBuilder esMappingBuilder;
    private final EsDocumentConverter esDocumentConverter;
    private final GlobalSearchSyncService globalSearchSyncService;
    private final Set<String> ensuredIndexSet = ConcurrentHashMap.newKeySet();

    /**
     * {
     *   "id": 123456789,
     *   "database": "bioinfo",
     *   "table": "task",
     *   "pkNames": ["id"],
     *   "isDdl": false,
     *   "type": "UPDATE",
     *   "es": 1713920000000,
     *   "ts": 1713920001000,
     *   "sql": "",
     *   "data": [
     *     {
     *       "id": "10001",
     *       "task_name": "任务标题",
     *       "task_content": "任务内容",
     *       "project_id": "2001",
     *       "owner_id": "u001",
     *       "status": "doing",
     *       "update_time": "2026-04-24 10:00:00"
     *     }
     *   ],
     *   "old": [
     *     {
     *       "status": "todo"
     *     }
     *   ]
     * }
     * @param message
     * @throws Exception
     */
    public void handle(CanalMessage message) throws Exception {
        if (Boolean.TRUE.equals(message.getIsDdl())) {
            return;
        }

        String table = parseTableName(message.getTable());
        String type = message.getType();
        String idField = resolveIdField(message);

        if (message.getData() == null || message.getData().isEmpty()) {
            return;
        }

        for (Map<String, Object> row : message.getData()) {
            Object idValue = row.get(idField);
            if (idValue == null && !DEFAULT_ID_FIELD.equals(idField)) {
                idValue = row.get(DEFAULT_ID_FIELD);
            }
            if (idValue == null) {
                continue;
            }
            String id = String.valueOf(idValue);

            if ("DELETE".equalsIgnoreCase(type)) {
                handleDelete(table, id);
            } else if ("INSERT".equalsIgnoreCase(type) || "UPDATE".equalsIgnoreCase(type)) {
                handleInsertOrUpdate(table, id);
            }
        }
    }

    private void handleInsertOrUpdate(String table, String id) throws Exception {
        Object dbEntity = mapperTableQueryService.queryByTableAndId(table, id);
        if (dbEntity == null) {
            return;
        }
        List<Map<String, Object>> docs = esDocumentConverter.toMapList(Collections.singletonList(dbEntity));
        if (docs.isEmpty()) {
            return;
        }
        Map<String, Object> doc = docs.get(0);
        Object idValue = doc.get(DEFAULT_ID_FIELD);
        String targetId = idValue == null ? id : String.valueOf(idValue);
        String index = table.toLowerCase(Locale.ROOT);
        ensureIndexIfNeeded(table, index);
        esCommonService.upsert(index, targetId, doc);
        globalSearchSyncService.upsert(table, doc);
    }

    private void handleDelete(String table, String id) throws Exception {
        String index = table.toLowerCase(Locale.ROOT);
        esCommonService.delete(index, id);
        globalSearchSyncService.delete(table, id);
    }

    private void ensureIndexIfNeeded(String table, String index) {
        if (!ensuredIndexSet.add(index)) {
            return;
        }
        Class<?> entityClass = domainEntityResolver.resolveEntityClass(table);
        if (entityClass == null) {
            throw new IllegalStateException("在包 com.bio.drqi.domain 下找不到表对应实体: " + table);
        }
        Map<String, Object> mapping = esMappingBuilder.buildMappingByEntity(entityClass);
        esCommonService.ensureIndex(index, mapping);
    }

    private String resolveIdField(CanalMessage message) {
        if (message.getPkNames() == null || message.getPkNames().isEmpty()) {
            return DEFAULT_ID_FIELD;
        }
        String idField = message.getPkNames().get(0);
        if (idField == null || idField.trim().isEmpty()) {
            return DEFAULT_ID_FIELD;
        }
        return idField.trim();
    }

    private String parseTableName(String table) {
        if (table == null || table.trim().isEmpty()) {
            throw new IllegalStateException("Canal消息缺少table");
        }
        String value = table.trim();
        int idx = value.indexOf('.');
        if (idx > 0 && idx < value.length() - 1) {
            return value.substring(idx + 1).trim();
        }
        return value;
    }
}
