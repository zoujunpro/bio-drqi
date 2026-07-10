package com.bio.drqi.ai.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.ai.dao.domain.AiSemanticPattern;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AiSemanticPatternMapper extends BaseMapper<AiSemanticPattern> {

    List<AiSemanticPattern> selectActiveByPatternType(@Param("patternType") String patternType);
}
