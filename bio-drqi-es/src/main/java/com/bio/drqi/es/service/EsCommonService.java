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

    public EsCommonService(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    public void ensureIndex(String index, Map<String, Object> mapping) {
        try {
            boolean exists = restHighLevelClient.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
            if (exists) {
                return;
            }
            CreateIndexRequest request = new CreateIndexRequest(index);
            Map<String, Object> settings = new LinkedHashMap<>();
            settings.put("number_of_shards", 1);
            settings.put("number_of_replicas", 1);
            request.settings(settings);
            request.mapping(mapping);
            restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new IllegalStateException("创建/检查索引失败: " + index, e);
        }
    }

    public void recreateIndex(String index, Map<String, Object> mapping) {
        try {
            boolean exists = restHighLevelClient.indices().exists(new GetIndexRequest(index), RequestOptions.DEFAULT);
            if (exists) {
                AcknowledgedResponse deleteResponse = restHighLevelClient.indices()
                        .delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);
                if (!deleteResponse.isAcknowledged()) {
                    throw new IllegalStateException("删除索引未确认: " + index);
                }
            }
            ensureIndex(index, mapping);
        } catch (Exception e) {
            throw new IllegalStateException("重建索引失败: " + index, e);
        }
    }

    public void saveBatch(String index, String idField, List<Map<String, Object>> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        BulkRequest request = new BulkRequest();
        for (Map<String, Object> row : rows) {
            Object idValue = row.get(idField);
            if (idValue == null) {
                continue;
            }
            request.add(new IndexRequest(index).id(normalizeId(idValue)).source(sanitizeMap(row)));
        }
        if (request.numberOfActions() == 0) {
            return;
        }
        try {
            BulkResponse response = restHighLevelClient.bulk(request, RequestOptions.DEFAULT);
            if (response.hasFailures()) {
                throw new IllegalStateException("ES bulk 写入失败: " + response.buildFailureMessage());
            }
        } catch (Exception e) {
            throw new IllegalStateException("ES bulk 写入异常", e);
        }
    }

    public void upsert(String index, String id, Map<String, Object> doc) {
        try {
            restHighLevelClient.index(new IndexRequest(index).id(id).source(sanitizeMap(doc)), RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new IllegalStateException("ES upsert 失败", e);
        }
    }

    public void delete(String index, String id) {
        try {
            restHighLevelClient.delete(new DeleteRequest(index, id), RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new IllegalStateException("ES delete 失败", e);
        }
    }

    public Map<String, Object> getById(String index, String id) {
        try {
            GetResponse response = restHighLevelClient.get(new GetRequest(index, id), RequestOptions.DEFAULT);
            return response.getSourceAsMap();
        } catch (Exception e) {
            throw new IllegalStateException("ES queryById 失败", e);
        }
    }

    private String normalizeId(Object idValue) {
        if (idValue instanceof BigDecimal) {
            return ((BigDecimal) idValue).stripTrailingZeros().toPlainString();
        }
        return String.valueOf(idValue);
    }

    private Map<String, Object> sanitizeMap(Map<String, Object> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            result.put(entry.getKey(), sanitizeValue(entry.getValue()));
        }
        return result;
    }

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
