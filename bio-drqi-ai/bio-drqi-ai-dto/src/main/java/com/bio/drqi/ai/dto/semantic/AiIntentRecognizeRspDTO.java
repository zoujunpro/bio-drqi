package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 意图识别结果。
 */
@Data
public class AiIntentRecognizeRspDTO implements Serializable {

    /**
     * 命中的业务意图编码；未命中时为 UNKNOWN。
     */
    private String intentCode;

    /**
     * 命中的业务意图名称。
     */
    private String intentName;

    /**
     * 业务领域，例如 项目管理、CER、种子库。
     */
    private String domain;

    /**
     * 意图处理类型：TOOL/RAG/FILE/CHAT/WORKFLOW。
     */
    private String handlerType;

    /**
     * 置信度，取值 0 到 1。
     */
    private BigDecimal confidence;

    /**
     * 匹配方式：NONE/RULE/KEYWORD/VECTOR/LLM。
     */
    private String matchType;

    /**
     * 命中原因或未命中原因。
     */
    private String reason;

    /**
     * 当前意图关联的可调用工具。
     */
    private List<AiToolDefinitionDTO> tools;

    /**
     * 候选意图列表，按分数从高到低返回。
     */
    private List<AiIntentCandidateDTO> candidates;

    private static final long serialVersionUID = 1L;

    public String getIntentCode() {
        return intentCode;
    }

    public void setIntentCode(String intentCode) {
        this.intentCode = intentCode;
    }

    public String getIntentName() {
        return intentName;
    }

    public void setIntentName(String intentName) {
        this.intentName = intentName;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getHandlerType() {
        return handlerType;
    }

    public void setHandlerType(String handlerType) {
        this.handlerType = handlerType;
    }

    public BigDecimal getConfidence() {
        return confidence;
    }

    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<AiToolDefinitionDTO> getTools() {
        return tools;
    }

    public void setTools(List<AiToolDefinitionDTO> tools) {
        this.tools = tools;
    }

    public List<AiIntentCandidateDTO> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<AiIntentCandidateDTO> candidates) {
        this.candidates = candidates;
    }
}
