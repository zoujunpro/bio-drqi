package com.bio.drqi.ai.dto.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AiReportReqDTO {

    /**
     * 用户自然语言报表需求，例如“导出最近一个月质粒质检汇总和gRNA明细”。
     */
    @NotBlank(message = "报表需求不能为空")
    private String question;

    /**
     * 前端可指定导出文件名，未传时使用 AI 报表计划里的 reportName。
     */
    private String fileName;
}
