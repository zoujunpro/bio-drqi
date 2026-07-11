package com.bio.drqi.ai.tool.adapter.impl;

import com.bio.drqi.ai.common.enums.AiToolTypeEnum;
import com.bio.drqi.ai.dao.domain.AiToolDefinition;
import com.bio.drqi.ai.dto.tool.AiToolExecuteReqDTO;
import com.bio.drqi.ai.dto.tool.AiToolExecuteRspDTO;
import com.bio.drqi.ai.tool.adapter.AiToolAdapter;
import org.springframework.stereotype.Component;

/**
 * MCP 工具适配器占位。后续在这里接入 MCP Client。
 */
@Component
public class McpToolAdapter implements AiToolAdapter {

    @Override
    public String supportToolType() {
        return AiToolTypeEnum.MCP.getCode();
    }

    @Override
    public AiToolExecuteRspDTO execute(AiToolDefinition tool, AiToolExecuteReqDTO reqDTO) {
        return AiToolExecuteRspDTO.fail(
                tool.getToolCode(),
                tool.getToolType(),
                tool.getTargetCode(),
                "McpToolAdapter 尚未接入 MCP Client 实现",
                0L
        );
    }
}
