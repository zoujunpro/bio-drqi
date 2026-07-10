package com.bio.drqi.ai.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.ai.dao.domain.AiMemorySummary;
import org.apache.ibatis.annotations.Param;

public interface AiMemorySummaryMapper extends BaseMapper<AiMemorySummary> {

    AiMemorySummary selectLatestBySessionId(@Param("sessionId") String sessionId);
}
