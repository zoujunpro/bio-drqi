package com.bio.drqi.ai.planner.impl;

import com.bio.drqi.ai.common.enums.AiPlanTaskTypeEnum;
import com.bio.drqi.ai.common.enums.AiToolSelectionStatusEnum;
import com.bio.drqi.ai.dto.planner.AiPlanReqDTO;
import com.bio.drqi.ai.dto.planner.AiPlanRspDTO;
import com.bio.drqi.ai.dto.planner.AiPlanStepDTO;
import com.bio.drqi.ai.dto.planner.AiPlanTaskDTO;
import com.bio.drqi.ai.dto.planner.AiToolSelectionDTO;
import com.bio.drqi.ai.dto.semantic.AiToolDefinitionDTO;
import com.bio.drqi.ai.planner.AiPlanGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 默认执行计划生成器。
 */
@Service
public class AiPlanGeneratorImpl implements AiPlanGenerator {

    private static final String PLAN_TYPE_TOOL = "TOOL";

    private static final String PLAN_TYPE_DIFY = "DIFY";

    private static final String PLAN_TYPE_MIXED = "MIXED";

    private static final String PLAN_TYPE_CLARIFY = "CLARIFY";

    private static final String PLAN_TYPE_UNKNOWN = "UNKNOWN";

    @Override
    public AiPlanRspDTO generate(AiPlanReqDTO reqDTO, List<AiPlanTaskDTO> tasks, List<AiToolSelectionDTO> selections) {
        AiPlanRspDTO rspDTO = new AiPlanRspDTO();
        if (reqDTO == null || !hasText(reqDTO.getNormalizedQuery())) {
            buildClarifyPlan(rspDTO, "用户问题为空，无法生成执行计划", "请补充你要处理的问题。");
            return rspDTO;
        }

        if (tasks == null || tasks.isEmpty()) {
            if (reqDTO.getIntentResult() == null || !hasText(reqDTO.getIntentResult().getIntentCode())) {
                rspDTO.setPlanType(PLAN_TYPE_UNKNOWN);
                rspDTO.setExecutable(Boolean.FALSE);
                rspDTO.setReason("未识别到明确意图，也没有可规划任务");
                rspDTO.setClarifyQuestion("请补充业务对象、查询范围或你希望我执行的操作。");
                return rspDTO;
            }
            buildDifyPlan(reqDTO, rspDTO);
            return rspDTO;
        }

        buildTaskPlan(reqDTO, rspDTO, tasks, selections);
        return rspDTO;
    }

    private void buildTaskPlan(AiPlanReqDTO reqDTO, AiPlanRspDTO rspDTO,
                               List<AiPlanTaskDTO> tasks, List<AiToolSelectionDTO> selections) {
        List<AiPlanTaskDTO> orderedTasks = sortTasks(tasks);
        Map<Integer, AiToolSelectionDTO> selectionMap = buildSelectionMap(selections);
        Map<Integer, String> outputKeyMap = new LinkedHashMap<Integer, String>();

        int stepNo = 1;
        for (AiPlanTaskDTO task : orderedTasks) {
            if (task == null) {
                continue;
            }
            if (AiPlanTaskTypeEnum.CLARIFY.getCode().equals(task.getTaskType())) {
                buildClarifyPlan(rspDTO, safeText(task.getDescription(), "当前信息不足，无法生成执行计划"),
                        "请补充业务对象、查询范围或必要条件。");
                return;
            }

            AiToolSelectionDTO selection = selectionMap.get(task.getTaskNo());
            AiToolDefinitionDTO tool = selection == null ? null : selection.getTool();
            String stepType = resolveStepType(task, selection);
            if (stepType == null) {
                buildClarifyPlan(rspDTO, buildNoToolReason(task, selection), "请补充业务对象、查询范围或确认可用工具。");
                return;
            }

            AiPlanStepDTO stepDTO = new AiPlanStepDTO();
            stepDTO.setStepNo(stepNo);
            stepDTO.setStepType(stepType);
            stepDTO.setTargetCode(resolveTargetCode(task, tool, stepType));
            stepDTO.setToolCode(tool == null ? null : tool.getToolCode());
            stepDTO.setDependsOn(calculateDependency(task));
            stepDTO.setInputJson(buildStepInputJson(reqDTO, task, tool, outputKeyMap));
            stepDTO.setOutputKey(buildOutputKey(task, stepType, stepNo));
            stepDTO.setDescription(buildStepDescription(task, tool, stepType));
            rspDTO.getSteps().add(stepDTO);
            outputKeyMap.put(task.getTaskNo(), stepDTO.getOutputKey());
            stepNo++;
        }

        if (rspDTO.getSteps().isEmpty()) {
            buildClarifyPlan(rspDTO, "没有生成可执行步骤", "请补充业务对象或操作目标。");
            return;
        }

        rspDTO.setPlanType(resolvePlanType(rspDTO.getSteps()));
        rspDTO.setExecutable(Boolean.TRUE);
        rspDTO.setReason("根据任务拆解、依赖关系和工具选择结果生成执行计划");
    }

    private void buildDifyPlan(AiPlanReqDTO reqDTO, AiPlanRspDTO rspDTO) {
        rspDTO.setPlanType(PLAN_TYPE_DIFY);
        rspDTO.setExecutable(Boolean.TRUE);
        rspDTO.setReason("识别到业务意图，但没有候选工具，交由 Dify 处理");

        AiPlanStepDTO stepDTO = new AiPlanStepDTO();
        stepDTO.setStepNo(1);
        stepDTO.setStepType(PLAN_TYPE_DIFY);
        stepDTO.setTargetCode(reqDTO.getIntentResult().getIntentCode());
        stepDTO.setInputJson(buildFallbackInputJson(reqDTO));
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

    private List<AiPlanTaskDTO> sortTasks(List<AiPlanTaskDTO> tasks) {
        List<AiPlanTaskDTO> orderedTasks = new ArrayList<AiPlanTaskDTO>();
        if (tasks != null) {
            orderedTasks.addAll(tasks);
        }
        Collections.sort(orderedTasks, new Comparator<AiPlanTaskDTO>() {
            @Override
            public int compare(AiPlanTaskDTO first, AiPlanTaskDTO second) {
                return safeTaskNo(first).compareTo(safeTaskNo(second));
            }
        });
        return orderedTasks;
    }

    private Map<Integer, AiToolSelectionDTO> buildSelectionMap(List<AiToolSelectionDTO> selections) {
        Map<Integer, AiToolSelectionDTO> selectionMap = new LinkedHashMap<Integer, AiToolSelectionDTO>();
        if (selections == null) {
            return selectionMap;
        }
        for (AiToolSelectionDTO selection : selections) {
            if (selection != null && selection.getTask() != null && selection.getTask().getTaskNo() != null) {
                selectionMap.put(selection.getTask().getTaskNo(), selection);
            }
        }
        return selectionMap;
    }

    private String resolveStepType(AiPlanTaskDTO task, AiToolSelectionDTO selection) {
        if (selection != null && selection.getTool() != null
                && AiToolSelectionStatusEnum.SELECTED.getCode().equals(selection.getSelectionStatus())) {
            return PLAN_TYPE_TOOL;
        }
        if (AiPlanTaskTypeEnum.DIFY.getCode().equals(task.getTaskType())
                || AiPlanTaskTypeEnum.ANALYSIS.getCode().equals(task.getTaskType())) {
            return PLAN_TYPE_DIFY;
        }
        if (AiPlanTaskTypeEnum.MERGE.getCode().equals(task.getTaskType())) {
            return AiPlanTaskTypeEnum.MERGE.getCode();
        }
        return null;
    }

    private String calculateDependency(AiPlanTaskDTO task) {
        if (task == null || !hasText(task.getDependsOn())) {
            return null;
        }
        return task.getDependsOn();
    }

    private String buildStepInputJson(AiPlanReqDTO reqDTO, AiPlanTaskDTO task,
                                      AiToolDefinitionDTO tool, Map<Integer, String> outputKeyMap) {
        if (task != null && hasText(task.getInputJson()) && outputKeyMap.isEmpty()) {
            return task.getInputJson();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        appendJsonField(builder, "sessionId", reqDTO.getSessionId());
        appendJsonField(builder, "userId", reqDTO.getUserId());
        appendJsonField(builder, "query", reqDTO.getNormalizedQuery());
        appendJsonField(builder, "originalQuery", reqDTO.getOriginalQuery());
        appendJsonField(builder, "rewrittenQuery", reqDTO.getRewrittenQuery());
        if (reqDTO.getIntentResult() != null) {
            appendJsonField(builder, "intentCode", reqDTO.getIntentResult().getIntentCode());
        }
        if (task != null) {
            appendJsonField(builder, "taskCode", task.getTaskCode());
            appendJsonField(builder, "taskType", task.getTaskType());
            appendJsonField(builder, "businessObject", task.getBusinessObject());
            appendDependsOnField(builder, task.getDependsOn());
            appendTaskInput(builder, task.getInputJson());
            appendDependencyOutputs(builder, task.getDependsOn(), outputKeyMap);
        }
        if (tool != null) {
            appendJsonField(builder, "toolCode", tool.getToolCode());
            appendJsonField(builder, "toolType", tool.getToolType());
            appendJsonField(builder, "targetCode", tool.getTargetCode());
        }
        appendSemanticSummary(builder, reqDTO);
        trimLastComma(builder);
        builder.append("}");
        return builder.toString();
    }

    private String buildFallbackInputJson(AiPlanReqDTO reqDTO) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        appendJsonField(builder, "sessionId", reqDTO.getSessionId());
        appendJsonField(builder, "userId", reqDTO.getUserId());
        appendJsonField(builder, "query", reqDTO.getNormalizedQuery());
        if (reqDTO.getIntentResult() != null) {
            appendJsonField(builder, "intentCode", reqDTO.getIntentResult().getIntentCode());
        }
        appendSemanticSummary(builder, reqDTO);
        trimLastComma(builder);
        builder.append("}");
        return builder.toString();
    }

    private void appendTaskInput(StringBuilder builder, String taskInputJson) {
        if (!hasText(taskInputJson)) {
            return;
        }
        if (isJsonObject(taskInputJson)) {
            appendJsonRawField(builder, "taskInput", taskInputJson);
            return;
        }
        appendJsonField(builder, "taskInput", taskInputJson);
    }

    private void appendDependencyOutputs(StringBuilder builder, String dependsOn, Map<Integer, String> outputKeyMap) {
        if (!hasText(dependsOn) || outputKeyMap == null || outputKeyMap.isEmpty()) {
            return;
        }
        Set<Integer> dependencyNos = parseDependencyNos(dependsOn);
        if (dependencyNos.isEmpty()) {
            return;
        }
        StringBuilder outputBuilder = new StringBuilder();
        outputBuilder.append("{");
        for (Map.Entry<Integer, String> entry : outputKeyMap.entrySet()) {
            if (dependencyNos.contains(entry.getKey())) {
                appendJsonField(outputBuilder, String.valueOf(entry.getKey()), "${" + entry.getValue() + "}");
            }
        }
        trimLastComma(outputBuilder);
        outputBuilder.append("}");
        appendJsonRawField(builder, "dependencyOutputs", outputBuilder.toString());
    }

    private void appendDependsOnField(StringBuilder builder, String dependsOn) {
        if (!hasText(dependsOn)) {
            return;
        }
        if (isJsonArray(dependsOn)) {
            appendJsonRawField(builder, "dependsOn", dependsOn);
            return;
        }
        appendJsonField(builder, "dependsOn", dependsOn);
    }

    private void appendSemanticSummary(StringBuilder builder, AiPlanReqDTO reqDTO) {
        if (reqDTO == null) {
            return;
        }
        if (reqDTO.getTimeResult() != null) {
            appendJsonField(builder, "timeExpression", reqDTO.getTimeResult().getExpression());
            appendJsonField(builder, "startDate", reqDTO.getTimeResult().getStartDate());
            appendJsonField(builder, "endDate", reqDTO.getTimeResult().getEndDate());
        }
        if (reqDTO.getScopeResult() != null) {
            appendJsonField(builder, "scopeType", reqDTO.getScopeResult().getScopeType());
            appendJsonField(builder, "scopeValue", reqDTO.getScopeResult().getScopeValue());
        }
    }

    private String resolveTargetCode(AiPlanTaskDTO task, AiToolDefinitionDTO tool, String stepType) {
        if (tool != null && hasText(tool.getTargetCode())) {
            return tool.getTargetCode();
        }
        if (task != null && hasText(task.getTargetCode())) {
            return task.getTargetCode();
        }
        return stepType;
    }

    private String buildOutputKey(AiPlanTaskDTO task, String stepType, int stepNo) {
        String prefix = "stepResult";
        if (PLAN_TYPE_TOOL.equals(stepType)) {
            prefix = "toolResult";
        } else if (PLAN_TYPE_DIFY.equals(stepType)) {
            prefix = "difyResult";
        } else if (AiPlanTaskTypeEnum.MERGE.getCode().equals(stepType)) {
            prefix = "mergeResult";
        }
        if (task != null && hasText(task.getTaskCode())) {
            return prefix + stepNo + "_" + normalizeKey(task.getTaskCode());
        }
        return prefix + stepNo;
    }

    private String buildStepDescription(AiPlanTaskDTO task, AiToolDefinitionDTO tool, String stepType) {
        if (tool != null) {
            return "调用工具：" + safeText(tool.getToolName(), tool.getToolCode());
        }
        if (PLAN_TYPE_DIFY.equals(stepType)) {
            return "调用 Dify/LLM 处理任务：" + safeText(task == null ? null : task.getTaskName(), task == null ? null : task.getTaskCode());
        }
        if (AiPlanTaskTypeEnum.MERGE.getCode().equals(stepType)) {
            return "合并前置步骤结果：" + safeText(task == null ? null : task.getTaskName(), task == null ? null : task.getTaskCode());
        }
        return task == null ? stepType : safeText(task.getDescription(), task.getTaskName());
    }

    private String resolvePlanType(List<AiPlanStepDTO> steps) {
        String planType = null;
        for (AiPlanStepDTO step : steps) {
            if (step == null || !hasText(step.getStepType())) {
                continue;
            }
            if (planType == null) {
                planType = step.getStepType();
                continue;
            }
            if (!planType.equals(step.getStepType())) {
                return PLAN_TYPE_MIXED;
            }
        }
        return planType == null ? PLAN_TYPE_UNKNOWN : planType;
    }

    private String buildNoToolReason(AiPlanTaskDTO task, AiToolSelectionDTO selection) {
        if (selection != null && hasText(selection.getRejectReason())) {
            return selection.getRejectReason();
        }
        if (selection != null && hasText(selection.getReason())) {
            return selection.getReason();
        }
        return "任务需要外部工具，但没有找到可执行工具：" + safeText(task == null ? null : task.getTaskName(), task == null ? null : task.getTaskCode());
    }

    private void appendJsonField(StringBuilder builder, String field, String value) {
        if (!hasText(value)) {
            return;
        }
        builder.append("\"").append(field).append("\":\"").append(escapeJson(value)).append("\",");
    }

    private void appendJsonRawField(StringBuilder builder, String field, String value) {
        if (!hasText(value)) {
            return;
        }
        builder.append("\"").append(field).append("\":").append(value).append(",");
    }

    private void trimLastComma(StringBuilder builder) {
        if (builder.length() > 1 && builder.charAt(builder.length() - 1) == ',') {
            builder.deleteCharAt(builder.length() - 1);
        }
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String normalizeKey(String value) {
        return value.replaceAll("[^A-Za-z0-9_]", "_");
    }

    private boolean isJsonObject(String value) {
        if (!hasText(value)) {
            return false;
        }
        String trimmed = value.trim();
        return trimmed.startsWith("{") && trimmed.endsWith("}");
    }

    private boolean isJsonArray(String value) {
        if (!hasText(value)) {
            return false;
        }
        String trimmed = value.trim();
        return trimmed.startsWith("[") && trimmed.endsWith("]");
    }

    private Set<Integer> parseDependencyNos(String dependsOn) {
        Set<Integer> dependencyNos = new HashSet<Integer>();
        if (!hasText(dependsOn)) {
            return dependencyNos;
        }
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < dependsOn.length(); i++) {
            char ch = dependsOn.charAt(i);
            if (Character.isDigit(ch)) {
                number.append(ch);
                continue;
            }
            addDependencyNo(dependencyNos, number);
        }
        addDependencyNo(dependencyNos, number);
        return dependencyNos;
    }

    private void addDependencyNo(Set<Integer> dependencyNos, StringBuilder number) {
        if (number.length() == 0) {
            return;
        }
        try {
            dependencyNos.add(Integer.valueOf(number.toString()));
        } catch (NumberFormatException ignored) {
            // 非法依赖序号忽略，后续校验器或执行器再处理缺失依赖。
        }
        number.setLength(0);
    }

    private Integer safeTaskNo(AiPlanTaskDTO task) {
        if (task == null || task.getTaskNo() == null) {
            return Integer.MAX_VALUE;
        }
        return task.getTaskNo();
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
