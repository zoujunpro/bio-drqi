package com.bio.drqi.ai.semantic;

import com.bio.drqi.ai.dto.semantic.AiQueryRewriteReqDTO;
import com.bio.drqi.ai.dto.semantic.AiQueryRewriteRspDTO;

/**
 * 问题改写服务，负责指代消解和上下文补全。
 */
public interface AiQueryRewriteService {

    /**
     * 将用户原始问题改写成更完整的问题。
     */
    AiQueryRewriteRspDTO rewrite(AiQueryRewriteReqDTO reqDTO);
}
