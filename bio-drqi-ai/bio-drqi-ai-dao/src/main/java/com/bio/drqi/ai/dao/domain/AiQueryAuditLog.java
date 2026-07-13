package com.bio.drqi.ai.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * AI 查询审计日志。
 */
@TableName(value = "ai_query_audit_log")
@Data
public class AiQueryAuditLog implements Serializable {

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
