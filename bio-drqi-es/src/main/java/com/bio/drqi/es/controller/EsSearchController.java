package com.bio.drqi.es.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.es.dto.EsPageResult;
import com.bio.drqi.es.dto.req.GlobalSearchPageReqDTO;
import com.bio.drqi.es.service.EsCommonService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/search")
@ConditionalOnProperty(prefix = "bio.es", name = "enabled", havingValue = "true")
public class EsSearchController {

    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;
    private static final int MAX_HIT_TABLE_SIZE = 100;
    private static final String GLOBAL_SEARCH_INDEX_SUFFIX = "_global_search";

    private final EsCommonService esCommonService;

    public EsSearchController(EsCommonService esCommonService) {
        this.esCommonService = esCommonService;
    }

    /**
     * 全局搜索分页查询。
     *
     * 示例：
     * {
     *   "systemCode": "drqi",
     *   "businessCodes": ["project"],
     *   "keyword": "玉米 张三",
     *   "tables": ["cer_project_tb", "cer_vector_task_tb"],
     *   "pageSize": 20,
     *   "searchAfter": ["1.0", "project", "cer_project_tb", "1"]
     * }
     */
    @PostMapping("/listPage")
    public ResponseResult<EsPageResult> globalPage(@RequestBody @Validated GlobalSearchPageReqDTO reqDTO) {
        String index = resolveIndex(reqDTO.getSystemCode());
        Map<String, Object> pageQueryBody = buildQuery(reqDTO, true);

        EsCommonService.EsPageQuery pageQuery = new EsCommonService.EsPageQuery();
        pageQuery.setIndex(index);
        pageQuery.setQuery(pageQueryBody);
        pageQuery.setSorts(buildSorts());
        pageQuery.setPageSize(resolvePageSize(reqDTO.getPageSize()));
        pageQuery.setSearchAfter(reqDTO.getSearchAfter());
        pageQuery.setIncludes(new String[]{
                "system_code",
                "business_code",
                "table_name",
                "biz_id",
                "title",
                "summary",
                "route",
                "display",
                "create_time"
        });

        EsPageResult result = esCommonService.searchAfterPage(pageQuery);
        result.setHitTables(esCommonService.termsAgg(index, buildQuery(reqDTO, false), "table_name", MAX_HIT_TABLE_SIZE));
        return ResponseResult.getSuccess(result);
    }

    /**
     * 构建默认查询条件。
     * 默认包含 tables 过滤，用于真正的搜索结果分页查询。
     */
    private Map<String, Object> buildQuery(GlobalSearchPageReqDTO reqDTO) {
        return buildQuery(reqDTO, true);
    }

    /**
     * 构建 Elasticsearch bool 查询。
     *
     * 查询参数说明：
     * 1. keyword：用户输入的搜索词，匹配 search_content 字段。
     *    operator=and 表示多个词都要命中，例如“玉米 张三”会要求两个词都在文档内容中出现。
     * 2. systemCode：系统编码，固定加 term 过滤，只查当前系统对应的数据。
     *    同时 systemCode 还会用于拼接索引名，例如 drqi -> drqi_global_search。
     * 3. businessCodes：业务模块编码列表，例如 project、seed、plant。
     *    为空时不限制业务模块；有值时只查询这些 business_code。
     * 4. tables：表名列表，例如 cer_project_tb、cer_vector_task_tb。
     *    includeTableFilter=true 时生效，用于页面点击某些表后只看这些表的数据。
     *    includeTableFilter=false 时不生效，通常用于统计命中的表分布，避免被当前表筛选影响。
     *
     * @param reqDTO 请求参数
     * @param includeTableFilter 是否把 reqDTO.tables 加入查询过滤条件
     * @return ES query 部分，不包含 sort、size、search_after、_source includes
     */
    private Map<String, Object> buildQuery(GlobalSearchPageReqDTO reqDTO, boolean includeTableFilter) {
        Map<String, Object> matchValue = new LinkedHashMap<>();
        matchValue.put("query", reqDTO.getKeyword().trim());
        matchValue.put("operator", "and");

        Map<String, Object> matchFields = new LinkedHashMap<>();
        matchFields.put("search_content", matchValue);

        Map<String, Object> matchQuery = new LinkedHashMap<>();
        matchQuery.put("match", matchFields);

        List<Map<String, Object>> filters = new ArrayList<>();
        Map<String, Object> systemTerm = new LinkedHashMap<>();
        systemTerm.put("system_code", normalize(reqDTO.getSystemCode()));
        filters.add(Collections.singletonMap("term", systemTerm));

        if (reqDTO.getBusinessCodes() != null && !reqDTO.getBusinessCodes().isEmpty()) {
            Map<String, Object> terms = new LinkedHashMap<>();
            terms.put("business_code", normalizeList(reqDTO.getBusinessCodes()));
            filters.add(Collections.singletonMap("terms", terms));
        }

        if (includeTableFilter && reqDTO.getTables() != null && !reqDTO.getTables().isEmpty()) {
            Map<String, Object> terms = new LinkedHashMap<>();
            terms.put("table_name", normalizeList(reqDTO.getTables()));
            filters.add(Collections.singletonMap("terms", terms));
        }

        Map<String, Object> bool = new LinkedHashMap<>();
        bool.put("must", Collections.singletonList(matchQuery));
        bool.put("filter", filters);

        Map<String, Object> query = new LinkedHashMap<>();
        query.put("bool", bool);
        return query;
    }

    private List<Map<String, Object>> buildSorts() {
        List<Map<String, Object>> sorts = new ArrayList<>();
        sorts.add(Collections.singletonMap("_score", Collections.singletonMap("order", "desc")));
        sorts.add(Collections.singletonMap("business_code", Collections.singletonMap("order", "asc")));
        sorts.add(Collections.singletonMap("table_name", Collections.singletonMap("order", "asc")));
        sorts.add(Collections.singletonMap("biz_id", Collections.singletonMap("order", "asc")));
        return sorts;
    }

    private int resolvePageSize(Integer pageSize) {
        if (pageSize == null || pageSize <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }

    private String resolveIndex(String systemCode) {
        return normalize(systemCode) + GLOBAL_SEARCH_INDEX_SUFFIX;
    }

    private List<String> normalizeList(List<String> values) {
        List<String> result = new ArrayList<>();
        for (String value : values) {
            if (value != null && !value.trim().isEmpty()) {
                result.add(normalize(value));
            }
        }
        return result;
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }



}
