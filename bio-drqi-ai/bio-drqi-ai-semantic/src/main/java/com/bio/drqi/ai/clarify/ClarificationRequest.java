package com.bio.drqi.ai.clarify;

import com.bio.drqi.ai.dto.memory.AiConversationContextDTO;
import lombok.Data;

/**
 * 澄清引擎入参。
 */
@Data
public class ClarificationRequest {

    private String conversationId;

    private String question;

    private String intent;

    private AiConversationContextDTO context;

    public static ClarificationRequest of(String conversationId, String question, String intent,
                                          AiConversationContextDTO context) {
        ClarificationRequest request = new ClarificationRequest();
        request.setConversationId(conversationId);
        request.setQuestion(question);
        request.setIntent(intent);
        request.setContext(context);
        return request;
    }
}
