package com.bio.drqi.ai.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.ai.dao.domain.AiSession;
import org.apache.ibatis.annotations.Param;

public interface AiSessionMapper extends BaseMapper<AiSession> {

    AiSession selectBySessionId(@Param("sessionId") String sessionId);

    AiSession selectBySessionIdAndUserId(@Param("sessionId") String sessionId, @Param("userId") String userId);
}
