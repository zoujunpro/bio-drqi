package com.bio.drqi.ai.tool.executor.impl;

import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.common.enums.AiToolRiskLevelEnum;
import com.bio.drqi.ai.dao.domain.AiToolDefinition;
import com.bio.drqi.ai.dao.mapper.AiToolDefinitionMapper;
import com.bio.drqi.ai.dto.tool.AiToolExecuteReqDTO;
import com.bio.drqi.ai.dto.tool.AiToolExecuteRspDTO;
import com.bio.drqi.ai.tool.adapter.AiToolAdapter;
import com.bio.drqi.ai.tool.executor.AiToolExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 默认工具执行器：查工具定义、做基础校验，并按工具类型分发给 Adapter。
 */
@Service
public class DefaultAiToolExecutor implements AiToolExecutor {

    @Resource
    private AiToolDefinitionMapper aiToolDefinitionMapper;

    private final Map<String, AiToolAdapter> adapterMap = new HashMap<String, AiToolAdapter>();

    public DefaultAiToolExecutor(List<AiToolAdapter> adapters) {
        if (adapters != null) {
            for (AiToolAdapter adapter : adapters) {
                adapterMap.put(adapter.supportToolType(), adapter);
            }
        }
    }

    @Override
    public AiToolExecuteRspDTO execute(AiToolExecuteReqDTO reqDTO) {
        validateRequest(reqDTO);
        AiToolDefinition tool = aiToolDefinitionMapper.selectActiveByToolCode(reqDTO.getToolCode());
        if (tool == null) {
            throw new BusinessException("工具不存在或未启用：" + reqDTO.getToolCode());
        }
        validateTool(tool, reqDTO);
        AiToolAdapter adapter = adapterMap.get(tool.getToolType());
        if (adapter == null) {
            throw new BusinessException("未找到工具适配器：" + tool.getToolType());
        }
        return adapter.execute(tool, reqDTO);
    }

    private void validateRequest(AiToolExecuteReqDTO reqDTO) {
        if (reqDTO == null || !hasText(reqDTO.getToolCode())) {
            throw new BusinessException("工具编码不能为空");
        }
    }

    private void validateTool(AiToolDefinition tool, AiToolExecuteReqDTO reqDTO) {
        if (!hasText(tool.getToolType())) {
            throw new BusinessException("工具类型不能为空：" + tool.getToolCode());
        }
        if (AiToolRiskLevelEnum.HIGH.getCode().equals(tool.getRiskLevel())
                && !Boolean.TRUE.equals(reqDTO.getConfirmed())) {
            throw new BusinessException("高风险工具需要用户确认：" + tool.getToolCode());
        }
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
