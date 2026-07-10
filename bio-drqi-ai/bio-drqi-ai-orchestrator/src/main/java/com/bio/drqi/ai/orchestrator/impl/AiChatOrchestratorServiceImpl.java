package com.bio.drqi.ai.orchestrator.impl;

import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.common.enums.AiMessageRoleEnum;
import com.bio.drqi.ai.common.enums.AiMessageSourceEnum;
import com.bio.drqi.ai.common.enums.AiSemanticCategoryEnum;
import com.bio.drqi.ai.dto.chat.AiChatReqDTO;
import com.bio.drqi.ai.dto.chat.AiChatRspDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryContextReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryContextRspDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryFileBindReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryMessageReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemorySessionCreateReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemorySessionCreateRspDTO;
import com.bio.drqi.ai.dto.semantic.AiConditionExtractReqDTO;
import com.bio.drqi.ai.dto.semantic.AiConditionExtractRspDTO;
import com.bio.drqi.ai.dto.semantic.AiEntityExtractReqDTO;
import com.bio.drqi.ai.dto.semantic.AiEntityExtractRspDTO;
import com.bio.drqi.ai.dto.semantic.AiIntentRecognizeReqDTO;
import com.bio.drqi.ai.dto.semantic.AiIntentRecognizeRspDTO;
import com.bio.drqi.ai.dto.semantic.AiNumberParseReqDTO;
import com.bio.drqi.ai.dto.semantic.AiNumberParseRspDTO;
import com.bio.drqi.ai.dto.semantic.AiQueryRewriteReqDTO;
import com.bio.drqi.ai.dto.semantic.AiQueryRewriteRspDTO;
import com.bio.drqi.ai.dto.semantic.AiScopeResolveReqDTO;
import com.bio.drqi.ai.dto.semantic.AiScopeResolveRspDTO;
import com.bio.drqi.ai.dto.semantic.AiSemanticClassifyReqDTO;
import com.bio.drqi.ai.dto.semantic.AiSemanticClassifyRspDTO;
import com.bio.drqi.ai.dto.semantic.AiSynonymNormalizeReqDTO;
import com.bio.drqi.ai.dto.semantic.AiSynonymNormalizeRspDTO;
import com.bio.drqi.ai.dto.semantic.AiTermMappingReqDTO;
import com.bio.drqi.ai.dto.semantic.AiTermMappingRspDTO;
import com.bio.drqi.ai.dto.semantic.AiTimeParseReqDTO;
import com.bio.drqi.ai.dto.semantic.AiTimeParseRspDTO;
import com.bio.drqi.ai.dto.semantic.AiUserContextResolveReqDTO;
import com.bio.drqi.ai.dto.semantic.AiUserContextResolveRspDTO;
import com.bio.drqi.ai.memory.AiMemoryService;
import com.bio.drqi.ai.orchestrator.AiChatOrchestratorService;
import com.bio.drqi.ai.semantic.AiConditionExtractService;
import com.bio.drqi.ai.semantic.AiEntityExtractService;
import com.bio.drqi.ai.semantic.AiIntentRouterService;
import com.bio.drqi.ai.semantic.AiNumberParseService;
import com.bio.drqi.ai.semantic.AiQueryRewriteService;
import com.bio.drqi.ai.semantic.AiScopeResolveService;
import com.bio.drqi.ai.semantic.AiSemanticClassifyService;
import com.bio.drqi.ai.semantic.AiSynonymNormalizeService;
import com.bio.drqi.ai.semantic.AiTermMappingService;
import com.bio.drqi.ai.semantic.AiTimeParseService;
import com.bio.drqi.ai.semantic.AiUserContextResolveService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * AI 聊天业务编排默认实现。
 */
@Service
public class AiChatOrchestratorServiceImpl implements AiChatOrchestratorService {

    @Resource
    private AiMemoryService aiMemoryService;

    @Resource
    private AiIntentRouterService aiIntentRouterService;

    @Resource
    private AiQueryRewriteService aiQueryRewriteService;

    @Resource
    private AiSemanticClassifyService aiSemanticClassifyService;

    @Resource
    private AiTimeParseService aiTimeParseService;

    @Resource
    private AiEntityExtractService aiEntityExtractService;

    @Resource
    private AiNumberParseService aiNumberParseService;

    @Resource
    private AiScopeResolveService aiScopeResolveService;

    @Resource
    private AiConditionExtractService aiConditionExtractService;

    @Resource
    private AiSynonymNormalizeService aiSynonymNormalizeService;

    @Resource
    private AiTermMappingService aiTermMappingService;

    @Resource
    private AiUserContextResolveService aiUserContextResolveService;

    @Override
    public AiChatRspDTO chat(AiChatReqDTO reqDTO) {
        if (!hasText(reqDTO.getUserId())) {
            throw new BusinessException("当前用户信息不能为空");
        }

        // 1. 确认或创建会话。
        String sessionId = ensureSession(reqDTO);

        // 2. 保存用户消息。
        Long userMessageId = aiMemoryService.saveMessage(
                buildMessageReq(sessionId, reqDTO.getUserId(), AiMessageRoleEnum.USER.getCode(), reqDTO.getMessage())
        );
        bindFilesToMessage(sessionId, userMessageId, reqDTO);

        // 3. 获取 Memory 上下文。
        AiMemoryContextRspDTO context = aiMemoryService.getContext(buildContextReq(sessionId, reqDTO));

        // 4. 系统话术分类。
        AiSemanticClassifyRspDTO classifyResult = aiSemanticClassifyService.classify(buildClassifyReq(reqDTO.getMessage()));
        String systemAnswer = buildSystemAnswer(classifyResult);
        if (hasText(systemAnswer)) {
            aiMemoryService.saveMessage(
                    buildMessageReq(sessionId, reqDTO.getUserId(), AiMessageRoleEnum.ASSISTANT.getCode(), systemAnswer)
            );

            AiChatRspDTO rspDTO = new AiChatRspDTO();
            rspDTO.setSessionId(sessionId);
            rspDTO.setSuccess(Boolean.TRUE);
            rspDTO.setAnswer(systemAnswer);
            return rspDTO;
        }

        // 5. 解析用户身份上下文，后续权限范围和工具调用都要基于当前人。
        AiUserContextResolveRspDTO userContext = aiUserContextResolveService.resolve(buildUserContextReq(reqDTO));

        // 6. 指代消解和问题改写。
        AiQueryRewriteRspDTO rewriteResult = aiQueryRewriteService.rewrite(buildRewriteReq(sessionId, reqDTO, context));
        String rewrittenQuery = rewriteResult.getRewrittenQuery();

        // 7. 同义词归一，把用户口语或别名转换成业务标准词。
        AiSynonymNormalizeRspDTO synonymResult = aiSynonymNormalizeService.normalize(buildSynonymNormalizeReq(rewrittenQuery));
        String normalizedQuery = synonymResult.getNormalizedText();

        // 8. 时间、数量、范围、术语解析，这些结果后续会进入 Planner 和工具参数组装。
        AiTimeParseRspDTO timeResult = aiTimeParseService.parse(buildTimeParseReq(normalizedQuery));
        AiNumberParseRspDTO numberResult = aiNumberParseService.parse(buildNumberParseReq(normalizedQuery));
        AiScopeResolveRspDTO scopeResult = aiScopeResolveService.resolve(buildScopeResolveReq(sessionId, reqDTO, normalizedQuery));
        AiTermMappingRspDTO termResult = aiTermMappingService.map(buildTermMappingReq(normalizedQuery, null));

        // 9. 通用实体抽取。
        AiEntityExtractRspDTO preIntentEntityResult = aiEntityExtractService.extract(
                buildEntityExtractReq(sessionId, reqDTO.getUserId(), normalizedQuery, null)
        );

        // 10. 识别 Intent。
        AiIntentRecognizeRspDTO intentResult = aiIntentRouterService.recognize(
                buildIntentReq(sessionId, reqDTO.getUserId(), normalizedQuery)
        );

        // 11. 意图约束实体抽取和条件抽取。
        AiEntityExtractRspDTO entityResult = aiEntityExtractService.extract(
                buildEntityExtractReq(sessionId, reqDTO.getUserId(), normalizedQuery, intentResult.getIntentCode())
        );
        AiConditionExtractRspDTO conditionResult = aiConditionExtractService.extract(
                buildConditionExtractReq(normalizedQuery, intentResult.getIntentCode())
        );

        // 12. Planner 生成 Dify 调用参数

        // 13. 调用 Dify
        String answer = "AI聊天编排入口已创建，已读取上下文：短期记忆"
                + context.getShortMemory().size()
                + "条，长期记忆"
                + context.getLongMemory().size()
                + "条，文件上下文"
                + context.getFiles().size()
                + "个，问题改写："
                + rewrittenQuery
                + "，归一问题："
                + normalizedQuery
                + "，时间解析："
                + buildTimeSummary(timeResult)
                + "，数量表达："
                + numberResult.getNumbers().size()
                + "个，范围："
                + buildScopeSummary(scopeResult, userContext)
                + "，术语映射："
                + termResult.getTerms().size()
                + "个"
                + "，通用实体数量："
                + preIntentEntityResult.getEntities().size()
                + "，意图实体数量："
                + entityResult.getEntities().size()
                + "，条件数量："
                + conditionResult.getConditions().size()
                + "，识别意图："
                + intentResult.getIntentCode()
                + "（置信度"
                + intentResult.getConfidence()
                + "）"
                + "。后续接入 Dify。";

        // 14. 保存 AI 回复。
        aiMemoryService.saveMessage(
                buildMessageReq(sessionId, reqDTO.getUserId(), AiMessageRoleEnum.ASSISTANT.getCode(), answer)
        );

        // 15. 返回前端。
        AiChatRspDTO rspDTO = new AiChatRspDTO();
        rspDTO.setSessionId(sessionId);
        rspDTO.setSuccess(Boolean.TRUE);
        rspDTO.setAnswer(answer);
        return rspDTO;
    }

    private String ensureSession(AiChatReqDTO reqDTO) {
        if (hasText(reqDTO.getSessionId())) {
            return reqDTO.getSessionId();
        }

        AiMemorySessionCreateReqDTO createReqDTO = new AiMemorySessionCreateReqDTO();
        createReqDTO.setUserId(reqDTO.getUserId());
        createReqDTO.setUsername(reqDTO.getUsername());
        createReqDTO.setNickname(reqDTO.getNickname());
        createReqDTO.setJobNum(reqDTO.getJobNum());
        createReqDTO.setTitle(buildSessionTitle(reqDTO.getMessage()));
        AiMemorySessionCreateRspDTO createRspDTO = aiMemoryService.createSession(createReqDTO);
        return createRspDTO.getSessionId();
    }

    private AiMemoryMessageReqDTO buildMessageReq(String sessionId, String userId, String role, String content) {
        AiMemoryMessageReqDTO reqDTO = new AiMemoryMessageReqDTO();
        reqDTO.setSessionId(sessionId);
        reqDTO.setUserId(userId);
        reqDTO.setRole(role);
        reqDTO.setContent(content);
        reqDTO.setSource(AiMessageSourceEnum.CONVERSATION.getCode());
        return reqDTO;
    }

    private AiMemoryContextReqDTO buildContextReq(String sessionId, AiChatReqDTO chatReqDTO) {
        AiMemoryContextReqDTO reqDTO = new AiMemoryContextReqDTO();
        reqDTO.setSessionId(sessionId);
        reqDTO.setUserId(chatReqDTO.getUserId());
        reqDTO.setQuery(chatReqDTO.getMessage());
        reqDTO.setFileIds(chatReqDTO.getFileIds());
        return reqDTO;
    }

    private AiSemanticClassifyReqDTO buildClassifyReq(String query) {
        AiSemanticClassifyReqDTO reqDTO = new AiSemanticClassifyReqDTO();
        reqDTO.setQuery(query);
        return reqDTO;
    }

    private AiQueryRewriteReqDTO buildRewriteReq(String sessionId, AiChatReqDTO chatReqDTO, AiMemoryContextRspDTO context) {
        AiQueryRewriteReqDTO reqDTO = new AiQueryRewriteReqDTO();
        reqDTO.setSessionId(sessionId);
        reqDTO.setUserId(chatReqDTO.getUserId());
        reqDTO.setOriginalQuery(chatReqDTO.getMessage());
        reqDTO.setShortMemory(context.getShortMemory());
        reqDTO.setLongMemory(context.getLongMemory());
        reqDTO.setFiles(context.getFiles());
        return reqDTO;
    }

    private AiIntentRecognizeReqDTO buildIntentReq(String sessionId, String userId, String query) {
        AiIntentRecognizeReqDTO reqDTO = new AiIntentRecognizeReqDTO();
        reqDTO.setSessionId(sessionId);
        reqDTO.setUserId(userId);
        reqDTO.setQuery(query);
        return reqDTO;
    }

    private AiTimeParseReqDTO buildTimeParseReq(String query) {
        AiTimeParseReqDTO reqDTO = new AiTimeParseReqDTO();
        reqDTO.setQuery(query);
        reqDTO.setReferenceDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        return reqDTO;
    }

    private AiEntityExtractReqDTO buildEntityExtractReq(String sessionId, String userId, String query, String intentCode) {
        AiEntityExtractReqDTO reqDTO = new AiEntityExtractReqDTO();
        reqDTO.setSessionId(sessionId);
        reqDTO.setUserId(userId);
        reqDTO.setQuery(query);
        reqDTO.setIntentCode(intentCode);
        return reqDTO;
    }

    private AiNumberParseReqDTO buildNumberParseReq(String query) {
        AiNumberParseReqDTO reqDTO = new AiNumberParseReqDTO();
        reqDTO.setQuery(query);
        return reqDTO;
    }

    private AiScopeResolveReqDTO buildScopeResolveReq(String sessionId, AiChatReqDTO chatReqDTO, String query) {
        AiScopeResolveReqDTO reqDTO = new AiScopeResolveReqDTO();
        reqDTO.setSessionId(sessionId);
        reqDTO.setUserId(chatReqDTO.getUserId());
        reqDTO.setQuery(query);
        return reqDTO;
    }

    private AiConditionExtractReqDTO buildConditionExtractReq(String query, String intentCode) {
        AiConditionExtractReqDTO reqDTO = new AiConditionExtractReqDTO();
        reqDTO.setQuery(query);
        reqDTO.setIntentCode(intentCode);
        return reqDTO;
    }

    private AiSynonymNormalizeReqDTO buildSynonymNormalizeReq(String query) {
        AiSynonymNormalizeReqDTO reqDTO = new AiSynonymNormalizeReqDTO();
        reqDTO.setQuery(query);
        return reqDTO;
    }

    private AiTermMappingReqDTO buildTermMappingReq(String query, String domain) {
        AiTermMappingReqDTO reqDTO = new AiTermMappingReqDTO();
        reqDTO.setQuery(query);
        reqDTO.setDomain(domain);
        return reqDTO;
    }

    private AiUserContextResolveReqDTO buildUserContextReq(AiChatReqDTO chatReqDTO) {
        AiUserContextResolveReqDTO reqDTO = new AiUserContextResolveReqDTO();
        reqDTO.setUserId(chatReqDTO.getUserId());
        reqDTO.setUsername(chatReqDTO.getUsername());
        reqDTO.setNickname(chatReqDTO.getNickname());
        reqDTO.setJobNum(chatReqDTO.getJobNum());
        return reqDTO;
    }

    private String buildTimeSummary(AiTimeParseRspDTO timeResult) {
        if (timeResult == null || !Boolean.TRUE.equals(timeResult.getMatched())) {
            return "未识别";
        }
        return timeResult.getExpression() + "(" + timeResult.getStartDate() + "~" + timeResult.getEndDate() + ")";
    }

    private String buildScopeSummary(AiScopeResolveRspDTO scopeResult, AiUserContextResolveRspDTO userContext) {
        if (scopeResult == null || !hasText(scopeResult.getScopeType()) || "UNKNOWN".equals(scopeResult.getScopeType())) {
            if (userContext == null) {
                return "未识别";
            }
            return userContext.getDefaultScopeType() + "(" + userContext.getDefaultScopeValue() + ")";
        }
        if (hasText(scopeResult.getScopeValue())) {
            return scopeResult.getScopeType() + "(" + scopeResult.getScopeValue() + ")";
        }
        return scopeResult.getScopeType();
    }

    private String buildSystemAnswer(AiSemanticClassifyRspDTO classifyResult) {
        if (classifyResult == null || !hasText(classifyResult.getCategory())) {
            return null;
        }
        if (AiSemanticCategoryEnum.GREETING.getCode().equals(classifyResult.getCategory())) {
            return "你好，我可以帮你查询业务数据、分析文件和处理企业知识问答。";
        }
        if (AiSemanticCategoryEnum.GOODBYE.getCode().equals(classifyResult.getCategory())) {
            return "再见。";
        }
        if (AiSemanticCategoryEnum.THANKS.getCode().equals(classifyResult.getCategory())) {
            return "不客气。";
        }
        if (AiSemanticCategoryEnum.HELP.getCode().equals(classifyResult.getCategory())) {
            return "你可以直接描述要查的问题，例如项目进度、CER取样检测、种子库存，或者上传文件让我分析。";
        }
        if (AiSemanticCategoryEnum.CHITCHAT.getCode().equals(classifyResult.getCategory())) {
            return "我主要负责企业业务查询、文件分析和知识问答。你可以直接告诉我要处理的业务问题。";
        }
        if (AiSemanticCategoryEnum.CONFIRMATION.getCode().equals(classifyResult.getCategory())) {
            return "已收到确认。";
        }
        if (AiSemanticCategoryEnum.REJECTION.getCode().equals(classifyResult.getCategory())) {
            return "已收到。你可以重新描述要处理的问题。";
        }
        if (AiSemanticCategoryEnum.CORRECTION.getCode().equals(classifyResult.getCategory())) {
            return null;
        }
        if (AiSemanticCategoryEnum.CLARIFICATION.getCode().equals(classifyResult.getCategory())) {
            return "请补充说明你要查询的对象或范围。";
        }
        if (AiSemanticCategoryEnum.SENSITIVE.getCode().equals(classifyResult.getCategory())) {
            return "这个问题可能涉及敏感信息。请确认只查询你有权限查看的业务数据。";
        }
        return null;
    }

    private void bindFilesToMessage(String sessionId, Long messageId, AiChatReqDTO chatReqDTO) {
        if (chatReqDTO.getFileIds() == null || chatReqDTO.getFileIds().isEmpty()) {
            return;
        }

        AiMemoryFileBindReqDTO reqDTO = new AiMemoryFileBindReqDTO();
        reqDTO.setSessionId(sessionId);
        reqDTO.setMessageId(messageId);
        reqDTO.setUserId(chatReqDTO.getUserId());
        reqDTO.setFileIds(chatReqDTO.getFileIds());
        aiMemoryService.bindFilesToMessage(reqDTO);
    }

    private String buildSessionTitle(String message) {
        if (!hasText(message)) {
            return "新会话";
        }
        String trimmed = message.trim();
        if (trimmed.length() <= 20) {
            return trimmed;
        }
        return trimmed.substring(0, 20);
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
