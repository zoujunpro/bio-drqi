package com.bio.drqi.ai.semantic.impl;

import com.bio.drqi.ai.dto.semantic.AiConditionDTO;
import com.bio.drqi.ai.dto.semantic.AiConditionExtractReqDTO;
import com.bio.drqi.ai.dto.semantic.AiConditionExtractRspDTO;
import com.bio.drqi.ai.dto.semantic.AiNumberItemDTO;
import com.bio.drqi.ai.dto.semantic.AiNumberParseReqDTO;
import com.bio.drqi.ai.dto.semantic.AiNumberParseRspDTO;
import com.bio.drqi.ai.semantic.AiConditionExtractService;
import com.bio.drqi.ai.semantic.AiNumberParseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 默认条件抽取实现。
 */
@Service
public class AiConditionExtractServiceImpl implements AiConditionExtractService {

    @Resource
    private AiNumberParseService aiNumberParseService;

    @Override
    public AiConditionExtractRspDTO extract(AiConditionExtractReqDTO reqDTO) {
        AiConditionExtractRspDTO rspDTO = new AiConditionExtractRspDTO();
        if (reqDTO == null || !hasText(reqDTO.getQuery())) {
            return rspDTO;
        }

        extractStatusCondition(reqDTO.getQuery(), rspDTO);
        extractNumberCondition(reqDTO.getQuery(), rspDTO);
        return rspDTO;
    }

    private void extractStatusCondition(String query, AiConditionExtractRspDTO rspDTO) {
        if (containsAny(query, "已完成", "完成了", "已结束")) {
            addCondition(rspDTO, "status", "EQ", "COMPLETED", null, "STRING", "RULE");
        }
        if (containsAny(query, "未完成", "没完成", "进行中")) {
            addCondition(rspDTO, "status", "EQ", "PROCESSING", null, "STRING", "RULE");
        }
        if (containsAny(query, "延期", "逾期", "超期")) {
            addCondition(rspDTO, "risk_status", "EQ", "DELAYED", null, "STRING", "RULE");
        }
        if (containsAny(query, "已检测", "检测完成")) {
            addCondition(rspDTO, "test_status", "EQ", "TESTED", null, "STRING", "RULE");
        }
        if (containsAny(query, "未检测", "没有检测")) {
            addCondition(rspDTO, "test_status", "EQ", "UNTESTED", null, "STRING", "RULE");
        }
        if (containsAny(query, "已审核", "有审核结果")) {
            addCondition(rspDTO, "audit_status", "EQ", "AUDITED", null, "STRING", "RULE");
        }
        if (containsAny(query, "未审核", "没有审核结果")) {
            addCondition(rspDTO, "audit_status", "EQ", "UNAUDITED", null, "STRING", "RULE");
        }
    }

    private void extractNumberCondition(String query, AiConditionExtractRspDTO rspDTO) {
        AiNumberParseReqDTO numberReqDTO = new AiNumberParseReqDTO();
        numberReqDTO.setQuery(query);
        AiNumberParseRspDTO numberRspDTO = aiNumberParseService.parse(numberReqDTO);
        for (AiNumberItemDTO numberItemDTO : numberRspDTO.getNumbers()) {
            String secondValue = numberItemDTO.getSecondValue() == null ? null : numberItemDTO.getSecondValue().toPlainString();
            addCondition(rspDTO, "number", numberItemDTO.getOperator(), numberItemDTO.getValue().toPlainString(), secondValue, "NUMBER", "RULE");
        }
    }

    private void addCondition(AiConditionExtractRspDTO rspDTO, String field, String operator, String value, String secondValue, String valueType, String source) {
        AiConditionDTO conditionDTO = new AiConditionDTO();
        conditionDTO.setField(field);
        conditionDTO.setOperator(operator);
        conditionDTO.setValue(value);
        conditionDTO.setSecondValue(secondValue);
        conditionDTO.setValueType(valueType);
        conditionDTO.setSource(source);
        rspDTO.getConditions().add(conditionDTO);
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
