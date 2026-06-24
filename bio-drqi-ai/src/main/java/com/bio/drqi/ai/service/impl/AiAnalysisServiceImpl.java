package com.bio.drqi.ai.service.impl;

import cn.hutool.json.JSONUtil;
import com.bio.drqi.ai.client.LlmClient;
import com.bio.drqi.ai.dto.llm.LlmChatMessageDTO;
import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.dto.req.AiAnalysisReqDTO;
import com.bio.drqi.ai.dto.rsp.AiAnalysisRspDTO;
import com.bio.drqi.ai.dto.rsp.AiTableDTO;
import com.bio.drqi.ai.exception.AiGeneralChatException;
import com.bio.drqi.ai.registry.AiDomainRegistry;
import com.bio.drqi.ai.schema.AiDomainSchema;
import com.bio.drqi.ai.schema.AiMetricSchema;
import com.bio.drqi.ai.service.AiAnalysisService;
import com.bio.drqi.ai.service.AiAuditLogService;
import com.bio.drqi.ai.service.AiMultiAnalysisService;
import com.bio.drqi.ai.service.AiQueryExecutorService;
import com.bio.drqi.ai.service.AiQueryPlanService;
import com.bio.drqi.ai.service.AiQueryPlanValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
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
    private LlmClient llmClient;

    @Resource
    private AiMultiAnalysisService aiMultiAnalysisService;

    @Override
    public AiAnalysisRspDTO analysis(AiAnalysisReqDTO reqDTO) {
        long startTime = System.currentTimeMillis();
        try {
            log.info("AI智能分析开始，question={}, chartType={}", reqDTO.getQuestion(), reqDTO.getChartType());
            if (aiMultiAnalysisService.support(reqDTO)) {
                AiAnalysisRspDTO rspDTO = aiMultiAnalysisService.analysis(reqDTO);
                aiAuditLogService.log("MULTI_QUERY", reqDTO.getQuestion(), "multiStepQuery", totalRowCount(rspDTO), System.currentTimeMillis() - startTime);
                log.info("AI智能分析命中多步骤查询，totalCost={}ms，question={}", System.currentTimeMillis() - startTime, reqDTO.getQuestion());
                return rspDTO;
            }

            // 第一步：让模型把自然语言问题转换成结构化查询计划。
            long planStartTime = System.currentTimeMillis();
            AiQueryPlanDTO plan = aiQueryPlanService.generate(reqDTO.getQuestion(), reqDTO.getChartType());
            log.info("AI查询计划生成完成，cost={}ms，domain={}，queryType={}，chartType={}",
                    System.currentTimeMillis() - planStartTime, plan.getDomain(), plan.getQueryType(), plan.getChartType());

            // 第二步：根据计划里的 domain 找到后端维护的业务域白名单配置。
            AiDomainSchema schema = aiDomainRegistry.getRequired(plan.getDomain());

            // 第三步：所有字段、指标、过滤条件、排序都必须二次校验，不能直接信任模型输出。
            long validateStartTime = System.currentTimeMillis();
            aiQueryPlanValidator.validate(plan, schema);
            log.info("AI查询计划校验完成，cost={}ms，domain={}", System.currentTimeMillis() - validateStartTime, plan.getDomain());

            // 第四步：按后端 schema 把查询计划转换成白名单 SQL 并执行，返回表格和图表数据。
            long executeStartTime = System.currentTimeMillis();
            AiAnalysisRspDTO rspDTO = aiQueryExecutorService.execute(plan, schema);
            log.info("AI查询执行完成，cost={}ms，rowCount={}，chartCount={}",
                    System.currentTimeMillis() - executeStartTime, rowCount(rspDTO), rspDTO.getCharts().size());
            rspDTO.setQueryPlan(plan);
            rspDTO.setAnswer(buildAnswer(plan, schema, rspDTO));
            aiAuditLogService.log("QUERY", reqDTO.getQuestion(), JSONUtil.toJsonStr(plan), rowCount(rspDTO), System.currentTimeMillis() - startTime);
            log.info("AI智能分析结束，totalCost={}ms，question={}", System.currentTimeMillis() - startTime, reqDTO.getQuestion());
            return rspDTO;
        } catch (AiGeneralChatException e) {
            log.info("AI智能分析切换普通问答，question={}", reqDTO.getQuestion());
            AiAnalysisRspDTO rspDTO = answerGeneralQuestion(reqDTO.getQuestion());
            aiAuditLogService.log("CHAT", reqDTO.getQuestion(), null, 0, System.currentTimeMillis() - startTime);
            log.info("AI普通问答结束，totalCost={}ms，question={}", System.currentTimeMillis() - startTime, reqDTO.getQuestion());
            return rspDTO;
        } catch (Exception e) {
            log.error("AI智能分析失败，totalCost={}ms，question={}，chartType={}",
                    System.currentTimeMillis() - startTime, reqDTO.getQuestion(), reqDTO.getChartType(), e);
            throw e;
        }
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
        String answer = llmClient.chat(Arrays.asList(
                new LlmChatMessageDTO("system", buildGeneralAnswerPrompt()),
                new LlmChatMessageDTO("user", question)
        ));
        log.info("AI普通问答模型返回完成，cost={}ms", System.currentTimeMillis() - startTime);
        AiAnalysisRspDTO rspDTO = new AiAnalysisRspDTO();
        rspDTO.setAnswer(answer);
        return rspDTO;
    }

    private String buildGeneralAnswerPrompt() {
        return "你是本系统的AI助手。用户问题如果不是系统数据查询，也要正常回答。"
                + "回答要友好、明白、简洁，使用中文。"
                + "不要输出JSON，不要输出查询计划，不要编造本系统数据库里不存在的数据。"
                + "如果用户的问题需要查询系统实时数据才能确定，请说明需要指定查询范围或让用户换成数据查询问题。"
                + "如果问题是普通知识、解释、操作建议或闲聊，直接给出自然语言回答。";
    }
}
