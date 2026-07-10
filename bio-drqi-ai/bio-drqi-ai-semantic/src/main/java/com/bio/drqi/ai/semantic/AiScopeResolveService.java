package com.bio.drqi.ai.semantic;

import com.bio.drqi.ai.dto.semantic.AiScopeResolveReqDTO;
import com.bio.drqi.ai.dto.semantic.AiScopeResolveRspDTO;

/**
 * 范围解析服务。
 */
public interface AiScopeResolveService {

    AiScopeResolveRspDTO resolve(AiScopeResolveReqDTO reqDTO);
}
