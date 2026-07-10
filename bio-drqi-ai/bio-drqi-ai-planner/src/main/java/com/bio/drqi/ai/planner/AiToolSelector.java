package com.bio.drqi.ai.planner;

import com.bio.drqi.ai.dto.planner.AiPlanReqDTO;
import com.bio.drqi.ai.dto.planner.AiPlanTaskDTO;
import com.bio.drqi.ai.dto.planner.AiToolSelectionDTO;

import java.util.List;

/**
 * 工具选择器。
 */
public interface AiToolSelector {

    /**
     * 为拆解后的任务选择候选工具。
     */
    List<AiToolSelectionDTO> select(AiPlanReqDTO reqDTO, List<AiPlanTaskDTO> tasks);
}
