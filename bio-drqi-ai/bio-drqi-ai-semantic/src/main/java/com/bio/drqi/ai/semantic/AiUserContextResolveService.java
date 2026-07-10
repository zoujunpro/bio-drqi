package com.bio.drqi.ai.semantic;

import com.bio.drqi.ai.dto.semantic.AiUserContextResolveReqDTO;
import com.bio.drqi.ai.dto.semantic.AiUserContextResolveRspDTO;

/**
 * 用户上下文解析服务。
 */
public interface AiUserContextResolveService {

    AiUserContextResolveRspDTO resolve(AiUserContextResolveReqDTO reqDTO);
}
