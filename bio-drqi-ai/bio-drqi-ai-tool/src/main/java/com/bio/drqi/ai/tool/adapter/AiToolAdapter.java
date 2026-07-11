package com.bio.drqi.ai.tool.adapter;

import com.bio.drqi.ai.dao.domain.AiToolDefinition;
import com.bio.drqi.ai.dto.tool.AiToolExecuteReqDTO;
import com.bio.drqi.ai.dto.tool.AiToolExecuteRspDTO;

/**
 * 不同工具协议的执行适配器。
 */
public interface AiToolAdapter {

    String supportToolType();

    AiToolExecuteRspDTO execute(AiToolDefinition tool, AiToolExecuteReqDTO reqDTO);
}
