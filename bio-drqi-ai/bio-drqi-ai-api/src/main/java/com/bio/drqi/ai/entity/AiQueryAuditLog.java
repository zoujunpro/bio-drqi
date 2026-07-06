package com.bio.drqi.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("ai_query_audit_log")
public class AiQueryAuditLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String traceId;

    private String conversationId;

    private String userId;

    private String scenario;

    private String question;

    private String intent;

    private String domain;

    private String queryPlan;

    private String sqlText;

    private Integer rowCount;

    private Long costMs;

    private Integer success;

    private String errorMessage;

    private Date createTime;
}
