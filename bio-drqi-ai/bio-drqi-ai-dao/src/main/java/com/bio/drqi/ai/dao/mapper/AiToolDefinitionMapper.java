package com.bio.drqi.ai.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.ai.dao.domain.AiToolDefinition;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface AiToolDefinitionMapper extends BaseMapper<AiToolDefinition> {

    AiToolDefinition selectByToolCode(@Param("toolCode") String toolCode);

    AiToolDefinition selectActiveByToolCode(@Param("toolCode") String toolCode);

    List<AiToolDefinition> selectActiveList();

    List<AiToolDefinition> selectActiveByToolCodes(@Param("toolCodes") Collection<String> toolCodes);
}
