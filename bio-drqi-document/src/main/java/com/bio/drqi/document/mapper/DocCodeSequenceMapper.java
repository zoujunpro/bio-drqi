package com.bio.drqi.document.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.document.domain.DocCodeSequence;
import org.apache.ibatis.annotations.Param;

public interface DocCodeSequenceMapper extends BaseMapper<DocCodeSequence> {
    int insertIgnore(@Param("categoryCode") String categoryCode, @Param("yearMonth") String yearMonth);

    DocCodeSequence selectForUpdate(@Param("categoryCode") String categoryCode, @Param("yearMonth") String yearMonth);

    int updateCurrentSeq(@Param("id") Long id, @Param("currentSeq") Integer currentSeq);
}
