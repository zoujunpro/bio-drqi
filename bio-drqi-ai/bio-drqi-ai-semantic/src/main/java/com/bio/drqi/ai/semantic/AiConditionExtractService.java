package com.bio.drqi.ai.semantic;

import com.bio.drqi.ai.dto.semantic.AiConditionExtractReqDTO;
import com.bio.drqi.ai.dto.semantic.AiConditionExtractRspDTO;

/**
 * 条件抽取服务。
 */
public interface AiConditionExtractService {

    AiConditionExtractRspDTO extract(AiConditionExtractReqDTO reqDTO);
}
