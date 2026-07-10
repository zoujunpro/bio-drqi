package com.bio.drqi.ai.semantic;

import com.bio.drqi.ai.dto.semantic.AiNumberParseReqDTO;
import com.bio.drqi.ai.dto.semantic.AiNumberParseRspDTO;

/**
 * 数量解析服务。
 */
public interface AiNumberParseService {

    AiNumberParseRspDTO parse(AiNumberParseReqDTO reqDTO);
}
