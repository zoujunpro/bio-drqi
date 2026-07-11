package com.bio.drqi.ai.orchestrator.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bio.drqi.ai.dto.chat.AiChatAnswerTypeEnum;
import com.bio.drqi.ai.dto.chat.AiChatAttachmentDTO;
import com.bio.drqi.ai.dto.chat.AiChatRspDTO;
import com.bio.drqi.ai.dto.chat.AiChatTableDTO;
import com.bio.drqi.ai.dto.planner.AiPlanRspDTO;
import com.bio.drqi.ai.dto.tool.AiToolExecuteRspDTO;
import com.bio.drqi.ai.orchestrator.AiChatResultProcessor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 默认聊天结果处理器。
 */
@Service
public class DefaultAiChatResultProcessor implements AiChatResultProcessor {

    @Override
    public AiChatRspDTO process(String sessionId, AiPlanRspDTO planResult, List<AiToolExecuteRspDTO> executeResults) {
        AiChatRspDTO rspDTO = new AiChatRspDTO();
        rspDTO.setSessionId(sessionId);
        rspDTO.setSuccess(Boolean.TRUE);
        rspDTO.setPlan(planResult);
        if (executeResults != null) {
            rspDTO.setExecuteResults(executeResults);
        }

        if (planResult == null) {
            return buildError(rspDTO, "当前没有生成可执行计划，请重新描述你要处理的问题。", "PLAN_EMPTY", null);
        }
        if (!Boolean.TRUE.equals(planResult.getExecutable())) {
            rspDTO.setAnswerType(AiChatAnswerTypeEnum.CLARIFY.getCode());
            rspDTO.setAnswer(safeText(planResult.getClarifyQuestion(), safeText(planResult.getReason(), "当前信息不足，请补充必要条件。")));
            return rspDTO;
        }
        if (executeResults == null || executeResults.isEmpty()) {
            return buildError(rspDTO, "已生成执行计划，但没有可执行步骤。", "NO_EXECUTABLE_STEP", null);
        }

        AiToolExecuteRspDTO lastSuccessResult = null;
        for (AiToolExecuteRspDTO executeResult : executeResults) {
            if (executeResult == null) {
                continue;
            }
            if (!Boolean.TRUE.equals(executeResult.getSuccess())) {
                return buildError(rspDTO, "执行失败：" + safeText(executeResult.getErrorMessage(), "工具调用未成功。"),
                        "TOOL_EXECUTE_FAILED", executeResult.getErrorMessage());
            }
            lastSuccessResult = executeResult;
        }

        if (lastSuccessResult == null) {
            return buildError(rspDTO, "执行完成，但没有返回有效结果。", "EMPTY_RESULT", null);
        }
        return buildSuccessResult(rspDTO, lastSuccessResult);
    }

    private AiChatRspDTO buildSuccessResult(AiChatRspDTO rspDTO, AiToolExecuteRspDTO executeResult) {
        String resultJson = executeResult.getResultJson();
        if (!hasText(resultJson)) {
            rspDTO.setAnswerType(AiChatAnswerTypeEnum.TEXT.getCode());
            rspDTO.setAnswer("执行完成。");
            return rspDTO;
        }

        JSONObject resultObject = parseObject(resultJson);
        if (resultObject == null) {
            rspDTO.setAnswerType(AiChatAnswerTypeEnum.TEXT.getCode());
            rspDTO.setAnswer(resultJson);
            return rspDTO;
        }

        String resultType = normalizeAnswerType(resultObject.getString("resultType"));
        rspDTO.setAnswerType(resultType);
        rspDTO.setAnswer(safeText(resultObject.getString("answer"), safeText(resultObject.getString("summary"), defaultAnswer(resultType))));
        rspDTO.setData(resultObject.get("data"));

        AiChatTableDTO table = parseTable(resultObject);
        if (table != null) {
            rspDTO.setTable(table);
        }
        List<AiChatAttachmentDTO> attachments = parseAttachments(resultObject);
        if (attachments != null && !attachments.isEmpty()) {
            rspDTO.setAttachments(attachments);
        }
        if (AiChatAnswerTypeEnum.TEXT.getCode().equals(resultType)
                && table != null && attachments != null && !attachments.isEmpty()) {
            rspDTO.setAnswerType(AiChatAnswerTypeEnum.MIXED.getCode());
        } else if (AiChatAnswerTypeEnum.TEXT.getCode().equals(resultType) && table != null) {
            rspDTO.setAnswerType(AiChatAnswerTypeEnum.TABLE.getCode());
        } else if (AiChatAnswerTypeEnum.TEXT.getCode().equals(resultType) && attachments != null && !attachments.isEmpty()) {
            rspDTO.setAnswerType(AiChatAnswerTypeEnum.FILE.getCode());
        }
        return rspDTO;
    }

    private AiChatRspDTO buildError(AiChatRspDTO rspDTO, String answer, String errorCode, String errorMessage) {
        rspDTO.setSuccess(Boolean.FALSE);
        rspDTO.setAnswerType(AiChatAnswerTypeEnum.ERROR.getCode());
        rspDTO.setAnswer(answer);
        rspDTO.setErrorCode(errorCode);
        rspDTO.setErrorMessage(errorMessage);
        return rspDTO;
    }

    private AiChatTableDTO parseTable(JSONObject resultObject) {
        Object tableObject = resultObject.get("table");
        if (tableObject == null) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(tableObject), AiChatTableDTO.class);
    }

    private List<AiChatAttachmentDTO> parseAttachments(JSONObject resultObject) {
        Object attachmentsObject = resultObject.get("attachments");
        if (attachmentsObject == null) {
            Object attachmentObject = resultObject.get("attachment");
            if (attachmentObject == null && hasText(resultObject.getString("downloadUrl"))) {
                JSONObject singleAttachment = new JSONObject();
                singleAttachment.put("fileName", resultObject.getString("fileName"));
                singleAttachment.put("fileType", resultObject.getString("fileType"));
                singleAttachment.put("downloadUrl", resultObject.getString("downloadUrl"));
                singleAttachment.put("fileSize", resultObject.getLong("fileSize"));
                singleAttachment.put("fileId", resultObject.getString("fileId"));
                attachmentsObject = singleAttachment;
            } else {
                attachmentsObject = attachmentObject;
            }
        }
        if (attachmentsObject == null) {
            return null;
        }
        if (attachmentsObject instanceof JSONArray) {
            return JSON.parseArray(JSON.toJSONString(attachmentsObject), AiChatAttachmentDTO.class);
        }
        JSONArray array = new JSONArray();
        array.add(attachmentsObject);
        return JSON.parseArray(JSON.toJSONString(array), AiChatAttachmentDTO.class);
    }

    private JSONObject parseObject(String resultJson) {
        try {
            return JSON.parseObject(resultJson);
        } catch (Exception ignored) {
            return null;
        }
    }

    private String normalizeAnswerType(String resultType) {
        if (!hasText(resultType)) {
            return AiChatAnswerTypeEnum.TEXT.getCode();
        }
        String upperType = resultType.trim().toUpperCase();
        if ("TABLE".equals(upperType)
                || "FILE".equals(upperType)
                || "MIXED".equals(upperType)
                || "ERROR".equals(upperType)
                || "CLARIFY".equals(upperType)) {
            return upperType;
        }
        return AiChatAnswerTypeEnum.TEXT.getCode();
    }

    private String defaultAnswer(String resultType) {
        if (AiChatAnswerTypeEnum.TABLE.getCode().equals(resultType)) {
            return "查询完成，结果如下。";
        }
        if (AiChatAnswerTypeEnum.FILE.getCode().equals(resultType)) {
            return "文件已生成。";
        }
        if (AiChatAnswerTypeEnum.MIXED.getCode().equals(resultType)) {
            return "处理完成，结果如下。";
        }
        return "执行完成。";
    }

    private String safeText(String first, String second) {
        if (hasText(first)) {
            return first;
        }
        return second;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
