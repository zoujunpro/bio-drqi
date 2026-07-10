package com.bio.drqi.ai.admin.service;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.ai.common.enums.AiFileParseStatusEnum;
import com.bio.drqi.ai.common.enums.AiFileTypeEnum;
import com.bio.drqi.ai.common.model.AiFileParseResult;
import com.bio.drqi.ai.common.util.AiFileParseUtil;
import com.bio.drqi.ai.dto.chat.AiChatFileUploadReqDTO;
import com.bio.drqi.ai.dto.chat.AiChatFileUploadRspDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryFileParseUpdateReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryFileSaveReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemorySessionCreateReqDTO;
import com.bio.drqi.ai.memory.AiMemoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * AI 聊天文件服务。
 */
@Service
public class AiChatFileService {

    @Resource
    private OssService ossService;

    @Resource
    private AiMemoryService aiMemoryService;

    @Transactional(rollbackFor = Exception.class)
    public AiChatFileUploadRspDTO upload(AiChatFileUploadReqDTO reqDTO) {
        MultipartFile file = reqDTO.getFile();
        if (file == null || file.isEmpty()) {
            throw new BusinessException("上传文件不能为空");
        }
        if (!hasText(reqDTO.getUserId())) {
            throw new BusinessException("当前用户信息不能为空");
        }

        String sessionId = ensureSession(reqDTO);
        String fileId = generateFileId();
        String originalFilename = hasText(file.getOriginalFilename()) ? file.getOriginalFilename() : "未命名文件";
        String fileType = AiFileTypeEnum.resolve(null, originalFilename).getCode();
        String dir = "ai/chat/" + new SimpleDateFormat("yyyyMMdd").format(new Date());
        String storedFileName = fileId + extension(originalFilename);

        ossService.upload(file, dir, storedFileName);
        String objectKey = dir + "/" + storedFileName;
        String fileUrl = ossService.getPresignedObjectUrl(objectKey);

        AiMemoryFileSaveReqDTO saveReqDTO = new AiMemoryFileSaveReqDTO();
        saveReqDTO.setSessionId(sessionId);
        saveReqDTO.setUserId(reqDTO.getUserId());
        saveReqDTO.setFileId(fileId);
        saveReqDTO.setFileName(originalFilename);
        saveReqDTO.setFileType(fileType);
        saveReqDTO.setMimeType(file.getContentType());
        saveReqDTO.setFileSize(file.getSize());
        saveReqDTO.setObjectKey(objectKey);
        saveReqDTO.setFileUrl(fileUrl);
        saveReqDTO.setParseStatus(AiFileParseStatusEnum.WAITING.getCode());
        aiMemoryService.saveFile(saveReqDTO);

        AiFileParseResult parseResult = parseFile(file, fileType);
        AiMemoryFileParseUpdateReqDTO parseUpdateReqDTO = buildParseUpdateReq(fileId, parseResult);
        aiMemoryService.updateFileParseResult(parseUpdateReqDTO);

        AiChatFileUploadRspDTO rspDTO = new AiChatFileUploadRspDTO();
        rspDTO.setSessionId(sessionId);
        rspDTO.setFileId(fileId);
        rspDTO.setFileName(originalFilename);
        rspDTO.setFileType(fileType);
        rspDTO.setFileSize(file.getSize());
        rspDTO.setObjectKey(objectKey);
        rspDTO.setFileUrl(fileUrl);
        rspDTO.setParseStatus(parseUpdateReqDTO.getParseStatus());
        rspDTO.setSummary(parseUpdateReqDTO.getSummary());
        rspDTO.setErrorMessage(parseUpdateReqDTO.getErrorMessage());
        return rspDTO;
    }

    private String ensureSession(AiChatFileUploadReqDTO reqDTO) {
        if (hasText(reqDTO.getSessionId())) {
            return reqDTO.getSessionId();
        }

        AiMemorySessionCreateReqDTO createReqDTO = new AiMemorySessionCreateReqDTO();
        createReqDTO.setUserId(reqDTO.getUserId());
        createReqDTO.setUsername(reqDTO.getUsername());
        createReqDTO.setNickname(reqDTO.getNickname());
        createReqDTO.setJobNum(reqDTO.getJobNum());
        createReqDTO.setTitle("文件会话");
        return aiMemoryService.createSession(createReqDTO).getSessionId();
    }

    private AiFileParseResult parseFile(MultipartFile file, String fileType) {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("ai-chat-file-", tempFileSuffix(file.getOriginalFilename()));
            file.transferTo(tempFile);
            return AiFileParseUtil.parse(tempFile.getAbsolutePath(), fileType);
        } catch (IOException e) {
            AiFileParseResult result = new AiFileParseResult();
            result.setSuccess(Boolean.FALSE);
            result.setErrorMessage("文件解析失败：" + e.getMessage());
            return result;
        } finally {
            if (tempFile != null && tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    private AiMemoryFileParseUpdateReqDTO buildParseUpdateReq(String fileId, AiFileParseResult parseResult) {
        AiMemoryFileParseUpdateReqDTO reqDTO = new AiMemoryFileParseUpdateReqDTO();
        reqDTO.setFileId(fileId);
        reqDTO.setParsedText(parseResult.getParsedText());
        reqDTO.setSummary(parseResult.getSummary());
        reqDTO.setErrorMessage(parseResult.getErrorMessage());
        reqDTO.setParseStatus(Boolean.TRUE.equals(parseResult.getSuccess())
                ? AiFileParseStatusEnum.SUCCESS.getCode()
                : AiFileParseStatusEnum.FAILED.getCode());
        return reqDTO;
    }

    private String generateFileId() {
        return "file_" + UUID.randomUUID().toString().replace("-", "");
    }

    private String extension(String fileName) {
        int index = fileName == null ? -1 : fileName.lastIndexOf('.');
        return index >= 0 ? fileName.substring(index) : "";
    }

    private String tempFileSuffix(String fileName) {
        String extension = extension(fileName);
        return hasText(extension) ? extension : null;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
