package com.bio.drqi.ai.service;

import com.bio.drqi.ai.dto.req.AiAnalysisReqDTO;
import com.bio.drqi.ai.dto.rsp.AiAnalysisRspDTO;

public interface AiMultiAnalysisService {

    boolean support(AiAnalysisReqDTO reqDTO);

    AiAnalysisRspDTO analysis(AiAnalysisReqDTO reqDTO);
}
