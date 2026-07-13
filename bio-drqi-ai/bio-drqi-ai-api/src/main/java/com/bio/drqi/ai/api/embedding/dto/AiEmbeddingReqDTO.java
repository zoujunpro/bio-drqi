package com.bio.drqi.ai.api.embedding.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 文本向量化请求。
 */
@Data
public class AiEmbeddingReqDTO implements Serializable {

    /**
     * 模型名称；为空时使用配置默认模型。
     */
    private String model;

    /**
     * 待向量化文本。
     */
    private String input;

    /**
     * 请求超时时间，单位毫秒；为空时使用配置默认值。
     */
    private Integer timeoutMs;

    private static final long serialVersionUID = 1L;
}
