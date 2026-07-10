package com.bio.drqi.ai.dto.chat;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

/**
 * AI 聊天文件上传请求。
 */
@Data
public class AiChatFileUploadReqDTO {

    /**
     * 当前会话 ID。首次上传文件时可为空，由后端自动创建。
     */
    private String sessionId;

    /**
     * 当前用户 ID。优先从请求头上下文补充。
     */
    private String userId;

    /**
     * 用户名，从请求头上下文补充。
     */
    private String username;

    /**
     * 用户昵称，从请求头上下文补充。
     */
    private String nickname;

    /**
     * 工号，从请求头上下文补充。
     */
    private String jobNum;

    /**
     * 上传文件。
     */
    @NotNull(message = "上传文件不能为空")
    private MultipartFile file;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getJobNum() {
        return jobNum;
    }

    public void setJobNum(String jobNum) {
        this.jobNum = jobNum;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
