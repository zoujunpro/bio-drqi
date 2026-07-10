package com.bio.drqi.ai.memory;

import com.bio.drqi.ai.dto.memory.AiLongTermMemoryDTO;
import com.bio.drqi.ai.dto.memory.AiLongTermMemorySaveReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryContextReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryContextRspDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryFileBindReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryFileParseUpdateReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryFileSaveReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryMessageReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemorySessionCreateReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemorySessionCreateRspDTO;

import java.util.List;

/**
 * AI 记忆管理服务接口。
 *
 * <p>这里只定义 Memory 第一版的 5 个核心能力，不包含 Controller 和具体实现。</p>
 */
public interface AiMemoryService {

    /**
     * 获取当前 AI 请求需要携带的上下文。
     */
    AiMemoryContextRspDTO getContext(AiMemoryContextReqDTO reqDTO);

    /**
     * 保存用户消息或 AI 回复。
     */
    Long saveMessage(AiMemoryMessageReqDTO reqDTO);

    /**
     * 创建 AI 会话。
     */
    AiMemorySessionCreateRspDTO createSession(AiMemorySessionCreateReqDTO reqDTO);

    /**
     * 保存长期记忆。
     */
    void saveLongTermMemory(AiLongTermMemorySaveReqDTO reqDTO);

    /**
     * 查询指定用户的长期记忆。
     */
    List<AiLongTermMemoryDTO> listLongTermMemory(String userId);

    /**
     * 保存会话文件元数据。
     */
    String saveFile(AiMemoryFileSaveReqDTO reqDTO);

    /**
     * 更新会话文件解析结果。
     */
    void updateFileParseResult(AiMemoryFileParseUpdateReqDTO reqDTO);

    /**
     * 将已上传的会话文件绑定到本轮消息。
     */
    void bindFilesToMessage(AiMemoryFileBindReqDTO reqDTO);
}
