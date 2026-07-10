package com.bio.drqi.ai.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.ai.dao.domain.AiBusinessDictionary;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AiBusinessDictionaryMapper extends BaseMapper<AiBusinessDictionary> {

    List<AiBusinessDictionary> selectActiveList();

    List<AiBusinessDictionary> selectActiveByDictType(@Param("dictType") String dictType);
}
