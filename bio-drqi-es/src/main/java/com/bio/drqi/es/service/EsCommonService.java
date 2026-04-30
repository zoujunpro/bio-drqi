package com.bio.drqi.es.service;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "bio.es", name = "enabled", havingValue = "true")
public class EsCommonService {

    private final RestHighLevelClient restHighLevelClient;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 构造注入 ES 客户端，供当前服务统一执行索引和文档操作。
     */
    public EsCommonService(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    /**
     * 确保索引存在：不存在时按传入 mapping 创建，存在时直接返回。
     */
    public void ensureIndex(String index, Map<String, Object> mapping) {
        try {
            boolean exists = restHighLevelClient.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
            if (exists) {
                log.info("ES 索引已存在，跳过创建 index={}", index);
                return;
            }
            log.info("ES 索引不存在，开始创建 index={}", index);
            CreateIndexRequest request = new CreateIndexRequest(index);
            Map<String, Object> settings = new LinkedHashMap<>();
            settings.put("number_of_shards", 1);
            settings.put("number_of_replicas", 1);
            request.settings(settings);
            request.mapping(mapping);
            restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
            log.info("ES 索引创建完成 index={}", index);
        } catch (Exception e) {
            throw new IllegalStateException("创建/检查索引失败: " + index, e);
        }
    }

    /**
     * 重建索引：若索引已存在先删除，再按 mapping 重新创建。
     */
    public void recreateIndex(String index, Map<String, Object> mapping) {
        try {
            log.info("ES 索引重建开始 index={}", index);
            boolean exists = restHighLevelClient.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
            if (exists) {
                log.info("ES 索引已存在，开始删除 index={}", index);
                AcknowledgedResponse deleteResponse = restHighLevelClient.indices()
                        .delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);
                if (!deleteResponse.isAcknowledged()) {
                    throw new IllegalStateException("删除索引未确认: " + index);
                }
                log.info("ES 索引删除完成 index={}", index);
            }
            ensureIndex(index, mapping);
            log.info("ES 索引重建完成 index={}", index);
        } catch (Exception e) {
            throw new IllegalStateException("重建索引失败: " + index, e);
        }
    }

    /**
     * 批量写入文档：按 idField 取文档ID，自动做数据清洗后执行 bulk 写入。
     */
    public void saveBatch(String index, String idField, List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) {
            log.info("ES 批量写入跳过，数据为空 index={}, idField={}", index, idField);
            return;
        }
        long start = System.currentTimeMillis();
        BulkRequest request = new BulkRequest();
        int skipped = 0;
        for (Map<String, Object> row : rows) {
            Object idValue = row.get(idField);
            if (idValue == null) {
                skipped++;
                continue;
            }
            request.add(new IndexRequest(index).id(normalizeId(idValue)).source(sanitizeMap(row)));
        }
        if (request.numberOfActions() == 0) {
            log.warn("ES 批量写入跳过，没有可写入文档 index={}, idField={}, sourceRows={}, skippedRows={}",
                    index, idField, rows.size(), skipped);
            return;
        }
        try {
            log.info("ES 批量写入开始 index={}, idField={}, sourceRows={}, actions={}, skippedRows={}",
                    index, idField, rows.size(), request.numberOfActions(), skipped);
            BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            if (response.hasFailures()) {
                log.error("ES 批量写入失败 index={}, failureMessage={}", index, response.buildFailureMessage());
                throw new IllegalStateException("ES bulk 写入失败: " + response.buildFailureMessage());
            }
            log.info("ES 批量写入完成 index={}, actions={}, took={}, costMs={}",
                    index, request.numberOfActions(), response.getTook(), System.currentTimeMillis() - start);
        } catch (Exception e) {
            throw new IllegalStateException("ES bulk 写入异常", e);
        }
    }

    /**
     * 单条新增/覆盖写入：同ID会覆盖原文档，不存在则新增。
     */
    public void upsert(String index, String id, Map<String, Object> doc) {
        try {
            restHighLevelClient.index(new IndexRequest(index).id(id).source(sanitizeMap(doc)), RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new IllegalStateException("ES upsert 失败", e);
        }
    }

    /**
     * 按文档ID删除指定索引中的数据。
     */
    public void delete(String index, String id) {
        try {
            restHighLevelClient.delete(new DeleteRequest(index, id), RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new IllegalStateException("ES delete 失败", e);
        }
    }

    /**
     * 按文档ID查询并返回原始 source map，不存在时返回 null。
     */
    public Map<String, Object> getById(String index, String id) {
        try {
            GetResponse response = restHighLevelClient.get(new GetRequest(index, id), RequestOptions.DEFAULT);
            return response.getSourceAsMap();
        } catch (Exception e) {
            throw new IllegalStateException("ES queryById 失败", e);
        }
    }

    /**
     * 统一规范化文档ID，避免 BigDecimal 出现科学计数法导致ID不一致。
     */
    private String normalizeId(Object idValue) {
        if (idValue instanceof BigDecimal) {
            return ((BigDecimal) idValue).stripTrailingZeros().toPlainString();
        }
        return String.valueOf(idValue);
    }

    /**
     * 递归清洗 map 中的值，确保可被 ES 正确序列化。
     */
    private Map<String, Object> sanitizeMap(Map<String, Object> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            result.put(entry.getKey(), sanitizeValue(entry.getValue()));
        }
        return result;
    }

    /**
     * 清洗单个值：统一时间格式，并递归处理嵌套 Map/List。
     */
    private Object sanitizeValue(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof java.sql.Timestamp) {
            LocalDateTime dateTime = ((java.sql.Timestamp) value).toLocalDateTime();
            return DATE_TIME_FORMATTER.format(dateTime);
        }
        if (value instanceof java.sql.Date) {
            LocalDate localDate = ((java.sql.Date) value).toLocalDate();
            return DATE_FORMATTER.format(localDate);
        }
        if (value instanceof Date) {
            LocalDateTime dateTime = LocalDateTime.ofInstant(((Date) value).toInstant(), ZoneId.systemDefault());
            return DATE_TIME_FORMATTER.format(dateTime);
        }
        if (value instanceof Map) {
            return sanitizeMap((Map<String, Object>) value);
        }
        if (value instanceof List) {
            List<?> source = (List<?>) value;
            List<Object> list = new ArrayList<>(source.size());
            for (Object item : source) {
                list.add(sanitizeValue(item));
            }
            return list;
        }
        return value;
    }
}
