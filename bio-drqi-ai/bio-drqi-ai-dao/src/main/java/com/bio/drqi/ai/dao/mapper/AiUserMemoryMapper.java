package com.bio.drqi.ai.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.ai.dao.domain.AiUserMemory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AiUserMemoryMapper extends BaseMapper<AiUserMemory> {

    List<AiUserMemory> selectActiveByUserId(@Param("userId") String userId);

    AiUserMemory selectActiveByUserIdAndKey(@Param("userId") String userId, @Param("memoryKey") String memoryKey);
}
