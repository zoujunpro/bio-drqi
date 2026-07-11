package com.bio.drqi.ai.orchestrator;

import com.bio.drqi.ai.dto.chat.AiChatRspDTO;
import com.bio.drqi.ai.dto.planner.AiPlanRspDTO;
import com.bio.drqi.ai.dto.tool.AiToolExecuteRspDTO;

import java.util.List;

/**
 * AI 聊天结果处理器。
 */
public interface AiChatResultProcessor {

    /**
     * 将执行计划和工具执行结果转换成前端可渲染的聊天响应。
     */
    AiChatRspDTO process(String sessionId, AiPlanRspDTO planResult, List<AiToolExecuteRspDTO> executeResults);
}
