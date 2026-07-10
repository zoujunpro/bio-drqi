package com.bio.drqi.ai.dto.semantic;

import com.bio.drqi.ai.dto.memory.AiLongTermMemoryDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryFileDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryMessageDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 用户问题改写请求。
 */
@Data
public class AiQueryRewriteReqDTO implements Serializable {

    /**
     * 用户 ID。
     */
    private String userId;

    /**
     * 会话 ID。
     */
    private String sessionId;

    /**
     * 用户原始问题。
     */
    private String originalQuery;

    /**
     * 短期对话记忆。
     */
    private List<AiMemoryMessageDTO> shortMemory;

    /**
     * 长期用户记忆。
     */
    private List<AiLongTermMemoryDTO> longMemory;

    /**
     * 文件上下文。
     */
    private List<AiMemoryFileDTO> files;

    private static final long serialVersionUID = 1L;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getOriginalQuery() {
        return originalQuery;
    }

    public void setOriginalQuery(String originalQuery) {
        this.originalQuery = originalQuery;
    }

    public List<AiMemoryMessageDTO> getShortMemory() {
        return shortMemory;
    }

    public void setShortMemory(List<AiMemoryMessageDTO> shortMemory) {
        this.shortMemory = shortMemory;
    }

    public List<AiLongTermMemoryDTO> getLongMemory() {
        return longMemory;
    }

    public void setLongMemory(List<AiLongTermMemoryDTO> longMemory) {
        this.longMemory = longMemory;
    }

    public List<AiMemoryFileDTO> getFiles() {
        return files;
    }

    public void setFiles(List<AiMemoryFileDTO> files) {
        this.files = files;
    }
}
