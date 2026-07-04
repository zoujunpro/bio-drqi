package com.bio.drqi.ai.dto.command;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 模型选择出的后端命令调用计划。
 */
@Data
public class AiCommandPlanDTO {

    private String command;

    private Map<String, Object> params = new LinkedHashMap<>();

    private Boolean needClarify = Boolean.FALSE;

    private String clarifyQuestion;
}
