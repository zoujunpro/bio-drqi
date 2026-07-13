package com.bio.drqi.ai.dto.rag;

import lombok.Data;

import java.io.Serializable;

/**
 * 文档检索命中片段。
 */
@Data
public class AiDocumentSearchItemDTO implements Serializable {

    /**
     * 文档 ID。
     */
    private String documentId;

    /**
     * 分块 ID。
     */
    private String chunkId;

    /**
     * 分块序号，从 1 开始。
     */
    private Integer chunkNo;

    /**
     * 文档标题。
     */
    private String title;

    /**
     * 命中的文本片段。
     */
    private String content;

    /**
     * 相似度得分，越大越相关。
     */
    private Double score;

    /**
     * 文档入库时传入的扩展元数据 JSON。
     */
    private String metadata;

    private static final long serialVersionUID = 1L;
}
