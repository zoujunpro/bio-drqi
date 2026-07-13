package com.bio.drqi.es.handler;

import com.bio.drqi.es.dto.CanalMessage;
import com.bio.drqi.es.enums.EsSyncRecordSourceEnum;
import com.bio.drqi.es.enums.EsSyncRecordStageEnum;
import com.bio.drqi.es.enums.EsSyncRecordStatusEnum;
import com.bio.drqi.es.service.EsCommonService;
import com.bio.drqi.es.service.EsSyncRecordService;
import com.bio.drqi.es.support.EsDocumentConverter;
import com.bio.drqi.es.support.EsMappingBuilder;
import com.bio.drqi.es.support.search.GlobalSearchSyncService;
import com.bio.drqi.es.support.search.SearchDocumentBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "bio.es", name = "enabled", havingValue = "true")
public class CanalMessageHandler {
    private static final String DEFAULT_ID_FIELD = "id";
    private final EsCommonService esCommonService;
    private final EsMappingBuilder esMappingBuilder;
    private final Map<String, SearchDocumentBuilder> builderMap;
    private final EsDocumentConverter esDocumentConverter;
    private final GlobalSearchSyncService globalSearchSyncService;
    private final EsSyncRecordService esSyncRecordService;
    private final Set<String> ensuredIndexSet = ConcurrentHashMap.newKeySet();

    public CanalMessageHandler(EsCommonService esCommonService,
                               EsMappingBuilder esMappingBuilder,
                               EsDocumentConverter esDocumentConverter,
                               GlobalSearchSyncService globalSearchSyncService,
                               EsSyncRecordService esSyncRecordService,
                               List<SearchDocumentBuilder> builders) {
        this.esCommonService = esCommonService;
        this.esMappingBuilder = esMappingBuilder;
        this.esDocumentConverter = esDocumentConverter;
        this.globalSearchSyncService = globalSearchSyncService;
        this.esSyncRecordService = esSyncRecordService;
        this.builderMap = builders == null ? Collections.emptyMap() : builders.stream().collect(Collectors.toMap(SearchDocumentBuilder::table, searchDocumentBuilder -> searchDocumentBuilder));

    }

    /**
     * {
     * "id": 123456789,
     * "database": "bioinfo",
     * "table": "task",
     * "pkNames": ["id"],
     * "isDdl": false,
     * "type": "UPDATE",
     * "es": 1713920000000,
     * "ts": 1713920001000,
     * "sql": "",
     * "data": [
     * {
     * "id": "10001",
     * "task_name": "任务标题",
     * "task_content": "任务内容",
     * "project_id": "2001",
     * "owner_id": "u001",
     * "status": "doing",
     * "update_time": "2026-04-24 10:00:00"
     * }
     * ],
     * "old": [
     * {
     * "status": "todo"
     * }
     * ]
     * }
     *
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
                handleDelete(table, id, type);
            } else if ("INSERT".equalsIgnoreCase(type) || "UPDATE".equalsIgnoreCase(type)) {
                handleInsertOrUpdate(table, id, type);
            }
        }
    }

    private void handleInsertOrUpdate(String table, String id, String operationType) throws Exception {
        String traceId = esSyncRecordService.start(table, id, operationType, EsSyncRecordSourceEnum.CANAL.getCode());
        EsSyncRecordStageEnum stage = EsSyncRecordStageEnum.RESOLVE_BUILDER;
        String syncTable = table;
        String targetId = id;
        String index = table.toLowerCase(Locale.ROOT);
        String globalIndex = null;
        String globalDocId = null;
        String docHash = null;
        try {
            SearchDocumentBuilder builder = resolveBuilder(table);
            syncTable = builder.table();
            index = syncTable.toLowerCase(Locale.ROOT);
            globalIndex = builder.systemCode().toLowerCase(Locale.ROOT) + "_global_search";
            stage = EsSyncRecordStageEnum.DB_READ;
            List<Map<String, Object>> docs = builder.buildRows(id);
            stage = EsSyncRecordStageEnum.BUILD_DOC;
            if (docs.isEmpty()) {
                log.warn("Canal 同步跳过，实体转换 ES 文档为空 table={}, id={}", table, id);
                esSyncRecordService.finish(traceId, EsSyncRecordStatusEnum.SKIPPED, stage, index, targetId,
                        globalIndex, null, null, "数据库未查询到可同步文档");
                return;
            }
            Map<String, Object> doc = docs.get(0);
            Object idValue = doc.get(DEFAULT_ID_FIELD);
            targetId = idValue == null ? id : String.valueOf(idValue);
            docHash = esSyncRecordService.hash(doc);
            globalDocId = builder.systemCode().toLowerCase(Locale.ROOT) + "_" + builder.businessCode().toLowerCase(Locale.ROOT) + "_" + syncTable.toLowerCase(Locale.ROOT) + "_" + targetId;
            ensureIndexIfNeeded(builder, index);
            stage = EsSyncRecordStageEnum.TABLE_INDEX_WRITE;
            esCommonService.upsert(index, targetId, doc);
            log.info("Canal 原表索引同步完成 table={}, index={}, id={}", syncTable, index, targetId);
            stage = EsSyncRecordStageEnum.GLOBAL_INDEX_WRITE;
            boolean globalSynced = globalSearchSyncService.upsert(syncTable, doc);
            log.info("Canal 统一索引同步结果 table={}, id={}, synced={}", syncTable, targetId, globalSynced);
            esSyncRecordService.finish(traceId, globalSynced ? EsSyncRecordStatusEnum.SUCCESS : EsSyncRecordStatusEnum.SKIPPED,
                    EsSyncRecordStageEnum.DONE, index, targetId, globalIndex, globalDocId, docHash,
                    globalSynced ? null : "全局索引同步被跳过");
        } catch (Exception ex) {
            esSyncRecordService.finish(traceId, EsSyncRecordStatusEnum.FAILED, stage, index, targetId,
                    globalIndex, globalDocId, docHash, ex.getMessage());
            throw ex;
        }
    }

    private void handleDelete(String table, String id, String operationType) throws Exception {
        String traceId = esSyncRecordService.start(table, id, operationType, EsSyncRecordSourceEnum.CANAL.getCode());
        EsSyncRecordStageEnum stage = EsSyncRecordStageEnum.TABLE_INDEX_DELETE;
        String index = table.toLowerCase(Locale.ROOT);
        try {
            esCommonService.delete(index, id);
            log.info("Canal 原表索引删除完成 table={}, index={}, id={}", table, index, id);
            stage = EsSyncRecordStageEnum.GLOBAL_INDEX_DELETE;
            boolean globalDeleted = globalSearchSyncService.delete(table, id);
            log.info("Canal 统一索引删除结果 table={}, id={}, deleted={}", table, id, globalDeleted);
            esSyncRecordService.finish(traceId, EsSyncRecordStatusEnum.SUCCESS, EsSyncRecordStageEnum.DONE, index, id,
                    null, null, null, globalDeleted ? null : "全局索引删除被跳过");
        } catch (Exception ex) {
            esSyncRecordService.finish(traceId, EsSyncRecordStatusEnum.FAILED, stage, index, id,
                    null, null, null, ex.getMessage());
            throw ex;
        }
    }

    private void ensureIndexIfNeeded(SearchDocumentBuilder builder, String index) {
        if (!ensuredIndexSet.add(index)) {
            return;
        }
        Class<?> entityClass = builder.entityClass();
        if (entityClass == null) {
            throw new IllegalStateException("未配置表对应实体: " + builder.table());
        }
        Map<String, Object> mapping = esMappingBuilder.buildMappingByEntity(entityClass);
        esCommonService.ensureIndex(index, mapping);
    }

    private SearchDocumentBuilder resolveBuilder(String table) {
        SearchDocumentBuilder builder = builderMap.get(table.toLowerCase(Locale.ROOT));
        if (builder == null) {
            throw new IllegalStateException("未配置该表的数据同步: " + table);
        }
        return builder;
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
