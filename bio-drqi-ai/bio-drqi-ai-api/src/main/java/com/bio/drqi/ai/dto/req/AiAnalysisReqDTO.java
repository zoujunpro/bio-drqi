package com.bio.drqi.ai.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AiAnalysisReqDTO {

    /**
     * 会话ID。为空时后端创建新会话；同一会话内支持“继续查、改分组、导出刚才结果”等上下文。
     */
    private String conversationId;

    /**
     * 用户自然语言问题，例如“统计每个月质粒质检合格率”。
     */
    @NotBlank(message = "问题不能为空")
    private String question;

    /**
     * 前端可传 table/bar/line/pie/auto，未传时由模型判断。
     */
    private String chartType;
}
