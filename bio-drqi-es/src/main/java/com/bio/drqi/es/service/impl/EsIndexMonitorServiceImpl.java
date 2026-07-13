package com.bio.drqi.es.service.impl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.es.dto.EsDocCheckDTO;
import com.bio.drqi.es.dto.EsIndexMonitorStatusDTO;
import com.bio.drqi.es.dto.EsIndexMonitorTaskDTO;
import com.bio.drqi.es.dto.EsIndexMonitorTaskPageDTO;
import com.bio.drqi.es.dto.req.TableSyncReqDTO;
import com.bio.drqi.es.enums.EsIndexStatusEnum;
import com.bio.drqi.es.enums.EsIndexTaskStatusEnum;
import com.bio.drqi.es.enums.EsIndexTaskTypeEnum;
import com.bio.drqi.es.service.EsCommonService;
import com.bio.drqi.es.service.EsIndexMonitorService;
import com.bio.drqi.es.service.EsSyncService;
import com.bio.drqi.es.service.EsSyncRecordService;
import com.bio.drqi.es.support.search.SearchDocumentBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * ES 索引监控服务。
 * 负责维护索引当前状态、记录检测/重建任务，并把 ES 侧文档数和数据库业务数据数做一致性比对。
 */
@Slf4j
@Service
@ConditionalOnProperty(prefix = "bio.es", name = "enabled", havingValue = "true")
public class EsIndexMonitorServiceImpl implements EsIndexMonitorService {

    private static final int ERROR_MESSAGE_MAX_LENGTH = 2000;
    private static final String GLOBAL_INDEX_SUFFIX = "_global_search";
    private static final String GLOBAL_BUSINESS_CODE = "global";
    private static final String GLOBAL_TABLE_NAME = "__global_search__";
    private static final String GLOBAL_INDEX_NAME = "全局搜索索引";

    private final JdbcTemplate jdbcTemplate;
    private final EsCommonService esCommonService;
    private final EsSyncService esSyncService;
    private final EsSyncRecordService esSyncRecordService;
    private final Map<String, SearchDocumentBuilder> builderMap;

    public EsIndexMonitorServiceImpl(JdbcTemplate jdbcTemplate,
                                     EsCommonService esCommonService,
                                     EsSyncService esSyncService,
                                     EsSyncRecordService esSyncRecordService,
                                     List<SearchDocumentBuilder> builders) {
        this.jdbcTemplate = jdbcTemplate;
        this.esCommonService = esCommonService;
        this.esSyncService = esSyncService;
        this.esSyncRecordService = esSyncRecordService;
        this.builderMap = builders == null ? Collections.emptyMap() : builders.stream()
                .collect(Collectors.toMap(builder -> normalizeCode(builder.table()), builder -> builder, (left, right) -> left, LinkedHashMap::new));
    }

    @Override
    public List<EsIndexMonitorStatusDTO> listStatus() {
        syncRegisteredIndexes();
        refreshClusterStatus();
        String sql = "select id, index_code, index_name, system_code, business_code, table_name, status, " +
                "es_doc_count, db_doc_count, diff_count, cluster_status, last_task_id, last_error_message, " +
                "date_format(last_sync_time, '%Y-%m-%d %H:%i:%s') as last_sync_time, " +
                "date_format(last_check_time, '%Y-%m-%d %H:%i:%s') as last_check_time, " +
                "date_format(create_time, '%Y-%m-%d %H:%i:%s') as create_time, " +
                "date_format(update_time, '%Y-%m-%d %H:%i:%s') as update_time " +
                "from es_index_status order by status asc, index_code asc";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            EsIndexMonitorStatusDTO dto = new EsIndexMonitorStatusDTO();
            dto.setId(rs.getLong("id"));
            dto.setIndexCode(rs.getString("index_code"));
            dto.setIndexName(rs.getString("index_name"));
            dto.setSystemCode(rs.getString("system_code"));
            dto.setBusinessCode(rs.getString("business_code"));
            dto.setTableName(rs.getString("table_name"));
            dto.setStatus(rs.getString("status"));
            dto.setEsDocCount(rs.getLong("es_doc_count"));
            dto.setDbDocCount(rs.getLong("db_doc_count"));
            dto.setDiffCount(rs.getLong("diff_count"));
            dto.setClusterStatus(rs.getString("cluster_status"));
            dto.setLastTaskId(rs.getString("last_task_id"));
            dto.setLastErrorMessage(rs.getString("last_error_message"));
            dto.setLastSyncTime(rs.getString("last_sync_time"));
            dto.setLastCheckTime(rs.getString("last_check_time"));
            dto.setCreateTime(rs.getString("create_time"));
            dto.setUpdateTime(rs.getString("update_time"));
            return dto;
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EsIndexMonitorStatusDTO check(String indexCode, String operatorId, String operatorName) {
        syncRegisteredIndexes();
        if (isGlobalIndex(indexCode)) {
            return checkGlobalIndex(indexCode, operatorId, operatorName);
        }
        SearchDocumentBuilder builder = resolveBuilder(indexCode);
        String taskId = startTask(builder, EsIndexTaskTypeEnum.CHECK, operatorId, operatorName);
        long start = System.currentTimeMillis();
        try {
            EsIndexMonitorStatusDTO statusDTO = doCheck(builder, taskId, false, null);
            finishTask(taskId, EsIndexTaskStatusEnum.SUCCESS, statusDTO.getDbDocCount(), statusDTO.getEsDocCount(),
                    Math.max(0, statusDTO.getDiffCount()), System.currentTimeMillis() - start, null);
            return statusDTO;
        } catch (RuntimeException ex) {
            finishTask(taskId, EsIndexTaskStatusEnum.FAILED, 0L, 0L, 1L, System.currentTimeMillis() - start, ex.getMessage());
            markStatusFailed(builder, taskId, ex.getMessage());
            throw ex;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public EsIndexMonitorStatusDTO rebuild(String indexCode, String operatorId, String operatorName) {
        syncRegisteredIndexes();
        if (isGlobalIndex(indexCode)) {
            return rebuildGlobalIndex(indexCode, operatorId, operatorName);
        }
        SearchDocumentBuilder builder = resolveBuilder(indexCode);
        String taskId = startTask(builder, EsIndexTaskTypeEnum.FULL_BUILD, operatorId, operatorName);
        markStatusBuilding(builder, taskId);
        long start = System.currentTimeMillis();
        try {
            TableSyncReqDTO reqDTO = new TableSyncReqDTO();
            reqDTO.setTableName(builder.table());
            esSyncService.syncTable(reqDTO);
            EsIndexMonitorStatusDTO statusDTO = doCheck(builder, taskId, true, null);
            long failCount = Math.max(0, statusDTO.getDiffCount());
            EsIndexTaskStatusEnum taskStatus = failCount == 0 ? EsIndexTaskStatusEnum.SUCCESS : EsIndexTaskStatusEnum.PARTIAL_SUCCESS;
            finishTask(taskId, taskStatus, statusDTO.getDbDocCount(), statusDTO.getEsDocCount(),
                    failCount, System.currentTimeMillis() - start, statusDTO.getLastErrorMessage());
            return statusDTO;
        } catch (RuntimeException ex) {
            finishTask(taskId, EsIndexTaskStatusEnum.FAILED, 0L, 0L, 1L, System.currentTimeMillis() - start, ex.getMessage());
            markStatusFailed(builder, taskId, ex.getMessage());
            throw ex;
        }
    }

    @Override
    public EsIndexMonitorTaskPageDTO listTasks(String indexCode, String taskType, String status, int pageNum, int pageSize) {
        int safePageNum = pageNum <= 0 ? 1 : pageNum;
        int safePageSize = pageSize <= 0 ? 20 : Math.min(pageSize, 100);
        List<Object> args = new ArrayList<>();
        String where = buildTaskWhere(indexCode, taskType, status, args);
        Long total = jdbcTemplate.queryForObject("select count(1) from es_index_task " + where, args.toArray(), Long.class);

        List<Object> pageArgs = new ArrayList<>(args);
        pageArgs.add((safePageNum - 1) * safePageSize);
        pageArgs.add(safePageSize);
        String sql = "select id, task_id, index_code, index_name, task_type, status, total_count, success_count, fail_count, cost_ms, " +
                "error_message, operator_id, operator_name, " +
                "date_format(start_time, '%Y-%m-%d %H:%i:%s') as start_time, " +
                "date_format(end_time, '%Y-%m-%d %H:%i:%s') as end_time, " +
                "date_format(create_time, '%Y-%m-%d %H:%i:%s') as create_time " +
                "from es_index_task " + where + " order by id desc limit ?, ?";
        List<EsIndexMonitorTaskDTO> records = jdbcTemplate.query(sql, pageArgs.toArray(), (rs, rowNum) -> {
            EsIndexMonitorTaskDTO dto = new EsIndexMonitorTaskDTO();
            dto.setId(rs.getLong("id"));
            dto.setTaskId(rs.getString("task_id"));
            dto.setIndexCode(rs.getString("index_code"));
            dto.setIndexName(rs.getString("index_name"));
            dto.setTaskType(rs.getString("task_type"));
            dto.setStatus(rs.getString("status"));
            dto.setTotalCount(rs.getLong("total_count"));
            dto.setSuccessCount(rs.getLong("success_count"));
            dto.setFailCount(rs.getLong("fail_count"));
            dto.setCostMs(rs.getLong("cost_ms"));
            dto.setErrorMessage(rs.getString("error_message"));
            dto.setOperatorId(rs.getString("operator_id"));
            dto.setOperatorName(rs.getString("operator_name"));
            dto.setStartTime(rs.getString("start_time"));
            dto.setEndTime(rs.getString("end_time"));
            dto.setCreateTime(rs.getString("create_time"));
            return dto;
        });

        EsIndexMonitorTaskPageDTO pageDTO = new EsIndexMonitorTaskPageDTO();
        pageDTO.setTotal(total == null ? 0 : total);
        pageDTO.setRecords(records);
        return pageDTO;
    }

    @Override
    public EsDocCheckDTO checkDoc(String tableName, String bizId) {
        if (!hasText(tableName)) {
            throw new IllegalArgumentException("表名不能为空");
        }
        if (!hasText(bizId)) {
            throw new IllegalArgumentException("业务ID不能为空");
        }
        SearchDocumentBuilder builder = resolveBuilder(tableName);
        String normalizedTable = normalizeCode(builder.table());
        String tableIndexName = normalizedTable;
        String tableDocId = bizId.trim();
        String globalIndexName = resolveGlobalIndexCode(builder.systemCode());
        String globalDocId = normalizeCode(builder.systemCode()) + "_" + normalizeCode(builder.businessCode()) + "_" + normalizedTable + "_" + tableDocId;

        List<Map<String, Object>> docs = builder.buildRows(tableDocId);
        Map<String, Object> expectedDoc = docs == null || docs.isEmpty() ? null : docs.get(0);
        if (expectedDoc != null && expectedDoc.get("id") != null) {
            tableDocId = String.valueOf(expectedDoc.get("id"));
            globalDocId = normalizeCode(builder.systemCode()) + "_" + normalizeCode(builder.businessCode()) + "_" + normalizedTable + "_" + tableDocId;
        }
        Map<String, Object> tableDoc = esCommonService.getById(tableIndexName, tableDocId);
        Map<String, Object> globalDoc = esCommonService.getById(globalIndexName, globalDocId);

        String expectedHash = esSyncRecordService.hash(expectedDoc);
        String tableHash = esSyncRecordService.hash(tableDoc);
        String globalHash = esSyncRecordService.hash(globalDoc);

        EsDocCheckDTO dto = new EsDocCheckDTO();
        dto.setTableName(normalizedTable);
        dto.setBizId(tableDocId);
        dto.setTableIndexName(tableIndexName);
        dto.setTableDocId(tableDocId);
        dto.setGlobalIndexName(globalIndexName);
        dto.setGlobalDocId(globalDocId);
        dto.setDbExists(expectedDoc != null);
        dto.setTableIndexExists(esCommonService.indexExists(tableIndexName));
        dto.setGlobalIndexExists(esCommonService.indexExists(globalIndexName));
        dto.setTableDocExists(tableDoc != null);
        dto.setGlobalDocExists(globalDoc != null);
        dto.setExpectedHash(expectedHash);
        dto.setTableDocHash(tableHash);
        dto.setGlobalDocHash(globalHash);
        dto.setTableConsistent(expectedHash != null && expectedHash.equals(tableHash));
        dto.setGlobalConsistent(globalDoc != null && tableDocId.equals(String.valueOf(globalDoc.get("biz_id"))));
        dto.setLastSyncRecord(esSyncRecordService.findLatest(normalizedTable, tableDocId));
        dto.setExpectedDoc(expectedDoc);
        dto.setTableDoc(tableDoc);
        dto.setGlobalDoc(globalDoc);
        return dto;
    }

    private EsIndexMonitorStatusDTO doCheck(SearchDocumentBuilder builder, String taskId, boolean syncTime, String errorMessage) {
        String indexCode = normalizeCode(builder.table());
        String indexName = indexCode;
        long dbCount = countDbRows(builder);
        esCommonService.updateAllOpenIndexReplicas(0);
        boolean exists = esCommonService.indexExists(indexName);
        if (exists) {
            esCommonService.updateNumberOfReplicas(indexName, 0);
        }
        String clusterStatus = esCommonService.clusterHealthStatus();
        long esCount = exists ? esCommonService.count(indexName) : 0L;
        long diff = Math.abs(dbCount - esCount);

        EsIndexStatusEnum status;
        String finalError = errorMessage;
        if (!exists) {
            status = EsIndexStatusEnum.FAILED;
            finalError = "ES索引不存在：" + indexName;
        } else if ("red".equalsIgnoreCase(clusterStatus)) {
            status = EsIndexStatusEnum.FAILED;
            finalError = "ES集群状态为 red";
        } else if (diff > 0) {
            status = EsIndexStatusEnum.WARNING;
            finalError = "ES文档数与数据库数量不一致，差异：" + diff;
        } else {
            status = EsIndexStatusEnum.READY;
            finalError = null;
        }

        updateStatus(builder, status, esCount, dbCount, diff, clusterStatus, taskId, finalError, syncTime);
        return findStatus(indexCode);
    }

    private void syncRegisteredIndexes() {
        for (SearchDocumentBuilder builder : builderMap.values()) {
            jdbcTemplate.update("insert into es_index_status " +
                            "(index_code, index_name, system_code, business_code, table_name, status, es_doc_count, db_doc_count, diff_count, create_time, update_time) " +
                            "values (?, ?, ?, ?, ?, ?, 0, 0, 0, now(), now()) " +
                            "on duplicate key update index_name = values(index_name), system_code = values(system_code), " +
                            "business_code = values(business_code), table_name = values(table_name), update_time = now()",
                    normalizeCode(builder.table()), normalizeCode(builder.table()), builder.systemCode(),
                    builder.businessCode(), builder.table(), EsIndexStatusEnum.WARNING.getCode());
        }
        for (String systemCode : resolveSystemCodes()) {
            String globalIndexCode = resolveGlobalIndexCode(systemCode);
            jdbcTemplate.update("insert into es_index_status " +
                            "(index_code, index_name, system_code, business_code, table_name, status, es_doc_count, db_doc_count, diff_count, create_time, update_time) " +
                            "values (?, ?, ?, ?, ?, ?, 0, 0, 0, now(), now()) " +
                            "on duplicate key update index_name = values(index_name), system_code = values(system_code), " +
                            "business_code = values(business_code), table_name = values(table_name), update_time = now()",
                    globalIndexCode, globalIndexCode, normalizeCode(systemCode), GLOBAL_BUSINESS_CODE,
                    GLOBAL_TABLE_NAME, EsIndexStatusEnum.WARNING.getCode());
        }
    }

    private EsIndexMonitorStatusDTO checkGlobalIndex(String indexCode, String operatorId, String operatorName) {
        String normalizedIndexCode = normalizeCode(indexCode);
        String taskId = startTask(normalizedIndexCode, normalizedIndexCode, EsIndexTaskTypeEnum.CHECK, operatorId, operatorName);
        long start = System.currentTimeMillis();
        try {
            EsIndexMonitorStatusDTO statusDTO = doCheckGlobal(normalizedIndexCode, taskId, false, null);
            finishTask(taskId, EsIndexTaskStatusEnum.SUCCESS, statusDTO.getDbDocCount(), statusDTO.getEsDocCount(),
                    Math.max(0, statusDTO.getDiffCount()), System.currentTimeMillis() - start, null);
            return statusDTO;
        } catch (RuntimeException ex) {
            finishTask(taskId, EsIndexTaskStatusEnum.FAILED, 0L, 0L, 1L, System.currentTimeMillis() - start, ex.getMessage());
            markStatusFailed(normalizedIndexCode, taskId, ex.getMessage());
            throw ex;
        }
    }

    private EsIndexMonitorStatusDTO rebuildGlobalIndex(String indexCode, String operatorId, String operatorName) {
        String normalizedIndexCode = normalizeCode(indexCode);
        String systemCode = resolveSystemCodeFromGlobalIndex(normalizedIndexCode);
        String taskId = startTask(normalizedIndexCode, normalizedIndexCode, EsIndexTaskTypeEnum.FULL_BUILD, operatorId, operatorName);
        markStatusBuilding(normalizedIndexCode, taskId);
        long start = System.currentTimeMillis();
        try {
            for (SearchDocumentBuilder builder : builderMap.values()) {
                if (!normalizeCode(builder.systemCode()).equals(systemCode)) {
                    continue;
                }
                TableSyncReqDTO reqDTO = new TableSyncReqDTO();
                reqDTO.setTableName(builder.table());
                esSyncService.syncTable(reqDTO);
            }
            EsIndexMonitorStatusDTO statusDTO = doCheckGlobal(normalizedIndexCode, taskId, true, null);
            long failCount = Math.max(0, statusDTO.getDiffCount());
            EsIndexTaskStatusEnum taskStatus = failCount == 0 ? EsIndexTaskStatusEnum.SUCCESS : EsIndexTaskStatusEnum.PARTIAL_SUCCESS;
            finishTask(taskId, taskStatus, statusDTO.getDbDocCount(), statusDTO.getEsDocCount(),
                    failCount, System.currentTimeMillis() - start, statusDTO.getLastErrorMessage());
            return statusDTO;
        } catch (RuntimeException ex) {
            finishTask(taskId, EsIndexTaskStatusEnum.FAILED, 0L, 0L, 1L, System.currentTimeMillis() - start, ex.getMessage());
            markStatusFailed(normalizedIndexCode, taskId, ex.getMessage());
            throw ex;
        }
    }

    private EsIndexMonitorStatusDTO doCheckGlobal(String indexCode, String taskId, boolean syncTime, String errorMessage) {
        long dbCount = countGlobalDbRows(indexCode);
        esCommonService.updateAllOpenIndexReplicas(0);
        boolean exists = esCommonService.indexExists(indexCode);
        if (exists) {
            esCommonService.updateNumberOfReplicas(indexCode, 0);
        }
        String clusterStatus = esCommonService.clusterHealthStatus();
        long esCount = exists ? esCommonService.count(indexCode) : 0L;
        long diff = Math.abs(dbCount - esCount);

        EsIndexStatusEnum status;
        String finalError = errorMessage;
        if (!exists) {
            status = EsIndexStatusEnum.FAILED;
            finalError = "ES全局索引不存在：" + indexCode;
        } else if ("red".equalsIgnoreCase(clusterStatus)) {
            status = EsIndexStatusEnum.FAILED;
            finalError = "ES集群状态为 red";
        } else if (diff > 0) {
            status = EsIndexStatusEnum.WARNING;
            finalError = "ES全局索引文档数与数据库总数量不一致，差异：" + diff;
        } else {
            status = EsIndexStatusEnum.READY;
            finalError = null;
        }

        updateStatus(indexCode, status, esCount, dbCount, diff, clusterStatus, taskId, finalError, syncTime);
        return findStatus(indexCode);
    }

    private String startTask(SearchDocumentBuilder builder, EsIndexTaskTypeEnum taskType, String operatorId, String operatorName) {
        return startTask(normalizeCode(builder.table()), normalizeCode(builder.table()), taskType, operatorId, operatorName);
    }

    private String startTask(String indexCode, String indexName, EsIndexTaskTypeEnum taskType, String operatorId, String operatorName) {
        String taskId = UUID.randomUUID().toString();
        jdbcTemplate.update("insert into es_index_task " +
                        "(task_id, index_code, index_name, task_type, status, operator_id, operator_name, start_time, create_time, update_time) " +
                        "values (?, ?, ?, ?, ?, ?, ?, now(), now(), now())",
                taskId, normalizeCode(indexCode), normalizeCode(indexName), taskType.getCode(),
                EsIndexTaskStatusEnum.RUNNING.getCode(), operatorId, operatorName);
        return taskId;
    }

    private void finishTask(String taskId, EsIndexTaskStatusEnum status, Long totalCount, Long successCount,
                            Long failCount, long costMs, String errorMessage) {
        jdbcTemplate.update("update es_index_task set status = ?, total_count = ?, success_count = ?, fail_count = ?, " +
                        "cost_ms = ?, error_message = ?, end_time = now(), update_time = now() where task_id = ?",
                status.getCode(), value(totalCount), value(successCount), value(failCount),
                costMs, limit(errorMessage), taskId);
    }

    private void updateStatus(SearchDocumentBuilder builder, EsIndexStatusEnum status, long esCount, long dbCount, long diff,
                              String clusterStatus, String taskId, String errorMessage, boolean syncTime) {
        updateStatus(normalizeCode(builder.table()), status, esCount, dbCount, diff, clusterStatus, taskId, errorMessage, syncTime);
    }

    private void updateStatus(String indexCode, EsIndexStatusEnum status, long esCount, long dbCount, long diff,
                              String clusterStatus, String taskId, String errorMessage, boolean syncTime) {
        String syncTimeSql = syncTime ? ", last_sync_time = now() " : "";
        jdbcTemplate.update("update es_index_status set status = ?, es_doc_count = ?, db_doc_count = ?, diff_count = ?, " +
                        "cluster_status = ?, last_task_id = ?, last_error_message = ?, last_check_time = now() " +
                        syncTimeSql + ", update_time = now() where index_code = ?",
                status.getCode(), esCount, dbCount, diff, clusterStatus, taskId, limit(errorMessage), normalizeCode(indexCode));
    }

    private void markStatusBuilding(SearchDocumentBuilder builder, String taskId) {
        markStatusBuilding(normalizeCode(builder.table()), taskId);
    }

    private void markStatusBuilding(String indexCode, String taskId) {
        jdbcTemplate.update("update es_index_status set status = ?, last_task_id = ?, last_error_message = null, update_time = now() where index_code = ?",
                EsIndexStatusEnum.BUILDING.getCode(), taskId, normalizeCode(indexCode));
    }

    private void markStatusFailed(SearchDocumentBuilder builder, String taskId, String errorMessage) {
        markStatusFailed(normalizeCode(builder.table()), taskId, errorMessage);
    }

    private void markStatusFailed(String indexCode, String taskId, String errorMessage) {
        jdbcTemplate.update("update es_index_status set status = ?, last_task_id = ?, last_error_message = ?, last_check_time = now(), update_time = now() where index_code = ?",
                EsIndexStatusEnum.FAILED.getCode(), taskId, limit(errorMessage), normalizeCode(indexCode));
    }

    private EsIndexMonitorStatusDTO findStatus(String indexCode) {
        List<EsIndexMonitorStatusDTO> list = listStatus();
        for (EsIndexMonitorStatusDTO item : list) {
            if (normalizeCode(indexCode).equals(item.getIndexCode())) {
                return item;
            }
        }
        throw new IllegalStateException("索引状态不存在：" + indexCode);
    }

    private void refreshClusterStatus() {
        try {
            esCommonService.updateAllOpenIndexReplicas(0);
            String clusterStatus = esCommonService.clusterHealthStatus();
            jdbcTemplate.update("update es_index_status set cluster_status = ?, update_time = now()", clusterStatus);
        } catch (RuntimeException ex) {
            log.warn("刷新 ES 集群状态失败", ex);
        }
    }

    private SearchDocumentBuilder resolveBuilder(String indexCode) {
        if (indexCode == null || indexCode.trim().isEmpty()) {
            throw new IllegalArgumentException("索引编码不能为空");
        }
        SearchDocumentBuilder builder = builderMap.get(normalizeCode(indexCode));
        if (builder == null) {
            throw new IllegalArgumentException("未注册的 ES 索引：" + indexCode);
        }
        return builder;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private long countDbRows(SearchDocumentBuilder builder) {
        BaseMapper mapper = builder.mapper();
        if (mapper == null) {
            return 0L;
        }
        Object count = mapper.selectCount(null);
        return count instanceof Number ? ((Number) count).longValue() : 0L;
    }

    private long countGlobalDbRows(String indexCode) {
        String systemCode = resolveSystemCodeFromGlobalIndex(indexCode);
        long count = 0L;
        for (SearchDocumentBuilder builder : builderMap.values()) {
            if (normalizeCode(builder.systemCode()).equals(systemCode)) {
                count += countDbRows(builder);
            }
        }
        return count;
    }

    private List<String> resolveSystemCodes() {
        List<String> systemCodes = new ArrayList<>();
        for (SearchDocumentBuilder builder : builderMap.values()) {
            String systemCode = normalizeCode(builder.systemCode());
            if (hasText(systemCode) && !systemCodes.contains(systemCode)) {
                systemCodes.add(systemCode);
            }
        }
        return systemCodes;
    }

    private String resolveGlobalIndexCode(String systemCode) {
        return normalizeCode(systemCode) + GLOBAL_INDEX_SUFFIX;
    }

    private boolean isGlobalIndex(String indexCode) {
        return normalizeCode(indexCode).endsWith(GLOBAL_INDEX_SUFFIX);
    }

    private String resolveSystemCodeFromGlobalIndex(String indexCode) {
        String normalizedIndexCode = normalizeCode(indexCode);
        if (!isGlobalIndex(normalizedIndexCode)) {
            throw new IllegalArgumentException("不是全局索引：" + indexCode);
        }
        String systemCode = normalizedIndexCode.substring(0, normalizedIndexCode.length() - GLOBAL_INDEX_SUFFIX.length());
        if (!resolveSystemCodes().contains(systemCode)) {
            throw new IllegalArgumentException("未注册的 ES 全局索引：" + indexCode);
        }
        return systemCode;
    }

    private String buildTaskWhere(String indexCode, String taskType, String status, List<Object> args) {
        StringBuilder where = new StringBuilder(" where 1 = 1 ");
        if (hasText(indexCode)) {
            where.append(" and index_code = ? ");
            args.add(normalizeCode(indexCode));
        }
        if (hasText(taskType)) {
            where.append(" and task_type = ? ");
            args.add(taskType.trim());
        }
        if (hasText(status)) {
            where.append(" and status = ? ");
            args.add(status.trim());
        }
        return where.toString();
    }

    private long value(Long value) {
        return value == null ? 0L : value;
    }

    private String normalizeCode(String code) {
        return code == null ? "" : code.trim().toLowerCase(Locale.ROOT);
    }

    private String limit(String message) {
        if (message == null || message.length() <= ERROR_MESSAGE_MAX_LENGTH) {
            return message;
        }
        return message.substring(0, ERROR_MESSAGE_MAX_LENGTH);
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
