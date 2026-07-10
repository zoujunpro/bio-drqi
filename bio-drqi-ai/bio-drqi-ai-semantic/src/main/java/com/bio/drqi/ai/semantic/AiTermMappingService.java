package com.bio.drqi.ai.semantic;

import com.bio.drqi.ai.dto.semantic.AiTermMappingReqDTO;
import com.bio.drqi.ai.dto.semantic.AiTermMappingRspDTO;

/**
 * 业务术语映射服务。
 */
public interface AiTermMappingService {

    AiTermMappingRspDTO map(AiTermMappingReqDTO reqDTO);
}
