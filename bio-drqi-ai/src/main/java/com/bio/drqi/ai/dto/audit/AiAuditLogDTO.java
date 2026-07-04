package com.bio.drqi.ai.dto.audit;

import lombok.Data;

@Data
public class AiAuditLogDTO {

    private String traceId;

    private String conversationId;

    private String userId;

    private String scenario;

    private String question;

    private String intent;

    private String domain;

    private String queryPlan;

    private String sqlText;

    private String sqlParams;

    private Integer rowCount;

    private Long costMillis;

    private Boolean success = true;

    private String errorMessage;
}
