package com.bio.drqi.ai.dto.rsp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AiAnalysisRspDTO {

    /**
     * 当前会话ID。前端后续请求带回该值即可延续短期上下文。
     */
    private String conversationId;

    /**
     * 给前端展示的文字回答。
     */
    private String answer;

    /**
     * 模型生成并经过后端校验后的查询计划，方便前端调试或展示。
     */
    private AiQueryPlanDTO queryPlan;

    /**
     * 表格结果。当前阶段先预留，后续由查询执行器填充。
     */
    private List<AiTableDTO> tables = new ArrayList<>();

    /**
     * 图表结果。当前阶段先预留，后续转换为前端图表配置或数据集。
     */
    private List<AiChartDTO> charts = new ArrayList<>();

    /**
     * 后端审计使用，不返回前端。
     */
    @JsonIgnore
    private String executedSql;

    /**
     * 后端审计使用，不返回前端。
     */
    @JsonIgnore
    private String executedSqlParams;
}
