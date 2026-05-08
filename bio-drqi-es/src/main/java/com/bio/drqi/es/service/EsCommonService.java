package com.bio.drqi.es.service;

import com.bio.drqi.es.dto.EsPageResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
@ConditionalOnProperty(prefix = "bio.es", name = "enabled", havingValue = "true")
public class EsCommonService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<Map<String, Object>>() {
    };
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 构造注入 ES 8 低层 REST 客户端，供当前服务统一执行索引和文档操作。
     */
    public EsCommonService(RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    /**
     * 确保索引存在：不存在时按传入 mapping 创建，存在时直接返回。
     */
    public void ensureIndex(String index, Map<String, Object> mapping) {
        try {
            if (indexExists(index)) {
                log.info("ES 索引已存在，跳过创建 index={}", index);
                return;
            }
            log.info("ES 索引不存在，开始创建 index={}", index);
            Map<String, Object> body = new LinkedHashMap<>();
            Map<String, Object> settings = new LinkedHashMap<>();
            settings.put("number_of_shards", 1);
            settings.put("number_of_replicas", 1);
            body.put("settings", settings);
            body.put("mappings", mapping);
            restClient.performRequest(jsonRequest("PUT", "/" + encodePath(index), body));
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
            if (indexExists(index)) {
                log.info("ES 索引已存在，开始删除 index={}", index);
                Response response = restClient.performRequest(new Request("DELETE", "/" + encodePath(index)));
                Map<String, Object> result = readMap(response.getEntity());
                if (!Boolean.TRUE.equals(result.get("acknowledged"))) {
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
     * 删除整个索引，索引不存在时直接跳过。
     */
    public void deleteIndex(String index) {
        try {
            if (!indexExists(index)) {
                log.info("ES 索引不存在，跳过删除 index={}", index);
                return;
            }
            log.info("ES 索引删除开始 index={}", index);
            Response response = restClient.performRequest(new Request("DELETE", "/" + encodePath(index)));
            Map<String, Object> result = readMap(response.getEntity());
            if (!Boolean.TRUE.equals(result.get("acknowledged"))) {
                throw new IllegalStateException("删除索引未确认: " + index);
            }
            log.info("ES 索引删除完成 index={}", index);
        } catch (Exception e) {
            throw new IllegalStateException("删除索引失败: " + index, e);
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
        StringBuilder body = new StringBuilder();
        int skipped = 0;
        int actions = 0;
        try {
            for (Map<String, Object> row : rows) {
                Object idValue = row.get(idField);
                if (idValue == null) {
                    skipped++;
                    continue;
                }
                Map<String, Object> action = new LinkedHashMap<>();
                Map<String, Object> indexMeta = new LinkedHashMap<>();
                indexMeta.put("_index", index);
                indexMeta.put("_id", normalizeId(idValue));
                action.put("index", indexMeta);
                body.append(objectMapper.writeValueAsString(action)).append('\n');
                body.append(objectMapper.writeValueAsString(sanitizeMap(row))).append('\n');
                actions++;
            }
            if (actions == 0) {
                log.warn("ES 批量写入跳过，没有可写入文档 index={}, idField={}, sourceRows={}, skippedRows={}",
                        index, idField, rows.size(), skipped);
                return;
            }
            log.info("ES 批量写入开始 index={}, idField={}, sourceRows={}, actions={}, skippedRows={}",
                    index, idField, rows.size(), actions, skipped);
            Request request = new Request("POST", "/_bulk");
            request.setEntity(new StringEntity(body.toString(), ContentType.create("application/x-ndjson", "UTF-8")));
            Response response = restClient.performRequest(request);
            Map<String, Object> result = readMap(response.getEntity());
            if (Boolean.TRUE.equals(result.get("errors"))) {
                log.error("ES 批量写入失败 index={}, result={}", index, result);
                throw new IllegalStateException("ES bulk 写入失败");
            }
            log.info("ES 批量写入完成 index={}, actions={}, took={}, costMs={}",
                    index, actions, result.get("took"), System.currentTimeMillis() - start);
        } catch (Exception e) {
            throw new IllegalStateException("ES bulk 写入异常", e);
        }
    }

    /**
     * 单条新增/覆盖写入：同ID会覆盖原文档，不存在则新增。
     */
    public void upsert(String index, String id, Map<String, Object> doc) {
        try {
            restClient.performRequest(jsonRequest(
                    "PUT",
                    "/" + encodePath(index) + "/_doc/" + encodePath(id),
                    sanitizeMap(doc)
            ));
        } catch (Exception e) {
            throw new IllegalStateException("ES upsert 失败", e);
        }
    }

    /**
     * 按文档ID删除指定索引中的数据。
     */
    public void delete(String index, String id) {
        try {
            restClient.performRequest(new Request("DELETE", "/" + encodePath(index) + "/_doc/" + encodePath(id)));
        } catch (ResponseException e) {
            if (e.getResponse().getStatusLine().getStatusCode() != 404) {
                throw new IllegalStateException("ES delete 失败", e);
            }
        } catch (Exception e) {
            throw new IllegalStateException("ES delete 失败", e);
        }
    }

    /**
     * 按查询条件删除索引中的文档，适用于清理统一搜索索引中某张业务表的旧数据。
     */
    public void deleteByQuery(String index, Map<String, Object> query) {
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("query", query == null ? matchAllQuery() : query);
            restClient.performRequest(jsonRequest(
                    "POST",
                    "/" + encodePath(index) + "/_delete_by_query?conflicts=proceed&refresh=true",
                    body
            ));
        } catch (ResponseException e) {
            if (e.getResponse().getStatusLine().getStatusCode() != 404) {
                throw new IllegalStateException("ES deleteByQuery 失败", e);
            }
        } catch (Exception e) {
            throw new IllegalStateException("ES deleteByQuery 失败", e);
        }
    }

    /**
     * 按文档ID查询并返回原始 source map，不存在时返回 null。
     */
    public Map<String, Object> getById(String index, String id) {
        try {
            Response response = restClient.performRequest(new Request("GET", "/" + encodePath(index) + "/_doc/" + encodePath(id)));
            Map<String, Object> result = readMap(response.getEntity());
            return (Map<String, Object>) result.get("_source");
        } catch (ResponseException e) {
            if (e.getResponse().getStatusLine().getStatusCode() == 404) {
                return null;
            }
            throw new IllegalStateException("ES queryById 失败", e);
        } catch (Exception e) {
            throw new IllegalStateException("ES queryById 失败", e);
        }
    }

    /**
     * 公共 search_after 分页查询。
     *
     * query 和 sort 使用 ES 原生 JSON 结构。下一页时把返回的 nextSearchAfter 原样传回。
     */
    public EsPageResult searchAfterPage(EsPageQuery pageQuery) {
        validatePageQuery(pageQuery);
        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("query", pageQuery.getQuery() == null ? matchAllQuery() : pageQuery.getQuery());
            body.put("size", pageQuery.getPageSize());
            body.put("track_total_hits", pageQuery.isTrackTotalHits());
            body.put("sort", pageQuery.getSorts());
            if (pageQuery.getSearchAfter() != null && pageQuery.getSearchAfter().length > 0) {
                body.put("search_after", pageQuery.getSearchAfter());
            }
            if (pageQuery.getIncludes() != null || pageQuery.getExcludes() != null) {
                Map<String, Object> source = new LinkedHashMap<>();
                if (pageQuery.getIncludes() != null) {
                    source.put("includes", pageQuery.getIncludes());
                }
                if (pageQuery.getExcludes() != null) {
                    source.put("excludes", pageQuery.getExcludes());
                }
                body.put("_source", source);
            }

            Response response = restClient.performRequest(jsonRequest(
                    "POST",
                    "/" + encodePath(pageQuery.getIndex()) + "/_search",
                    body
            ));
            Map<String, Object> resultBody = readMap(response.getEntity());
            Map<String, Object> hitsBody = (Map<String, Object>) resultBody.get("hits");
            List<Map<String, Object>> hits = (List<Map<String, Object>>) hitsBody.get("hits");
            List<String> ids = new ArrayList<>(hits.size());
            List<Map<String, Object>> records = new ArrayList<>(hits.size());
            Object[] nextSearchAfter = null;
            for (Map<String, Object> hit : hits) {
                ids.add(String.valueOf(hit.get("_id")));
                records.add((Map<String, Object>) hit.get("_source"));
                List<Object> sortValues = (List<Object>) hit.get("sort");
                nextSearchAfter = sortValues == null ? null : sortValues.toArray();
            }

            EsPageResult result = new EsPageResult();
            result.setIds(ids);
            result.setRecords(records);
            result.setNextSearchAfter(nextSearchAfter);
            result.setTotal(resolveTotal(hitsBody.get("total")));
            result.setHasNext(hits.size() == pageQuery.getPageSize());
            return result;
        } catch (Exception e) {
            throw new IllegalStateException("ES search_after 分页查询失败: " + pageQuery.getIndex(), e);
        }
    }

    private void validatePageQuery(EsPageQuery pageQuery) {
        if (pageQuery == null) {
            throw new IllegalArgumentException("ES 分页参数不能为空");
        }
        if (pageQuery.getIndex() == null || pageQuery.getIndex().trim().isEmpty()) {
            throw new IllegalArgumentException("ES index 不能为空");
        }
        if (pageQuery.getPageSize() <= 0) {
            throw new IllegalArgumentException("ES pageSize 必须大于 0");
        }
        if (pageQuery.getSorts() == null || pageQuery.getSorts().isEmpty()) {
            throw new IllegalArgumentException("ES search_after 分页必须指定稳定排序");
        }
    }

    private boolean indexExists(String index) throws Exception {
        try {
            Response response = restClient.performRequest(new Request("HEAD", "/" + encodePath(index)));
            return response.getStatusLine().getStatusCode() == 200;
        } catch (ResponseException e) {
            if (e.getResponse().getStatusLine().getStatusCode() == 404) {
                return false;
            }
            throw e;
        }
    }

    private Request jsonRequest(String method, String endpoint, Object body) throws Exception {
        Request request = new Request(method, endpoint);
        request.setJsonEntity(objectMapper.writeValueAsString(body));
        return request;
    }

    private Map<String, Object> readMap(HttpEntity entity) throws Exception {
        String body = EntityUtils.toString(entity);
        return objectMapper.readValue(body, MAP_TYPE);
    }

    private long resolveTotal(Object total) {
        if (total instanceof Map) {
            Object value = ((Map<?, ?>) total).get("value");
            return value instanceof Number ? ((Number) value).longValue() : 0;
        }
        return total instanceof Number ? ((Number) total).longValue() : 0;
    }

    private Map<String, Object> matchAllQuery() {
        Map<String, Object> query = new LinkedHashMap<>();
        query.put("match_all", Collections.emptyMap());
        return query;
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

    private String encodePath(String path) {
        try {
            return URLEncoder.encode(path, "UTF-8").replace("+", "%20");
        } catch (Exception e) {
            throw new IllegalArgumentException("ES path 编码失败: " + path, e);
        }
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

    public static class EsPageQuery {
        private String index;
        private Map<String, Object> query;
        private List<Map<String, Object>> sorts = Collections.emptyList();
        private int pageSize = 20;
        private Object[] searchAfter;
        private String[] includes;
        private String[] excludes;
        private boolean trackTotalHits = true;

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public Map<String, Object> getQuery() {
            return query;
        }

        public void setQuery(Map<String, Object> query) {
            this.query = query;
        }

        public List<Map<String, Object>> getSorts() {
            return sorts;
        }

        public void setSorts(List<Map<String, Object>> sorts) {
            this.sorts = sorts;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public Object[] getSearchAfter() {
            return searchAfter;
        }

        public void setSearchAfter(Object[] searchAfter) {
            this.searchAfter = searchAfter;
        }

        public String[] getIncludes() {
            return includes;
        }

        public void setIncludes(String[] includes) {
            this.includes = includes;
        }

        public String[] getExcludes() {
            return excludes;
        }

        public void setExcludes(String[] excludes) {
            this.excludes = excludes;
        }

        public boolean isTrackTotalHits() {
            return trackTotalHits;
        }

        public void setTrackTotalHits(boolean trackTotalHits) {
            this.trackTotalHits = trackTotalHits;
        }
    }


}
