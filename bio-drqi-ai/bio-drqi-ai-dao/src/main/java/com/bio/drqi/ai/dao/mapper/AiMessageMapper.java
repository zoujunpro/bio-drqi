package com.bio.drqi.ai.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.ai.dao.domain.AiMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AiMessageMapper extends BaseMapper<AiMessage> {

    List<AiMessage> selectRecentBySessionId(@Param("sessionId") String sessionId, @Param("limit") Integer limit);
}
