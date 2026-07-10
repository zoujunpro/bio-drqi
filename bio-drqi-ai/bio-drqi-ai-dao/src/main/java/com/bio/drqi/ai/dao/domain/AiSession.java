package com.bio.drqi.ai.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * AI 会话。
 */
@TableName(value = "ai_session")
@Data
public class AiSession implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务会话 ID，对外暴露。
     */
    private String sessionId;

    /**
     * 用户 ID。
     */
    private String userId;

    /**
     * 用户名。
     */
    private String username;

    /**
     * 用户昵称。
     */
    private String nickname;

    /**
     * 工号。
     */
    private String jobNum;

    /**
     * Agent 或应用 ID，预留多 Agent 场景。
     */
    private String agentId;

    /**
     * 会话标题。
     */
    private String title;

    /**
     * Dify 会话 ID。
     */
    private String difyConversationId;

    /**
     * 状态：ACTIVE/CLOSED。
     */
    private String status;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
