package com.bio.drqi.ai.dto.rag;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文档索引写入入参。
 * 由文档系统在文档新增、更新或重新解析后调用。
 * 调用方可以直接传 contentText，也可以传 fileObject 让 AI 服务下载并解析文档。
 */
@Data
public class AiDocumentIndexUpsertReqDTO implements Serializable {

    /**
     * 事件 ID，用于幂等追踪和排查问题；建议文档系统每次推送生成一个唯一值。
     */
    private String eventId;

    /**
     * 文档 ID，文档系统内稳定唯一编码。
     */
    @NotBlank(message = "documentId 不能为空")
    private String documentId;

    /**
     * 来源系统编码，例如 DOC、CER_DOC。
     */
    private String sourceSystem;

    /**
     * 业务类型，用于检索时缩小范围，例如 PROJECT、CER、SOP。
     */
    private String bizType;

    /**
     * 业务 ID，用于绑定项目、实施方案、流程等业务对象。
     */
    private String bizId;

    /**
     * 文档标题，用于检索结果展示。
     */
    private String title;

    /**
     * 原始文件名。AI 服务解析 fileObject 时会用它辅助判断文件扩展名。
     */
    private String fileName;

    /**
     * 文档版本号；同一 documentId 更新版本时会覆盖旧向量。
     */
    private String versionNo;

    /**
     * 文件类型，例如 docx、pdf、txt。
     */
    private String fileType;

    /**
     * 文档系统中的 OSS 对象地址。
     * 当 contentText 为空时，AI 服务会通过该地址下载文件并解析正文。
     */
    private String fileObject;

    /**
     * 文档正文。
     * 如果文档系统已经完成解析，可以直接传该字段；为空时 AI 服务会尝试解析 fileObject。
     */
    private String contentText;

    /**
     * 额外业务元数据，写入 PG jsonb，用于后续展示或调试，不参与权限判断。
     */
    private Map<String, Object> metadata;

    /**
     * 文档可读权限快照；为空表示没有任何用户能通过 RAG 检索到该文档。
     */
    @Valid
    private List<AiDocumentPermissionDTO> permissions = new ArrayList<AiDocumentPermissionDTO>();

    private static final long serialVersionUID = 1L;

    @AssertTrue(message = "contentText 和 fileObject 不能同时为空")
    public boolean isContentSourcePresent() {
        return hasText(contentText) || hasText(fileObject);
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
