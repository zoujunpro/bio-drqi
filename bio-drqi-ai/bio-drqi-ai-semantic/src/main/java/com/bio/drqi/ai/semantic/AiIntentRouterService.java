package com.bio.drqi.ai.semantic;

import com.bio.drqi.ai.dto.semantic.AiIntentRecognizeReqDTO;
import com.bio.drqi.ai.dto.semantic.AiIntentRecognizeRspDTO;

/**
 * 企业业务意图路由。
 */
public interface AiIntentRouterService {

    /**
     * 根据用户自然语言识别业务意图。
     *
     * @param reqDTO 意图识别请求
     * @return 意图识别结果
     */
    AiIntentRecognizeRspDTO recognize(AiIntentRecognizeReqDTO reqDTO);
}
