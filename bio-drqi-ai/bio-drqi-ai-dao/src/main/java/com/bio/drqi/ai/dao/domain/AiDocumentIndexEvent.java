package com.bio.drqi.ai.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * AI 文档索引事件。
 */
@TableName(value = "ai_document_index_event")
@Data
public class AiDocumentIndexEvent implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String eventId;

    private String eventType;

    private String documentId;

    private String sourceSystem;

    private String status;

    private String errorMessage;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
