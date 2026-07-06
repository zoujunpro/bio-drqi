package com.bio.drqi.ai.service;

import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.schema.AiDomainSchema;

public interface AiQueryRiskChecker {

    void check(AiQueryPlanDTO plan, AiDomainSchema schema);
}
