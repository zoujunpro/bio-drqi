package com.bio.drqi.ai.planner.impl;

import com.bio.drqi.ai.common.enums.AiPlanTaskSourceEnum;
import com.bio.drqi.ai.common.enums.AiPlanTaskTypeEnum;
import com.bio.drqi.ai.dao.domain.AiTaskTemplate;
import com.bio.drqi.ai.dao.domain.AiTaskTemplateStep;
import com.bio.drqi.ai.dao.mapper.AiTaskTemplateMapper;
import com.bio.drqi.ai.dao.mapper.AiTaskTemplateStepMapper;
import com.bio.drqi.ai.dto.planner.AiPlanReqDTO;
import com.bio.drqi.ai.dto.planner.AiPlanTaskDTO;
import com.bio.drqi.ai.dto.semantic.AiIntentRecognizeRspDTO;
import com.bio.drqi.ai.planner.AiTaskDecomposer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 默认任务拆解器。
 *
 * <p>生产级任务拆解不直接让模型随意生成步骤，而是按“简单任务优先、任务模板优先、LLM 兜底”的顺序处理。
 * 这样固定业务流程稳定可控，复杂问题又保留模型补全能力。</p>
 */
@Service
public class AiTaskDecomposerImpl implements AiTaskDecomposer {

    /**
     * 任务模板主表。按 intentCode 找到企业固定业务流程模板。
     */
    @Resource
    private AiTaskTemplateMapper aiTaskTemplateMapper;

    /**
     * 任务模板步骤表。一个模板可以拆成多个可执行任务。
     */
    @Resource
    private AiTaskTemplateStepMapper aiTaskTemplateStepMapper;

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

        // 第二优先级：任务模板。生产里固定流程优先走配置模板，避免每次让 LLM 临时规划造成不稳定。
        tasks.addAll(decomposeByTemplate(reqDTO));
        if (!tasks.isEmpty()) {
            return tasks;
        }

        // 第三优先级：LLM/Dify 兜底。模板没有覆盖的新问题，先生成兜底任务，后续再接结构化 LLM 拆解。
        tasks.add(buildLlmFallbackTask(reqDTO));
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
     * 模板任务：生产优先路径。固定业务流程不让模型每次临时猜，按 ai_task_template_step 稳定拆分。
     */
    private List<AiPlanTaskDTO> decomposeByTemplate(AiPlanReqDTO reqDTO) {
        List<AiPlanTaskDTO> tasks = new ArrayList<AiPlanTaskDTO>();
        try {
            // 先按意图编码找模板。比如 PROJECT_EXECUTION_ANALYSIS 可以对应一套固定项目执行分析流程。
            AiTaskTemplate template = aiTaskTemplateMapper.selectActiveByIntentCode(reqDTO.getIntentResult().getIntentCode());
            if (template == null || !hasText(template.getTemplateCode())) {
                return tasks;
            }

            // 再按模板编码找步骤。每一行步骤都会转换成一个 AiPlanTaskDTO。
            List<AiTaskTemplateStep> steps = aiTaskTemplateStepMapper.selectActiveByTemplateCode(template.getTemplateCode());
            if (steps == null || steps.isEmpty()) {
                return tasks;
            }
            for (AiTaskTemplateStep step : steps) {
                tasks.add(buildTaskFromTemplateStep(reqDTO, template, step));
            }
        } catch (Exception ignored) {
            // 模板不可用时不阻断 Planner，后续进入 LLM 兜底任务。
        }
        return tasks;
    }

    /**
     * LLM 兜底任务：当前先生成 DIFY/LLM 类型任务，后续接入千问结构化输出后再展开成多个任务。
     */
    private AiPlanTaskDTO buildLlmFallbackTask(AiPlanReqDTO reqDTO) {
        AiPlanTaskDTO taskDTO = new AiPlanTaskDTO();
        taskDTO.setTaskNo(1);
        taskDTO.setTaskCode(reqDTO.getIntentResult().getIntentCode());
        taskDTO.setTaskName("LLM任务拆解：" + reqDTO.getIntentResult().getIntentCode());
        taskDTO.setTaskType(AiPlanTaskTypeEnum.DIFY.getCode());
        taskDTO.setIntentCode(reqDTO.getIntentResult().getIntentCode());
        taskDTO.setTargetCode(reqDTO.getIntentResult().getIntentCode());
        taskDTO.setInputJson(buildBasicInputJson(reqDTO));
        taskDTO.setSource(AiPlanTaskSourceEnum.LLM.getCode());
        taskDTO.setDescription("未命中任务模板，交给 LLM/Dify 做复杂任务拆解或处理");
        return taskDTO;
    }

    private AiPlanTaskDTO buildTaskFromTemplateStep(AiPlanReqDTO reqDTO, AiTaskTemplate template, AiTaskTemplateStep step) {
        AiPlanTaskDTO taskDTO = new AiPlanTaskDTO();

        // step_no 决定任务执行顺序；depends_on 决定是否必须等其他步骤结果出来后再执行。
        taskDTO.setTaskNo(step.getStepNo());
        taskDTO.setTaskCode(step.getTaskCode());
        taskDTO.setTaskName(step.getTaskName());
        taskDTO.setTaskType(step.getTaskType());
        taskDTO.setDomain(template.getDomain());
        taskDTO.setBusinessObject(step.getBusinessObject());
        taskDTO.setIntentCode(template.getIntentCode());

        // target_code 通常是工具编码。后续 ToolSelector 会优先用它查 ai_tool_definition。
        taskDTO.setTargetCode(step.getTargetCode());

        // input_mapping 是模板里配置的入参映射；没有配置时先使用基础上下文，后续可由 PlanGenerator/Executor 再补齐。
        taskDTO.setInputJson(hasText(step.getInputMapping()) ? step.getInputMapping() : buildBasicInputJson(reqDTO));
        taskDTO.setRequiredParams(step.getRequiredParams());
        taskDTO.setDependsOn(step.getDependsOn());
        taskDTO.setSource(AiPlanTaskSourceEnum.TEMPLATE.getCode());
        taskDTO.setDescription(template.getTemplateName() + " - " + step.getTaskName());
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
