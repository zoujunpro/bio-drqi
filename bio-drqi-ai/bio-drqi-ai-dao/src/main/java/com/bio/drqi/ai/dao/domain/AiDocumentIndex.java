package com.bio.drqi.ai.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * AI 文档索引元数据。
 */
@TableName(value = "ai_document_index")
@Data
public class AiDocumentIndex implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String documentId;

    private String sourceSystem;

    private String bizType;

    private String bizId;

    private String title;

    private String fileName;

    private String fileType;

    private String versionNo;

    private String contentHash;

    private String embeddingModel;

    private Integer chunkCount;

    private String status;

    private Date indexTime;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
