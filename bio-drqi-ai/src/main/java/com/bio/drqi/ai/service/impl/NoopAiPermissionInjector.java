package com.bio.drqi.ai.service.impl;

import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.dto.req.AiAnalysisReqDTO;
import com.bio.drqi.ai.schema.AiDomainSchema;
import com.bio.drqi.ai.service.AiPermissionInjector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NoopAiPermissionInjector implements AiPermissionInjector {

    @Override
    public void inject(AiAnalysisReqDTO reqDTO, AiQueryPlanDTO plan, AiDomainSchema schema) {
        log.debug("AI查询权限注入未配置，domain={}", plan == null ? null : plan.getDomain());
    }
}
