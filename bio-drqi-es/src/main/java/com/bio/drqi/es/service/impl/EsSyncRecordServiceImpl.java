package com.bio.drqi.es.service.impl;

import com.bio.drqi.es.dto.EsSyncRecordDTO;
import com.bio.drqi.es.enums.EsSyncRecordStageEnum;
import com.bio.drqi.es.enums.EsSyncRecordStatusEnum;
import com.bio.drqi.es.service.EsSyncRecordService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "bio.es", name = "enabled", havingValue = "true")
public class EsSyncRecordServiceImpl implements EsSyncRecordService {

    private static final int ERROR_MESSAGE_MAX_LENGTH = 2000;

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public EsSyncRecordServiceImpl(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public String start(String tableName, String bizId, String operationType, String sourceType) {
        String traceId = UUID.randomUUID().toString();
        jdbcTemplate.update("insert into es_sync_record " +
                        "(trace_id, table_name, biz_id, operation_type, source_type, status, stage, start_time, create_time, update_time) " +
                        "values (?, ?, ?, ?, ?, ?, ?, now(), now(), now())",
                traceId, normalize(tableName), bizId, operationType, sourceType,
                EsSyncRecordStatusEnum.FAILED.getCode(), EsSyncRecordStageEnum.RECEIVE.getCode());
        return traceId;
    }

    @Override
    public void finish(String traceId,
                       EsSyncRecordStatusEnum status,
                       EsSyncRecordStageEnum stage,
                       String tableIndexName,
                       String tableDocId,
                       String globalIndexName,
                       String globalDocId,
                       String docHash,
                       String errorMessage) {
        jdbcTemplate.update("update es_sync_record set status = ?, stage = ?, table_index_name = ?, table_doc_id = ?, " +
                        "global_index_name = ?, global_doc_id = ?, doc_hash = ?, error_message = ?, end_time = now(), " +
                        "cost_ms = timestampdiff(microsecond, start_time, now()) / 1000, update_time = now() where trace_id = ?",
                status.getCode(), stage.getCode(), tableIndexName, tableDocId,
                globalIndexName, globalDocId, docHash, limit(errorMessage), traceId);
    }

    @Override
    public EsSyncRecordDTO findLatest(String tableName, String bizId) {
        List<EsSyncRecordDTO> list = jdbcTemplate.query("select id, trace_id, table_name, biz_id, operation_type, source_type, status, stage, " +
                        "table_index_name, table_doc_id, global_index_name, global_doc_id, error_message, doc_hash, cost_ms, " +
                        "date_format(create_time, '%Y-%m-%d %H:%i:%s') as create_time " +
                        "from es_sync_record where table_name = ? and biz_id = ? order by id desc limit 1",
                new Object[]{normalize(tableName), bizId}, (rs, rowNum) -> {
                    EsSyncRecordDTO dto = new EsSyncRecordDTO();
                    dto.setId(rs.getLong("id"));
                    dto.setTraceId(rs.getString("trace_id"));
                    dto.setTableName(rs.getString("table_name"));
                    dto.setBizId(rs.getString("biz_id"));
                    dto.setOperationType(rs.getString("operation_type"));
                    dto.setSourceType(rs.getString("source_type"));
                    dto.setStatus(rs.getString("status"));
                    dto.setStage(rs.getString("stage"));
                    dto.setTableIndexName(rs.getString("table_index_name"));
                    dto.setTableDocId(rs.getString("table_doc_id"));
                    dto.setGlobalIndexName(rs.getString("global_index_name"));
                    dto.setGlobalDocId(rs.getString("global_doc_id"));
                    dto.setErrorMessage(rs.getString("error_message"));
                    dto.setDocHash(rs.getString("doc_hash"));
                    dto.setCostMs(rs.getLong("cost_ms"));
                    dto.setCreateTime(rs.getString("create_time"));
                    return dto;
                });
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public String hash(Map<String, Object> doc) {
        if (doc == null || doc.isEmpty()) {
            return null;
        }
        try {
            String json = objectMapper.writeValueAsString(new TreeMap<>(doc));
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(json.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (Exception e) {
            log.warn("ES文档hash计算失败", e);
            return null;
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private String limit(String message) {
        if (message == null || message.length() <= ERROR_MESSAGE_MAX_LENGTH) {
            return message;
        }
        return message.substring(0, ERROR_MESSAGE_MAX_LENGTH);
    }
}
