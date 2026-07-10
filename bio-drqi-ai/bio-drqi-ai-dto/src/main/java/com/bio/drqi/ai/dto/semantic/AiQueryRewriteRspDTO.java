package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户问题改写结果。
 */
@Data
public class AiQueryRewriteRspDTO implements Serializable {

    /**
     * 用户原始问题。
     */
    private String originalQuery;

    /**
     * 改写后的完整问题。
     */
    private String rewrittenQuery;

    /**
     * 是否发生改写。
     */
    private Boolean rewritten;

    /**
     * 改写原因。
     */
    private String reason;

    private static final long serialVersionUID = 1L;

    public String getOriginalQuery() {
        return originalQuery;
    }

    public void setOriginalQuery(String originalQuery) {
        this.originalQuery = originalQuery;
    }

    public String getRewrittenQuery() {
        return rewrittenQuery;
    }

    public void setRewrittenQuery(String rewrittenQuery) {
        this.rewrittenQuery = rewrittenQuery;
    }

    public Boolean getRewritten() {
        return rewritten;
    }

    public void setRewritten(Boolean rewritten) {
        this.rewritten = rewritten;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
