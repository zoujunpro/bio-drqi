package com.bio.drqi.ai.service.impl;

import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.clarify.ClarificationCandidate;
import com.bio.drqi.ai.clarify.ClarificationDecision;
import com.bio.drqi.ai.clarify.ClarificationEngine;
import com.bio.drqi.ai.clarify.ClarificationRequest;
import com.bio.drqi.ai.config.AiProperties;
import com.bio.drqi.ai.dto.audit.AiAuditLogDTO;
import com.bio.drqi.ai.dto.memory.AiConversationContextDTO;
import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.dto.req.AiAnalysisReqDTO;
import com.bio.drqi.ai.dto.rsp.AiAnalysisRspDTO;
import com.bio.drqi.ai.dto.rsp.AiTableDTO;
import com.bio.drqi.ai.exception.AiErrorCode;
import com.bio.drqi.ai.exception.AiGeneralChatException;
import com.bio.drqi.ai.model.ChatService;
import com.bio.drqi.ai.registry.AiDomainRegistry;
import com.bio.drqi.ai.router.IntentRouter;
import com.bio.drqi.ai.schema.AiDomainSchema;
import com.bio.drqi.ai.schema.AiMetricSchema;
import com.bio.drqi.ai.service.AiAnalysisService;
import com.bio.drqi.ai.service.AiAuditLogService;
import com.bio.drqi.ai.service.AiAgentAnalysisService;
import com.bio.drqi.ai.service.AiCommandAnalysisService;
import com.bio.drqi.ai.service.AiConversationMemoryService;
import com.bio.drqi.ai.service.AiContextFilterEnhancer;
import com.bio.drqi.ai.service.AiMultiAnalysisService;
import com.bio.drqi.ai.service.AiPermissionInjector;
import com.bio.drqi.ai.service.AiQueryExecutorService;
import com.bio.drqi.ai.service.AiQueryPlanService;
import com.bio.drqi.ai.service.AiQueryPlanValidator;
import com.bio.drqi.ai.service.AiQueryRiskChecker;
import com.bio.drqi.ai.service.AiRuntimeMetricsService;
import com.bio.drqi.ai.service.AiTermService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AiAnalysisServiceImpl implements AiAnalysisService {

    private static final String QUERY_TYPE_DETAIL = "detail";

    @Resource
    private AiQueryPlanService aiQueryPlanService;

    @Resource
    private AiQueryPlanValidator aiQueryPlanValidator;

    @Resource
    private AiDomainRegistry aiDomainRegistry;

    @Resource
    private AiQueryExecutorService aiQueryExecutorService;

    @Resource
    private AiAuditLogService aiAuditLogService;

    @Resource
    private ChatService chatService;

    @Resource
    private AiMultiAnalysisService aiMultiAnalysisService;

    @Resource
    private IntentRouter intentRouter;

    @Resource
    private AiCommandAnalysisService aiCommandAnalysisService;

    @Resource
    private AiAgentAnalysisService aiAgentAnalysisService;

    @Resource
    private AiConversationMemoryService aiConversationMemoryService;

    @Resource
    private AiTermService aiTermService;

    @Resource
    private AiPermissionInjector aiPermissionInjector;

    @Resource
    private AiQueryRiskChecker aiQueryRiskChecker;

    @Resource
    private AiContextFilterEnhancer aiContextFilterEnhancer;

    @Resource
    private AiRuntimeMetricsService aiRuntimeMetricsService;

    @Resource
    private ClarificationEngine clarificationEngine;

    @Override
    public AiAnalysisRspDTO analysis(AiAnalysisReqDTO reqDTO) {
        long startTime = System.currentTimeMillis();
        // 获取本轮请求所属的短期会话上下文。
        // 1. 前端传了 conversationId：说明用户在继续同一轮对话，从数据库/内存里恢复上一次查询的业务域、查询计划、结果摘要等上下文。
        // 2. 前端没传 conversationId：说明这是新会话，服务端生成一个新的 conversationId，并返回给前端后续继续使用。
        // 后面的意图识别、代词/省略条件补全、查询计划生成都会依赖这个 context。
        AiConversationContextDTO context = aiConversationMemoryService.getOrCreate(reqDTO.getConversationId());
        // 先保存用户原始问题，便于后续审计、排查和多轮对话补上下文。
        aiConversationMemoryService.saveUserMessage(context.getConversationId(), reqDTO.getQuestion());
        try {
            log.info("AI智能分析开始，conversationId={}，question={}, chartType={}", context.getConversationId(), reqDTO.getQuestion(), reqDTO.getChartType());
            // 业务节点 1：意图识别。
            // 先判断用户这句话到底属于哪类请求：
            // - 普通聊天：例如“你是谁”“怎么使用”
            // - 业务查询：例如“查询项目数量”
            // - 工作流查询：例如“我的待办”
            // - 报表导出：例如“导出刚才的数据”
            // - 未知意图：模型或规则都无法确定时，返回澄清提示。
            String intent = intentRouter.route(reqDTO.getQuestion(), context);
            log.info("AI意图路由完成，conversationId={}，intent={}，question={}", context.getConversationId(), intent, reqDTO.getQuestion());

            // 业务节点 2：普通聊天分支。
            // 这类问题不走数据库查询，也不生成 SQL，只调用大模型做普通回答。
            if (IntentRouter.INTENT_CHAT.equals(intent)) {
                AiAnalysisRspDTO rspDTO = answerGeneralQuestion(reqDTO.getQuestion());
                rspDTO.setConversationId(context.getConversationId());
                markSuccess(rspDTO);
                // 普通回答也写入会话记忆，后续用户追问“刚才你说的...”时可以有上下文。
                aiConversationMemoryService.updateAfterAnswer(context.getConversationId(), reqDTO.getQuestion(), rspDTO.getAnswer());
                // 写审计日志，标记本次是 CHAT 场景，方便后续统计和排查。
                audit(context.getConversationId(), "CHAT", intent, null, reqDTO.getQuestion(), null, 0, System.currentTimeMillis() - startTime);
                return finishSuccess(rspDTO, startTime);
            }

            // 业务节点 3：未知意图分支。
            // 用户输入太模糊时，不强行猜测、不生成 SQL，而是提示用户补充说明。
            if (IntentRouter.INTENT_UNKNOWN.equals(intent)) {
                AiAnalysisRspDTO rspDTO = clarifyIntent(context, reqDTO.getQuestion(), intent);
                markFailure(rspDTO, AiErrorCode.AI_INTENT_UNKNOWN);
                aiConversationMemoryService.updateAfterAnswer(context.getConversationId(), reqDTO.getQuestion(), rspDTO.getAnswer());
                audit(context.getConversationId(), "UNKNOWN", intent, null, reqDTO.getQuestion(), null, 0, System.currentTimeMillis() - startTime);
                return finishFailure(rspDTO, startTime);
            }

            // 业务节点 4：工作流/审批类请求分支。
            // 例如“我的待办”“已办流程”等，这类请求优先走现有业务接口能力，不走自由 SQL 查询。
            if (IntentRouter.INTENT_WORKFLOW.equals(intent)) {
                AiAnalysisRspDTO rspDTO = analysisWorkflow(reqDTO, context.getConversationId());
                rspDTO.setConversationId(context.getConversationId());
                markSuccess(rspDTO);
                aiConversationMemoryService.updateAfterAnswer(context.getConversationId(), reqDTO.getQuestion(), rspDTO.getAnswer());
                audit(context.getConversationId(), "WORKFLOW", intent, null, reqDTO.getQuestion(), null, rowCount(rspDTO), System.currentTimeMillis() - startTime);
                return finishSuccess(rspDTO, startTime);
            }

            // 业务节点 5：报表导出意图分支。
            // 当前主流程只负责提示导出入口/方式，不在这里直接生成下载文件，避免查询和导出职责混在一起。
            if (IntentRouter.INTENT_REPORT_EXPORT.equals(intent)) {
                AiAnalysisRspDTO rspDTO = reportExportHint(context.getConversationId());
                markSuccess(rspDTO);
                aiConversationMemoryService.updateAfterAnswer(context.getConversationId(), reqDTO.getQuestion(), rspDTO.getAnswer());
                audit(context.getConversationId(), "REPORT_EXPORT_HINT", intent, null, reqDTO.getQuestion(), null, 0, System.currentTimeMillis() - startTime);
                return finishSuccess(rspDTO, startTime);
            }

            // 业务节点 6：受控业务 Agent 分支。
            // 复杂跨模块问题先走固定业务编排，例如“实施方案执行进度分析”“实施方案全链路明细”。
            // Agent 分支只暴露少量业务任务，不让模型在大量底层接口里自由规划。
            if (aiAgentAnalysisService.support(reqDTO)) {
                AiAnalysisRspDTO rspDTO = aiAgentAnalysisService.analysis(reqDTO);
                rspDTO.setConversationId(context.getConversationId());
                if ("clarify".equals(rspDTO.getAction())) {
                    markFailure(rspDTO, AiErrorCode.AI_INTENT_UNKNOWN);
                    aiConversationMemoryService.updateAfterAnswer(context.getConversationId(), reqDTO.getQuestion(), rspDTO.getAnswer());
                    auditFailure(context.getConversationId(), "AGENT_CLARIFY", intent, null, reqDTO.getQuestion(), null,
                            rspDTO.getAnswer(), System.currentTimeMillis() - startTime);
                    return finishFailure(rspDTO, startTime);
                }
                markSuccess(rspDTO);
                aiConversationMemoryService.updateAfterAnswer(context.getConversationId(), reqDTO.getQuestion(), rspDTO.getAnswer());
                audit(context.getConversationId(), "AGENT_QUERY", intent, null, reqDTO.getQuestion(), "controlledBusinessAgent",
                        rowCount(rspDTO), System.currentTimeMillis() - startTime);
                return finishSuccess(rspDTO, startTime);
            }

            // 业务节点 7：多步骤查询分支。
            // 如果用户问题包含多个查询目标，或者需要拆成多次查询再汇总，就交给多步骤查询服务处理。
            // 例如“分别统计项目、样品、实施方案数量”这种不适合一次 SQL 或单个工具解决的问题。
            if (aiMultiAnalysisService.support(reqDTO)) {
                AiAnalysisRspDTO rspDTO = aiMultiAnalysisService.analysis(reqDTO);
                rspDTO.setConversationId(context.getConversationId());
                markSuccess(rspDTO);
                aiConversationMemoryService.updateAfterAnswer(context.getConversationId(), reqDTO.getQuestion(), rspDTO.getAnswer());
                audit(context.getConversationId(), "MULTI_QUERY", intent, null, reqDTO.getQuestion(), "multiStepQuery", totalRowCount(rspDTO), System.currentTimeMillis() - startTime);
                log.info("AI智能分析命中多步骤查询，totalCost={}ms，question={}", System.currentTimeMillis() - startTime, reqDTO.getQuestion());
                return finishSuccess(rspDTO, startTime);
            }

            // 业务节点 8：只读业务接口工具调用分支。
            // 如果后台 ai_api_registry / ai_api_param 中启用了合适的只读接口，优先调用现有业务接口。
            // 只有找不到合适工具时，后面才进入 QueryPlan -> SQL 查询链路。
            if (aiCommandAnalysisService.support(reqDTO)) {
                AiAnalysisRspDTO rspDTO = aiCommandAnalysisService.analysis(reqDTO);
                rspDTO.setConversationId(context.getConversationId());
                if ("clarify".equals(rspDTO.getAction())) {
                    markFailure(rspDTO, AiErrorCode.AI_INTENT_UNKNOWN);
                    aiConversationMemoryService.updateAfterAnswer(context.getConversationId(), reqDTO.getQuestion(), rspDTO.getAnswer());
                    auditFailure(context.getConversationId(), "TOOL_CLARIFY", intent, null, reqDTO.getQuestion(), null,
                            rspDTO.getAnswer(), System.currentTimeMillis() - startTime);
                    return finishFailure(rspDTO, startTime);
                }
                markSuccess(rspDTO);
                aiConversationMemoryService.updateAfterAnswer(context.getConversationId(), reqDTO.getQuestion(), rspDTO.getAnswer());
                audit(context.getConversationId(), "TOOL_CALL", intent, null, reqDTO.getQuestion(), null, rowCount(rspDTO), System.currentTimeMillis() - startTime);
                return finishSuccess(rspDTO, startTime);
            }

            // 业务节点 8：查询计划生成。
            // 让模型把自然语言问题转换成结构化 QueryPlan，而不是直接让模型生成 SQL。
            // QueryPlan 里通常包含：业务域 domain、查询类型 queryType、指标 metrics、维度 dimensions、过滤条件 filters、排序、limit 等。
            long planStartTime = System.currentTimeMillis();
            AiQueryPlanDTO plan = aiQueryPlanService.generate(reqDTO.getQuestion(), reqDTO.getChartType(), context);
            log.info("AI查询计划生成完成，cost={}ms，domain={}，queryType={}，chartType={}",
                    System.currentTimeMillis() - planStartTime, plan.getDomain(), plan.getQueryType(), plan.getChartType());

            // 业务节点 8：业务域 schema 加载。
            // 根据模型识别出的 domain 找到后端维护的白名单配置。
            // 这里决定模型“最多能查哪些表、哪些字段、哪些指标”，避免模型越权访问未知表。
            AiDomainSchema schema = aiDomainRegistry.getRequired(plan.getDomain());

            // 业务节点 9：上下文增强。
            // 处理用户省略信息和代词，例如：
            // - “这些项目的实施方案”需要结合上一次查询结果补项目范围
            // - “刚才的数据只保留描述列”需要结合 lastResultSnapshot
            // 这一步会在安全边界内补充过滤条件，但不会绕过后面的白名单校验。
            aiContextFilterEnhancer.enhance(reqDTO.getQuestion(), context, plan, schema);

            // 业务节点 10：权限条件注入。
            // 当前如果没有细粒度数据权限，这里可以是空实现；以后有部门/项目/角色数据范围时，在这里统一加过滤条件。
            aiPermissionInjector.inject(reqDTO, plan, schema);

            // 业务节点 11：查询计划安全校验。
            // 所有字段、指标、过滤条件、排序、limit 都必须二次校验，不能直接信任模型输出。
            // 校验失败会抛 BusinessException，主流程会返回可读错误，不会执行 SQL。
            long validateStartTime = System.currentTimeMillis();
            aiQueryPlanValidator.validate(plan, schema);

            // 业务节点 12：查询风险检查。
            // 控制大结果集、无过滤明细查询、危险查询类型等，避免一次自然语言请求拖垮数据库。
            aiQueryRiskChecker.check(plan, schema);
            log.info("AI查询计划校验完成，cost={}ms，domain={}", System.currentTimeMillis() - validateStartTime, plan.getDomain());

            // 业务节点 13：SQL 构建和查询执行。
            // 执行器只会基于后端 schema 白名单组装 SQL，不使用模型直接生成的 SQL。
            // 返回结果包括：表格数据、图表数据、实际执行 SQL、SQL 参数等。
            long executeStartTime = System.currentTimeMillis();
            AiAnalysisRspDTO rspDTO = aiQueryExecutorService.execute(plan, schema);
            rspDTO.setConversationId(context.getConversationId());
            markSuccess(rspDTO);
            log.info("AI查询执行完成，cost={}ms，rowCount={}，chartCount={}",
                    System.currentTimeMillis() - executeStartTime, rowCount(rspDTO), rspDTO.getCharts().size());

            // 业务节点 14：响应结果组装。
            // 把查询计划和自然语言回答一起返回给前端，前端可以展示表格、图表、查询解释。
            rspDTO.setQueryPlan(plan);
            rspDTO.setAnswer(buildAnswer(plan, schema, rspDTO));

            // 业务节点 15：更新会话记忆。
            // 保存本次 queryPlan、业务域、结果摘要、结果快照。
            // 后续用户说“刚才那个”“这些数据”“只保留某几列”时，就依赖这里保存的上下文。
            aiConversationMemoryService.updateAfterQuery(context.getConversationId(), reqDTO.getQuestion(), plan, rspDTO, confirmedTerms(reqDTO.getQuestion()));

            // 业务节点 16：写查询审计日志。
            // 审计内容包括意图、业务域、原始问题、查询计划、实际 SQL、参数、返回行数、耗时。
            // 生产环境排查“谁查了什么数据”主要靠这条日志。
            audit(context.getConversationId(), "QUERY", intent, plan.getDomain(), reqDTO.getQuestion(),
                    JSONUtil.toJsonStr(plan), rspDTO.getExecutedSql(), rspDTO.getExecutedSqlParams(),
                    rowCount(rspDTO), System.currentTimeMillis() - startTime);
            log.info("AI智能分析结束，totalCost={}ms，question={}", System.currentTimeMillis() - startTime, reqDTO.getQuestion());
            return finishSuccess(rspDTO, startTime);
        } catch (AiGeneralChatException e) {
            // 异常节点 1：查询计划生成阶段判断该问题更像普通聊天。
            // 例如用户输入不像业务查询，模型/规则主动抛出该异常，主流程切换到普通问答。
            log.info("AI智能分析切换普通问答，question={}", reqDTO.getQuestion());
            AiAnalysisRspDTO rspDTO = answerGeneralQuestion(reqDTO.getQuestion());
            rspDTO.setConversationId(context.getConversationId());
            markSuccess(rspDTO);
            aiConversationMemoryService.updateAfterAnswer(context.getConversationId(), reqDTO.getQuestion(), rspDTO.getAnswer());
            audit(context.getConversationId(), "CHAT", IntentRouter.INTENT_CHAT, null, reqDTO.getQuestion(), null, 0, System.currentTimeMillis() - startTime);
            log.info("AI普通问答结束，totalCost={}ms，question={}", System.currentTimeMillis() - startTime, reqDTO.getQuestion());
            return finishSuccess(rspDTO, startTime);
        } catch (BusinessException e) {
            // 异常节点 2：业务可预期拒绝。
            // 例如字段不存在、业务域不存在、查询风险过高、意图需要澄清等。
            // 这类错误不当成系统异常抛给前端，而是返回一段可读提示。
            log.warn("AI智能分析业务校验失败，totalCost={}ms，conversationId={}，question={}，message={}",
                    System.currentTimeMillis() - startTime, context.getConversationId(), reqDTO.getQuestion(), e.getMessage());
            AiAnalysisRspDTO rspDTO = new AiAnalysisRspDTO();
            rspDTO.setConversationId(context.getConversationId());
            rspDTO.setAnswer(e.getMessage());
            markFailure(rspDTO, classifyBusinessError(e.getMessage()));
            aiConversationMemoryService.updateAfterAnswer(context.getConversationId(), reqDTO.getQuestion(), rspDTO.getAnswer());
            auditFailure(context.getConversationId(), "BUSINESS_REJECT", null, null, reqDTO.getQuestion(), null, e.getMessage(), System.currentTimeMillis() - startTime);
            return finishFailure(rspDTO, startTime);
        } catch (Exception e) {
            // 异常节点 3：非预期系统异常。
            // 例如数据库连接异常、代码空指针、外部服务不可用等。
            // 这里保留异常向上抛，由全局异常处理统一返回系统错误，并在日志里保留堆栈。
            log.error("AI智能分析失败，totalCost={}ms，question={}，chartType={}",
                    System.currentTimeMillis() - startTime, reqDTO.getQuestion(), reqDTO.getChartType(), e);
            aiRuntimeMetricsService.record("analysis", false, System.currentTimeMillis() - startTime);
            throw e;
        }
    }

    private AiAnalysisRspDTO clarifyIntent(AiConversationContextDTO context, String question, String intent) {
        ClarificationDecision decision = clarificationEngine.decide(ClarificationRequest.of(
                context.getConversationId(), question, intent, context
        ));
        AiAnalysisRspDTO rspDTO = new AiAnalysisRspDTO();
        rspDTO.setConversationId(context.getConversationId());
        rspDTO.setAction("REJECTED".equals(decision.getState().name()) ? "reject" : "clarify");
        rspDTO.setClarifyType(decision.getClarifyType());
        rspDTO.setNextQuestion(decision.getQuestion());
        List<String> options = toClarifyOptions(decision);
        rspDTO.setClarifyOptions(options);
        rspDTO.setAnswer(buildClarifyAnswer(decision.getMessage(), decision.getQuestion(), options));
        return rspDTO;
    }

    private List<String> toClarifyOptions(ClarificationDecision decision) {
        List<String> options = new java.util.ArrayList<>();
        if (decision == null || decision.getCandidates() == null) {
            return options;
        }
        for (ClarificationCandidate candidate : decision.getCandidates()) {
            if (candidate == null) {
                continue;
            }
            Object templates = candidate.getPayload().get("templates");
            if (templates instanceof List) {
                for (Object template : (List<?>) templates) {
                    if (template != null) {
                        options.add(String.valueOf(template));
                    }
                }
            } else if (candidate.getName() != null) {
                options.add(candidate.getName());
            }
        }
        return options;
    }

    private String buildClarifyAnswer(String reason, String nextQuestion, List<String> options) {
        StringBuilder answer = new StringBuilder(reason).append(nextQuestion);
        if (options == null || options.isEmpty()) {
            return answer.toString();
        }
        answer.append("可以这样问：");
        for (int i = 0; i < options.size(); i++) {
            if (i > 0) {
                answer.append("；");
            }
            answer.append(options.get(i));
        }
        return answer.toString();
    }

    private void audit(String conversationId, String scenario, String intent, String domain, String question,
                       String queryPlan, Integer rowCount, Long costMillis) {
        audit(conversationId, scenario, intent, domain, question, queryPlan, null, null, rowCount, costMillis);
    }

    private void audit(String conversationId, String scenario, String intent, String domain, String question,
                       String queryPlan, String sqlText, String sqlParams, Integer rowCount, Long costMillis) {
        AiAuditLogDTO logDTO = new AiAuditLogDTO();
        logDTO.setConversationId(conversationId);
        logDTO.setScenario(scenario);
        logDTO.setIntent(intent);
        logDTO.setDomain(domain);
        logDTO.setQuestion(question);
        logDTO.setQueryPlan(queryPlan);
        logDTO.setSqlText(sqlText);
        logDTO.setSqlParams(sqlParams);
        logDTO.setRowCount(rowCount);
        logDTO.setCostMillis(costMillis);
        logDTO.setSuccess(true);
        aiAuditLogService.log(logDTO);
    }

    private void auditFailure(String conversationId, String scenario, String intent, String domain, String question,
                              String queryPlan, String errorMessage, Long costMillis) {
        AiAuditLogDTO logDTO = new AiAuditLogDTO();
        logDTO.setConversationId(conversationId);
        logDTO.setScenario(scenario);
        logDTO.setIntent(intent);
        logDTO.setDomain(domain);
        logDTO.setQuestion(question);
        logDTO.setQueryPlan(queryPlan);
        logDTO.setRowCount(0);
        logDTO.setCostMillis(costMillis);
        logDTO.setSuccess(false);
        logDTO.setErrorMessage(errorMessage);
        aiAuditLogService.log(logDTO);
    }

    private AiAnalysisRspDTO analysisWorkflow(AiAnalysisReqDTO reqDTO, String conversationId) {
        try {
            return aiCommandAnalysisService.analysis(reqDTO);
        } catch (BusinessException e) {
            log.warn("AI工作流命令接口不可用，conversationId={}，question={}，message={}",
                    conversationId, reqDTO.getQuestion(), e.getMessage());
            AiAnalysisRspDTO rspDTO = new AiAnalysisRspDTO();
            rspDTO.setConversationId(conversationId);
            rspDTO.setAnswer("当前工作流类能力还没有配置可调用的后端命令。可以先使用业务数据查询，或在配置中补充 workflow commands 后再试。");
            return rspDTO;
        }
    }

    private AiAnalysisRspDTO reportExportHint(String conversationId) {
        AiAnalysisRspDTO rspDTO = new AiAnalysisRspDTO();
        rspDTO.setConversationId(conversationId);
        rspDTO.setAnswer("这是导出类请求，请先完成自然语言查询，再使用对应业务页面的导出按钮导出数据。");
        return rspDTO;
    }

    private Map<String, String> confirmedTerms(String question) {
        Map<String, String> terms = new LinkedHashMap<>();
        List<AiProperties.Term> recalledTerms = aiTermService.recall(question);
        for (AiProperties.Term term : recalledTerms) {
            terms.put(term.getPhrase(), term.getMeaning());
        }
        return terms;
    }

    private int rowCount(AiAnalysisRspDTO rspDTO) {
        if (rspDTO == null || rspDTO.getTables().isEmpty()) {
            return 0;
        }
        return rspDTO.getTables().get(0).getData().size();
    }

    private int totalRowCount(AiAnalysisRspDTO rspDTO) {
        if (rspDTO == null || rspDTO.getTables().isEmpty()) {
            return 0;
        }
        int total = 0;
        for (AiTableDTO table : rspDTO.getTables()) {
            total += table.getData().size();
        }
        return total;
    }

    private String buildAnswer(AiQueryPlanDTO plan, AiDomainSchema schema, AiAnalysisRspDTO rspDTO) {
        int rows = rowCount(rspDTO);
        if (rows == 0) {
            return "没有查到符合条件的数据。可以换个时间范围或查询条件再试。";
        }
        if (QUERY_TYPE_DETAIL.equals(plan.getQueryType())) {
            return "查到 " + rows + " 条记录，已按表格展示。";
        }
        if (plan.getDimensions().isEmpty() && !plan.getMetrics().isEmpty()) {
            return buildSingleMetricAnswer(plan, schema, rspDTO);
        }
        if (rspDTO.getCharts().isEmpty()) {
            return "统计完成，共返回 " + rows + " 行结果，已按表格展示。";
        }
        return "统计完成，共返回 " + rows + " 行结果，已生成图表并保留表格明细。";
    }

    private String buildSingleMetricAnswer(AiQueryPlanDTO plan, AiDomainSchema schema, AiAnalysisRspDTO rspDTO) {
        AiTableDTO table = rspDTO.getTables().isEmpty() ? null : rspDTO.getTables().get(0);
        if (table == null || table.getData().isEmpty()) {
            return "统计完成。";
        }
        String metric = plan.getMetrics().get(0);
        AiMetricSchema metricSchema = schema.getMetrics().get(metric);
        String metricLabel = metricSchema == null ? metric : metricSchema.getLabel();
        Map<String, Object> firstRow = table.getData().get(0);
        Object value = firstRow.get(metric);
        return "统计结果：" + metricLabel + "为 " + (value == null ? 0 : value) + "。";
    }

    private AiAnalysisRspDTO answerGeneralQuestion(String question) {
        long startTime = System.currentTimeMillis();
        String answer = chatService.generalChat(question);
        log.info("AI普通问答模型返回完成，cost={}ms", System.currentTimeMillis() - startTime);
        AiAnalysisRspDTO rspDTO = new AiAnalysisRspDTO();
        rspDTO.setAnswer(answer);
        return rspDTO;
    }

    private AiAnalysisRspDTO finishSuccess(AiAnalysisRspDTO rspDTO, long startTime) {
        markSuccess(rspDTO);
        aiRuntimeMetricsService.record("analysis", true, System.currentTimeMillis() - startTime);
        return rspDTO;
    }

    private AiAnalysisRspDTO finishFailure(AiAnalysisRspDTO rspDTO, long startTime) {
        aiRuntimeMetricsService.record("analysis", false, System.currentTimeMillis() - startTime);
        return rspDTO;
    }

    private void markSuccess(AiAnalysisRspDTO rspDTO) {
        rspDTO.setSuccess(Boolean.TRUE);
        rspDTO.setErrorCode(AiErrorCode.SUCCESS.getCode());
    }

    private void markFailure(AiAnalysisRspDTO rspDTO, AiErrorCode errorCode) {
        rspDTO.setSuccess(Boolean.FALSE);
        rspDTO.setErrorCode(errorCode.getCode());
    }

    private AiErrorCode classifyBusinessError(String message) {
        if (message == null) {
            return AiErrorCode.AI_SYSTEM_ERROR;
        }
        if (message.contains("频繁") || message.contains("请求较多")) {
            return AiErrorCode.AI_RATE_LIMITED;
        }
        if (message.contains("模型")) {
            return AiErrorCode.AI_LLM_ERROR;
        }
        if (message.contains("数据库") || message.contains("JdbcTemplate") || message.contains("DataSource")) {
            return AiErrorCode.AI_DB_ERROR;
        }
        if (message.contains("风险") || message.contains("敏感") || message.contains("禁止") || message.contains("范围过大")
                || message.contains("过多") || message.contains("过长") || message.contains("过滤条件")) {
            return AiErrorCode.AI_QUERY_RISK;
        }
        if (message.contains("业务域") || message.contains("字段") || message.contains("指标") || message.contains("配置")) {
            return AiErrorCode.AI_CONFIG_ERROR;
        }
        if (message.contains("意图") || message.contains("不能确定") || message.contains("未能识别")) {
            return AiErrorCode.AI_INTENT_UNKNOWN;
        }
        return AiErrorCode.AI_SYSTEM_ERROR;
    }

}
