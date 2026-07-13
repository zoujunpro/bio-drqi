package com.bio.drqi.ai.api.embedding.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 文本向量化响应。
 */
@Data
public class AiEmbeddingRspDTO implements Serializable {

    /**
     * 向量模型。
     */
    private String model;

    /**
     * 向量维度。
     */
    private Integer dim;

    /**
     * 向量数据。
     */
    private List<Double> embedding;

    /**
     * 原始响应。
     */
    private String rawResponse;

    private static final long serialVersionUID = 1L;
}
