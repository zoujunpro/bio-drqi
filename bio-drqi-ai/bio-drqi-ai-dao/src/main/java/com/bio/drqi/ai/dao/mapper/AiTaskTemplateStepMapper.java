package com.bio.drqi.ai.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.ai.dao.domain.AiTaskTemplateStep;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AiTaskTemplateStepMapper extends BaseMapper<AiTaskTemplateStep> {

    List<AiTaskTemplateStep> selectActiveByTemplateCode(@Param("templateCode") String templateCode);
}
