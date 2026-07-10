package com.bio.drqi.ai.dto.memory;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 获取记忆上下文响应。
 */
@Data
public class AiMemoryContextRspDTO {

    private String sessionId;

    private List<AiMemoryMessageDTO> shortMemory = new ArrayList<>();

    private List<AiLongTermMemoryDTO> longMemory = new ArrayList<>();

    private List<AiMemoryFileDTO> files = new ArrayList<>();
}
