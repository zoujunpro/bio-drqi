package com.bio.drqi.ai.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * AI 长会话摘要。
 */
@TableName(value = "ai_memory_summary")
@Data
public class AiMemorySummary implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String sessionId;

    private String userId;

    private String agentId;

    /**
     * 摘要内容。
     */
    private String summary;

    /**
     * 本摘要覆盖到的最后一条消息 ID。
     */
    private Long lastMessageId;

    /**
     * 摘要版本。
     */
    private Integer version;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
