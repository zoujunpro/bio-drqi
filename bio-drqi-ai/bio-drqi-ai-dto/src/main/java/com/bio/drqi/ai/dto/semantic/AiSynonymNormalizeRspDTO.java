package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;

/**
 * 同义词归一结果。
 */
@Data
public class AiSynonymNormalizeRspDTO implements Serializable {

    private String originalText;

    private String normalizedText;

    private Boolean normalized;

    private String reason;

    private static final long serialVersionUID = 1L;
}
