package com.bio.drqi.ai.planner;

import com.bio.drqi.ai.dto.planner.AiPlanReqDTO;
import com.bio.drqi.ai.dto.planner.AiPlanTaskDTO;

import java.util.List;

/**
 * 任务拆解器。
 */
public interface AiTaskDecomposer {

    /**
     * 根据用户问题、意图和语义结果拆解任务。
     */
    List<AiPlanTaskDTO> decompose(AiPlanReqDTO reqDTO);
}
