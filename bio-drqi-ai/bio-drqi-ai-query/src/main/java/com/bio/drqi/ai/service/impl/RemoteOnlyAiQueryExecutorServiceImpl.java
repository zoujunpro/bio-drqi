package com.bio.drqi.ai.service.impl;

import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.dto.rsp.AiAnalysisRspDTO;
import com.bio.drqi.ai.schema.AiDomainSchema;
import com.bio.drqi.ai.service.AiQueryExecutorService;
import org.springframework.stereotype.Service;

/**
 * 独立AI服务未配置数据源时的兜底执行器。
 * 独立部署场景推荐走 /ai/analysis，由主流程内部选择可用业务工具。
 */
@Service
public class RemoteOnlyAiQueryExecutorServiceImpl implements AiQueryExecutorService {

    @Override
    public AiAnalysisRspDTO execute(AiQueryPlanDTO plan, AiDomainSchema schema) {
        throw new BusinessException("当前AI服务未启用数据库查询，请使用 /ai/analysis，由主流程内部选择可用业务工具");
    }
}
