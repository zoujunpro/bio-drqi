package com.bio.drqi.ai.planner.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bio.drqi.ai.api.llm.AiLlmService;
import com.bio.drqi.ai.api.llm.dto.AiLlmChatReqDTO;
import com.bio.drqi.ai.api.llm.dto.AiLlmChatRspDTO;
import com.bio.drqi.ai.api.llm.dto.AiLlmMessageDTO;
import com.bio.drqi.ai.common.enums.AiPlanTaskSourceEnum;
import com.bio.drqi.ai.common.enums.AiPlanTaskTypeEnum;
import com.bio.drqi.ai.common.enums.AiToolSelectionStatusEnum;
import com.bio.drqi.ai.dto.planner.AiPlanReqDTO;
import com.bio.drqi.ai.dto.planner.AiPlanRspDTO;
import com.bio.drqi.ai.dto.planner.AiPlanStepDTO;
import com.bio.drqi.ai.dto.planner.AiPlanTaskDTO;
import com.bio.drqi.ai.dto.planner.AiToolSelectionDTO;
import com.bio.drqi.ai.dto.semantic.AiToolDefinitionDTO;
import com.bio.drqi.ai.planner.AiPlanGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    @Autowired(required = false)
    private AiLlmService aiLlmService;

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

        AiPlanRspDTO llmPlan = generateByLlm(reqDTO, tasks, selections);
        if (llmPlan != null) {
            return llmPlan;
        }

        if (hasLlmPlanTask(tasks)) {
            buildClarifyPlan(rspDTO, "LLM Planner 未生成可执行计划", "请补充业务对象、查询范围，或检查模型配置后重试。");
            return rspDTO;
        }

        buildTaskPlan(reqDTO, rspDTO, tasks, selections);
        return rspDTO;
    }

    private AiPlanRspDTO generateByLlm(AiPlanReqDTO reqDTO, List<AiPlanTaskDTO> tasks, List<AiToolSelectionDTO> selections) {
        if (aiLlmService == null) {
            return null;
        }
        List<AiToolDefinitionDTO> candidateTools = collectCandidateTools(reqDTO, selections);
        if (candidateTools.isEmpty()) {
            return null;
        }
        try {
            AiLlmChatReqDTO chatReqDTO = new AiLlmChatReqDTO();
            chatReqDTO.setTemperature(new BigDecimal("0.1"));
            chatReqDTO.setMaxTokens(2000);
            chatReqDTO.getMessages().add(new AiLlmMessageDTO("system", buildPlannerSystemPrompt()));
            chatReqDTO.getMessages().add(new AiLlmMessageDTO("user", buildPlannerUserPrompt(reqDTO, candidateTools)));
            AiLlmChatRspDTO chatRspDTO = aiLlmService.chat(chatReqDTO);
            return parseLlmPlan(chatRspDTO == null ? null : chatRspDTO.getContent(), reqDTO, candidateTools);
        } catch (Exception ignored) {
            return null;
        }
    }

    private boolean hasLlmPlanTask(List<AiPlanTaskDTO> tasks) {
        if (tasks == null) {
            return false;
        }
        for (AiPlanTaskDTO task : tasks) {
            if (task == null) {
                continue;
            }
            if (AiPlanTaskSourceEnum.LLM.getCode().equals(task.getSource())) {
                return true;
            }
        }
        return false;
    }

    private List<AiToolDefinitionDTO> collectCandidateTools(AiPlanReqDTO reqDTO, List<AiToolSelectionDTO> selections) {
        Map<String, AiToolDefinitionDTO> toolMap = new LinkedHashMap<String, AiToolDefinitionDTO>();
        if (reqDTO != null) {
            putTools(toolMap, reqDTO.getCandidateTools());
            if (reqDTO.getIntentResult() != null) {
                putTools(toolMap, reqDTO.getIntentResult().getTools());
            }
        }
        if (selections != null) {
            for (AiToolSelectionDTO selection : selections) {
                if (selection != null && selection.getTool() != null) {
                    putTool(toolMap, selection.getTool());
                }
            }
        }
        return new ArrayList<AiToolDefinitionDTO>(toolMap.values());
    }

    private void putTools(Map<String, AiToolDefinitionDTO> toolMap, List<AiToolDefinitionDTO> tools) {
        if (tools == null) {
            return;
        }
        for (AiToolDefinitionDTO tool : tools) {
            putTool(toolMap, tool);
        }
    }

    private void putTool(Map<String, AiToolDefinitionDTO> toolMap, AiToolDefinitionDTO tool) {
        if (tool == null || !hasText(tool.getToolCode()) || toolMap.containsKey(tool.getToolCode())) {
            return;
        }
        toolMap.put(tool.getToolCode(), tool);
    }

    private String buildPlannerSystemPrompt() {
        return "你是企业AI Agent的Planner，只能基于候选工具生成执行计划。"
                + "必须返回JSON，不要返回解释、Markdown或代码块。"
                + "JSON格式：{\"executable\":true,\"planType\":\"TOOL|MIXED|CLARIFY\","
                + "\"reason\":\"原因\",\"clarifyQuestion\":\"需要澄清时的问题\","
                + "\"steps\":[{\"stepNo\":1,\"stepType\":\"TOOL|MERGE\","
                + "\"toolCode\":\"候选工具编码\",\"targetCode\":\"工具编码\","
                + "\"inputJson\":{},\"dependsOn\":[],\"outputKey\":\"变量名\",\"description\":\"步骤说明\"}]}。"
                + "规则：toolCode只能来自候选工具；不要编造工具；简单问题只选必要工具；"
                + "多工具问题根据inputSchema/outputSchema和业务语义建立dependsOn；"
                + "缺少必要业务对象时返回planType=CLARIFY且steps为空。";
    }

    private String buildPlannerUserPrompt(AiPlanReqDTO reqDTO, List<AiToolDefinitionDTO> candidateTools) {
        JSONObject context = new JSONObject();
        context.put("originalQuery", reqDTO.getOriginalQuery());
        context.put("rewrittenQuery", reqDTO.getRewrittenQuery());
        context.put("normalizedQuery", reqDTO.getNormalizedQuery());
        context.put("userId", reqDTO.getUserId());
        if (reqDTO.getIntentResult() != null) {
            context.put("intentCode", reqDTO.getIntentResult().getIntentCode());
            context.put("intentName", reqDTO.getIntentResult().getIntentName());
            context.put("domain", reqDTO.getIntentResult().getDomain());
        }
        if (reqDTO.getTimeResult() != null) {
            context.put("startDate", reqDTO.getTimeResult().getStartDate());
            context.put("endDate", reqDTO.getTimeResult().getEndDate());
            context.put("timeExpression", reqDTO.getTimeResult().getExpression());
        }
        if (reqDTO.getScopeResult() != null) {
            context.put("scopeType", reqDTO.getScopeResult().getScopeType());
            context.put("scopeValue", reqDTO.getScopeResult().getScopeValue());
        }
        if (reqDTO.getEntityResult() != null) {
            context.put("entities", JSONObject.toJSONString(reqDTO.getEntityResult()));
        }
        if (reqDTO.getConditionResult() != null) {
            context.put("conditions", JSONObject.toJSONString(reqDTO.getConditionResult()));
        }

        JSONArray tools = new JSONArray();
        for (AiToolDefinitionDTO tool : candidateTools) {
            JSONObject item = new JSONObject();
            item.put("toolCode", tool.getToolCode());
            item.put("toolName", tool.getToolName());
            item.put("description", tool.getDescription());
            item.put("toolType", tool.getToolType());
            item.put("readOnly", tool.getReadOnly());
            item.put("riskLevel", tool.getRiskLevel());
            item.put("inputSchema", compact(tool.getInputSchema(), 1500));
            item.put("outputSchema", compact(tool.getOutputSchema(), 1500));
            tools.add(item);
        }

        JSONObject prompt = new JSONObject();
        prompt.put("context", context);
        prompt.put("candidateTools", tools);
        return prompt.toJSONString();
    }

    private AiPlanRspDTO parseLlmPlan(String content, AiPlanReqDTO reqDTO, List<AiToolDefinitionDTO> candidateTools) {
        JSONObject root = extractJsonObject(content);
        if (root == null) {
            return null;
        }

        String planType = safeText(root.getString("planType"), PLAN_TYPE_TOOL);
        if (PLAN_TYPE_CLARIFY.equals(planType)) {
            AiPlanRspDTO clarify = new AiPlanRspDTO();
            buildClarifyPlan(clarify, safeText(root.getString("reason"), "模型判断当前信息不足"),
                    safeText(root.getString("clarifyQuestion"), "请补充业务对象、查询范围或必要条件。"));
            return clarify;
        }

        JSONArray steps = root.getJSONArray("steps");
        if (steps == null || steps.isEmpty()) {
            return null;
        }

        Set<String> candidateToolCodes = new HashSet<String>();
        for (AiToolDefinitionDTO tool : candidateTools) {
            if (tool != null && hasText(tool.getToolCode())) {
                candidateToolCodes.add(tool.getToolCode());
            }
        }

        Map<String, AiToolDefinitionDTO> candidateToolMap = new LinkedHashMap<String, AiToolDefinitionDTO>();
        for (AiToolDefinitionDTO tool : candidateTools) {
            if (tool != null && hasText(tool.getToolCode())) {
                candidateToolMap.put(tool.getToolCode(), tool);
            }
        }

        AiPlanRspDTO rspDTO = new AiPlanRspDTO();
        for (int i = 0; i < steps.size(); i++) {
            JSONObject stepJson = steps.getJSONObject(i);
            if (stepJson == null) {
                continue;
            }
            AiPlanStepDTO stepDTO = buildStepFromLlmJson(stepJson, i + 1, reqDTO, candidateToolCodes, candidateToolMap);
            if (stepDTO == null) {
                return null;
            }
            rspDTO.getSteps().add(stepDTO);
        }
        if (rspDTO.getSteps().isEmpty()) {
            return null;
        }

        rspDTO.setPlanType(resolvePlanType(rspDTO.getSteps()));
        rspDTO.setExecutable(Boolean.TRUE);
        rspDTO.setReason(safeText(root.getString("reason"), "LLM Planner 根据用户问题和候选工具生成执行计划"));
        return rspDTO;
    }

    private AiPlanStepDTO buildStepFromLlmJson(JSONObject stepJson, int defaultStepNo, AiPlanReqDTO reqDTO,
                                              Set<String> candidateToolCodes,
                                              Map<String, AiToolDefinitionDTO> candidateToolMap) {
        String stepType = safeText(stepJson.getString("stepType"), PLAN_TYPE_TOOL);
        String toolCode = stepJson.getString("toolCode");
        if (PLAN_TYPE_TOOL.equals(stepType) && (!hasText(toolCode) || !candidateToolCodes.contains(toolCode))) {
            return null;
        }

        AiPlanStepDTO stepDTO = new AiPlanStepDTO();
        Integer stepNo = stepJson.getInteger("stepNo");
        stepDTO.setStepNo(stepNo == null || stepNo.intValue() <= 0 ? defaultStepNo : stepNo);
        stepDTO.setStepType(stepType);
        stepDTO.setToolCode(toolCode);
        stepDTO.setTargetCode(safeText(stepJson.getString("targetCode"), toolCode));
        stepDTO.setInputJson(normalizeInputJson(stepJson.get("inputJson"), reqDTO, toolCode,
                candidateToolMap == null ? null : candidateToolMap.get(toolCode)));
        stepDTO.setDependsOn(normalizeDependsOn(stepJson.get("dependsOn")));
        stepDTO.setOutputKey(safeText(stepJson.getString("outputKey"), buildOutputKey(null, stepType, stepDTO.getStepNo())));
        stepDTO.setDescription(safeText(stepJson.getString("description"), "LLM Planner 生成步骤：" + stepDTO.getOutputKey()));
        return stepDTO;
    }

    private String normalizeInputJson(Object inputJson, AiPlanReqDTO reqDTO, String toolCode, AiToolDefinitionDTO tool) {
        JSONObject input = new JSONObject();
        input.put("sessionId", reqDTO.getSessionId());
        input.put("userId", reqDTO.getUserId());
        input.put("query", reqDTO.getNormalizedQuery());
        input.put("originalQuery", reqDTO.getOriginalQuery());
        if (reqDTO.getIntentResult() != null) {
            input.put("intentCode", reqDTO.getIntentResult().getIntentCode());
        }
        if (hasText(toolCode)) {
            input.put("toolCode", toolCode);
        }
        JSONObject generatedInput = null;
        if (inputJson instanceof JSONObject) {
            generatedInput = (JSONObject) inputJson;
        } else if (inputJson instanceof String && isJsonObject((String) inputJson)) {
            generatedInput = JSONObject.parseObject((String) inputJson);
        }
        if (generatedInput != null && !generatedInput.isEmpty()) {
            JSONObject filteredInput = filterToolInputBySchema(generatedInput, tool);
            if (filteredInput != null && !filteredInput.isEmpty()) {
                input.putAll(filteredInput);
            }
        }
        return input.toJSONString();
    }

    private JSONObject filterToolInputBySchema(JSONObject inputJson, AiToolDefinitionDTO tool) {
        if (inputJson == null || inputJson.isEmpty()) {
            return new JSONObject();
        }
        if (tool == null || !hasText(tool.getInputSchema())) {
            return inputJson;
        }
        Set<String> allowedFields = parseSchemaProperties(tool.getInputSchema());
        if (allowedFields.isEmpty()) {
            return inputJson;
        }
        return filterGeneratedInput(inputJson, allowedFields);
    }

    private String normalizeDependsOn(Object dependsOn) {
        if (dependsOn == null) {
            return null;
        }
        if (dependsOn instanceof JSONArray) {
            return ((JSONArray) dependsOn).isEmpty() ? null : ((JSONArray) dependsOn).toJSONString();
        }
        if (dependsOn instanceof List) {
            JSONArray array = new JSONArray();
            array.addAll((List<?>) dependsOn);
            return array.isEmpty() ? null : array.toJSONString();
        }
        String text = String.valueOf(dependsOn);
        return hasText(text) ? text : null;
    }

    private JSONObject extractJsonObject(String content) {
        if (!hasText(content)) {
            return null;
        }
        int start = content.indexOf('{');
        int end = content.lastIndexOf('}');
        if (start < 0 || end <= start) {
            return null;
        }
        return JSONObject.parseObject(content.substring(start, end + 1));
    }

    private String compact(String value, int maxLength) {
        if (!hasText(value)) {
            return null;
        }
        String compact = value.replace('\n', ' ').replace('\r', ' ').trim();
        if (compact.length() <= maxLength) {
            return compact;
        }
        return compact.substring(0, maxLength);
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
        appendGeneratedToolInput(builder, generateToolInputByLlm(reqDTO, tool));
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

    private JSONObject generateToolInputByLlm(AiPlanReqDTO reqDTO, AiToolDefinitionDTO tool) {
        if (aiLlmService == null || reqDTO == null || tool == null || !hasText(tool.getInputSchema())) {
            return null;
        }
        Set<String> allowedFields = parseSchemaProperties(tool.getInputSchema());
        if (allowedFields.isEmpty()) {
            return null;
        }
        try {
            AiLlmChatReqDTO chatReqDTO = new AiLlmChatReqDTO();
            chatReqDTO.setTemperature(new BigDecimal("0.0"));
            chatReqDTO.setMaxTokens(800);
            chatReqDTO.getMessages().add(new AiLlmMessageDTO("system", buildToolInputSystemPrompt()));
            chatReqDTO.getMessages().add(new AiLlmMessageDTO("user", buildToolInputUserPrompt(reqDTO, tool)));
            AiLlmChatRspDTO chatRspDTO = aiLlmService.chat(chatReqDTO);
            JSONObject generated = extractJsonObject(chatRspDTO == null ? null : chatRspDTO.getContent());
            return filterGeneratedInput(generated, allowedFields);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String buildToolInputSystemPrompt() {
        return "你是企业AI工具参数生成器。只能根据用户问题和工具inputSchema生成工具入参JSON。"
                + "必须只返回JSON对象，不要解释、不要Markdown、不要代码块。"
                + "字段只能来自inputSchema.properties，不能编造字段。"
                + "根据字段description判断用户文本中的业务编码、名称、状态、时间等应该放到哪个字段。"
                + "无法确定的字段不要输出；如果schema包含pageNum/pageSize，可以默认输出1和20。";
    }

    private String buildToolInputUserPrompt(AiPlanReqDTO reqDTO, AiToolDefinitionDTO tool) {
        JSONObject prompt = new JSONObject();
        prompt.put("query", reqDTO.getNormalizedQuery());
        prompt.put("originalQuery", reqDTO.getOriginalQuery());
        prompt.put("toolCode", tool.getToolCode());
        prompt.put("toolName", tool.getToolName());
        prompt.put("description", tool.getDescription());
        prompt.put("inputSchema", tool.getInputSchema());
        if (reqDTO.getTimeResult() != null) {
            prompt.put("time", JSONObject.toJSONString(reqDTO.getTimeResult()));
        }
        if (reqDTO.getEntityResult() != null) {
            prompt.put("entities", JSONObject.toJSONString(reqDTO.getEntityResult()));
        }
        if (reqDTO.getConditionResult() != null) {
            prompt.put("conditions", JSONObject.toJSONString(reqDTO.getConditionResult()));
        }
        return prompt.toJSONString();
    }

    private Set<String> parseSchemaProperties(String inputSchema) {
        Set<String> fields = new HashSet<String>();
        if (!hasText(inputSchema)) {
            return fields;
        }
        try {
            JSONObject schema = JSONObject.parseObject(inputSchema);
            JSONObject properties = schema.getJSONObject("properties");
            if (properties != null) {
                fields.addAll(properties.keySet());
            }
        } catch (Exception ignored) {
            return fields;
        }
        return fields;
    }

    private JSONObject filterGeneratedInput(JSONObject generated, Set<String> allowedFields) {
        if (generated == null || generated.isEmpty() || allowedFields == null || allowedFields.isEmpty()) {
            return null;
        }
        JSONObject filtered = new JSONObject();
        for (String field : allowedFields) {
            Object value = generated.get(field);
            if (value == null) {
                continue;
            }
            if (value instanceof String && !hasText((String) value)) {
                continue;
            }
            filtered.put(field, value);
        }
        return filtered.isEmpty() ? null : filtered;
    }

    private void appendGeneratedToolInput(StringBuilder builder, JSONObject generatedInput) {
        if (generatedInput == null || generatedInput.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> entry : generatedInput.entrySet()) {
            if (!hasText(entry.getKey()) || entry.getValue() == null) {
                continue;
            }
            builder.append("\"")
                    .append(escapeJson(entry.getKey()))
                    .append("\":")
                    .append(JSON.toJSONString(entry.getValue()))
                    .append(",");
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

    private void appendJsonNumberField(StringBuilder builder, String field, int value) {
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
