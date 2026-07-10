package com.bio.drqi.ai.dto.memory;

import lombok.Data;

import java.util.List;

/**
 * 会话文件绑定消息请求。
 */
@Data
public class AiMemoryFileBindReqDTO {

    private String sessionId;

    private Long messageId;

    private String userId;

    private List<String> fileIds;
}
