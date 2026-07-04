package com.bio.drqi.ai.model;

import com.bio.drqi.ai.client.LlmClient;
import com.bio.drqi.ai.config.AiProperties;
import com.bio.drqi.ai.dto.llm.LlmCallOptionsDTO;
import com.bio.drqi.ai.dto.llm.LlmChatMessageDTO;
import com.bio.drqi.ai.prompt.SearchPrompt;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 对话模型服务。
 * 上层业务尽量调用这里，不直接依赖具体 LLM 客户端，便于后续切换千问、DeepSeek 或本地模型。
 */
@Service
public class ChatService {

    @Resource
    private LlmClient llmClient;

    @Resource
    private AiProperties aiProperties;

    public String chat(List<LlmChatMessageDTO> messages) {
        return llmClient.chat(messages);
    }

    public String generalChat(String question) {
        return llmClient.chat(Arrays.asList(
                new LlmChatMessageDTO("system", SearchPrompt.generalAnswerPrompt()),
                new LlmChatMessageDTO("user", question)
        ), LlmCallOptionsDTO.of("chat", aiProperties.getLlm().getChatTemperature()));
    }
}
