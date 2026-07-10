package com.bio.drqi.ai.dto.planner;

import com.bio.drqi.ai.dto.memory.AiMemoryContextRspDTO;
import com.bio.drqi.ai.dto.semantic.AiConditionExtractRspDTO;
import com.bio.drqi.ai.dto.semantic.AiEntityExtractRspDTO;
import com.bio.drqi.ai.dto.semantic.AiIntentRecognizeRspDTO;
import com.bio.drqi.ai.dto.semantic.AiNumberParseRspDTO;
import com.bio.drqi.ai.dto.semantic.AiScopeResolveRspDTO;
import com.bio.drqi.ai.dto.semantic.AiSemanticClassifyRspDTO;
import com.bio.drqi.ai.dto.semantic.AiTermMappingRspDTO;
import com.bio.drqi.ai.dto.semantic.AiTimeParseRspDTO;
import com.bio.drqi.ai.dto.semantic.AiToolDefinitionDTO;
import com.bio.drqi.ai.dto.semantic.AiUserContextResolveRspDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * AI Planner 请求。
 */
@Data
public class AiPlanReqDTO implements Serializable {

    private String sessionId;

    private String userId;

    /**
     * 用户原始问题。
     */
    private String originalQuery;

    /**
     * 指代消解后的问题。
     */
    private String rewrittenQuery;

    /**
     * 同义词归一后的问题。
     */
    private String normalizedQuery;

    /**
     * Memory 上下文。
     */
    private AiMemoryContextRspDTO memoryContext;

    /**
     * 系统话术分类结果。
     */
    private AiSemanticClassifyRspDTO semanticClassifyResult;

    /**
     * 当前用户上下文。
     */
    private AiUserContextResolveRspDTO userContext;

    /**
     * 意图识别结果。
     */
    private AiIntentRecognizeRspDTO intentResult;

    /**
     * 时间解析结果。
     */
    private AiTimeParseRspDTO timeResult;

    /**
     * 数量解析结果。
     */
    private AiNumberParseRspDTO numberResult;

    /**
     * 范围解析结果。
     */
    private AiScopeResolveRspDTO scopeResult;

    /**
     * 业务术语映射结果。
     */
    private AiTermMappingRspDTO termResult;

    /**
     * 实体抽取结果。
     */
    private AiEntityExtractRspDTO entityResult;

    /**
     * 条件抽取结果。
     */
    private AiConditionExtractRspDTO conditionResult;

    /**
     * 候选工具。通常来自意图识别结果，也允许调用方补充。
     */
    private List<AiToolDefinitionDTO> candidateTools = new ArrayList<AiToolDefinitionDTO>();

    private static final long serialVersionUID = 1L;
}
