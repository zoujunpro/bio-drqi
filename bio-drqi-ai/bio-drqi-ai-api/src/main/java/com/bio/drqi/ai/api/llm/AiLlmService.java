package com.bio.drqi.ai.api.llm;

import com.bio.drqi.ai.api.llm.dto.AiLlmChatReqDTO;
import com.bio.drqi.ai.api.llm.dto.AiLlmChatRspDTO;

/**
 * 大模型调用服务。
 */
public interface AiLlmService {

    /**
     * 非流式聊天补全。
     *
     * @param reqDTO 聊天请求
     * @return 模型回复
     */
    AiLlmChatRspDTO chat(AiLlmChatReqDTO reqDTO);
}
