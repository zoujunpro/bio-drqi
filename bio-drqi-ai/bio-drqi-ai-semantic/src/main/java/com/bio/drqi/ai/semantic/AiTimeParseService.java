package com.bio.drqi.ai.semantic;

import com.bio.drqi.ai.dto.semantic.AiTimeParseReqDTO;
import com.bio.drqi.ai.dto.semantic.AiTimeParseRspDTO;

/**
 * 时间解析服务。
 */
public interface AiTimeParseService {

    /**
     * 解析自然语言时间。
     */
    AiTimeParseRspDTO parse(AiTimeParseReqDTO reqDTO);
}
