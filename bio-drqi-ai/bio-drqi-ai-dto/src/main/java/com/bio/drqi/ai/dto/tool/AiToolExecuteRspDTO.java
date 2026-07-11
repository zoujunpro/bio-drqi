package com.bio.drqi.ai.dto.tool;

import lombok.Data;

import java.io.Serializable;

/**
 * AI 工具执行响应。
 */
@Data
public class AiToolExecuteRspDTO implements Serializable {

    private Boolean success;

    private String toolCode;

    private String toolType;

    private String targetCode;

    private Integer statusCode;

    private String resultJson;

    private String errorMessage;

    private Long costMs;

    public static AiToolExecuteRspDTO success(String toolCode, String toolType, String targetCode,
                                              Integer statusCode, String resultJson, Long costMs) {
        AiToolExecuteRspDTO rspDTO = new AiToolExecuteRspDTO();
        rspDTO.setSuccess(Boolean.TRUE);
        rspDTO.setToolCode(toolCode);
        rspDTO.setToolType(toolType);
        rspDTO.setTargetCode(targetCode);
        rspDTO.setStatusCode(statusCode);
        rspDTO.setResultJson(resultJson);
        rspDTO.setCostMs(costMs);
        return rspDTO;
    }

    public static AiToolExecuteRspDTO fail(String toolCode, String toolType, String targetCode,
                                           String errorMessage, Long costMs) {
        AiToolExecuteRspDTO rspDTO = new AiToolExecuteRspDTO();
        rspDTO.setSuccess(Boolean.FALSE);
        rspDTO.setToolCode(toolCode);
        rspDTO.setToolType(toolType);
        rspDTO.setTargetCode(targetCode);
        rspDTO.setErrorMessage(errorMessage);
        rspDTO.setCostMs(costMs);
        return rspDTO;
    }

    private static final long serialVersionUID = 1L;
}
