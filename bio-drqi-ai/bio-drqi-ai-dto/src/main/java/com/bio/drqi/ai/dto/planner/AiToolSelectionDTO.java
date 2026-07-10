package com.bio.drqi.ai.dto.planner;

import com.bio.drqi.ai.dto.semantic.AiToolDefinitionDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * Planner 工具选择结果。
 */
@Data
public class AiToolSelectionDTO implements Serializable {

    /**
     * 对应任务。
     */
    private AiPlanTaskDTO task;

    /**
     * 选中的工具。为空时表示该任务不走工具。
     */
    private AiToolDefinitionDTO tool;

    /**
     * 选择状态：SELECTED/SKIPPED/NO_TOOL/REJECTED。
     */
    private String selectionStatus;

    /**
     * 选择分数。分数越高，越适合当前任务。
     */
    private Integer score;

    /**
     * 选择原因。
     */
    private String reason;

    /**
     * 拒绝原因。
     */
    private String rejectReason;

    private static final long serialVersionUID = 1L;
}
