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
 * AI 用户长期记忆。
 */
@TableName(value = "ai_user_memory")
@Data
public class AiUserMemory implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String userId;

    /**
     * Agent 或应用 ID，空表示通用记忆。
     */
    private String agentId;

    /**
     * 记忆类型，例如 PREFERENCE/PROFILE/BUSINESS_CONTEXT。
     */
    private String memoryType;

    private String memoryKey;

    private String memoryValue;

    /**
     * 来源，例如 conversation/manual/import。
     */
    private String source;

    private BigDecimal confidence;

    private Integer importance;

    /**
     * 状态：ACTIVE/DELETED/EXPIRED。
     */
    private String status;

    private Date expireTime;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
