package com.bio.drqi.ai.semantic;

import com.bio.drqi.ai.dto.semantic.AiSynonymNormalizeReqDTO;
import com.bio.drqi.ai.dto.semantic.AiSynonymNormalizeRspDTO;

/**
 * 同义词归一服务。
 */
public interface AiSynonymNormalizeService {

    AiSynonymNormalizeRspDTO normalize(AiSynonymNormalizeReqDTO reqDTO);
}
