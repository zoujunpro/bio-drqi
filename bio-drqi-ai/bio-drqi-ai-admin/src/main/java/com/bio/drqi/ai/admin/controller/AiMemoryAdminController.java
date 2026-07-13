package com.bio.drqi.ai.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.ai.admin.service.AiMemoryAdminService;
import com.bio.drqi.ai.dao.domain.AiMemorySummary;
import com.bio.drqi.ai.dao.domain.AiMessage;
import com.bio.drqi.ai.dao.domain.AiMessageFile;
import com.bio.drqi.ai.dao.domain.AiSession;
import com.bio.drqi.ai.dao.domain.AiUserMemory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * AI 记忆运行数据查看接口。
 */
@RestController
@RequestMapping("/ai/admin/memory")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", exposedHeaders = "Content-Disposition", allowCredentials = "true")
public class AiMemoryAdminController {

    @Resource
    private AiMemoryAdminService aiMemoryAdminService;

    @PostMapping("/sessions/page")
    @WebLog(desc = "AI记忆-会话列表")
    public ResponseResult<IPage<AiSession>> pageSessions(@RequestBody Map<String, Object> params) {
        return ResponseResult.getSuccess(aiMemoryAdminService.pageSessions(params));
    }

    @PostMapping("/messages/page")
    @WebLog(desc = "AI记忆-消息列表")
    public ResponseResult<IPage<AiMessage>> pageMessages(@RequestBody Map<String, Object> params) {
        return ResponseResult.getSuccess(aiMemoryAdminService.pageMessages(params));
    }

    @PostMapping("/summaries/page")
    @WebLog(desc = "AI记忆-摘要列表")
    public ResponseResult<IPage<AiMemorySummary>> pageSummaries(@RequestBody Map<String, Object> params) {
        return ResponseResult.getSuccess(aiMemoryAdminService.pageSummaries(params));
    }

    @PostMapping("/userMemories/page")
    @WebLog(desc = "AI记忆-用户记忆列表")
    public ResponseResult<IPage<AiUserMemory>> pageUserMemories(@RequestBody Map<String, Object> params) {
        return ResponseResult.getSuccess(aiMemoryAdminService.pageUserMemories(params));
    }

    @PostMapping("/files/page")
    @WebLog(desc = "AI记忆-文件列表")
    public ResponseResult<IPage<AiMessageFile>> pageFiles(@RequestBody Map<String, Object> params) {
        return ResponseResult.getSuccess(aiMemoryAdminService.pageFiles(params));
    }
}
