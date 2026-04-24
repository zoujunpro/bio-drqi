package com.bio.drqi.es.sync;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "sync.es", name = "enabled", havingValue = "true")
public class EsFullSyncService {

    private final JdbcTemplate jdbcTemplate;
    private final RestHighLevelClient restHighLevelClient;
    private final EsSyncProperties properties;
    private final ReentrantLock fullSyncLock = new ReentrantLock();

    public EsFullSyncService(JdbcTemplate jdbcTemplate,
                             RestHighLevelClient restHighLevelClient,
                             EsSyncProperties properties) {
        this.jdbcTemplate = jdbcTemplate;
        this.restHighLevelClient = restHighLevelClient;
        this.properties = properties;
    }

    public Map<String, Object> syncAllConfiguredTables() {
        return syncByRuleKeysInternal(null);
    }

    public Map<String, Object> syncByRuleKeys(Set<String> ruleKeys) {
        return syncByRuleKeysInternal(ruleKeys);
    }

    private Map<String, Object> syncByRuleKeysInternal(Set<String> ruleKeys) {
        if (!fullSyncLock.tryLock()) {
            throw new IllegalStateException("已有全量同步任务在执行中，请稍后重试");
        }
        try {
            List<Map<String, Object>> details = new ArrayList<>();
            long total = 0;
            for (Map.Entry<String, EsSyncProperties.TableRule> entry : properties.getTableRules().entrySet()) {
                String ruleKey = entry.getKey();
                if (ruleKeys != null && !ruleKeys.contains(ruleKey)) {
                    continue;
                }
                long count = syncOneTable(ruleKey, entry.getValue());
                total += count;
                Map<String, Object> detail = new LinkedHashMap<>();
                detail.put("ruleKey", ruleKey);
                detail.put("count", count);
                details.add(detail);
            }
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("total", total);
            result.put("details", details);
            return result;
        } finally {
            fullSyncLock.unlock();
        }
    }

    private long syncOneTable(String ruleKey, EsSyncProperties.TableRule rule) {
        if (!rule.isFullSync()) {
            return 0;
        }
        String sourceTable = nonEmpty(rule.getSourceTable()) ? rule.getSourceTable() : ruleKey;
        int batchSize = Math.max(100, properties.getFull().getBatchSize());
        log.info("ES全量同步开始，ruleKey={}, sourceTable={}, index={}", ruleKey, sourceTable, rule.getIndex());

        int offset = 0;
        long total = 0;
        while (true) {
            String sql = buildPageSql(sourceTable, rule.getWhereClause(), batchSize, offset);
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            if (rows.isEmpty()) {
                break;
            }
            bulkUpsert(ruleKey, rule, rows);
            total += rows.size();
            offset += batchSize;
            if (rows.size() < batchSize) {
                break;
            }
        }
        log.info("ES全量同步完成，ruleKey={}, index={}, total={}", ruleKey, rule.getIndex(), total);
        return total;
    }

    private void bulkUpsert(String ruleKey, EsSyncProperties.TableRule rule, List<Map<String, Object>> rows) {
        BulkRequest bulkRequest = new BulkRequest();
        for (Map<String, Object> row : rows) {
            Object idValue = row.get(rule.getIdField());
            if (idValue == null) {
                log.warn("全量同步忽略：缺少主键字段 {}, ruleKey={}, row={}", rule.getIdField(), ruleKey, row);
                continue;
            }
            String id = normalizeId(idValue);
            bulkRequest.add(new IndexRequest(rule.getIndex()).id(id).source(row));
        }
        if (bulkRequest.numberOfActions() == 0) {
            return;
        }
        try {
            BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (response.hasFailures()) {
                throw new IllegalStateException("ES全量 bulk 失败: " + response.buildFailureMessage());
            }
        } catch (Exception e) {
            throw new IllegalStateException("ES全量同步写入异常，ruleKey=" + ruleKey, e);
        }
    }

    private String buildPageSql(String sourceTable, String whereClause, int batchSize, int offset) {
        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(sourceTable);
        if (nonEmpty(whereClause)) {
            sql.append(" WHERE ").append(whereClause);
        }
        sql.append(" LIMIT ").append(batchSize).append(" OFFSET ").append(offset);
        return sql.toString();
    }

    private String normalizeId(Object idValue) {
        if (idValue instanceof BigDecimal) {
            return ((BigDecimal) idValue).stripTrailingZeros().toPlainString();
        }
        return String.valueOf(idValue);
    }

    private boolean nonEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
