package com.bio.drqi.ai.service;

import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;

public interface AiQueryPlanService {

    AiQueryPlanDTO generate(String question, String preferredChartType);
}
