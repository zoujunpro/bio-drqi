package com.bio.drqi.ai.semantic.impl;

import com.bio.drqi.ai.common.enums.AiIntentHandlerTypeEnum;
import com.bio.drqi.ai.common.enums.AiIntentMatchTypeEnum;
import com.bio.drqi.ai.common.enums.AiSemanticPatternTypeEnum;
import com.bio.drqi.ai.dao.domain.AiIntent;
import com.bio.drqi.ai.dao.domain.AiIntentExample;
import com.bio.drqi.ai.dao.domain.AiIntentToolRel;
import com.bio.drqi.ai.dao.domain.AiSemanticPattern;
import com.bio.drqi.ai.dao.domain.AiToolDefinition;
import com.bio.drqi.ai.dao.mapper.AiIntentExampleMapper;
import com.bio.drqi.ai.dao.mapper.AiIntentMapper;
import com.bio.drqi.ai.dao.mapper.AiIntentToolRelMapper;
import com.bio.drqi.ai.dao.mapper.AiSemanticPatternMapper;
import com.bio.drqi.ai.dao.mapper.AiToolDefinitionMapper;
import com.bio.drqi.ai.dto.semantic.AiIntentCandidateDTO;
import com.bio.drqi.ai.dto.semantic.AiIntentRecognizeReqDTO;
import com.bio.drqi.ai.dto.semantic.AiIntentRecognizeRspDTO;
import com.bio.drqi.ai.dto.semantic.AiToolDefinitionDTO;
import com.bio.drqi.ai.semantic.AiIntentRouterService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 基于业务意图库的默认意图路由实现。
 */
@Service
public class AiIntentRouterServiceImpl implements AiIntentRouterService {

    private static final String UNKNOWN_INTENT = "UNKNOWN";

    @Resource
    private AiIntentMapper aiIntentMapper;

    @Resource
    private AiIntentExampleMapper aiIntentExampleMapper;

    @Resource
    private AiIntentToolRelMapper aiIntentToolRelMapper;

    @Resource
    private AiToolDefinitionMapper aiToolDefinitionMapper;

    @Resource
    private AiSemanticPatternMapper aiSemanticPatternMapper;

    @Override
    public AiIntentRecognizeRspDTO recognize(AiIntentRecognizeReqDTO reqDTO) {
        if (reqDTO == null || !hasText(reqDTO.getQuery())) {
            return buildUnknownResult("用户问题为空");
        }

        List<AiIntent> intents = aiIntentMapper.selectActiveList();
        if (intents == null || intents.isEmpty()) {
            return buildUnknownResult("未配置启用的业务意图");
        }

        List<String> intentCodes = collectIntentCodes(intents);
        List<AiIntentExample> examples = aiIntentExampleMapper.selectActiveByIntentCodes(intentCodes);
        Map<String, List<AiIntentExample>> exampleMap = groupExamples(examples);
        Map<String, List<AiSemanticPattern>> keywordMap = groupIntentKeywords();

        List<AiIntentCandidateDTO> candidates = new ArrayList<AiIntentCandidateDTO>();
        String normalizedQuery = normalize(reqDTO.getQuery());
        for (AiIntent intent : intents) {
            AiIntentCandidateDTO candidate = scoreIntent(
                    intent,
                    exampleMap.get(intent.getIntentCode()),
                    keywordMap.get(intent.getIntentCode()),
                    normalizedQuery
            );
            if (candidate.getScore().compareTo(BigDecimal.ZERO) > 0) {
                candidates.add(candidate);
            }
        }

        if (candidates.isEmpty()) {
            return buildUnknownResult("未匹配到业务意图");
        }

        Collections.sort(candidates, new Comparator<AiIntentCandidateDTO>() {
            @Override
            public int compare(AiIntentCandidateDTO left, AiIntentCandidateDTO right) {
                return right.getScore().compareTo(left.getScore());
            }
        });

        if (candidates.size() > 5) {
            candidates = new ArrayList<AiIntentCandidateDTO>(candidates.subList(0, 5));
        }

        AiIntentCandidateDTO best = candidates.get(0);
        List<AiToolDefinitionDTO> tools = loadTools(best.getIntentCode());
        best.setTools(tools);

        AiIntentRecognizeRspDTO rspDTO = new AiIntentRecognizeRspDTO();
        rspDTO.setIntentCode(best.getIntentCode());
        rspDTO.setIntentName(best.getIntentName());
        rspDTO.setDomain(best.getDomain());
        rspDTO.setHandlerType(best.getHandlerType());
        rspDTO.setConfidence(best.getScore());
        rspDTO.setMatchType(best.getMatchType());
        rspDTO.setReason("命中：" + best.getMatchedText());
        rspDTO.setTools(tools);
        rspDTO.setCandidates(candidates);
        return rspDTO;
    }

    private AiIntentCandidateDTO scoreIntent(
            AiIntent intent,
            List<AiIntentExample> examples,
            List<AiSemanticPattern> keywords,
            String normalizedQuery
    ) {
        AiIntentCandidateDTO candidate = new AiIntentCandidateDTO();
        candidate.setIntentCode(intent.getIntentCode());
        candidate.setIntentName(intent.getIntentName());
        candidate.setDomain(intent.getDomain());
        candidate.setDescription(intent.getDescription());
        candidate.setHandlerType(intent.getHandlerType());
        candidate.setMatchType(AiIntentMatchTypeEnum.RULE.getCode());

        BigDecimal bestScore = BigDecimal.ZERO;
        String matchedText = "";

        ScoreResult codeScore = scoreText(normalizedQuery, intent.getIntentCode(), new BigDecimal("0.60"));
        if (codeScore.getScore().compareTo(bestScore) > 0) {
            bestScore = codeScore.getScore();
            matchedText = intent.getIntentCode();
        }

        ScoreResult nameScore = scoreText(normalizedQuery, intent.getIntentName(), new BigDecimal("0.85"));
        if (nameScore.getScore().compareTo(bestScore) > 0) {
            bestScore = nameScore.getScore();
            matchedText = intent.getIntentName();
        }

        ScoreResult descScore = scoreText(normalizedQuery, intent.getDescription(), new BigDecimal("0.65"));
        if (descScore.getScore().compareTo(bestScore) > 0) {
            bestScore = descScore.getScore();
            matchedText = intent.getDescription();
        }

        if (examples != null) {
            for (AiIntentExample example : examples) {
                ScoreResult exampleScore = scoreText(normalizedQuery, example.getExampleText(), BigDecimal.ONE);
                if (exampleScore.getScore().compareTo(bestScore) > 0) {
                    bestScore = exampleScore.getScore();
                    matchedText = example.getExampleText();
                }
            }
        }

        if (keywords != null) {
            for (AiSemanticPattern keyword : keywords) {
                ScoreResult keywordScore = scoreText(
                        normalizedQuery,
                        keyword.getPatternText(),
                        keyword.getWeight() == null ? new BigDecimal("0.90") : keyword.getWeight()
                );
                if (keywordScore.getScore().compareTo(bestScore) > 0) {
                    bestScore = keywordScore.getScore();
                    matchedText = keyword.getPatternText();
                    candidate.setMatchType(AiIntentMatchTypeEnum.KEYWORD.getCode());
                }
            }
        }

        if (!hasText(candidate.getHandlerType())) {
            candidate.setHandlerType(AiIntentHandlerTypeEnum.TOOL.getCode());
        }
        candidate.setScore(bestScore.setScale(2, RoundingMode.HALF_UP));
        candidate.setMatchedText(matchedText);
        return candidate;
    }

    private ScoreResult scoreText(String normalizedQuery, String sourceText, BigDecimal weight) {
        if (!hasText(normalizedQuery) || !hasText(sourceText)) {
            return new ScoreResult(BigDecimal.ZERO);
        }
        String normalizedSource = normalize(sourceText);
        if (!hasText(normalizedSource)) {
            return new ScoreResult(BigDecimal.ZERO);
        }
        if (normalizedQuery.contains(normalizedSource) || normalizedSource.contains(normalizedQuery)) {
            return new ScoreResult(weight);
        }

        Set<String> queryTokens = tokenize(normalizedQuery);
        Set<String> sourceTokens = tokenize(normalizedSource);
        if (queryTokens.isEmpty() || sourceTokens.isEmpty()) {
            return new ScoreResult(BigDecimal.ZERO);
        }

        int hit = 0;
        for (String token : sourceTokens) {
            if (queryTokens.contains(token)) {
                hit++;
            }
        }
        if (hit == 0) {
            return new ScoreResult(BigDecimal.ZERO);
        }
        BigDecimal ratio = new BigDecimal(hit).divide(new BigDecimal(sourceTokens.size()), 4, RoundingMode.HALF_UP);
        return new ScoreResult(ratio.multiply(weight));
    }

    private List<AiToolDefinitionDTO> loadTools(String intentCode) {
        List<AiIntentToolRel> relList = aiIntentToolRelMapper.selectActiveByIntentCode(intentCode);
        if (relList == null || relList.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> toolCodes = new ArrayList<String>();
        for (AiIntentToolRel rel : relList) {
            if (hasText(rel.getToolCode()) && !toolCodes.contains(rel.getToolCode())) {
                toolCodes.add(rel.getToolCode());
            }
        }
        if (toolCodes.isEmpty()) {
            return Collections.emptyList();
        }

        List<AiToolDefinition> tools = aiToolDefinitionMapper.selectActiveByToolCodes(toolCodes);
        Map<String, AiToolDefinition> toolMap = new HashMap<String, AiToolDefinition>();
        for (AiToolDefinition tool : tools) {
            toolMap.put(tool.getToolCode(), tool);
        }

        List<AiToolDefinitionDTO> result = new ArrayList<AiToolDefinitionDTO>();
        for (String toolCode : toolCodes) {
            AiToolDefinition tool = toolMap.get(toolCode);
            if (tool != null) {
                result.add(convertTool(tool));
            }
        }
        return result;
    }

    private AiToolDefinitionDTO convertTool(AiToolDefinition tool) {
        AiToolDefinitionDTO dto = new AiToolDefinitionDTO();
        dto.setToolCode(tool.getToolCode());
        dto.setToolName(tool.getToolName());
        dto.setDescription(tool.getDescription());
        dto.setToolType(tool.getToolType());
        dto.setTargetCode(tool.getTargetCode());
        dto.setInputSchema(tool.getInputSchema());
        dto.setOutputSchema(tool.getOutputSchema());
        dto.setServiceUrl(tool.getServiceUrl());
        dto.setHttpMethod(tool.getHttpMethod());
        dto.setRiskLevel(tool.getRiskLevel());
        dto.setReadOnly(tool.getReadOnly());
        return dto;
    }

    private List<String> collectIntentCodes(List<AiIntent> intents) {
        List<String> result = new ArrayList<String>();
        for (AiIntent intent : intents) {
            if (hasText(intent.getIntentCode())) {
                result.add(intent.getIntentCode());
            }
        }
        return result;
    }

    private Map<String, List<AiIntentExample>> groupExamples(List<AiIntentExample> examples) {
        Map<String, List<AiIntentExample>> result = new HashMap<String, List<AiIntentExample>>();
        if (examples == null) {
            return result;
        }
        for (AiIntentExample example : examples) {
            if (!hasText(example.getIntentCode())) {
                continue;
            }
            List<AiIntentExample> list = result.get(example.getIntentCode());
            if (list == null) {
                list = new ArrayList<AiIntentExample>();
                result.put(example.getIntentCode(), list);
            }
            list.add(example);
        }
        return result;
    }

    private Map<String, List<AiSemanticPattern>> groupIntentKeywords() {
        Map<String, List<AiSemanticPattern>> result = new HashMap<String, List<AiSemanticPattern>>();
        try {
            List<AiSemanticPattern> patterns = aiSemanticPatternMapper.selectActiveByPatternType(
                    AiSemanticPatternTypeEnum.INTENT_KEYWORD.getCode()
            );
            if (patterns == null) {
                return result;
            }
            for (AiSemanticPattern pattern : patterns) {
                if (!hasText(pattern.getTargetValue())) {
                    continue;
                }
                List<AiSemanticPattern> list = result.get(pattern.getTargetValue());
                if (list == null) {
                    list = new ArrayList<AiSemanticPattern>();
                    result.put(pattern.getTargetValue(), list);
                }
                list.add(pattern);
            }
        } catch (Exception ignored) {
            return result;
        }
        return result;
    }

    private AiIntentRecognizeRspDTO buildUnknownResult(String reason) {
        AiIntentRecognizeRspDTO rspDTO = new AiIntentRecognizeRspDTO();
        rspDTO.setIntentCode(UNKNOWN_INTENT);
        rspDTO.setIntentName("未知意图");
        rspDTO.setHandlerType(AiIntentHandlerTypeEnum.CHAT.getCode());
        rspDTO.setConfidence(BigDecimal.ZERO);
        rspDTO.setMatchType(AiIntentMatchTypeEnum.NONE.getCode());
        rspDTO.setReason(reason);
        rspDTO.setTools(Collections.<AiToolDefinitionDTO>emptyList());
        rspDTO.setCandidates(Collections.<AiIntentCandidateDTO>emptyList());
        return rspDTO;
    }

    private Set<String> tokenize(String value) {
        Set<String> result = new HashSet<String>();
        if (!hasText(value)) {
            return result;
        }
        String[] parts = value.split("[^a-zA-Z0-9\\u4e00-\\u9fa5]+");
        for (String part : parts) {
            if (part.length() >= 2) {
                result.add(part);
                addBigrams(result, part);
            }
        }
        return result;
    }

    private void addBigrams(Set<String> result, String value) {
        if (value == null || value.length() < 2) {
            return;
        }
        for (int i = 0; i < value.length() - 1; i++) {
            result.add(value.substring(i, i + 2));
        }
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toLowerCase();
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    private static class ScoreResult {

        private final BigDecimal score;

        ScoreResult(BigDecimal score) {
            this.score = score;
        }

        BigDecimal getScore() {
            return score;
        }
    }
}
