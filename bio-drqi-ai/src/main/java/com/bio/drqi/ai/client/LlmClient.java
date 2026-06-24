package com.bio.drqi.ai.client;

import java.util.List;

import com.bio.drqi.ai.dto.llm.LlmChatMessageDTO;

/**
 * 大模型客户端抽象。
 * 后续如果从 Ollama 换成 vLLM、千问官方服务或 DeepSeek，只需要新增实现类，不影响业务服务。
 */
public interface LlmClient {

    String chat(List<LlmChatMessageDTO> messages);
}
