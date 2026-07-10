package com.bio.drqi.ai.semantic.impl;

import com.bio.drqi.ai.common.enums.AiSemanticCategoryEnum;
import com.bio.drqi.ai.common.enums.AiSemanticPatternTypeEnum;
import com.bio.drqi.ai.dao.domain.AiSemanticPattern;
import com.bio.drqi.ai.dao.mapper.AiSemanticPatternMapper;
import com.bio.drqi.ai.dto.semantic.AiSemanticClassifyReqDTO;
import com.bio.drqi.ai.dto.semantic.AiSemanticClassifyRspDTO;
import com.bio.drqi.ai.semantic.AiSemanticClassifyService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 默认语义分类实现。
 */
@Service
public class AiSemanticClassifyServiceImpl implements AiSemanticClassifyService {

    @Resource
    private AiSemanticPatternMapper aiSemanticPatternMapper;

    @Override
    public AiSemanticClassifyRspDTO classify(AiSemanticClassifyReqDTO reqDTO) {
        AiSemanticClassifyRspDTO rspDTO = new AiSemanticClassifyRspDTO();
        String query = reqDTO == null ? null : reqDTO.getQuery();
        if (!hasText(query)) {
            rspDTO.setCategory(AiSemanticCategoryEnum.UNKNOWN.getCode());
            rspDTO.setConfidence(BigDecimal.ZERO);
            rspDTO.setReason("用户问题为空");
            return rspDTO;
        }

        //如果数据库 ai_semantic_pattern 里配置了系统话术分类规则，并且当前问题命中了，就优先用数据库配置结果。
        AiSemanticClassifyRspDTO configuredResult = classifyByConfiguredPattern(query);
        if (configuredResult != null) {
            return configuredResult;
        }

        if (containsAny(query, "你好", "您好", "在吗", "哈喽", "hello")) {
            rspDTO.setCategory(AiSemanticCategoryEnum.GREETING.getCode());
            rspDTO.setConfidence(BigDecimal.ONE);
            rspDTO.setReason("命中问候语");
        } else if (containsAny(query, "再见", "拜拜", "bye")) {
            rspDTO.setCategory(AiSemanticCategoryEnum.GOODBYE.getCode());
            rspDTO.setConfidence(BigDecimal.ONE);
            rspDTO.setReason("命中告别语");
        } else if (containsAny(query, "谢谢", "感谢")) {
            rspDTO.setCategory(AiSemanticCategoryEnum.THANKS.getCode());
            rspDTO.setConfidence(BigDecimal.ONE);
            rspDTO.setReason("命中感谢语");
        } else if (containsAny(query, "你能做什么", "怎么用", "帮助")) {
            rspDTO.setCategory(AiSemanticCategoryEnum.HELP.getCode());
            rspDTO.setConfidence(BigDecimal.ONE);
            rspDTO.setReason("命中帮助意图");
        } else if (containsAny(query, "是的", "对", "确认", "继续", "可以", "好的", "嗯")) {
            rspDTO.setCategory(AiSemanticCategoryEnum.CONFIRMATION.getCode());
            rspDTO.setConfidence(BigDecimal.ONE);
            rspDTO.setReason("命中确认语义");
        } else if (containsAny(query, "不是", "改成", "应该是", "我说的是")) {
            rspDTO.setCategory(AiSemanticCategoryEnum.CORRECTION.getCode());
            rspDTO.setConfidence(BigDecimal.ONE);
            rspDTO.setReason("命中纠错语义");
        } else if (containsAny(query, "不是", "不对", "不要", "取消", "算了")) {
            rspDTO.setCategory(AiSemanticCategoryEnum.REJECTION.getCode());
            rspDTO.setConfidence(BigDecimal.ONE);
            rspDTO.setReason("命中否定语义");
        } else if (containsAny(query, "它", "这个", "那个", "上个", "刚才", "继续看", "然后呢")) {
            rspDTO.setCategory(AiSemanticCategoryEnum.FOLLOW_UP.getCode());
            rspDTO.setConfidence(new BigDecimal("0.90"));
            rspDTO.setReason("命中追问语义");
        } else if (containsAny(query, "文档", "制度", "规范", "知识库", "说明书")) {
            rspDTO.setCategory(AiSemanticCategoryEnum.KNOWLEDGE_QUERY.getCode());
            rspDTO.setConfidence(new BigDecimal("0.80"));
            rspDTO.setReason("命中知识库语义");
        } else if (containsAny(query, "文件", "附件", "表格", "excel", "pdf", "word", "帮我分析这个")) {
            rspDTO.setCategory(AiSemanticCategoryEnum.FILE_ANALYSIS.getCode());
            rspDTO.setConfidence(new BigDecimal("0.80"));
            rspDTO.setReason("命中文件分析语义");
        } else if (containsAny(query, "身份证", "银行卡", "密码", "密钥", "token", "工资")) {
            rspDTO.setCategory(AiSemanticCategoryEnum.SENSITIVE.getCode());
            rspDTO.setConfidence(new BigDecimal("0.80"));
            rspDTO.setReason("命中敏感语义");
        } else if (containsAny(query, "你是谁", "讲个笑话", "无聊")) {
            rspDTO.setCategory(AiSemanticCategoryEnum.CHITCHAT.getCode());
            rspDTO.setConfidence(new BigDecimal("0.80"));
            rspDTO.setReason("命中闲聊语义");
        } else {
            rspDTO.setCategory(AiSemanticCategoryEnum.BUSINESS.getCode());
            rspDTO.setConfidence(new BigDecimal("0.60"));
            rspDTO.setReason("未命中系统话术，进入业务语义处理");
        }
        return rspDTO;
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

    private AiSemanticClassifyRspDTO classifyByConfiguredPattern(String query) {
        try {
            List<AiSemanticPattern> patterns = aiSemanticPatternMapper.selectActiveByPatternType(
                    AiSemanticPatternTypeEnum.SYSTEM_CLASSIFY.getCode()
            );
            if (patterns == null) {
                return null;
            }
            for (AiSemanticPattern pattern : patterns) {
                if (hasText(pattern.getPatternText()) && query.contains(pattern.getPatternText())) {
                    AiSemanticClassifyRspDTO rspDTO = new AiSemanticClassifyRspDTO();
                    rspDTO.setCategory(pattern.getTargetValue());
                    rspDTO.setConfidence(pattern.getWeight() == null ? BigDecimal.ONE : pattern.getWeight());
                    rspDTO.setReason("命中配置话术：" + pattern.getPatternText());
                    return rspDTO;
                }
            }
        } catch (Exception ignored) {
            return null;
        }
        return null;
    }
}
