package com.bio.drqi.ai.tool.executor;

import com.bio.drqi.ai.dto.tool.AiToolExecuteReqDTO;
import com.bio.drqi.ai.dto.tool.AiToolExecuteRspDTO;

/**
 * AI 工具执行入口。
 */
public interface AiToolExecutor {

    AiToolExecuteRspDTO execute(AiToolExecuteReqDTO reqDTO);
}
