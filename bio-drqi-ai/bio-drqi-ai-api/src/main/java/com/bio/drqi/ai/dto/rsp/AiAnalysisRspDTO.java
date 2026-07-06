package com.bio.drqi.ai.dto.rsp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.exception.AiErrorCode;
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
     * 本次AI处理是否成功。业务拒绝也会正常返回HTTP成功，但 success=false。
     */
    private Boolean success = Boolean.TRUE;

    /**
     * 前端可识别的错误码，避免只靠中文文案判断失败原因。
     */
    private String errorCode = AiErrorCode.SUCCESS.getCode();

    /**
     * 前端行为建议：answer=直接展示答案，clarify=需要用户补充，reject=拒绝执行。
     */
    private String action = "answer";

    /**
     * 澄清类型，例如 unknown_intent、missing_context、missing_domain、unsafe_action。
     */
    private String clarifyType;

    /**
     * 后端推荐的候选问法或可选业务方向，前端可以渲染成快捷按钮。
     */
    private List<String> clarifyOptions = new ArrayList<>();

    /**
     * 建议用户下一步补充的问题。
     */
    private String nextQuestion;

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
