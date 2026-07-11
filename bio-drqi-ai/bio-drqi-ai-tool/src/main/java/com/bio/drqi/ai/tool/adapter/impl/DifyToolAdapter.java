package com.bio.drqi.ai.tool.adapter.impl;

import com.bio.drqi.ai.common.enums.AiToolTypeEnum;
import com.bio.drqi.ai.dao.domain.AiToolDefinition;
import com.bio.drqi.ai.dto.tool.AiToolExecuteReqDTO;
import com.bio.drqi.ai.dto.tool.AiToolExecuteRspDTO;
import com.bio.drqi.ai.tool.adapter.AiToolAdapter;
import org.springframework.stereotype.Component;

/**
 * Dify 工具适配器占位。后续在这里接入 Dify API Key、应用配置和调用模式。
 */
@Component
public class DifyToolAdapter implements AiToolAdapter {

    @Override
    public String supportToolType() {
        return AiToolTypeEnum.DIFY.getCode();
    }

    @Override
    public AiToolExecuteRspDTO execute(AiToolDefinition tool, AiToolExecuteReqDTO reqDTO) {
        return AiToolExecuteRspDTO.fail(
                tool.getToolCode(),
                tool.getToolType(),
                tool.getTargetCode(),
                "DifyToolAdapter 尚未接入 Dify 调用实现",
                0L
        );
    }
}
