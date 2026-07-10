package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;

/**
 * 条件抽取请求。
 */
@Data
public class AiConditionExtractReqDTO implements Serializable {

    private String query;

    private String intentCode;

    private static final long serialVersionUID = 1L;
}
