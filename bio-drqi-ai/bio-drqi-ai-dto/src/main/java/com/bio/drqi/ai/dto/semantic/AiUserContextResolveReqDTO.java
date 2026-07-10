package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户上下文解析请求。
 */
@Data
public class AiUserContextResolveReqDTO implements Serializable {

    private String userId;

    private String username;

    private String nickname;

    private String jobNum;

    private static final long serialVersionUID = 1L;
}
