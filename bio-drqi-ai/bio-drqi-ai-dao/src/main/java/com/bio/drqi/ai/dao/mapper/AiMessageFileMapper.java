package com.bio.drqi.ai.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.ai.dao.domain.AiMessageFile;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface AiMessageFileMapper extends BaseMapper<AiMessageFile> {

    AiMessageFile selectByFileId(@Param("fileId") String fileId);

    List<AiMessageFile> selectByFileIds(@Param("sessionId") String sessionId,
                                         @Param("userId") String userId,
                                         @Param("fileIds") Collection<String> fileIds);

    List<AiMessageFile> selectRecentBySessionId(@Param("sessionId") String sessionId, @Param("limit") Integer limit);

    int updateMessageIdByFileIds(@Param("sessionId") String sessionId,
                                 @Param("userId") String userId,
                                 @Param("messageId") Long messageId,
                                 @Param("fileIds") Collection<String> fileIds);
}
