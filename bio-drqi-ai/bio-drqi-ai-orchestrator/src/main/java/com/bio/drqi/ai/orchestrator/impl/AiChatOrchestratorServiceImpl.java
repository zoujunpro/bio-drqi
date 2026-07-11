package com.bio.drqi.ai.orchestrator.impl;

import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.common.enums.AiMessageRoleEnum;
import com.bio.drqi.ai.common.enums.AiMessageSourceEnum;
import com.bio.drqi.ai.common.enums.AiPlanTaskTypeEnum;
import com.bio.drqi.ai.common.enums.AiSemanticCategoryEnum;
import com.bio.drqi.ai.dto.chat.AiChatReqDTO;
import com.bio.drqi.ai.dto.chat.AiChatRspDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryContextReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryContextRspDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryFileBindReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemoryMessageReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemorySessionCreateReqDTO;
import com.bio.drqi.ai.dto.memory.AiMemorySessionCreateRspDTO;
import com.bio.drqi.ai.dto.planner.AiPlanReqDTO;
import com.bio.drqi.ai.dto.planner.AiPlanRspDTO;
import com.bio.drqi.ai.dto.planner.AiPlanStepDTO;
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
import com.bio.drqi.ai.dto.tool.AiToolExecuteReqDTO;
import com.bio.drqi.ai.dto.tool.AiToolExecuteRspDTO;
import com.bio.drqi.ai.dto.semantic.AiUserContextResolveReqDTO;
import com.bio.drqi.ai.dto.semantic.AiUserContextResolveRspDTO;
import com.bio.drqi.ai.memory.AiMemoryService;
import com.bio.drqi.ai.orchestrator.AiChatOrchestratorService;
import com.bio.drqi.ai.orchestrator.AiChatResultProcessor;
import com.bio.drqi.ai.planner.AiPlannerService;
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
import com.bio.drqi.ai.tool.executor.AiToolExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @Resource
    private AiPlannerService aiPlannerService;

    @Resource
    private AiToolExecutor aiToolExecutor;

    @Resource
    private AiChatResultProcessor aiChatResultProcessor;

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

        // 12. Planner 生成工具或 Dify 调用计划。
        AiPlanRspDTO planResult = aiPlannerService.plan(
                buildPlanReq(
                        sessionId,
                        reqDTO,
                        context,
                        classifyResult,
                        userContext,
                        rewriteResult,
                        normalizedQuery,
                        intentResult,
                        timeResult,
                        numberResult,
                        scopeResult,
                        termResult,
                        entityResult,
                        conditionResult
                )
        );

        // 13. 执行计划。编排层只触发计划步骤，具体执行协议由 AiToolExecutor 根据工具配置选择。
        List<AiToolExecuteRspDTO> executeResults = executePlanSteps(sessionId, reqDTO, planResult);

        AiChatRspDTO rspDTO = aiChatResultProcessor.process(sessionId, planResult, executeResults);
        String answer = rspDTO.getAnswer();

        // 14. 保存 AI 回复。
        aiMemoryService.saveMessage(
                buildMessageReq(sessionId, reqDTO.getUserId(), AiMessageRoleEnum.ASSISTANT.getCode(), answer)
        );

        // 15. 返回前端。
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

    private AiPlanReqDTO buildPlanReq(String sessionId,
                                      AiChatReqDTO chatReqDTO,
                                      AiMemoryContextRspDTO context,
                                      AiSemanticClassifyRspDTO classifyResult,
                                      AiUserContextResolveRspDTO userContext,
                                      AiQueryRewriteRspDTO rewriteResult,
                                      String normalizedQuery,
                                      AiIntentRecognizeRspDTO intentResult,
                                      AiTimeParseRspDTO timeResult,
                                      AiNumberParseRspDTO numberResult,
                                      AiScopeResolveRspDTO scopeResult,
                                      AiTermMappingRspDTO termResult,
                                      AiEntityExtractRspDTO entityResult,
                                      AiConditionExtractRspDTO conditionResult) {
        AiPlanReqDTO reqDTO = new AiPlanReqDTO();
        reqDTO.setSessionId(sessionId);
        reqDTO.setUserId(chatReqDTO.getUserId());
        reqDTO.setOriginalQuery(chatReqDTO.getMessage());
        reqDTO.setRewrittenQuery(rewriteResult == null ? null : rewriteResult.getRewrittenQuery());
        reqDTO.setNormalizedQuery(normalizedQuery);
        reqDTO.setMemoryContext(context);
        reqDTO.setSemanticClassifyResult(classifyResult);
        reqDTO.setUserContext(userContext);
        reqDTO.setIntentResult(intentResult);
        reqDTO.setTimeResult(timeResult);
        reqDTO.setNumberResult(numberResult);
        reqDTO.setScopeResult(scopeResult);
        reqDTO.setTermResult(termResult);
        reqDTO.setEntityResult(entityResult);
        reqDTO.setConditionResult(conditionResult);
        if (intentResult != null && intentResult.getTools() != null) {
            reqDTO.setCandidateTools(intentResult.getTools());
        }
        return reqDTO;
    }

    private List<AiToolExecuteRspDTO> executePlanSteps(String sessionId, AiChatReqDTO chatReqDTO, AiPlanRspDTO planResult) {
        List<AiToolExecuteRspDTO> results = new ArrayList<AiToolExecuteRspDTO>();
        if (planResult == null || !Boolean.TRUE.equals(planResult.getExecutable())
                || planResult.getSteps() == null || planResult.getSteps().isEmpty()) {
            return results;
        }

        Map<Integer, AiToolExecuteRspDTO> resultMap = new LinkedHashMap<Integer, AiToolExecuteRspDTO>();
        for (AiPlanStepDTO step : planResult.getSteps()) {
            if (step == null) {
                continue;
            }

            AiToolExecuteRspDTO stepResult;
            String dependencyError = findDependencyError(step, resultMap);
            if (hasText(dependencyError)) {
                stepResult = AiToolExecuteRspDTO.fail(step.getToolCode(), step.getStepType(), step.getTargetCode(),
                        dependencyError, 0L);
            } else if (hasText(step.getToolCode())) {
                // 编排层只负责按计划触发工具；API/Dify/MCP/Local 由 AiToolExecutor 根据工具配置的 toolType 分发。
                stepResult = executeToolStep(sessionId, chatReqDTO, step);
            } else if (AiPlanTaskTypeEnum.MERGE.getCode().equals(step.getStepType())) {
                stepResult = buildMergeResult(step, resultMap);
            } else {
                stepResult = AiToolExecuteRspDTO.fail(step.getToolCode(), step.getStepType(), step.getTargetCode(),
                        "步骤没有绑定工具，无法执行：" + step.getStepType(), 0L);
            }

            results.add(stepResult);
            if (step.getStepNo() != null) {
                resultMap.put(step.getStepNo(), stepResult);
            }
            saveToolMessage(sessionId, chatReqDTO.getUserId(), step, stepResult);
        }
        return results;
    }

    private AiToolExecuteRspDTO executeToolStep(String sessionId, AiChatReqDTO chatReqDTO, AiPlanStepDTO step) {
        try {
            return aiToolExecutor.execute(buildToolExecuteReq(sessionId, chatReqDTO, step));
        } catch (Exception e) {
            return AiToolExecuteRspDTO.fail(step.getToolCode(), step.getStepType(), step.getTargetCode(),
                    e.getMessage(), 0L);
        }
    }

    private AiToolExecuteReqDTO buildToolExecuteReq(String sessionId, AiChatReqDTO chatReqDTO, AiPlanStepDTO step) {
        AiToolExecuteReqDTO reqDTO = new AiToolExecuteReqDTO();
        reqDTO.setToolCode(step.getToolCode());
        reqDTO.setInputJson(step.getInputJson());
        reqDTO.setSessionId(sessionId);
        reqDTO.setUserId(chatReqDTO.getUserId());
        reqDTO.setUsername(chatReqDTO.getUsername());
        reqDTO.setNickname(chatReqDTO.getNickname());
        reqDTO.setConfirmed(Boolean.FALSE);
        return reqDTO;
    }

    private AiToolExecuteRspDTO buildMergeResult(AiPlanStepDTO step, Map<Integer, AiToolExecuteRspDTO> resultMap) {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        Set<Integer> dependencyNos = parseDependencyNos(step.getDependsOn());
        for (Integer dependencyNo : dependencyNos) {
            AiToolExecuteRspDTO dependencyResult = resultMap.get(dependencyNo);
            if (dependencyResult == null) {
                continue;
            }
            builder.append("\"step").append(dependencyNo).append("\":");
            if (hasText(dependencyResult.getResultJson())) {
                builder.append(dependencyResult.getResultJson());
            } else {
                builder.append("\"").append(escapeJson(dependencyResult.getErrorMessage())).append("\"");
            }
            builder.append(",");
        }
        trimLastComma(builder);
        builder.append("}");
        return AiToolExecuteRspDTO.success(step.getToolCode(), step.getStepType(), step.getTargetCode(),
                200, builder.toString(), 0L);
    }

    private String findDependencyError(AiPlanStepDTO step, Map<Integer, AiToolExecuteRspDTO> resultMap) {
        Set<Integer> dependencyNos = parseDependencyNos(step.getDependsOn());
        for (Integer dependencyNo : dependencyNos) {
            AiToolExecuteRspDTO dependencyResult = resultMap.get(dependencyNo);
            if (dependencyResult == null) {
                return "依赖步骤未执行：step " + dependencyNo;
            }
            if (!Boolean.TRUE.equals(dependencyResult.getSuccess())) {
                return "依赖步骤执行失败：step " + dependencyNo + "，" + dependencyResult.getErrorMessage();
            }
        }
        return null;
    }

    private Set<Integer> parseDependencyNos(String dependsOn) {
        Set<Integer> dependencyNos = new HashSet<Integer>();
        if (!hasText(dependsOn)) {
            return dependencyNos;
        }
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < dependsOn.length(); i++) {
            char ch = dependsOn.charAt(i);
            if (Character.isDigit(ch)) {
                number.append(ch);
                continue;
            }
            addDependencyNo(dependencyNos, number);
        }
        addDependencyNo(dependencyNos, number);
        return dependencyNos;
    }

    private void addDependencyNo(Set<Integer> dependencyNos, StringBuilder number) {
        if (number.length() == 0) {
            return;
        }
        try {
            dependencyNos.add(Integer.valueOf(number.toString()));
        } catch (NumberFormatException ignored) {
            // 非法依赖序号忽略，Planner 校验阶段会兜底。
        }
        number.setLength(0);
    }

    private void saveToolMessage(String sessionId, String userId, AiPlanStepDTO step, AiToolExecuteRspDTO result) {
        StringBuilder content = new StringBuilder();
        content.append("step=").append(step.getStepNo())
                .append(", type=").append(step.getStepType())
                .append(", tool=").append(step.getToolCode())
                .append(", success=").append(result.getSuccess());
        if (Boolean.TRUE.equals(result.getSuccess())) {
            content.append(", result=").append(safeShortText(result.getResultJson(), 1000));
        } else {
            content.append(", error=").append(safeShortText(result.getErrorMessage(), 1000));
        }

        AiMemoryMessageReqDTO reqDTO = buildMessageReq(sessionId, userId, AiMessageRoleEnum.TOOL.getCode(), content.toString());
        reqDTO.setSource(AiMessageSourceEnum.TOOL.getCode());
        aiMemoryService.saveMessage(reqDTO);
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

    private String safeShortText(String value, int maxLength) {
        if (!hasText(value)) {
            return "";
        }
        String trimmed = value.trim();
        if (trimmed.length() <= maxLength) {
            return trimmed;
        }
        return trimmed.substring(0, maxLength);
    }

    private void trimLastComma(StringBuilder builder) {
        if (builder != null && builder.length() > 0 && builder.charAt(builder.length() - 1) == ',') {
            builder.deleteCharAt(builder.length() - 1);
        }
    }

    private String escapeJson(String value) {
        if (!hasText(value)) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
