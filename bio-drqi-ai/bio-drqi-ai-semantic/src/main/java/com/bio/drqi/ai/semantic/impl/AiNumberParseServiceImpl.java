package com.bio.drqi.ai.semantic.impl;

import com.bio.drqi.ai.dto.semantic.AiNumberItemDTO;
import com.bio.drqi.ai.dto.semantic.AiNumberParseReqDTO;
import com.bio.drqi.ai.dto.semantic.AiNumberParseRspDTO;
import com.bio.drqi.ai.semantic.AiNumberParseService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 默认数量解析实现。
 */
@Service
public class AiNumberParseServiceImpl implements AiNumberParseService {

    private static final Pattern COMPARE_PATTERN = Pattern.compile("(大于等于|不少于|至少|>=|大于|超过|>|小于等于|不超过|最多|<=|小于|低于|少于|<|等于|=)?\\s*(\\d+(?:\\.\\d+)?)\\s*([个条株亩公斤千克kgKG天周月年%％]*)");

    private static final Pattern BETWEEN_PATTERN = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(?:到|至|~|-|—)\\s*(\\d+(?:\\.\\d+)?)\\s*([个条株亩公斤千克kgKG天周月年%％]*)");

    @Override
    public AiNumberParseRspDTO parse(AiNumberParseReqDTO reqDTO) {
        AiNumberParseRspDTO rspDTO = new AiNumberParseRspDTO();
        if (reqDTO == null || !hasText(reqDTO.getQuery())) {
            return rspDTO;
        }

        Set<String> dedupe = new HashSet<String>();
        extractBetween(reqDTO.getQuery(), rspDTO, dedupe);
        extractCompare(reqDTO.getQuery(), rspDTO, dedupe);
        return rspDTO;
    }

    private void extractBetween(String query, AiNumberParseRspDTO rspDTO, Set<String> dedupe) {
        Matcher matcher = BETWEEN_PATTERN.matcher(query);
        while (matcher.find()) {
            AiNumberItemDTO itemDTO = new AiNumberItemDTO();
            itemDTO.setExpression(matcher.group());
            itemDTO.setOperator("BETWEEN");
            itemDTO.setValue(new BigDecimal(matcher.group(1)));
            itemDTO.setSecondValue(new BigDecimal(matcher.group(2)));
            itemDTO.setUnit(normalizeUnit(matcher.group(3)));
            addNumber(rspDTO, dedupe, itemDTO);
        }
    }

    private void extractCompare(String query, AiNumberParseRspDTO rspDTO, Set<String> dedupe) {
        Matcher matcher = COMPARE_PATTERN.matcher(query);
        while (matcher.find()) {
            String expression = matcher.group();
            String value = matcher.group(2);
            if (!hasText(expression) || !hasText(value)) {
                continue;
            }
            AiNumberItemDTO itemDTO = new AiNumberItemDTO();
            itemDTO.setExpression(expression.trim());
            itemDTO.setOperator(normalizeOperator(matcher.group(1)));
            itemDTO.setValue(new BigDecimal(value));
            itemDTO.setUnit(normalizeUnit(matcher.group(3)));
            addNumber(rspDTO, dedupe, itemDTO);
        }
    }

    private void addNumber(AiNumberParseRspDTO rspDTO, Set<String> dedupe, AiNumberItemDTO itemDTO) {
        String key = itemDTO.getOperator() + ":" + itemDTO.getValue() + ":" + itemDTO.getSecondValue() + ":" + itemDTO.getUnit();
        if (dedupe.add(key)) {
            rspDTO.getNumbers().add(itemDTO);
        }
    }

    private String normalizeOperator(String operatorText) {
        if (!hasText(operatorText)) {
            return "EQ";
        }
        if ("大于等于".equals(operatorText) || "不少于".equals(operatorText) || "至少".equals(operatorText) || ">=".equals(operatorText)) {
            return "GTE";
        }
        if ("大于".equals(operatorText) || "超过".equals(operatorText) || ">".equals(operatorText)) {
            return "GT";
        }
        if ("小于等于".equals(operatorText) || "不超过".equals(operatorText) || "最多".equals(operatorText) || "<=".equals(operatorText)) {
            return "LTE";
        }
        if ("小于".equals(operatorText) || "低于".equals(operatorText) || "少于".equals(operatorText) || "<".equals(operatorText)) {
            return "LT";
        }
        return "EQ";
    }

    private String normalizeUnit(String unit) {
        if (!hasText(unit)) {
            return null;
        }
        if ("KG".equals(unit) || "kg".equals(unit) || "公斤".equals(unit)) {
            return "千克";
        }
        if ("％".equals(unit)) {
            return "%";
        }
        return unit;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
