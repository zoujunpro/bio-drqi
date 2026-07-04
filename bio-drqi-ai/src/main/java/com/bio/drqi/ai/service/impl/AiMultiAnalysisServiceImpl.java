package com.bio.drqi.ai.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.dto.plan.AiReportPlanDTO;
import com.bio.drqi.ai.dto.plan.AiReportStepDTO;
import com.bio.drqi.ai.dto.req.AiAnalysisReqDTO;
import com.bio.drqi.ai.dto.rsp.AiAnalysisRspDTO;
import com.bio.drqi.ai.dto.rsp.AiTableDTO;
import com.bio.drqi.ai.registry.AiDomainRegistry;
import com.bio.drqi.ai.schema.AiDomainSchema;
import com.bio.drqi.ai.service.AiMultiAnalysisService;
import com.bio.drqi.ai.service.AiQueryExecutorService;
import com.bio.drqi.ai.service.AiQueryPlanValidator;
import com.bio.drqi.ai.service.AiQueryRiskChecker;
import com.bio.drqi.ai.service.AiReportPlanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class AiMultiAnalysisServiceImpl implements AiMultiAnalysisService {

    private static final List<String> MULTI_RESULT_WORDS = Arrays.asList("同时", "以及", "并且", "和", "、", "明细", "excel", "Excel", "表格");

    @Resource
    private AiReportPlanService aiReportPlanService;

    @Resource
    private AiDomainRegistry aiDomainRegistry;

    @Resource
    private AiQueryPlanValidator aiQueryPlanValidator;

    @Resource
    private AiQueryExecutorService aiQueryExecutorService;

    @Resource
    private AiQueryRiskChecker aiQueryRiskChecker;

    @Override
    public boolean support(AiAnalysisReqDTO reqDTO) {
        String question = reqDTO == null ? null : reqDTO.getQuestion();
        if (StrUtil.isBlank(question)) {
            return false;
        }
        int hitCount = 0;
        for (String word : MULTI_RESULT_WORDS) {
            if (question.contains(word)) {
                hitCount++;
            }
        }
        return hitCount >= 2;
    }

    @Override
    public AiAnalysisRspDTO analysis(AiAnalysisReqDTO reqDTO) {
        long startTime = System.currentTimeMillis();
        AiReportPlanDTO reportPlan = aiReportPlanService.generate(reqDTO.getQuestion());
        if (reportPlan == null || CollectionUtil.isEmpty(reportPlan.getSteps())) {
            throw new BusinessException("AI未能生成多步骤查询计划");
        }

        AiAnalysisRspDTO rspDTO = new AiAnalysisRspDTO();
        for (AiReportStepDTO step : reportPlan.getSteps()) {
            AiQueryPlanDTO queryPlan = step.getQueryPlan();
            if (queryPlan == null) {
                continue;
            }
            AiDomainSchema schema = aiDomainRegistry.getRequired(queryPlan.getDomain());
            aiQueryPlanValidator.validate(queryPlan, schema);
            aiQueryRiskChecker.check(queryPlan, schema);
            AiAnalysisRspDTO stepResult = aiQueryExecutorService.execute(queryPlan, schema);
            if (CollectionUtil.isEmpty(stepResult.getTables())) {
                continue;
            }
            AiTableDTO table = stepResult.getTables().get(0);
            table.setTitle(StrUtil.blankToDefault(step.getSheetName(), table.getTitle()));
            rspDTO.getTables().add(table);
            rspDTO.getCharts().addAll(stepResult.getCharts());
        }

        rspDTO.setAnswer(buildAnswer(reportPlan, rspDTO));
        log.info("AI多步骤分析完成，cost={}ms，stepCount={}，tableCount={}",
                System.currentTimeMillis() - startTime, reportPlan.getSteps().size(), rspDTO.getTables().size());
        return rspDTO;
    }

    private String buildAnswer(AiReportPlanDTO reportPlan, AiAnalysisRspDTO rspDTO) {
        String reportName = StrUtil.blankToDefault(reportPlan.getReportName(), "查询结果");
        int tableCount = rspDTO.getTables().size();
        if (tableCount == 0) {
            return reportName + "没有查到符合条件的数据。";
        }
        return reportName + "已完成，结果分为 " + tableCount + " 张表展示，可按需下载 Excel。";
    }
}
