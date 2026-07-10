package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;

/**
 * 范围解析请求。
 */
@Data
public class AiScopeResolveReqDTO implements Serializable {

    private String userId;

    private String sessionId;

    /**
     * 用户问题。
     */
    private String query;

    private static final long serialVersionUID = 1L;
}
