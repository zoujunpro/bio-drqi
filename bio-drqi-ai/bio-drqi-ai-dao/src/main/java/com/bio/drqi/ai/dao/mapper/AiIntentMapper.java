package com.bio.drqi.ai.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.ai.dao.domain.AiIntent;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AiIntentMapper extends BaseMapper<AiIntent> {

    AiIntent selectByIntentCode(@Param("intentCode") String intentCode);

    List<AiIntent> selectActiveList();
}
