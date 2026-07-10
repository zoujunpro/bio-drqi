package com.bio.drqi.ai.planner;

import com.bio.drqi.ai.dto.planner.AiPlanReqDTO;
import com.bio.drqi.ai.dto.planner.AiPlanRspDTO;

/**
 * 执行计划校验器。
 */
public interface AiPlanValidator {

    /**
     * 校验计划是否可执行。
     */
    AiPlanRspDTO validate(AiPlanReqDTO reqDTO, AiPlanRspDTO planRspDTO);
}
