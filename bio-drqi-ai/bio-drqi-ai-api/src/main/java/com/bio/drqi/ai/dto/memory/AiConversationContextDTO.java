package com.bio.drqi.ai.dto.memory;

import cn.hutool.json.JSONUtil;
import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class AiConversationContextDTO {

    /**
     * 一轮连续 AI 对话的唯一标识，由前端持续传回。
     */
    private String conversationId;

    /**
     * 当前会话最近一次命中的业务域，例如 sample、project、vector_task。
     */
    private String currentDomain;

    /**
     * 最近一次成功生成并执行的查询计划，用于处理“刚才那个”“继续查”等追问。
     */
    private AiQueryPlanDTO lastQueryPlan;

    /**
     * 用户已经确认过的业务术语解释，例如“取样数量”应理解为“统计取样编号数量”。
     */
    private Map<String, String> confirmedTerms = new LinkedHashMap<>();

    /**
     * 等待用户补充的问题，例如意图不清时提示用户选择查询对象。
     */
    private String pendingClarification;

    /**
     * 上一次回复或查询结果摘要，适合放进 prompt 作为短上下文。
     */
    private String lastResultSummary;

    /**
     * 上一次表格结果快照，适合支持“把刚才结果整理一下”“只保留某些列”等追问。
     */
    private String lastResultSnapshot;

    public String toPromptText() {
        if (lastQueryPlan == null && confirmedTerms.isEmpty() && pendingClarification == null
                && lastResultSummary == null && lastResultSnapshot == null) {
            return "无";
        }
        Map<String, Object> prompt = new LinkedHashMap<>();
        prompt.put("currentDomain", currentDomain);
        prompt.put("lastQueryPlan", lastQueryPlan);
        prompt.put("confirmedTerms", confirmedTerms);
        prompt.put("pendingClarification", pendingClarification);
        prompt.put("lastResultSummary", lastResultSummary);
        prompt.put("lastResultSnapshot", lastResultSnapshot);
        return JSONUtil.toJsonStr(prompt);
    }
}
