package com.bio.drqi.ai.semantic.impl;

import com.bio.drqi.ai.dao.domain.AiBusinessDictionary;
import com.bio.drqi.ai.dao.mapper.AiBusinessDictionaryMapper;
import com.bio.drqi.ai.dto.semantic.AiSynonymNormalizeReqDTO;
import com.bio.drqi.ai.dto.semantic.AiSynonymNormalizeRspDTO;
import com.bio.drqi.ai.semantic.AiSynonymNormalizeService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 默认同义词归一实现。
 */
@Service
public class AiSynonymNormalizeServiceImpl implements AiSynonymNormalizeService {

    @Resource
    private AiBusinessDictionaryMapper aiBusinessDictionaryMapper;

    @Override
    public AiSynonymNormalizeRspDTO normalize(AiSynonymNormalizeReqDTO reqDTO) {
        AiSynonymNormalizeRspDTO rspDTO = new AiSynonymNormalizeRspDTO();
        String query = reqDTO == null ? null : reqDTO.getQuery();
        rspDTO.setOriginalText(query);
        rspDTO.setNormalizedText(query);
        rspDTO.setNormalized(Boolean.FALSE);

        if (!hasText(query)) {
            rspDTO.setReason("问题为空，无需归一");
            return rspDTO;
        }

        try {
            List<AiBusinessDictionary> dictionaries = aiBusinessDictionaryMapper.selectActiveList();
            if (dictionaries == null || dictionaries.isEmpty()) {
                rspDTO.setReason("业务词典为空");
                return rspDTO;
            }

            String normalizedText = query;
            for (AiBusinessDictionary dictionary : dictionaries) {
                normalizedText = normalizeByDictionary(normalizedText, dictionary);
            }
            rspDTO.setNormalizedText(normalizedText);
            rspDTO.setNormalized(!query.equals(normalizedText));
            rspDTO.setReason(Boolean.TRUE.equals(rspDTO.getNormalized()) ? "命中业务词典别名" : "未命中业务词典别名");
        } catch (Exception ignored) {
            rspDTO.setReason("业务词典不可用，保留原始问题");
        }
        return rspDTO;
    }

    private String normalizeByDictionary(String text, AiBusinessDictionary dictionary) {
        if (dictionary == null || !hasText(dictionary.getDictName()) || !hasText(dictionary.getAliases())) {
            return text;
        }
        String normalizedText = text;
        String[] aliases = dictionary.getAliases().split(",");
        for (String alias : aliases) {
            String trimmedAlias = alias == null ? null : alias.trim();
            if (hasText(trimmedAlias) && !trimmedAlias.equals(dictionary.getDictName())) {
                normalizedText = normalizedText.replace(trimmedAlias, dictionary.getDictName());
            }
        }
        return normalizedText;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
