package com.bio.drqi.ai.planner.impl;

import com.bio.drqi.ai.common.enums.AiPlanTaskTypeEnum;
import com.bio.drqi.ai.common.enums.AiToolRiskLevelEnum;
import com.bio.drqi.ai.common.enums.AiToolSelectionStatusEnum;
import com.bio.drqi.ai.common.enums.AiToolTypeEnum;
import com.bio.drqi.ai.dao.domain.AiToolDefinition;
import com.bio.drqi.ai.dao.mapper.AiToolDefinitionMapper;
import com.bio.drqi.ai.dto.planner.AiPlanReqDTO;
import com.bio.drqi.ai.dto.planner.AiPlanTaskDTO;
import com.bio.drqi.ai.dto.planner.AiToolSelectionDTO;
import com.bio.drqi.ai.dto.semantic.AiToolDefinitionDTO;
import com.bio.drqi.ai.planner.AiToolSelector;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认工具选择器。
 *
 * <p>生产级工具选择不能简单取第一个候选工具。这里按模板指定、意图候选、工具启用状态、
 * 风险等级、任务类型、业务对象和必填参数契合度综合打分，最终选择分数最高且未被拒绝的工具。</p>
 */
@Service
public class AiToolSelectorImpl implements AiToolSelector {

    /**
     * 最高优先级：任务模板明确指定 targetCode 时，说明运营或研发已经确定该任务应使用哪个工具。
     */
    private static final int SCORE_EXACT_TARGET = 1000;

    /**
     * 意图识别阶段返回的候选工具，比全局工具兜底更可信。
     */
    private static final int SCORE_INTENT_CANDIDATE = 300;

    /**
     * 任务编码和工具编码一致或互相包含，说明命名层面高度相关。
     */
    private static final int SCORE_TASK_CODE_MATCH = 180;

    /**
     * 业务对象匹配，例如 SAMPLE 任务优先选择样品/取样相关工具。
     */
    private static final int SCORE_BUSINESS_OBJECT_MATCH = 120;

    /**
     * 工具 inputSchema 覆盖任务 requiredParams 时加分，避免选择参数不匹配的工具。
     */
    private static final int SCORE_REQUIRED_PARAMS_MATCH = 100;

    /**
     * 查询任务优先使用只读工具，降低误调用写接口的风险。
     */
    private static final int SCORE_READONLY_QUERY = 80;

    /**
     * 任务名称/描述和工具名称/描述文本相关时少量加分。
     */
    private static final int SCORE_TEXT_MATCH = 40;

    /**
     * 最低选择分。低于这个分数说明只是启用工具，但和当前任务没有足够关系。
     */
    private static final int SCORE_MIN_SELECT = 100;

    /**
     * 工具定义表，保存 AI 可调用的企业 API、Dify、MCP、本地工具等能力。
     */
    @Resource
    private AiToolDefinitionMapper aiToolDefinitionMapper;

    @Override
    public List<AiToolSelectionDTO> select(AiPlanReqDTO reqDTO, List<AiPlanTaskDTO> tasks) {
        List<AiToolSelectionDTO> selections = new ArrayList<AiToolSelectionDTO>();
        if (tasks == null || tasks.isEmpty()) {
            return selections;
        }

        List<AiToolDefinitionDTO> intentCandidateTools = collectIntentCandidateTools(reqDTO);
        for (AiPlanTaskDTO task : tasks) {
            // 分析、合并、澄清类任务不一定需要外部工具，避免强行绑定一个无关工具。
            if (!needsExternalTool(task)) {
                selections.add(buildSelection(task, null, AiToolSelectionStatusEnum.SKIPPED.getCode(), 0,
                        "当前任务不需要外部工具", null));
                continue;
            }

            // 每个任务单独构建候选池，因为模板 targetCode、业务对象、必填参数都跟任务有关。
            List<ToolCandidate> candidates = collectToolCandidates(task, intentCandidateTools);
            ToolCandidate selected = selectBestCandidate(task, candidates);
            if (selected == null) {
                selections.add(buildSelection(task, null, AiToolSelectionStatusEnum.NO_TOOL.getCode(), 0,
                        "没有找到满足任务要求的可用工具", buildRejectSummary(task, candidates)));
                continue;
            }

            selections.add(buildSelection(task, selected.getTool(), AiToolSelectionStatusEnum.SELECTED.getCode(),
                    selected.getScore(), selected.getReason(), null));
        }
        return selections;
    }

    /**
     * 只有查询、工具、Dify 任务需要进入工具选择。分析、合并、澄清任务由 PlanGenerator/Executor 本地处理或转 Dify。
     */
    private boolean needsExternalTool(AiPlanTaskDTO task) {
        if (task == null || !hasText(task.getTaskType())) {
            return false;
        }
        return AiPlanTaskTypeEnum.QUERY.getCode().equals(task.getTaskType())
                || AiPlanTaskTypeEnum.TOOL.getCode().equals(task.getTaskType())
                || AiPlanTaskTypeEnum.DIFY.getCode().equals(task.getTaskType());
    }

    /**
     * 构建工具候选池：
     * 1. 任务模板 targetCode 指定的工具优先；
     * 2. 意图识别返回的候选工具其次；
     * 3. 仍然没有候选时，从启用工具表里按任务信息评分兜底。
     */
    private List<ToolCandidate> collectToolCandidates(AiPlanTaskDTO task, List<AiToolDefinitionDTO> intentCandidateTools) {
        Map<String, ToolCandidate> candidateMap = new LinkedHashMap<String, ToolCandidate>();

        // 模板明确指定 targetCode 时，只接受数据库里 ACTIVE 的同名工具。
        AiToolDefinitionDTO targetTool = findActiveToolByCode(task == null ? null : task.getTargetCode());
        if (targetTool != null) {
            putCandidate(candidateMap, targetTool, Boolean.TRUE, Boolean.FALSE);
        }

        // 意图候选工具需要重新按 toolCode 查询 ACTIVE 工具定义，确保状态、风险、URL 等信息以工具库为准。
        if (intentCandidateTools != null) {
            for (AiToolDefinitionDTO tool : intentCandidateTools) {
                AiToolDefinitionDTO activeTool = refreshActiveTool(tool);
                if (activeTool != null) {
                    putCandidate(candidateMap, activeTool, Boolean.FALSE, Boolean.TRUE);
                }
            }
        }

        // 兜底路径：模板和意图都没给工具时，才从全部启用工具里打分筛选，避免无谓扩大候选范围。
        if (candidateMap.isEmpty()) {
            List<AiToolDefinition> activeTools = safeSelectActiveTools();
            for (AiToolDefinition activeTool : activeTools) {
                putCandidate(candidateMap, convertTool(activeTool), Boolean.FALSE, Boolean.FALSE);
            }
        }

        return new ArrayList<ToolCandidate>(candidateMap.values());
    }

    private ToolCandidate selectBestCandidate(AiPlanTaskDTO task, List<ToolCandidate> candidates) {
        ToolCandidate best = null;
        if (candidates == null || candidates.isEmpty()) {
            return null;
        }

        for (ToolCandidate candidate : candidates) {
            evaluateCandidate(task, candidate);
            // 被安全规则拒绝的工具不能参与排序，即使它命中了模板或意图候选。
            if (candidate.isRejected()) {
                continue;
            }
            if (best == null || candidate.getScore() > best.getScore()) {
                best = candidate;
            }
        }

        // 分数过低表示“只是一个启用工具”，但和当前任务没有足够证据关联。
        if (best == null || best.getScore() < SCORE_MIN_SELECT) {
            return null;
        }
        return best;
    }

    private void evaluateCandidate(AiPlanTaskDTO task, ToolCandidate candidate) {
        AiToolDefinitionDTO tool = candidate.getTool();
        if (tool == null) {
            candidate.reject("工具定义为空");
            return;
        }

        // 查询任务默认应是只读能力，不能自动选择可能写库、审批、删除的工具。
        if (isQueryTask(task) && tool.getReadOnly() != null && tool.getReadOnly().intValue() == 0) {
            candidate.reject("查询任务不能选择非只读工具");
            return;
        }

        // 高风险工具必须走显式确认、权限审批或人工策略，Planner 不做自动选择。
        if (AiToolRiskLevelEnum.HIGH.getCode().equals(tool.getRiskLevel())) {
            candidate.reject("高风险工具需要显式审批，当前阶段不自动选择");
            return;
        }

        // API 类型工具至少要有真实地址和 HTTP 方法，否则 Executor 后面无法执行。
        if (AiToolTypeEnum.API.getCode().equals(tool.getToolType())
                && (!hasText(tool.getServiceUrl()) || !hasText(tool.getHttpMethod()))) {
            candidate.reject("API工具缺少serviceUrl或httpMethod");
            return;
        }

        int score = 0;
        StringBuilder reason = new StringBuilder();

        // 以下是正向匹配分。模板指定 > 意图候选 > 编码/业务对象/参数/文本相似。
        if (Boolean.TRUE.equals(candidate.getTargetMatched())) {
            score += SCORE_EXACT_TARGET;
            reason.append("模板目标工具匹配;");
        }
        if (Boolean.TRUE.equals(candidate.getIntentCandidate())) {
            score += SCORE_INTENT_CANDIDATE;
            reason.append("意图候选工具匹配;");
        }
        if (matchesTaskCode(task, tool)) {
            score += SCORE_TASK_CODE_MATCH;
            reason.append("任务编码匹配;");
        }
        if (matchesBusinessObject(task, tool)) {
            score += SCORE_BUSINESS_OBJECT_MATCH;
            reason.append("业务对象匹配;");
        }
        if (matchesRequiredParams(task, tool)) {
            score += SCORE_REQUIRED_PARAMS_MATCH;
            reason.append("必填参数匹配;");
        }
        if (isQueryTask(task) && tool.getReadOnly() != null && tool.getReadOnly().intValue() == 1) {
            score += SCORE_READONLY_QUERY;
            reason.append("查询任务匹配只读工具;");
        }
        if (matchesText(task, tool)) {
            score += SCORE_TEXT_MATCH;
            reason.append("任务文本匹配;");
        }
        // 中风险不是绝对不能用，但要降权，优先选择低风险同类工具。
        if (AiToolRiskLevelEnum.MEDIUM.getCode().equals(tool.getRiskLevel())) {
            score -= 50;
            reason.append("中风险工具降权;");
        }

        candidate.setScore(score);
        candidate.setReason(reason.length() == 0 ? "工具启用但匹配度不足" : reason.toString());
    }

    private List<AiToolDefinitionDTO> collectIntentCandidateTools(AiPlanReqDTO reqDTO) {
        Map<String, AiToolDefinitionDTO> toolMap = new LinkedHashMap<String, AiToolDefinitionDTO>();
        if (reqDTO == null) {
            return new ArrayList<AiToolDefinitionDTO>();
        }
        addTools(toolMap, reqDTO.getCandidateTools());
        if (reqDTO.getIntentResult() != null) {
            addTools(toolMap, reqDTO.getIntentResult().getTools());
        }
        return new ArrayList<AiToolDefinitionDTO>(toolMap.values());
    }

    private void addTools(Map<String, AiToolDefinitionDTO> toolMap, List<AiToolDefinitionDTO> tools) {
        if (tools == null) {
            return;
        }
        for (AiToolDefinitionDTO tool : tools) {
            if (tool != null && hasText(tool.getToolCode())) {
                toolMap.put(tool.getToolCode(), tool);
            }
        }
    }

    private AiToolDefinitionDTO refreshActiveTool(AiToolDefinitionDTO tool) {
        if (tool == null || !hasText(tool.getToolCode())) {
            return null;
        }
        // 候选工具可能来自 Semantic/Intent 的缓存结果，这里重新查工具库，避免使用已禁用或过期配置。
        AiToolDefinitionDTO activeTool = findActiveToolByCode(tool.getToolCode());
        return activeTool == null ? tool : activeTool;
    }

    private AiToolDefinitionDTO findActiveToolByCode(String toolCode) {
        if (!hasText(toolCode)) {
            return null;
        }
        try {
            return convertTool(aiToolDefinitionMapper.selectActiveByToolCode(toolCode));
        } catch (Exception ignored) {
            return null;
        }
    }

    private List<AiToolDefinition> safeSelectActiveTools() {
        try {
            List<AiToolDefinition> tools = aiToolDefinitionMapper.selectActiveList();
            return tools == null ? new ArrayList<AiToolDefinition>() : tools;
        } catch (Exception ignored) {
            return new ArrayList<AiToolDefinition>();
        }
    }

    private void putCandidate(Map<String, ToolCandidate> candidateMap, AiToolDefinitionDTO tool, Boolean targetMatched, Boolean intentCandidate) {
        if (tool == null || !hasText(tool.getToolCode())) {
            return;
        }
        // 同一个工具可能同时来自模板和意图候选，需要合并来源标记，后续评分会同时加分。
        ToolCandidate candidate = candidateMap.get(tool.getToolCode());
        if (candidate == null) {
            candidate = new ToolCandidate(tool);
            candidateMap.put(tool.getToolCode(), candidate);
        }
        if (Boolean.TRUE.equals(targetMatched)) {
            candidate.setTargetMatched(Boolean.TRUE);
        }
        if (Boolean.TRUE.equals(intentCandidate)) {
            candidate.setIntentCandidate(Boolean.TRUE);
        }
    }

    private boolean matchesTaskCode(AiPlanTaskDTO task, AiToolDefinitionDTO tool) {
        if (task == null || tool == null || !hasText(task.getTaskCode())) {
            return false;
        }
        return equalsIgnoreCase(task.getTaskCode(), tool.getToolCode())
                || containsIgnoreCase(tool.getToolCode(), task.getTaskCode())
                || containsIgnoreCase(task.getTaskCode(), tool.getToolCode());
    }

    private boolean matchesBusinessObject(AiPlanTaskDTO task, AiToolDefinitionDTO tool) {
        if (task == null || tool == null || !hasText(task.getBusinessObject())) {
            return false;
        }
        String object = task.getBusinessObject();
        return containsIgnoreCase(tool.getToolCode(), object)
                || containsIgnoreCase(tool.getToolName(), object)
                || containsIgnoreCase(tool.getDescription(), object)
                || containsIgnoreCase(tool.getTargetCode(), object);
    }

    private boolean matchesRequiredParams(AiPlanTaskDTO task, AiToolDefinitionDTO tool) {
        if (task == null || tool == null || !hasText(task.getRequiredParams())) {
            return true;
        }
        if (!hasText(tool.getInputSchema())) {
            return false;
        }
        // 第一版先做轻量包含判断：requiredParams 里的字段必须能在 inputSchema 中找到。
        // 后续可以升级为真正 JSON Schema 校验。
        String params = task.getRequiredParams();
        String schema = tool.getInputSchema();
        String[] tokens = params.replace("[", "")
                .replace("]", "")
                .replace("\"", "")
                .split(",");
        for (String token : tokens) {
            String param = token == null ? null : token.trim();
            if (hasText(param) && !containsIgnoreCase(schema, param)) {
                return false;
            }
        }
        return true;
    }

    private boolean matchesText(AiPlanTaskDTO task, AiToolDefinitionDTO tool) {
        if (task == null || tool == null) {
            return false;
        }
        return textIntersects(task.getTaskName(), tool)
                || textIntersects(task.getDescription(), tool);
    }

    private boolean textIntersects(String text, AiToolDefinitionDTO tool) {
        if (!hasText(text)) {
            return false;
        }
        return containsAny(text, tool.getToolName(), tool.getDescription(), tool.getToolCode());
    }

    private boolean containsAny(String text, String... candidates) {
        if (!hasText(text) || candidates == null) {
            return false;
        }
        for (String candidate : candidates) {
            if (hasText(candidate) && (containsIgnoreCase(text, candidate) || containsIgnoreCase(candidate, text))) {
                return true;
            }
        }
        return false;
    }

    private boolean isQueryTask(AiPlanTaskDTO task) {
        return task != null && (AiPlanTaskTypeEnum.QUERY.getCode().equals(task.getTaskType())
                || AiPlanTaskTypeEnum.TOOL.getCode().equals(task.getTaskType()));
    }

    private String buildRejectSummary(AiPlanTaskDTO task, List<ToolCandidate> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return "候选工具为空";
        }
        // 把每个候选工具的拒绝原因带出去，方便 PlanValidator、日志和前端定位为什么没选上工具。
        StringBuilder builder = new StringBuilder();
        for (ToolCandidate candidate : candidates) {
            if (candidate == null || candidate.getTool() == null) {
                continue;
            }
            builder.append(candidate.getTool().getToolCode())
                    .append(":")
                    .append(candidate.getRejectReason() == null ? candidate.getReason() : candidate.getRejectReason())
                    .append(";");
        }
        if (builder.length() == 0 && task != null) {
            return "任务" + task.getTaskCode() + "没有可选择工具";
        }
        return builder.toString();
    }

    private AiToolDefinitionDTO convertTool(AiToolDefinition toolDefinition) {
        if (toolDefinition == null) {
            return null;
        }
        AiToolDefinitionDTO dto = new AiToolDefinitionDTO();
        dto.setToolCode(toolDefinition.getToolCode());
        dto.setToolName(toolDefinition.getToolName());
        dto.setDescription(toolDefinition.getDescription());
        dto.setToolType(toolDefinition.getToolType());
        dto.setTargetCode(toolDefinition.getTargetCode());
        dto.setInputSchema(toolDefinition.getInputSchema());
        dto.setOutputSchema(toolDefinition.getOutputSchema());
        dto.setServiceUrl(toolDefinition.getServiceUrl());
        dto.setHttpMethod(toolDefinition.getHttpMethod());
        dto.setRiskLevel(toolDefinition.getRiskLevel());
        dto.setReadOnly(toolDefinition.getReadOnly());
        return dto;
    }

    private AiToolSelectionDTO buildSelection(AiPlanTaskDTO task, AiToolDefinitionDTO tool, String selectionStatus,
                                             Integer score, String reason, String rejectReason) {
        AiToolSelectionDTO selectionDTO = new AiToolSelectionDTO();
        selectionDTO.setTask(task);
        selectionDTO.setTool(tool);
        selectionDTO.setSelectionStatus(selectionStatus);
        selectionDTO.setScore(score);
        selectionDTO.setReason(reason);
        selectionDTO.setRejectReason(rejectReason);
        return selectionDTO;
    }

    private boolean equalsIgnoreCase(String first, String second) {
        return first != null && second != null && first.equalsIgnoreCase(second);
    }

    private boolean containsIgnoreCase(String text, String keyword) {
        return text != null && keyword != null && text.toLowerCase().contains(keyword.toLowerCase());
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    /**
     * 工具候选及评分上下文。
     */
    private static class ToolCandidate {

        private final AiToolDefinitionDTO tool;

        private Boolean targetMatched = Boolean.FALSE;

        private Boolean intentCandidate = Boolean.FALSE;

        private Integer score = 0;

        private String reason;

        private String rejectReason;

        ToolCandidate(AiToolDefinitionDTO tool) {
            this.tool = tool;
        }

        void reject(String rejectReason) {
            this.rejectReason = rejectReason;
            this.score = 0;
        }

        boolean isRejected() {
            return rejectReason != null;
        }

        AiToolDefinitionDTO getTool() {
            return tool;
        }

        Boolean getTargetMatched() {
            return targetMatched;
        }

        void setTargetMatched(Boolean targetMatched) {
            this.targetMatched = targetMatched;
        }

        Boolean getIntentCandidate() {
            return intentCandidate;
        }

        void setIntentCandidate(Boolean intentCandidate) {
            this.intentCandidate = intentCandidate;
        }

        Integer getScore() {
            return score;
        }

        void setScore(Integer score) {
            this.score = score;
        }

        String getReason() {
            return reason;
        }

        void setReason(String reason) {
            this.reason = reason;
        }

        String getRejectReason() {
            return rejectReason;
        }
    }
}
