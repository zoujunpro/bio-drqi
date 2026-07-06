package com.bio.drqi.ai.service;

import com.bio.drqi.ai.dto.plan.AiReportPlanDTO;

public interface AiReportPlanService {

    AiReportPlanDTO generate(String question);
}
