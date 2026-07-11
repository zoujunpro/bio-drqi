package com.bio.drqi.ai.planner.impl;

import com.bio.drqi.ai.common.enums.AiPlanTaskSourceEnum;
import com.bio.drqi.ai.common.enums.AiPlanTaskTypeEnum;
import com.bio.drqi.ai.dto.planner.AiPlanReqDTO;
import com.bio.drqi.ai.dto.planner.AiPlanTaskDTO;
import com.bio.drqi.ai.dto.semantic.AiIntentRecognizeRspDTO;
import com.bio.drqi.ai.planner.AiTaskDecomposer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 默认任务拆解器。
 *
 * <p>这里只区分简单单工具任务和需要 LLM Planner 的复杂任务。
 * 复杂执行步骤不再从任务模板生成，避免把 Planner 做成工作流配置。</p>
 */
@Service
public class AiTaskDecomposerImpl implements AiTaskDecomposer {

    @Override
    public List<AiPlanTaskDTO> decompose(AiPlanReqDTO reqDTO) {
        List<AiPlanTaskDTO> tasks = new ArrayList<AiPlanTaskDTO>();

        // 没有用户问题时，不能进入工具调用，直接生成澄清任务。
        if (reqDTO == null || !hasText(reqDTO.getNormalizedQuery())) {
            tasks.add(buildClarifyTask("用户问题为空，需要澄清"));
            return tasks;
        }

        // 没有明确意图时，Planner 不猜业务目标，交给前端继续澄清或重新识别语义。
        AiIntentRecognizeRspDTO intentResult = reqDTO.getIntentResult();
        if (intentResult == null || !hasText(intentResult.getIntentCode())) {
            tasks.add(buildClarifyTask("未识别到明确业务意图，需要澄清"));
            return tasks;
        }

        // 第一优先级：简单任务。一个意图只对应一个工具，并且不是分析/统计/汇总类问题，就不需要拆成多步。
        tasks.addAll(decomposeBySingleAction(reqDTO));
        if (!tasks.isEmpty()) {
            return tasks;
        }

        // 复杂任务只生成一个 Planner 标记任务，真正的模型调用在 AiPlanGeneratorImpl 中完成。
        tasks.add(buildPlannerMarkerTask(reqDTO));
        return tasks;
    }

    /**
     * 简单任务：意图识别已经给出一个候选工具，并且用户问题不明显包含分析/汇总/多个对象时，直接生成一个工具任务。
     */
    private List<AiPlanTaskDTO> decomposeBySingleAction(AiPlanReqDTO reqDTO) {
        List<AiPlanTaskDTO> tasks = new ArrayList<AiPlanTaskDTO>();

        // 简单任务必须满足三个条件：
        // 1. 语义层已经识别出意图；
        // 2. 意图只匹配到一个候选工具；
        // 3. 用户问题没有明显的复杂分析、汇总、统计、对比等表达。
        if (reqDTO.getIntentResult() == null
                || reqDTO.getIntentResult().getTools() == null
                || reqDTO.getIntentResult().getTools().size() != 1
                || isComplexQuery(reqDTO.getNormalizedQuery())) {
            return tasks;
        }

        AiPlanTaskDTO taskDTO = new AiPlanTaskDTO();
        taskDTO.setTaskNo(1);
        taskDTO.setTaskCode(reqDTO.getIntentResult().getIntentCode());
        taskDTO.setTaskName("处理业务意图：" + reqDTO.getIntentResult().getIntentCode());
        taskDTO.setTaskType(AiPlanTaskTypeEnum.TOOL.getCode());
        taskDTO.setIntentCode(reqDTO.getIntentResult().getIntentCode());
        taskDTO.setTargetCode(reqDTO.getIntentResult().getTools().get(0).getToolCode());
        taskDTO.setInputJson(buildBasicInputJson(reqDTO));
        taskDTO.setSource(AiPlanTaskSourceEnum.SINGLE.getCode());
        taskDTO.setDescription("简单任务，直接使用意图候选工具执行");
        tasks.add(taskDTO);
        return tasks;
    }

    /**
     * Planner 标记任务：这里只告诉后续 PlanGenerator 需要走 LLM Planner。
     * 这个方法不直接调用模型，真正调用发生在 AiPlanGeneratorImpl.generateByLlm。
     */
    private AiPlanTaskDTO buildPlannerMarkerTask(AiPlanReqDTO reqDTO) {
        AiPlanTaskDTO taskDTO = new AiPlanTaskDTO();
        taskDTO.setTaskNo(1);
        taskDTO.setTaskCode(reqDTO.getIntentResult().getIntentCode());
        taskDTO.setTaskName("LLM Planner：" + reqDTO.getIntentResult().getIntentCode());
        taskDTO.setTaskType(AiPlanTaskTypeEnum.DIFY.getCode());
        taskDTO.setIntentCode(reqDTO.getIntentResult().getIntentCode());
        taskDTO.setTargetCode(reqDTO.getIntentResult().getIntentCode());
        taskDTO.setInputJson(buildBasicInputJson(reqDTO));
        taskDTO.setSource(AiPlanTaskSourceEnum.LLM.getCode());
        taskDTO.setDescription("Planner 标记任务，后续由 AiPlanGeneratorImpl 调用模型生成结构化执行计划");
        return taskDTO;
    }

    private AiPlanTaskDTO buildClarifyTask(String description) {
        AiPlanTaskDTO taskDTO = new AiPlanTaskDTO();

        // 澄清任务不是业务执行任务，它告诉后续 PlanGenerator/Validator 当前需要继续向用户追问。
        taskDTO.setTaskNo(1);
        taskDTO.setTaskCode(AiPlanTaskTypeEnum.CLARIFY.getCode());
        taskDTO.setTaskName("补充信息");
        taskDTO.setTaskType(AiPlanTaskTypeEnum.CLARIFY.getCode());
        taskDTO.setSource(AiPlanTaskSourceEnum.CLARIFY.getCode());
        taskDTO.setDescription(description);
        return taskDTO;
    }

    private boolean isComplexQuery(String query) {
        // 第一版先用关键词识别复杂任务。后续可以升级为：任务模板匹配分数 + LLM 分类 + 工具数量综合判断。
        return containsAny(query, "分析", "汇总", "统计", "对比", "总结", "报告", "风险", "取样和种植", "多个", "分别");
    }

    private String buildBasicInputJson(AiPlanReqDTO reqDTO) {
        // 基础入参只放 Planner 必须的上下文信息。具体业务参数由模板 input_mapping 或后续参数解析器补充。
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        appendJsonField(builder, "sessionId", reqDTO.getSessionId());
        appendJsonField(builder, "userId", reqDTO.getUserId());
        appendJsonField(builder, "query", reqDTO.getNormalizedQuery());
        appendJsonField(builder, "intentCode", reqDTO.getIntentResult() == null ? null : reqDTO.getIntentResult().getIntentCode());
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

    private boolean containsAny(String query, String... words) {
        if (!hasText(query)) {
            return false;
        }
        for (String word : words) {
            if (query.contains(word)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
