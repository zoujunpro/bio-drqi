package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;

/**
 * 实体抽取请求。
 */
@Data
public class AiEntityExtractReqDTO implements Serializable {

    /**
     * 用户 ID。
     */
    private String userId;

    /**
     * 会话 ID。
     */
    private String sessionId;

    /**
     * 待抽取文本，通常使用改写后的问题。
     */
    private String query;

    /**
     * 已识别意图编码，可为空。
     */
    private String intentCode;

    private static final long serialVersionUID = 1L;
}
