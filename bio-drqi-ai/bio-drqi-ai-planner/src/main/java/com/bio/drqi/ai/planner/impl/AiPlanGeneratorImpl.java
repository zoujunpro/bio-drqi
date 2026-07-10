package com.bio.drqi.ai.planner.impl;

import com.bio.drqi.ai.dto.planner.AiPlanReqDTO;
import com.bio.drqi.ai.dto.planner.AiPlanRspDTO;
import com.bio.drqi.ai.dto.planner.AiPlanStepDTO;
import com.bio.drqi.ai.dto.planner.AiPlanTaskDTO;
import com.bio.drqi.ai.dto.planner.AiToolSelectionDTO;
import com.bio.drqi.ai.dto.semantic.AiToolDefinitionDTO;
import com.bio.drqi.ai.planner.AiPlanGenerator;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 默认执行计划生成器。
 */
@Service
public class AiPlanGeneratorImpl implements AiPlanGenerator {

    private static final String PLAN_TYPE_TOOL = "TOOL";

    private static final String PLAN_TYPE_DIFY = "DIFY";

    private static final String PLAN_TYPE_CLARIFY = "CLARIFY";

    private static final String PLAN_TYPE_UNKNOWN = "UNKNOWN";

    @Override
    public AiPlanRspDTO generate(AiPlanReqDTO reqDTO, List<AiPlanTaskDTO> tasks, List<AiToolSelectionDTO> selections) {
        AiPlanRspDTO rspDTO = new AiPlanRspDTO();
        if (reqDTO == null || !hasText(reqDTO.getNormalizedQuery())) {
            buildClarifyPlan(rspDTO, "用户问题为空，无法生成执行计划", "请补充你要处理的问题。");
            return rspDTO;
        }

        if (hasToolSelection(selections)) {
            buildToolPlan(reqDTO, rspDTO, selections);
            return rspDTO;
        }

        if (reqDTO.getIntentResult() != null && hasText(reqDTO.getIntentResult().getIntentCode())) {
            buildDifyPlan(reqDTO, rspDTO);
            return rspDTO;
        }

        rspDTO.setPlanType(PLAN_TYPE_UNKNOWN);
        rspDTO.setExecutable(Boolean.FALSE);
        rspDTO.setReason("未识别到明确意图，也没有候选工具");
        rspDTO.setClarifyQuestion("请补充业务对象、查询范围或你希望我执行的操作。");
        return rspDTO;
    }

    private void buildToolPlan(AiPlanReqDTO reqDTO, AiPlanRspDTO rspDTO, List<AiToolSelectionDTO> selections) {
        rspDTO.setPlanType(PLAN_TYPE_TOOL);
        rspDTO.setExecutable(Boolean.TRUE);
        rspDTO.setReason("根据任务和工具选择结果生成工具调用计划");

        int stepNo = 1;
        for (AiToolSelectionDTO selection : selections) {
            AiToolDefinitionDTO tool = selection.getTool();
            if (tool == null) {
                continue;
            }
            AiPlanStepDTO stepDTO = new AiPlanStepDTO();
            stepDTO.setStepNo(stepNo);
            stepDTO.setStepType(PLAN_TYPE_TOOL);
            stepDTO.setTargetCode(tool.getTargetCode());
            stepDTO.setToolCode(tool.getToolCode());
            stepDTO.setInputJson(buildStepInputJson(reqDTO, selection));
            stepDTO.setOutputKey("toolResult" + stepNo);
            stepDTO.setDescription("调用工具：" + safeText(tool.getToolName(), tool.getToolCode()));
            rspDTO.getSteps().add(stepDTO);
            stepNo++;
        }
    }

    private void buildDifyPlan(AiPlanReqDTO reqDTO, AiPlanRspDTO rspDTO) {
        rspDTO.setPlanType(PLAN_TYPE_DIFY);
        rspDTO.setExecutable(Boolean.TRUE);
        rspDTO.setReason("识别到业务意图，但没有候选工具，交由 Dify 处理");

        AiPlanStepDTO stepDTO = new AiPlanStepDTO();
        stepDTO.setStepNo(1);
        stepDTO.setStepType(PLAN_TYPE_DIFY);
        stepDTO.setTargetCode(reqDTO.getIntentResult().getIntentCode());
        stepDTO.setInputJson(buildStepInputJson(reqDTO, null));
        stepDTO.setOutputKey("difyResult");
        stepDTO.setDescription("调用 Dify 应用处理业务意图：" + reqDTO.getIntentResult().getIntentCode());
        rspDTO.getSteps().add(stepDTO);
    }

    private void buildClarifyPlan(AiPlanRspDTO rspDTO, String reason, String clarifyQuestion) {
        rspDTO.setPlanType(PLAN_TYPE_CLARIFY);
        rspDTO.setExecutable(Boolean.FALSE);
        rspDTO.setReason(reason);
        rspDTO.setClarifyQuestion(clarifyQuestion);
    }

    private boolean hasToolSelection(List<AiToolSelectionDTO> selections) {
        if (selections == null || selections.isEmpty()) {
            return false;
        }
        for (AiToolSelectionDTO selection : selections) {
            if (selection != null && selection.getTool() != null) {
                return true;
            }
        }
        return false;
    }

    private String buildStepInputJson(AiPlanReqDTO reqDTO, AiToolSelectionDTO selection) {
        if (selection != null && selection.getTask() != null && hasText(selection.getTask().getInputJson())) {
            return selection.getTask().getInputJson();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        appendJsonField(builder, "sessionId", reqDTO.getSessionId());
        appendJsonField(builder, "userId", reqDTO.getUserId());
        appendJsonField(builder, "query", reqDTO.getNormalizedQuery());
        if (reqDTO.getIntentResult() != null) {
            appendJsonField(builder, "intentCode", reqDTO.getIntentResult().getIntentCode());
        }
        trimLastComma(builder);
        builder.append("}");
        return builder.toString();
    }

    private void appendJsonField(StringBuilder builder, String field, String value) {
        if (!hasText(value)) {
            return;
        }
        builder.append("\"").append(field).append("\":\"").append(escapeJson(value)).append("\",");
    }

    private void trimLastComma(StringBuilder builder) {
        if (builder.length() > 1 && builder.charAt(builder.length() - 1) == ',') {
            builder.deleteCharAt(builder.length() - 1);
        }
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String safeText(String first, String second) {
        if (hasText(first)) {
            return first;
        }
        return second;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
