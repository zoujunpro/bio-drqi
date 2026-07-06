package com.bio.drqi.ai.clarify;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 澄清候选项。
 * 可以代表意图、业务域、工具/API 或一个推荐问法。
 */
@Data
public class ClarificationCandidate {

    private String code;

    private String name;

    /**
     * intent/domain/tool/template。
     */
    private String type;

    /**
     * 0-1 之间的归一化分数，越高越可能是用户真实意图。
     */
    private Double score;

    /**
     * 命中原因，方便排查和前端展示解释。
     */
    private String reason;

    /**
     * 后续执行需要的扩展信息。
     */
    private Map<String, Object> payload = new LinkedHashMap<>();

    public static ClarificationCandidate of(String code, String name, String type, double score, String reason) {
        ClarificationCandidate candidate = new ClarificationCandidate();
        candidate.setCode(code);
        candidate.setName(name);
        candidate.setType(type);
        candidate.setScore(score);
        candidate.setReason(reason);
        return candidate;
    }
}
