package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;

/**
 * 同义词归一请求。
 */
@Data
public class AiSynonymNormalizeReqDTO implements Serializable {

    private String query;

    private static final long serialVersionUID = 1L;
}
