package com.bio.drqi.ai.service;

import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.dto.req.AiAnalysisReqDTO;
import com.bio.drqi.ai.schema.AiDomainSchema;

public interface AiPermissionInjector {

    void inject(AiAnalysisReqDTO reqDTO, AiQueryPlanDTO plan, AiDomainSchema schema);
}
