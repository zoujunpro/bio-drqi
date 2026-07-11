package com.bio.drqi.ai.tool.adapter.impl;

import com.bio.drqi.ai.common.enums.AiToolTypeEnum;
import com.bio.drqi.ai.dao.domain.AiToolDefinition;
import com.bio.drqi.ai.dto.tool.AiToolExecuteReqDTO;
import com.bio.drqi.ai.dto.tool.AiToolExecuteRspDTO;
import com.bio.drqi.ai.tool.adapter.AiToolAdapter;
import org.springframework.stereotype.Component;

/**
 * 本地 Java 工具适配器占位。
 */
@Component
public class LocalToolAdapter implements AiToolAdapter {

    @Override
    public String supportToolType() {
        return AiToolTypeEnum.LOCAL.getCode();
    }

    @Override
    public AiToolExecuteRspDTO execute(AiToolDefinition tool, AiToolExecuteReqDTO reqDTO) {
        return AiToolExecuteRspDTO.fail(
                tool.getToolCode(),
                tool.getToolType(),
                tool.getTargetCode(),
                "LocalToolAdapter 尚未配置本地工具实现",
                0L
        );
    }
}
