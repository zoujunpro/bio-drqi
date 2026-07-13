package com.bio.drqi.ai.dto.rag;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 文档语义检索响应。
 */
@Data
public class AiDocumentSearchRspDTO implements Serializable {

    /**
     * 检索命中的文档片段列表。
     */
    private List<AiDocumentSearchItemDTO> items = new ArrayList<AiDocumentSearchItemDTO>();

    private static final long serialVersionUID = 1L;
}
