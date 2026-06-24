package com.bio.drqi.ai.dto.plan;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AiReportPlanDTO {

    /**
     * 报表编码，后续可以接固定报表模板。
     */
    private String reportCode;

    /**
     * 报表名称，用作默认 Excel 文件名。
     */
    private String reportName;

    /**
     * 报表步骤。每个步骤都是一次受白名单保护的查询。
     */
    private List<AiReportStepDTO> steps = new ArrayList<>();

    /**
     * 多个查询结果的二次聚合规则。
     * 模型只能生成规则，真实数据合并由后端执行。
     */
    private List<AiReportAggregationDTO> aggregations = new ArrayList<>();
}
