package com.bio.drqi.ai.planner.impl;

import com.bio.drqi.ai.dto.planner.AiPlanReqDTO;
import com.bio.drqi.ai.dto.planner.AiPlanRspDTO;
import com.bio.drqi.ai.planner.AiPlanValidator;
import org.springframework.stereotype.Service;

/**
 * 默认执行计划校验器。
 */
@Service
public class AiPlanValidatorImpl implements AiPlanValidator {

    private static final String PLAN_TYPE_CLARIFY = "CLARIFY";

    @Override
    public AiPlanRspDTO validate(AiPlanReqDTO reqDTO, AiPlanRspDTO planRspDTO) {
        if (planRspDTO == null) {
            return buildClarifyPlan("执行计划为空", "请重新描述你要处理的问题。");
        }
        if (reqDTO == null || !hasText(reqDTO.getNormalizedQuery())) {
            return buildClarifyPlan("用户问题为空", "请补充你要处理的问题。");
        }
        if (Boolean.TRUE.equals(planRspDTO.getExecutable())
                && (planRspDTO.getSteps() == null || planRspDTO.getSteps().isEmpty())) {
            return buildClarifyPlan("计划标记为可执行，但没有执行步骤", "请补充业务对象或操作目标。");
        }
        return planRspDTO;
    }

    private AiPlanRspDTO buildClarifyPlan(String reason, String clarifyQuestion) {
        AiPlanRspDTO rspDTO = new AiPlanRspDTO();
        rspDTO.setPlanType(PLAN_TYPE_CLARIFY);
        rspDTO.setExecutable(Boolean.FALSE);
        rspDTO.setReason(reason);
        rspDTO.setClarifyQuestion(clarifyQuestion);
        return rspDTO;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
