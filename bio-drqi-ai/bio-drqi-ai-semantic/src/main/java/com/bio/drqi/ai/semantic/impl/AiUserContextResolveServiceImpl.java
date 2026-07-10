package com.bio.drqi.ai.semantic.impl;

import com.bio.drqi.ai.dto.semantic.AiUserContextResolveReqDTO;
import com.bio.drqi.ai.dto.semantic.AiUserContextResolveRspDTO;
import com.bio.drqi.ai.semantic.AiUserContextResolveService;
import org.springframework.stereotype.Service;

/**
 * 默认用户上下文解析实现。
 */
@Service
public class AiUserContextResolveServiceImpl implements AiUserContextResolveService {

    @Override
    public AiUserContextResolveRspDTO resolve(AiUserContextResolveReqDTO reqDTO) {
        AiUserContextResolveRspDTO rspDTO = new AiUserContextResolveRspDTO();
        if (reqDTO == null) {
            return rspDTO;
        }
        rspDTO.setUserId(reqDTO.getUserId());
        rspDTO.setUsername(reqDTO.getUsername());
        rspDTO.setNickname(reqDTO.getNickname());
        rspDTO.setJobNum(reqDTO.getJobNum());
        rspDTO.setDefaultScopeType("USER");
        rspDTO.setDefaultScopeValue(reqDTO.getUserId());
        return rspDTO;
    }
}
