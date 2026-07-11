package com.bio.drqi.ai.dto.tool;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * AI 工具执行请求。
 */
@Data
public class AiToolExecuteReqDTO implements Serializable {

    /**
     * 工具编码，对应 ai_tool_definition.tool_code。
     */
    private String toolCode;

    /**
     * 原始入参 JSON。Planner 当前生成的是字符串，Executor 先透传给 Adapter。
     */
    private String inputJson;

    /**
     * 结构化入参。后续 Planner 升级后可以直接传 Map。
     */
    private Map<String, Object> inputs;

    private String sessionId;

    private String userId;

    private String username;

    private String nickname;

    /**
     * 高风险或写操作工具是否已完成用户确认。
     */
    private Boolean confirmed;

    private static final long serialVersionUID = 1L;
}
