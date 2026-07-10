package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;

/**
 * 数量解析请求。
 */
@Data
public class AiNumberParseReqDTO implements Serializable {

    /**
     * 用户问题。
     */
    private String query;

    private static final long serialVersionUID = 1L;
}
