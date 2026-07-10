package com.bio.drqi.ai.memory.impl;

import com.bio.drqi.ai.common.enums.AiFileParseStatusEnum;
import com.bio.drqi.ai.common.enums.AiFileTypeEnum;
import com.bio.drqi.ai.common.enums.AiMemoryStatusEnum;
import com.bio.drqi.ai.common.enums.AiMessageSourceEnum;
import com.bio.drqi.ai.common.enums.AiSessionStatusEnum;
import com.bio.drqi.ai.dao.domain.AiMessage;
import com.bio.drqi.ai.dao.domain.AiMessageFile;
import com.bio.drqi.ai.dao.domain.AiSession;
import com.bio.drqi.ai.dao.domain.AiUserMemory;
import com.bio.drqi.ai.dao.mapper.AiMessageFileMapper;
import com.bio.drqi.ai.dao.mapper.AiMessageMapper;
import com.bio.drqi.ai.dao.mapper.AiSessionMapper;
import com.bio.drqi.ai.dao.mapper.AiUserMemoryMapper;
import com.bio.drqi.ai.dto.memory.AiLongTermMemoryDTO;
import com.bio.drqi.ai.dto.memory.AiLongTermMemorySaveReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryContextReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryContextRspDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryFileBindReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryFileDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryFileParseUpdateReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryFileSaveReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryMessageDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryMessageReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemorySessionCreateReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemorySessionCreateRspDTO;
import com.bio.drqi.ai.memory.AiMemoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * AI 记忆管理第一版实现。
 */
@Service
public class AiMemoryServiceImpl implements AiMemoryService {

    /**
     * 构建上下文时读取的最近消息数量。
     */
    private static final int RECENT_MESSAGE_LIMIT = 20;

    /**
     * 构建上下文时读取的最近文件数量。
     */
    private static final int RECENT_FILE_LIMIT = 10;

    @Resource
    private AiSessionMapper aiSessionMapper;

    @Resource
    private AiMessageMapper aiMessageMapper;

    @Resource
    private AiMessageFileMapper aiMessageFileMapper;

    @Resource
    private AiUserMemoryMapper aiUserMemoryMapper;

    @Override
    public AiMemoryContextRspDTO getContext(AiMemoryContextReqDTO reqDTO) {
        AiMemoryContextRspDTO rspDTO = new AiMemoryContextRspDTO();
        rspDTO.setSessionId(reqDTO.getSessionId());

        if (hasText(reqDTO.getSessionId())) {
            List<AiMemoryMessageDTO> shortMemory = aiMessageMapper
                    .selectRecentBySessionId(reqDTO.getSessionId(), RECENT_MESSAGE_LIMIT)
                    .stream()
                    .map(this::toMessageDTO)
                    .collect(Collectors.toList());
            rspDTO.setShortMemory(shortMemory);
        }

        if (hasText(reqDTO.getUserId())) {
            rspDTO.setLongMemory(listLongTermMemory(reqDTO.getUserId()));
        }

        rspDTO.setFiles(listContextFiles(reqDTO));

        return rspDTO;
    }

    @Override
    public Long saveMessage(AiMemoryMessageReqDTO reqDTO) {
        AiMessage message = new AiMessage();
        message.setSessionId(reqDTO.getSessionId());
        message.setUserId(reqDTO.getUserId());
        message.setRole(reqDTO.getRole());
        message.setContent(reqDTO.getContent());
        message.setSource(hasText(reqDTO.getSource()) ? reqDTO.getSource() : AiMessageSourceEnum.CONVERSATION.getCode());
        message.setCreateTime(new Date());
        aiMessageMapper.insert(message);
        return message.getId();
    }

    @Override
    public AiMemorySessionCreateRspDTO createSession(AiMemorySessionCreateReqDTO reqDTO) {
        AiSession session = new AiSession();
        session.setSessionId(generateSessionId());
        session.setUserId(reqDTO.getUserId());
        session.setUsername(reqDTO.getUsername());
        session.setNickname(reqDTO.getNickname());
        session.setJobNum(reqDTO.getJobNum());
        session.setTitle(reqDTO.getTitle());
        session.setStatus(AiSessionStatusEnum.ACTIVE.getCode());
        session.setCreateTime(new Date());
        session.setUpdateTime(new Date());
        aiSessionMapper.insert(session);

        AiMemorySessionCreateRspDTO rspDTO = new AiMemorySessionCreateRspDTO();
        rspDTO.setSessionId(session.getSessionId());
        return rspDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveLongTermMemory(AiLongTermMemorySaveReqDTO reqDTO) {
        AiUserMemory memory = aiUserMemoryMapper.selectActiveByUserIdAndKey(reqDTO.getUserId(), reqDTO.getMemoryKey());
        Date now = new Date();
        if (memory == null) {
            memory = new AiUserMemory();
            memory.setUserId(reqDTO.getUserId());
            memory.setMemoryKey(reqDTO.getMemoryKey());
            memory.setCreateTime(now);
            memory.setStatus(AiMemoryStatusEnum.ACTIVE.getCode());
            memory.setImportance(0);
        }

        memory.setMemoryType(reqDTO.getMemoryType());
        memory.setMemoryValue(reqDTO.getMemoryValue());
        memory.setSource(reqDTO.getSource());
        memory.setConfidence(reqDTO.getConfidence());
        memory.setUpdateTime(now);

        if (memory.getId() == null) {
            aiUserMemoryMapper.insert(memory);
        } else {
            aiUserMemoryMapper.updateById(memory);
        }
    }

    @Override
    public List<AiLongTermMemoryDTO> listLongTermMemory(String userId) {
        return aiUserMemoryMapper.selectActiveByUserId(userId)
                .stream()
                .map(this::toLongTermMemoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public String saveFile(AiMemoryFileSaveReqDTO reqDTO) {
        AiMessageFile messageFile = new AiMessageFile();
        messageFile.setSessionId(reqDTO.getSessionId());
        messageFile.setMessageId(reqDTO.getMessageId());
        messageFile.setUserId(reqDTO.getUserId());
        messageFile.setFileId(hasText(reqDTO.getFileId()) ? reqDTO.getFileId() : generateFileId());
        messageFile.setFileName(reqDTO.getFileName());
        messageFile.setFileType(AiFileTypeEnum.resolve(reqDTO.getFileType(), reqDTO.getFileName()).getCode());
        messageFile.setMimeType(reqDTO.getMimeType());
        messageFile.setFileSize(reqDTO.getFileSize());
        messageFile.setBucketName(reqDTO.getBucketName());
        messageFile.setObjectKey(reqDTO.getObjectKey());
        messageFile.setFileUrl(reqDTO.getFileUrl());
        messageFile.setParseStatus(hasText(reqDTO.getParseStatus())
                ? reqDTO.getParseStatus()
                : AiFileParseStatusEnum.WAITING.getCode());
        messageFile.setCreateTime(new Date());
        messageFile.setUpdateTime(new Date());
        aiMessageFileMapper.insert(messageFile);
        return messageFile.getFileId();
    }

    @Override
    public void updateFileParseResult(AiMemoryFileParseUpdateReqDTO reqDTO) {
        AiMessageFile messageFile = aiMessageFileMapper.selectByFileId(reqDTO.getFileId());
        if (messageFile == null) {
            return;
        }
        messageFile.setParseStatus(reqDTO.getParseStatus());
        messageFile.setParsedText(reqDTO.getParsedText());
        messageFile.setSummary(reqDTO.getSummary());
        messageFile.setErrorMessage(reqDTO.getErrorMessage());
        messageFile.setUpdateTime(new Date());
        aiMessageFileMapper.updateById(messageFile);
    }

    @Override
    public void bindFilesToMessage(AiMemoryFileBindReqDTO reqDTO) {
        if (!hasItems(reqDTO.getFileIds())) {
            return;
        }
        aiMessageFileMapper.updateMessageIdByFileIds(
                reqDTO.getSessionId(),
                reqDTO.getUserId(),
                reqDTO.getMessageId(),
                reqDTO.getFileIds()
        );
    }

    private List<AiMemoryFileDTO> listContextFiles(AiMemoryContextReqDTO reqDTO) {
        if (hasItems(reqDTO.getFileIds()) && hasText(reqDTO.getSessionId()) && hasText(reqDTO.getUserId())) {
            return aiMessageFileMapper.selectByFileIds(reqDTO.getSessionId(), reqDTO.getUserId(), reqDTO.getFileIds())
                    .stream()
                    .map(this::toFileDTO)
                    .collect(Collectors.toList());
        }

        if (hasText(reqDTO.getSessionId())) {
            return aiMessageFileMapper.selectRecentBySessionId(reqDTO.getSessionId(), RECENT_FILE_LIMIT)
                    .stream()
                    .map(this::toFileDTO)
                    .collect(Collectors.toList());
        }

        return java.util.Collections.emptyList();
    }

    private AiMemoryMessageDTO toMessageDTO(AiMessage message) {
        AiMemoryMessageDTO dto = new AiMemoryMessageDTO();
        dto.setRole(message.getRole());
        dto.setContent(message.getContent());
        return dto;
    }

    private AiLongTermMemoryDTO toLongTermMemoryDTO(AiUserMemory memory) {
        AiLongTermMemoryDTO dto = new AiLongTermMemoryDTO();
        dto.setKey(memory.getMemoryKey());
        dto.setValue(memory.getMemoryValue());
        return dto;
    }

    private AiMemoryFileDTO toFileDTO(AiMessageFile messageFile) {
        AiMemoryFileDTO dto = new AiMemoryFileDTO();
        dto.setFileId(messageFile.getFileId());
        dto.setFileName(messageFile.getFileName());
        dto.setFileType(messageFile.getFileType());
        dto.setMimeType(messageFile.getMimeType());
        dto.setFileSize(messageFile.getFileSize());
        dto.setBucketName(messageFile.getBucketName());
        dto.setObjectKey(messageFile.getObjectKey());
        dto.setFileUrl(messageFile.getFileUrl());
        dto.setParseStatus(messageFile.getParseStatus());
        dto.setParsedText(messageFile.getParsedText());
        dto.setSummary(messageFile.getSummary());
        return dto;
    }

    private String generateSessionId() {
        return "ai_" + UUID.randomUUID().toString().replace("-", "");
    }

    private String generateFileId() {
        return "file_" + UUID.randomUUID().toString().replace("-", "");
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }

    private boolean hasItems(List<?> values) {
        return values != null && !values.isEmpty();
    }
}
