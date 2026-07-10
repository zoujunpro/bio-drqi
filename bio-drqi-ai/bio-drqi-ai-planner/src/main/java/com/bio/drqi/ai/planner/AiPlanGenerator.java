package com.bio.drqi.ai.planner;

import com.bio.drqi.ai.dto.planner.AiPlanReqDTO;
import com.bio.drqi.ai.dto.planner.AiPlanRspDTO;
import com.bio.drqi.ai.dto.planner.AiPlanTaskDTO;
import com.bio.drqi.ai.dto.planner.AiToolSelectionDTO;

import java.util.List;

/**
 * 执行计划生成器。
 */
public interface AiPlanGenerator {

    /**
     * 根据任务和工具选择结果生成执行计划。
     */
    AiPlanRspDTO generate(AiPlanReqDTO reqDTO, List<AiPlanTaskDTO> tasks, List<AiToolSelectionDTO> selections);
}
