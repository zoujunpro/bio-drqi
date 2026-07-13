package com.bio.drqi.ai.dto.rag;

import lombok.Data;

import java.io.Serializable;

/**
 * 文档索引操作响应。
 */
@Data
public class AiDocumentIndexRspDTO implements Serializable {

    /**
     * 文档 ID。
     */
    private String documentId;

    /**
     * 当前索引状态：READY、DELETED、UNKNOWN。
     */
    private String status;

    /**
     * 当前有效分块数量。
     */
    private Integer chunkCount;

    /**
     * 文档正文 hash，用于判断内容是否变更。
     */
    private String contentHash;

    private static final long serialVersionUID = 1L;
}
