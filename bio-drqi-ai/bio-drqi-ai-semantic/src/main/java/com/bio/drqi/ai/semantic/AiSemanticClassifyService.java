package com.bio.drqi.ai.semantic;

import com.bio.drqi.ai.dto.semantic.AiSemanticClassifyReqDTO;
import com.bio.drqi.ai.dto.semantic.AiSemanticClassifyRspDTO;

/**
 * 语义分类服务。
 */
public interface AiSemanticClassifyService {

    /**
     * 判断用户问题属于闲聊、帮助、业务请求等哪类语义。
     */
    AiSemanticClassifyRspDTO classify(AiSemanticClassifyReqDTO reqDTO);
}
