package com.bio.drqi.ai.service;

import com.bio.drqi.ai.dto.memory.AiConversationContextDTO;
import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.dto.rsp.AiAnalysisRspDTO;

import java.util.Map;

public interface AiConversationMemoryService {

    AiConversationContextDTO getOrCreate(String conversationId);

    void saveUserMessage(String conversationId, String question);

    void updateAfterQuery(String conversationId, String question, AiQueryPlanDTO plan, AiAnalysisRspDTO response, Map<String, String> confirmedTerms);

    void updateAfterAnswer(String conversationId, String question, String answer);
}
