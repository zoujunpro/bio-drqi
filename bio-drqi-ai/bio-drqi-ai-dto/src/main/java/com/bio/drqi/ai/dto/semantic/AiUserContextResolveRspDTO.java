package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户上下文解析结果。
 */
@Data
public class AiUserContextResolveRspDTO implements Serializable {

    private String userId;

    private String username;

    private String nickname;

    private String jobNum;

    /**
     * 默认权限范围类型，第一版按当前用户处理。
     */
    private String defaultScopeType;

    /**
     * 默认权限范围值。
     */
    private String defaultScopeValue;

    private static final long serialVersionUID = 1L;
}
