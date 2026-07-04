package com.bio.drqi.ai.service;

import com.bio.drqi.ai.dto.memory.AiConversationContextDTO;
import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.schema.AiDomainSchema;

public interface AiContextFilterEnhancer {

    void enhance(String question, AiConversationContextDTO context, AiQueryPlanDTO plan, AiDomainSchema schema);
}
