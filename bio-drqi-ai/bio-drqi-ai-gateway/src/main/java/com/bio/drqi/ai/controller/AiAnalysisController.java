package com.bio.drqi.ai.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.ai.dto.req.AiAnalysisReqDTO;
import com.bio.drqi.ai.dto.rsp.AiAnalysisRspDTO;
import com.bio.drqi.ai.exception.AiErrorCode;
import com.bio.drqi.ai.service.AiAnalysisService;
import com.bio.drqi.ai.service.AiRateLimitService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/ai")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", exposedHeaders = "Content-Disposition", allowCredentials = "true")
public class AiAnalysisController {

    @Resource
    private AiAnalysisService aiAnalysisService;

    @Resource
    private AiRateLimitService aiRateLimitService;

    @PostMapping("/analysis")
    @WebLog(desc = "AI智能分析")
    public ResponseResult<AiAnalysisRspDTO> analysis(@Validated @RequestBody AiAnalysisReqDTO reqDTO, HttpServletRequest request) {
        // 入口只负责参数接收和统一返回，具体 AI 计划生成、校验、后续查询执行都放到 service。
        boolean acquired = false;
        try {
            aiRateLimitService.acquire(clientKey(request));
            acquired = true;
        } catch (BusinessException e) {
            return ResponseResult.getSuccess(errorRsp(e.getMessage(), AiErrorCode.AI_RATE_LIMITED));
        }
        try {
            return ResponseResult.getSuccess(aiAnalysisService.analysis(reqDTO));
        } finally {
            if (acquired) {
                aiRateLimitService.release();
            }
        }
    }

    private String clientKey(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && forwardedFor.length() > 0) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private AiAnalysisRspDTO errorRsp(String message, AiErrorCode errorCode) {
        AiAnalysisRspDTO rspDTO = new AiAnalysisRspDTO();
        rspDTO.setSuccess(Boolean.FALSE);
        rspDTO.setErrorCode(errorCode.getCode());
        rspDTO.setAnswer(message);
        return rspDTO;
    }
}
