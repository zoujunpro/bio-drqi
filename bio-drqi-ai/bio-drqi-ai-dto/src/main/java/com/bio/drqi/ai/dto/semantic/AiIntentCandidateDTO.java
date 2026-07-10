package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 意图候选项。
 */
@Data
public class AiIntentCandidateDTO implements Serializable {

    private String intentCode;

    private String intentName;

    private String domain;

    private String description;

    private String handlerType;

    private BigDecimal score;

    private String matchedText;

    private String matchType;

    private List<AiToolDefinitionDTO> tools;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHandlerType() {
        return handlerType;
    }

    public void setHandlerType(String handlerType) {
        this.handlerType = handlerType;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getMatchedText() {
        return matchedText;
    }

    public void setMatchedText(String matchedText) {
        this.matchedText = matchedText;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
    }

    public List<AiToolDefinitionDTO> getTools() {
        return tools;
    }

    public void setTools(List<AiToolDefinitionDTO> tools) {
        this.tools = tools;
    }
}
