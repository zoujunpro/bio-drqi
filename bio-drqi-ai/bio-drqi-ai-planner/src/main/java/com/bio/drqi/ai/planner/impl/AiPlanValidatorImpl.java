package com.bio.drqi.ai.planner.impl;

import com.bio.drqi.ai.dto.planner.AiPlanReqDTO;
import com.bio.drqi.ai.dto.planner.AiPlanRspDTO;
import com.bio.drqi.ai.dto.planner.AiPlanStepDTO;
import com.bio.drqi.ai.planner.AiPlanValidator;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 默认执行计划校验器。
 */
@Service
public class AiPlanValidatorImpl implements AiPlanValidator {

    private static final String PLAN_TYPE_TOOL = "TOOL";

    private static final String PLAN_TYPE_DIFY = "DIFY";

    private static final String PLAN_TYPE_MERGE = "MERGE";

    private static final String PLAN_TYPE_CLARIFY = "CLARIFY";

    private static final String PLAN_TYPE_UNKNOWN = "UNKNOWN";

    @Override
    public AiPlanRspDTO validate(AiPlanReqDTO reqDTO, AiPlanRspDTO planRspDTO) {
        if (planRspDTO == null) {
            return buildClarifyPlan("执行计划为空", "请重新描述你要处理的问题。");
        }
        if (reqDTO == null || !hasText(reqDTO.getNormalizedQuery())) {
            return buildClarifyPlan("用户问题为空", "请补充你要处理的问题。");
        }

        if (PLAN_TYPE_CLARIFY.equals(planRspDTO.getPlanType())) {
            return validateClarifyPlan(planRspDTO);
        }
        if (PLAN_TYPE_UNKNOWN.equals(planRspDTO.getPlanType())) {
            return buildClarifyPlan(safeText(planRspDTO.getReason(), "未生成明确执行计划"),
                    safeText(planRspDTO.getClarifyQuestion(), "请补充业务对象、查询范围或你希望我执行的操作。"));
        }
        if (!Boolean.TRUE.equals(planRspDTO.getExecutable())) {
            return buildClarifyPlan(safeText(planRspDTO.getReason(), "当前计划不可执行"),
                    safeText(planRspDTO.getClarifyQuestion(), "请补充必要信息后重试。"));
        }
        if (Boolean.TRUE.equals(planRspDTO.getExecutable())
                && (planRspDTO.getSteps() == null || planRspDTO.getSteps().isEmpty())) {
            return buildClarifyPlan("计划标记为可执行，但没有执行步骤", "请补充业务对象或操作目标。");
        }

        String invalidReason = validateSteps(planRspDTO.getSteps());
        if (hasText(invalidReason)) {
            return buildClarifyPlan(invalidReason, "请补充业务对象、查询范围或确认可用工具后重试。");
        }
        return planRspDTO;
    }

    private AiPlanRspDTO validateClarifyPlan(AiPlanRspDTO planRspDTO) {
        planRspDTO.setExecutable(Boolean.FALSE);
        if (!hasText(planRspDTO.getReason())) {
            planRspDTO.setReason("当前信息不足，需要继续澄清");
        }
        if (!hasText(planRspDTO.getClarifyQuestion())) {
            planRspDTO.setClarifyQuestion("请补充业务对象、查询范围或必要条件。");
        }
        return planRspDTO;
    }

    private String validateSteps(List<AiPlanStepDTO> steps) {
        Set<Integer> stepNos = new HashSet<Integer>();
        Set<String> outputKeys = new HashSet<String>();
        for (AiPlanStepDTO step : steps) {
            String basicInvalidReason = validateBasicStep(step, stepNos, outputKeys);
            if (hasText(basicInvalidReason)) {
                return basicInvalidReason;
            }
        }

        for (AiPlanStepDTO step : steps) {
            String dependencyInvalidReason = validateDependency(step, stepNos);
            if (hasText(dependencyInvalidReason)) {
                return dependencyInvalidReason;
            }
        }
        return null;
    }

    private String validateBasicStep(AiPlanStepDTO step, Set<Integer> stepNos, Set<String> outputKeys) {
        if (step == null) {
            return "执行步骤为空";
        }
        if (step.getStepNo() == null || step.getStepNo().intValue() <= 0) {
            return "执行步骤缺少有效 stepNo";
        }
        if (!stepNos.add(step.getStepNo())) {
            return "执行步骤编号重复：" + step.getStepNo();
        }
        if (!hasText(step.getStepType())) {
            return "执行步骤缺少 stepType：" + step.getStepNo();
        }
        if (!isSupportedStepType(step.getStepType())) {
            return "执行步骤类型不支持：" + step.getStepType();
        }
        if (!hasText(step.getInputJson())) {
            return "执行步骤缺少输入参数：" + step.getStepNo();
        }
        if (!hasText(step.getOutputKey())) {
            return "执行步骤缺少输出变量：" + step.getStepNo();
        }
        if (!outputKeys.add(step.getOutputKey())) {
            return "执行步骤输出变量重复：" + step.getOutputKey();
        }
        if (PLAN_TYPE_TOOL.equals(step.getStepType()) && !hasText(step.getToolCode())) {
            return "工具步骤缺少 toolCode：" + step.getStepNo();
        }
        if ((PLAN_TYPE_TOOL.equals(step.getStepType()) || PLAN_TYPE_DIFY.equals(step.getStepType()))
                && !hasText(step.getTargetCode())) {
            return "执行步骤缺少 targetCode：" + step.getStepNo();
        }
        return null;
    }

    private String validateDependency(AiPlanStepDTO step, Set<Integer> stepNos) {
        Set<Integer> dependencyNos = parseDependencyNos(step.getDependsOn());
        for (Integer dependencyNo : dependencyNos) {
            if (!stepNos.contains(dependencyNo)) {
                return "执行步骤依赖不存在：step " + step.getStepNo() + " dependsOn " + dependencyNo;
            }
            if (dependencyNo.intValue() >= step.getStepNo().intValue()) {
                return "执行步骤不能依赖自身或后续步骤：step " + step.getStepNo() + " dependsOn " + dependencyNo;
            }
        }
        return null;
    }

    private boolean isSupportedStepType(String stepType) {
        return PLAN_TYPE_TOOL.equals(stepType)
                || PLAN_TYPE_DIFY.equals(stepType)
                || PLAN_TYPE_MERGE.equals(stepType);
    }

    private AiPlanRspDTO buildClarifyPlan(String reason, String clarifyQuestion) {
        AiPlanRspDTO rspDTO = new AiPlanRspDTO();
        rspDTO.setPlanType(PLAN_TYPE_CLARIFY);
        rspDTO.setExecutable(Boolean.FALSE);
        rspDTO.setReason(reason);
        rspDTO.setClarifyQuestion(clarifyQuestion);
        return rspDTO;
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
            // 非法依赖序号忽略，依赖缺失会由其他校验规则兜底。
        }
        number.setLength(0);
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
