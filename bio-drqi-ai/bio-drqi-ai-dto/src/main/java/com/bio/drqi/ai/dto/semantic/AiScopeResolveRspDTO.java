package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;

/**
 * 范围解析结果。
 */
@Data
public class AiScopeResolveRspDTO implements Serializable {

    /**
     * 范围类型：USER/DEPARTMENT/BASE/ALL/UNKNOWN。
     */
    private String scopeType;

    /**
     * 范围值，例如用户 ID、基地名称。
     */
    private String scopeValue;

    /**
     * 解析原因。
     */
    private String reason;

    private static final long serialVersionUID = 1L;
}
