package com.bio.drqi.ai.dto.chat;

import com.bio.drqi.ai.dto.planner.AiPlanRspDTO;
import com.bio.drqi.ai.dto.tool.AiToolExecuteRspDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * AI 聊天响应。
 */
@Data
public class AiChatRspDTO {

    /**
     * 当前会话 ID。
     */
    private String sessionId;

    /**
     * AI 回复内容。
     */
    private String answer;

    /**
     * 回复类型：TEXT/TABLE/FILE/MIXED/ERROR/CLARIFY。
     */
    private String answerType;

    /**
     * 文件类结果，例如 Excel、Word、PDF。
     */
    private List<AiChatAttachmentDTO> attachments = new ArrayList<AiChatAttachmentDTO>();

    /**
     * 表格类结果。
     */
    private AiChatTableDTO table;

    /**
     * 原始结构化数据，前端可按需扩展渲染。
     */
    private Object data;

    /**
     * Planner 生成的执行计划，用于前端展示执行过程和排查问题。
     */
    private AiPlanRspDTO plan;

    /**
     * 工具执行结果，用于前端展示每个步骤的执行状态和错误信息。
     */
    private List<AiToolExecuteRspDTO> executeResults = new ArrayList<AiToolExecuteRspDTO>();

    /**
     * 本次回复是否成功生成。
     */
    private Boolean success;

    /**
     * 错误码。成功时为空。
     */
    private String errorCode;

    /**
     * 错误消息。成功时为空。
     */
    private String errorMessage;
}
