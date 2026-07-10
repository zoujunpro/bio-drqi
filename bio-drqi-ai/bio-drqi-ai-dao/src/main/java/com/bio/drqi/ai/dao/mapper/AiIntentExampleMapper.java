package com.bio.drqi.ai.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.ai.dao.domain.AiIntentExample;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface AiIntentExampleMapper extends BaseMapper<AiIntentExample> {

    List<AiIntentExample> selectActiveByIntentCode(@Param("intentCode") String intentCode);

    List<AiIntentExample> selectActiveByIntentCodes(@Param("intentCodes") Collection<String> intentCodes);
}
