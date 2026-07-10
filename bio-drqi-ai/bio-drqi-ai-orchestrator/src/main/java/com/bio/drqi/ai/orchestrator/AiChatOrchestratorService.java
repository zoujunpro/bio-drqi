package com.bio.drqi.ai.orchestrator;

import com.bio.drqi.ai.dto.chat.AiChatReqDTO;
import com.bio.drqi.ai.dto.chat.AiChatRspDTO;

/**
 * AI 聊天业务编排入口。
 *
 * <p>Controller 只负责 HTTP 入参和统一响应；聊天过程中的 Memory、意图识别、
 * 工具调用、模型调用和结果组织都从这里开始编排。</p>
 */
public interface AiChatOrchestratorService {

    /**
     * 处理一轮用户聊天请求。
     */
    AiChatRspDTO chat(AiChatReqDTO reqDTO);
}
