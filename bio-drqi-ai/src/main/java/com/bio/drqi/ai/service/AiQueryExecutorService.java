package com.bio.drqi.ai.service;

import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.dto.rsp.AiAnalysisRspDTO;
import com.bio.drqi.ai.schema.AiDomainSchema;

public interface AiQueryExecutorService {

    AiAnalysisRspDTO execute(AiQueryPlanDTO plan, AiDomainSchema schema);
}
