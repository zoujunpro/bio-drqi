package com.bio.drqi.ai.planner;

import com.bio.drqi.ai.dto.planner.AiPlanReqDTO;
import com.bio.drqi.ai.dto.planner.AiPlanRspDTO;

/**
 * AI Planner 核心服务。
 *
 * <p>Planner 负责把语义层结果转换成可执行计划，不直接查业务库，也不直接调用 Dify。
 * 它只决定下一步应该走直接回答、澄清、Dify，还是 Tool。</p>
 */
public interface AiPlannerService {

    /**
     * 生成执行计划。
     *
     * @param reqDTO 语义分析、上下文和候选工具信息
     * @return 可执行计划
     */
    AiPlanRspDTO plan(AiPlanReqDTO reqDTO);
}
