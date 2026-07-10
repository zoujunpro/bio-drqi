package com.bio.drqi.ai.semantic.impl;

import com.bio.drqi.ai.common.enums.AiSemanticPatternTypeEnum;
import com.bio.drqi.ai.dao.domain.AiBusinessDictionary;
import com.bio.drqi.ai.dao.domain.AiSemanticPattern;
import com.bio.drqi.ai.dao.mapper.AiBusinessDictionaryMapper;
import com.bio.drqi.ai.dao.mapper.AiSemanticPatternMapper;
import com.bio.drqi.ai.dto.semantic.AiEntityDTO;
import com.bio.drqi.ai.dto.semantic.AiEntityExtractReqDTO;
import com.bio.drqi.ai.dto.semantic.AiEntityExtractRspDTO;
import com.bio.drqi.ai.semantic.AiEntityExtractService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 默认实体抽取实现。
 */
@Service
public class AiEntityExtractServiceImpl implements AiEntityExtractService {

    private static final Pattern CODE_PATTERN = Pattern.compile("[A-Za-z]{1,8}[0-9]{2,}");

    @Resource
    private AiBusinessDictionaryMapper aiBusinessDictionaryMapper;

    @Resource
    private AiSemanticPatternMapper aiSemanticPatternMapper;

    @Override
    public AiEntityExtractRspDTO extract(AiEntityExtractReqDTO reqDTO) {
        AiEntityExtractRspDTO rspDTO = new AiEntityExtractRspDTO();
        if (reqDTO == null || reqDTO.getQuery() == null) {
            return rspDTO;
        }

        Set<String> dedupe = new HashSet<String>();
        extractByBuiltinRule(reqDTO.getQuery(), rspDTO, dedupe);
        extractByConfiguredRegex(reqDTO.getQuery(), rspDTO, dedupe);
        extractByDictionary(reqDTO.getQuery(), rspDTO, dedupe);
        return rspDTO;
    }

    private void extractByBuiltinRule(String query, AiEntityExtractRspDTO rspDTO, Set<String> dedupe) {
        Matcher matcher = CODE_PATTERN.matcher(query);
        while (matcher.find()) {
            addEntity(rspDTO, dedupe, "code", "BUSINESS_CODE", matcher.group(), "RULE");
        }
    }

    private void extractByConfiguredRegex(String query, AiEntityExtractRspDTO rspDTO, Set<String> dedupe) {
        try {
            List<AiSemanticPattern> patterns = aiSemanticPatternMapper.selectActiveByPatternType(
                    AiSemanticPatternTypeEnum.ENTITY_REGEX.getCode()
            );
            if (patterns == null) {
                return;
            }
            for (AiSemanticPattern pattern : patterns) {
                if (!hasText(pattern.getPatternText())) {
                    continue;
                }
                Matcher matcher = Pattern.compile(pattern.getPatternText()).matcher(query);
                while (matcher.find()) {
                    String type = hasText(pattern.getTargetValue()) ? pattern.getTargetValue() : "BUSINESS_CODE";
                    addEntity(rspDTO, dedupe, pattern.getPatternCode(), type, matcher.group(), "PATTERN");
                }
            }
        } catch (Exception ignored) {
            // 配置规则异常不影响内置规则结果。
        }
    }

    private void extractByDictionary(String query, AiEntityExtractRspDTO rspDTO, Set<String> dedupe) {
        try {
            List<AiBusinessDictionary> dictionaries = aiBusinessDictionaryMapper.selectActiveList();
            if (dictionaries == null) {
                return;
            }
            for (AiBusinessDictionary dictionary : dictionaries) {
                if (matchesDictionary(query, dictionary)) {
                    String value = hasText(dictionary.getDictCode()) ? dictionary.getDictCode() : dictionary.getDictName();
                    addEntity(rspDTO, dedupe, dictionary.getDictType(), dictionary.getDictType(), value, "DICTIONARY");
                }
            }
        } catch (Exception ignored) {
            // 词典不可用时只返回规则抽取结果。
        }
    }

    private boolean matchesDictionary(String query, AiBusinessDictionary dictionary) {
        if (dictionary == null) {
            return false;
        }
        if (hasText(dictionary.getDictName()) && query.contains(dictionary.getDictName())) {
            return true;
        }
        if (!hasText(dictionary.getAliases())) {
            return false;
        }
        String[] aliases = dictionary.getAliases().split(",");
        for (String alias : aliases) {
            if (hasText(alias) && query.contains(alias.trim())) {
                return true;
            }
        }
        return false;
    }

    private void addEntity(AiEntityExtractRspDTO rspDTO, Set<String> dedupe, String name, String type, String value, String source) {
        if (!hasText(value)) {
            return;
        }
        String key = type + ":" + value;
        if (!dedupe.add(key)) {
            return;
        }
        AiEntityDTO entityDTO = new AiEntityDTO();
        entityDTO.setName(name);
        entityDTO.setType(type);
        entityDTO.setValue(value);
        entityDTO.setSource(source);
        rspDTO.getEntities().add(entityDTO);
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
