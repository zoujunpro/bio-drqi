package com.bio.drqi.ai.semantic.impl;

import com.bio.drqi.ai.dao.domain.AiBusinessDictionary;
import com.bio.drqi.ai.dao.mapper.AiBusinessDictionaryMapper;
import com.bio.drqi.ai.dto.semantic.AiTermDTO;
import com.bio.drqi.ai.dto.semantic.AiTermMappingReqDTO;
import com.bio.drqi.ai.dto.semantic.AiTermMappingRspDTO;
import com.bio.drqi.ai.semantic.AiTermMappingService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 默认业务术语映射实现。
 */
@Service
public class AiTermMappingServiceImpl implements AiTermMappingService {

    @Resource
    private AiBusinessDictionaryMapper aiBusinessDictionaryMapper;

    @Override
    public AiTermMappingRspDTO map(AiTermMappingReqDTO reqDTO) {
        AiTermMappingRspDTO rspDTO = new AiTermMappingRspDTO();
        if (reqDTO == null || !hasText(reqDTO.getQuery())) {
            return rspDTO;
        }

        try {
            List<AiBusinessDictionary> dictionaries = aiBusinessDictionaryMapper.selectActiveList();
            if (dictionaries == null) {
                return rspDTO;
            }
            Set<String> dedupe = new HashSet<String>();
            for (AiBusinessDictionary dictionary : dictionaries) {
                if (reqDTO.getDomain() != null && hasText(dictionary.getDomain()) && !reqDTO.getDomain().equals(dictionary.getDomain())) {
                    continue;
                }
                String matchedTerm = findMatchedTerm(reqDTO.getQuery(), dictionary);
                if (hasText(matchedTerm)) {
                    addTerm(rspDTO, dedupe, matchedTerm, dictionary);
                }
            }
        } catch (Exception ignored) {
            // 词典不可用时返回空映射，由后续 LLM 或工具参数校验兜底。
        }
        return rspDTO;
    }

    private String findMatchedTerm(String query, AiBusinessDictionary dictionary) {
        if (dictionary == null) {
            return null;
        }
        if (hasText(dictionary.getDictName()) && query.contains(dictionary.getDictName())) {
            return dictionary.getDictName();
        }
        if (!hasText(dictionary.getAliases())) {
            return null;
        }
        String[] aliases = dictionary.getAliases().split(",");
        for (String alias : aliases) {
            String trimmedAlias = alias == null ? null : alias.trim();
            if (hasText(trimmedAlias) && query.contains(trimmedAlias)) {
                return trimmedAlias;
            }
        }
        return null;
    }

    private void addTerm(AiTermMappingRspDTO rspDTO, Set<String> dedupe, String matchedTerm, AiBusinessDictionary dictionary) {
        String key = dictionary.getDictType() + ":" + dictionary.getDictCode();
        if (!dedupe.add(key)) {
            return;
        }
        AiTermDTO termDTO = new AiTermDTO();
        termDTO.setTerm(matchedTerm);
        termDTO.setMappedCode(dictionary.getDictCode());
        termDTO.setMappedName(dictionary.getDictName());
        termDTO.setTermType(dictionary.getDictType());
        termDTO.setSource("DICTIONARY");
        rspDTO.getTerms().add(termDTO);
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
