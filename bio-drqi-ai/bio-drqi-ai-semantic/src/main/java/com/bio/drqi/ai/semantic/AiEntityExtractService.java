package com.bio.drqi.ai.semantic;

import com.bio.drqi.ai.dto.semantic.AiEntityExtractReqDTO;
import com.bio.drqi.ai.dto.semantic.AiEntityExtractRspDTO;

/**
 * 实体抽取服务。
 */
public interface AiEntityExtractService {

    /**
     * 从用户问题中抽取业务实体。
     */
    AiEntityExtractRspDTO extract(AiEntityExtractReqDTO reqDTO);
}
