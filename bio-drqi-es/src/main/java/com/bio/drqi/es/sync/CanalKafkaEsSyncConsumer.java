package com.bio.drqi.es.sync;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
@Slf4j
@ConditionalOnProperty(prefix = "sync.es", name = {"enabled", "realtime-enabled"}, havingValue = "true")
public class CanalKafkaEsSyncConsumer {

    private static final TypeReference<List<CanalBinlogEvent>> EVENT_LIST_TYPE = new TypeReference<List<CanalBinlogEvent>>() {
    };
    private static final TypeReference<CanalBinlogEvent> EVENT_TYPE = new TypeReference<CanalBinlogEvent>() {
    };

    private final ObjectMapper objectMapper;
    private final RestHighLevelClient restHighLevelClient;
    private final EsSyncProperties properties;

    public CanalKafkaEsSyncConsumer(ObjectMapper objectMapper,
                                    RestHighLevelClient restHighLevelClient,
                                    EsSyncProperties properties) {
        this.objectMapper = objectMapper;
        this.restHighLevelClient = restHighLevelClient;
        this.properties = properties;
    }

    @KafkaListener(
            id = "canalEsSyncListener",
            topics = "${sync.es.topic:canal_binlog}",
            groupId = "${sync.es.group-id:${sync.es.groupId:bio-drqi-es-sync}}",
            autoStartup = "false"
    )
    public void consume(String message) {
        List<CanalBinlogEvent> events = parseEvents(message);
        if (events.isEmpty()) {
            return;
        }
        BulkRequest bulkRequest = new BulkRequest();
        for (CanalBinlogEvent event : events) {
            appendRequests(event, bulkRequest);
        }
        if (bulkRequest.numberOfActions() == 0) {
            return;
        }
        executeBulk(bulkRequest);
    }

    private List<CanalBinlogEvent> parseEvents(String message) {
        try {
            String text = message == null ? "" : message.trim();
            if (text.startsWith("[")) {
                return objectMapper.readValue(text, EVENT_LIST_TYPE);
            }
            return Collections.singletonList(objectMapper.readValue(text, EVENT_TYPE));
        } catch (Exception e) {
            log.error("canal 消息解析失败，message={}", message, e);
            throw new IllegalStateException("canal 消息解析失败", e);
        }
    }

    private void appendRequests(CanalBinlogEvent event, BulkRequest bulkRequest) {
        if (event == null || Boolean.TRUE.equals(event.getIsDdl())) {
            return;
        }
        String ruleKey = buildRuleKey(event.getDatabase(), event.getTable());
        EsSyncProperties.TableRule rule = properties.getTableRules().get(ruleKey);
        if (rule == null) {
            return;
        }
        List<Map<String, Object>> rows = event.getData();
        if (rows == null || rows.isEmpty()) {
            return;
        }
        String type = event.getType() == null ? "" : event.getType().toUpperCase(Locale.ROOT);
        for (Map<String, Object> row : rows) {
            if (row == null || row.isEmpty()) {
                continue;
            }
            Object idValue = row.get(rule.getIdField());
            if (idValue == null) {
                log.warn("ES同步忽略：缺少主键字段 {}, ruleKey={}, row={}", rule.getIdField(), ruleKey, row);
                continue;
            }
            String id = String.valueOf(idValue);
            if ("DELETE".equals(type)) {
                bulkRequest.add(new DeleteRequest(rule.getIndex(), id));
            } else {
                bulkRequest.add(new IndexRequest(rule.getIndex()).id(id).source(row));
            }
        }
    }

    private void executeBulk(BulkRequest bulkRequest) {
        try {
            BulkResponse response = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (!response.hasFailures()) {
                return;
            }
            StringBuilder failure = new StringBuilder("ES bulk 写入失败: ");
            for (BulkItemResponse item : response.getItems()) {
                if (item.isFailed()) {
                    failure.append("[")
                            .append(item.getOpType())
                            .append(" index=")
                            .append(item.getIndex())
                            .append(" id=")
                            .append(item.getId())
                            .append(" reason=")
                            .append(item.getFailureMessage())
                            .append("]");
                }
            }
            throw new IllegalStateException(failure.toString());
        } catch (Exception e) {
            throw new IllegalStateException("ES bulk 请求异常", e);
        }
    }

    private String buildRuleKey(String database, String table) {
        return String.valueOf(database) + "." + String.valueOf(table);
    }
}
