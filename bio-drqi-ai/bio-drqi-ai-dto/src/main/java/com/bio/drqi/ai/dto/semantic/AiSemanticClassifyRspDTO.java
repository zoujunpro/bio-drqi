package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 语义分类结果。
 */
@Data
public class AiSemanticClassifyRspDTO implements Serializable {

    /**
     * 语义粗分类。
     * 取值对应 AiSemanticCategoryEnum 的 code。
     */
    private String category;

    /**
     * 置信度。
     */
    private BigDecimal confidence;

    /**
     * 分类原因。
     */
    private String reason;

    private static final long serialVersionUID = 1L;
}
