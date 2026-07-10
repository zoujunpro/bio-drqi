package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;

/**
 * 业务术语映射请求。
 */
@Data
public class AiTermMappingReqDTO implements Serializable {

    private String query;

    private String domain;

    private static final long serialVersionUID = 1L;
}
