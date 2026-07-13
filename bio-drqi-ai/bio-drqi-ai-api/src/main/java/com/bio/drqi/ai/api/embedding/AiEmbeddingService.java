package com.bio.drqi.ai.api.embedding;

import com.bio.drqi.ai.api.embedding.dto.AiEmbeddingReqDTO;
import com.bio.drqi.ai.api.embedding.dto.AiEmbeddingRspDTO;

/**
 * 文本向量化服务。
 */
public interface AiEmbeddingService {

    /**
     * 将文本转换成向量。
     */
    AiEmbeddingRspDTO embed(AiEmbeddingReqDTO reqDTO);
}
