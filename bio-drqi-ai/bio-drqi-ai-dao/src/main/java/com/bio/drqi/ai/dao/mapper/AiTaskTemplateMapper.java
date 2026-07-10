package com.bio.drqi.ai.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.ai.dao.domain.AiTaskTemplate;
import org.apache.ibatis.annotations.Param;

public interface AiTaskTemplateMapper extends BaseMapper<AiTaskTemplate> {

    AiTaskTemplate selectActiveByIntentCode(@Param("intentCode") String intentCode);
}
