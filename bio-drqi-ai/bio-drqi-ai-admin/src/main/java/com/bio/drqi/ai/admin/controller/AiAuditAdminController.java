package com.bio.drqi.ai.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.ai.admin.service.AiAuditAdminService;
import com.bio.drqi.ai.dao.domain.AiQueryAuditLog;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * AI 查询审计后台接口。
 */
@RestController
@RequestMapping("/ai/admin/audit")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", exposedHeaders = "Content-Disposition", allowCredentials = "true")
public class AiAuditAdminController {

    @Resource
    private AiAuditAdminService aiAuditAdminService;

    @PostMapping("/page")
    @WebLog(desc = "AI查询审计列表")
    public ResponseResult<IPage<AiQueryAuditLog>> page(@RequestBody Map<String, Object> params) {
        return ResponseResult.getSuccess(aiAuditAdminService.page(params));
    }
}
