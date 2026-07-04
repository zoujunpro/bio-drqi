package com.bio.drqi.ai.dto.router;

import lombok.Data;

/**
 * AI意图识别结果。
 */
@Data
public class AiIntentDTO {

    /**
     * chat：普通问答
     * business_query：业务数据查询、统计、分析、导出
     * workflow：审批、工单、待办查询
     * report_export：生成、下载、导出报表或Excel
     * unknown：模型无法判断
     */
    private String intent;

    /**
     * 置信度，0到1之间。
     */
    private Double confidence;

    /**
     * 简短原因，主要用于日志排查。
     */
    private String reason;
}
