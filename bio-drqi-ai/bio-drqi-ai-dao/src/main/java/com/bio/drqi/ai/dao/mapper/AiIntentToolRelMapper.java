package com.bio.drqi.ai.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.ai.dao.domain.AiIntentToolRel;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface AiIntentToolRelMapper extends BaseMapper<AiIntentToolRel> {

    List<AiIntentToolRel> selectActiveByIntentCode(@Param("intentCode") String intentCode);

    List<AiIntentToolRel> selectActiveByIntentCodes(@Param("intentCodes") Collection<String> intentCodes);
}
