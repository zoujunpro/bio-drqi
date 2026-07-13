package com.bio.drqi.ai.dto.rag;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 文档权限刷新入参。
 * 只更新文档权限快照，不重新分块、不重新生成向量。
 */
@Data
public class AiDocumentPermissionRefreshReqDTO implements Serializable {

    /**
     * 事件 ID，用于追踪权限变更来源。
     */
    private String eventId;

    /**
     * 文档 ID，必须已在 AI 索引中存在。
     */
    @NotBlank(message = "documentId 不能为空")
    private String documentId;

    /**
     * 来源系统编码。
     */
    private String sourceSystem;

    /**
     * 最新的可读权限快照；会全量替换该文档旧权限。
     */
    @Valid
    private List<AiDocumentPermissionDTO> permissions = new ArrayList<AiDocumentPermissionDTO>();

    private static final long serialVersionUID = 1L;
}
