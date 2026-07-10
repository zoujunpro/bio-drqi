package com.bio.drqi.ai.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * AI 语义规则模式。
 */
@TableName(value = "ai_semantic_pattern")
@Data
public class AiSemanticPattern implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模式类型：REFERENCE_WORD/SYSTEM_CLASSIFY/INTENT_KEYWORD/ENTITY_REGEX/TIME_RULE。
     */
    private String patternType;

    /**
     * 模式编码。
     */
    private String patternCode;

    /**
     * 模式内容，普通词、关键词或正则表达式。
     */
    private String patternText;

    /**
     * 目标值，例如语义类别、意图编码、实体类型。
     */
    private String targetValue;

    /**
     * 权重。
     */
    private BigDecimal weight;

    /**
     * 业务领域。
     */
    private String domain;

    /**
     * 状态：ACTIVE/DISABLED/DELETED。
     */
    private String status;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
