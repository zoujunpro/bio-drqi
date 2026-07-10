package com.bio.drqi.ai.dto.memory;

import lombok.Data;

import java.util.List;

/**
 * 获取记忆上下文请求。
 */
@Data
public class AiMemoryContextReqDTO {

    private String userId;

    private String sessionId;

    private String query;

    /**
     * 本轮会话明确关联的文件 ID。为空时可返回会话最近文件上下文。
     */
    private List<String> fileIds;
}
