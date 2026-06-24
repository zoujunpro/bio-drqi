package com.bio.drqi.ai.dto.plan;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AiQueryPlanDTO {

    /**
     * 业务域，例如 plasmid_quality。
     */
    private String domain;

    /**
     * 查询类型：aggregate 表示统计聚合，detail 表示明细列表。
     */
    private String queryType;

    /**
     * 明细查询需要返回的字段，例如 projectCode/plasmidName/createTime。
     */
    private List<String> selectFields = new ArrayList<>();

    /**
     * 需要统计的指标，例如 totalCount/passRate。
     */
    private List<String> metrics = new ArrayList<>();

    /**
     * 分组维度，例如 projectCode/month。
     */
    private List<String> dimensions = new ArrayList<>();

    /**
     * 查询过滤条件。
     */
    private List<AiQueryFilterDTO> filters = new ArrayList<>();

    /**
     * 排序规则，可以按维度字段或指标排序。
     */
    private List<AiQueryOrderDTO> orderBy = new ArrayList<>();

    /**
     * 前端展示类型：table/bar/line/pie/auto。
     */
    private String chartType;

    /**
     * 返回行数限制，由后端兜底并限制最大值。
     */
    private Integer limit;
}
