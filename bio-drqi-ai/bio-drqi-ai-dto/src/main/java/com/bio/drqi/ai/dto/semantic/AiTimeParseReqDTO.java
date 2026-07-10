package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;

/**
 * 时间解析请求。
 */
@Data
public class AiTimeParseReqDTO implements Serializable {

    /**
     * 待解析文本。
     */
    private String query;

    /**
     * 参考日期，格式 yyyy-MM-dd。
     */
    private String referenceDate;

    private static final long serialVersionUID = 1L;
}
