package com.bio.drqi.ai.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.ai.dto.req.AiAnalysisReqDTO;
import com.bio.drqi.ai.dto.req.AiReportReqDTO;
import com.bio.drqi.ai.dto.rsp.AiAnalysisRspDTO;
import com.bio.drqi.ai.service.AiAnalysisService;
import com.bio.drqi.ai.service.AiReportService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/ai")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", exposedHeaders = "Content-Disposition", allowCredentials = "true")
public class AiAnalysisController {

    @Resource
    private AiAnalysisService aiAnalysisService;

    @Resource
    private AiReportService aiReportService;

    @PostMapping("/analysis")
    @WebLog(desc = "AI智能分析")
    public ResponseResult<AiAnalysisRspDTO> analysis(@Validated @RequestBody AiAnalysisReqDTO reqDTO) {
        // 入口只负责参数接收和统一返回，具体 AI 计划生成、校验、后续查询执行都放到 service。
        return ResponseResult.getSuccess(aiAnalysisService.analysis(reqDTO));
    }

    @PostMapping("/report/export")
    @WebLog(desc = "AI智能报表导出")
    public void exportReport(@Validated @RequestBody AiReportReqDTO reqDTO, HttpServletResponse response) {
        aiReportService.export(reqDTO, response);
    }
}
