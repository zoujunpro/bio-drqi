package com.bio.drqi.ai.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * AI 会话消息。
 */
@TableName(value = "ai_message")
@Data
public class AiMessage implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String sessionId;

    private String userId;

    /**
     * user/assistant/tool/system。
     */
    private String role;

    /**
     * 消息内容。
     */
    private String content;

    /**
     * 消息来源，例如 gateway/dify/tool/memory。
     */
    private String source;

    /**
     * Dify 返回的消息 ID，便于追踪。
     */
    private String difyMessageId;

    /**
     * 扩展 JSON。
     */
    private String metadata;

    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
