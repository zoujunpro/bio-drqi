package com.bio.drqi.ai.dto.rag;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 文档索引删除入参。
 * 由文档系统在文档删除、禁用或撤回时调用；AI 服务会软删除 MySQL 元数据和 PG 向量片段。
 */
@Data
public class AiDocumentIndexDeleteReqDTO implements Serializable {

    /**
     * 事件 ID，用于追踪删除来源。
     */
    private String eventId;

    /**
     * 文档 ID，必须和写入索引时的 documentId 一致。
     */
    @NotBlank(message = "documentId 不能为空")
    private String documentId;

    /**
     * 来源系统编码。
     */
    private String sourceSystem;

    private static final long serialVersionUID = 1L;
}
