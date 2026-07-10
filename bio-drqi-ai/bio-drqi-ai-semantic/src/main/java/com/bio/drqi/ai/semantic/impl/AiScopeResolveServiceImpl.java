package com.bio.drqi.ai.semantic.impl;

import com.bio.drqi.ai.dto.semantic.AiScopeResolveReqDTO;
import com.bio.drqi.ai.dto.semantic.AiScopeResolveRspDTO;
import com.bio.drqi.ai.semantic.AiScopeResolveService;
import org.springframework.stereotype.Service;

/**
 * 默认范围解析实现。
 */
@Service
public class AiScopeResolveServiceImpl implements AiScopeResolveService {

    @Override
    public AiScopeResolveRspDTO resolve(AiScopeResolveReqDTO reqDTO) {
        AiScopeResolveRspDTO rspDTO = new AiScopeResolveRspDTO();
        String query = reqDTO == null ? null : reqDTO.getQuery();
        if (!hasText(query)) {
            rspDTO.setScopeType("UNKNOWN");
            rspDTO.setReason("问题为空，未识别范围");
            return rspDTO;
        }

        if (containsAny(query, "全部", "所有", "全公司", "全量")) {
            rspDTO.setScopeType("ALL");
            rspDTO.setReason("命中全量范围表达");
            return rspDTO;
        }
        if (containsAny(query, "本部门", "我们部门", "所在部门")) {
            rspDTO.setScopeType("DEPARTMENT");
            rspDTO.setReason("命中部门范围表达");
            return rspDTO;
        }
        if (containsAny(query, "基地", "试验站", "园区")) {
            rspDTO.setScopeType("BASE");
            rspDTO.setScopeValue(extractBaseName(query));
            rspDTO.setReason("命中基地范围表达");
            return rspDTO;
        }
        if (containsAny(query, "我的", "我负责", "我参与", "自己")) {
            rspDTO.setScopeType("USER");
            rspDTO.setScopeValue(reqDTO == null ? null : reqDTO.getUserId());
            rspDTO.setReason("命中当前用户范围表达");
            return rspDTO;
        }

        rspDTO.setScopeType("UNKNOWN");
        rspDTO.setReason("未命中明确范围");
        return rspDTO;
    }

    private String extractBaseName(String query) {
        int index = query.indexOf("基地");
        if (index < 0) {
            index = query.indexOf("试验站");
        }
        if (index < 0) {
            return null;
        }
        int start = Math.max(0, index - 6);
        int end = Math.min(query.length(), index + 3);
        return query.substring(start, end);
    }

    private boolean containsAny(String query, String... words) {
        for (String word : words) {
            if (query.contains(word)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
