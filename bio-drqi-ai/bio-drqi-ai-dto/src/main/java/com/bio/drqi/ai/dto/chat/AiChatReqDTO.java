package com.bio.drqi.ai.dto.chat;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * AI 聊天请求。
 */
@Data
public class AiChatReqDTO {

    /**
     * 当前用户 ID。
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
     * 当前会话 ID。为空时后续可由服务层创建新会话。
     */
    private String sessionId;

    /**
     * 用户本轮输入内容。
     */
    @NotBlank(message = "消息内容不能为空")
    private String message;

    /**
     * 本轮聊天关联的会话文件 ID。
     */
    private List<String> fileIds;

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

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getFileIds() {
        return fileIds;
    }

    public void setFileIds(List<String> fileIds) {
        this.fileIds = fileIds;
    }
}
