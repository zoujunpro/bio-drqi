package com.bio.drqi.ai.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.ai.dto.admin.AiPageReqDTO;
import com.bio.drqi.ai.entity.AiQueryAuditLog;
import com.bio.drqi.ai.service.AiAdminObserveService;
import com.bio.drqi.ai.service.AiRuntimeMetricsService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/ai/admin")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", allowCredentials = "true")
public class AiAdminObserveController {

    @Resource
    private AiAdminObserveService aiAdminObserveService;

    @Resource
    private AiRuntimeMetricsService aiRuntimeMetricsService;

    @PostMapping("/audit/page")
    @WebLog(desc = "AI查询审计分页")
    public ResponseResult<Page<AiQueryAuditLog>> auditPage(@RequestBody AiPageReqDTO reqDTO) {
        return ResponseResult.getSuccess(aiAdminObserveService.auditPage(reqDTO));
    }

    @GetMapping("/metrics")
    @WebLog(desc = "AI运行指标")
    public ResponseResult<Map<String, Object>> metrics() {
        return ResponseResult.getSuccess(aiRuntimeMetricsService.snapshot());
    }
}
