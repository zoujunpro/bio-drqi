package com.bio.drqi.ai.dto.plan;

import lombok.Data;

@Data
public class AiReportStepDTO {

    /**
     * 步骤编码，同一个报表内必须唯一。
     */
    private String stepCode;

    /**
     * 当前步骤写入 Excel 的 sheet 名称。
     */
    private String sheetName;

    /**
     * 当前步骤对应的一次查询计划。
     */
    private AiQueryPlanDTO queryPlan;
}
