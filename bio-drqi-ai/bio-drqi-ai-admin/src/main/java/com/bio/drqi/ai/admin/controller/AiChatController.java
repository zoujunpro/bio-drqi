package com.bio.drqi.ai.admin.controller;

import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.ai.admin.service.AiChatFileService;
import com.bio.drqi.ai.dto.chat.AiChatFileUploadReqDTO;
import com.bio.drqi.ai.dto.chat.AiChatFileUploadRspDTO;
import com.bio.drqi.ai.dto.chat.AiChatReqDTO;
import com.bio.drqi.ai.dto.chat.AiChatRspDTO;
import com.bio.drqi.ai.orchestrator.AiChatOrchestratorService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * AI 聊天入口。
 */
@RestController
@RequestMapping("/ai/chat")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", exposedHeaders = "Content-Disposition", allowCredentials = "true")
public class AiChatController {

    @Resource
    private AiChatOrchestratorService aiChatOrchestratorService;

    @Resource
    private AiChatFileService aiChatFileService;

    /**
     * 聊天主入口。
     *
     * <p>Controller 只处理 HTTP 入参和统一响应，具体业务逻辑进入 Orchestrator。</p>
     */
    @PostMapping
    public ResponseResult<AiChatRspDTO> chat(@Validated @RequestBody AiChatReqDTO reqDTO) {
        fillCurrentUser(reqDTO);
        return ResponseResult.getSuccess(aiChatOrchestratorService.chat(reqDTO));
    }

    /**
     * 上传本次聊天使用的会话文件。
     */
    @PostMapping("/files/upload")
    public ResponseResult<AiChatFileUploadRspDTO> uploadFile(@Validated AiChatFileUploadReqDTO reqDTO) {
        fillCurrentUser(reqDTO);
        return ResponseResult.getSuccess(aiChatFileService.upload(reqDTO));
    }

    private void fillCurrentUser(AiChatReqDTO reqDTO) {
        String currentUserId = currentUserId();
        if (hasText(currentUserId)) {
            reqDTO.setUserId(currentUserId);
        }
        if (hasText(SecurityContextHolder.getUserName())) {
            reqDTO.setUsername(SecurityContextHolder.getUserName());
        }
        if (hasText(SecurityContextHolder.getNickName())) {
            reqDTO.setNickname(SecurityContextHolder.getNickName());
        }
        if (hasText(SecurityContextHolder.getJobNum())) {
            reqDTO.setJobNum(SecurityContextHolder.getJobNum());
        }
    }

    private void fillCurrentUser(AiChatFileUploadReqDTO reqDTO) {
        String currentUserId = currentUserId();
        if (hasText(currentUserId)) {
            reqDTO.setUserId(currentUserId);
        }
        if (hasText(SecurityContextHolder.getUserName())) {
            reqDTO.setUsername(SecurityContextHolder.getUserName());
        }
        if (hasText(SecurityContextHolder.getNickName())) {
            reqDTO.setNickname(SecurityContextHolder.getNickName());
        }
        if (hasText(SecurityContextHolder.getJobNum())) {
            reqDTO.setJobNum(SecurityContextHolder.getJobNum());
        }
    }

    private String currentUserId() {
        Integer userId = SecurityContextHolder.getUserId();
        return userId == null || userId <= 0 ? null : String.valueOf(userId);
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
