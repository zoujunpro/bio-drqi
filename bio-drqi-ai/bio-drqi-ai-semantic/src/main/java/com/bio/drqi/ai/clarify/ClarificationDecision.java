package com.bio.drqi.ai.clarify;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 澄清引擎输出的最终决策。
 */
@Data
public class ClarificationDecision {

    private Boolean needClarify = Boolean.FALSE;

    private ClarificationState state = ClarificationState.RESOLVED;

    private String clarifyType;

    private String question;

    private String message;

    private List<ClarificationCandidate> candidates = new ArrayList<>();

    public static ClarificationDecision resolved() {
        return new ClarificationDecision();
    }

    public static ClarificationDecision rejected(String type, String message, String question) {
        ClarificationDecision decision = new ClarificationDecision();
        decision.setNeedClarify(Boolean.TRUE);
        decision.setState(ClarificationState.REJECTED);
        decision.setClarifyType(type);
        decision.setMessage(message);
        decision.setQuestion(question);
        return decision;
    }

    public static ClarificationDecision clarify(ClarificationState state, String type, String message,
                                                String question, List<ClarificationCandidate> candidates) {
        ClarificationDecision decision = new ClarificationDecision();
        decision.setNeedClarify(Boolean.TRUE);
        decision.setState(state);
        decision.setClarifyType(type);
        decision.setMessage(message);
        decision.setQuestion(question);
        if (candidates != null) {
            decision.setCandidates(candidates);
        }
        return decision;
    }
}
