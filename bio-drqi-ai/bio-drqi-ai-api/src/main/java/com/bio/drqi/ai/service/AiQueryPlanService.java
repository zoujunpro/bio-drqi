package com.bio.drqi.ai.service;

import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.dto.memory.AiConversationContextDTO;

public interface AiQueryPlanService {

    AiQueryPlanDTO generate(String question, String preferredChartType);

    AiQueryPlanDTO generate(String question, String preferredChartType, AiConversationContextDTO context);
}
