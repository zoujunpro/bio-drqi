package com.bio.drqi.ai.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.dto.plan.AiReportAggregationDTO;
import com.bio.drqi.ai.dto.plan.AiReportPlanDTO;
import com.bio.drqi.ai.dto.plan.AiReportStepDTO;
import com.bio.drqi.ai.dto.req.AiAnalysisReqDTO;
import com.bio.drqi.ai.dto.rsp.AiAnalysisRspDTO;
import com.bio.drqi.ai.dto.rsp.AiTableColumnDTO;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

@Service
@Slf4j
public class AiMultiAnalysisServiceImpl implements AiMultiAnalysisService {

    private static final int MAX_STEP_COUNT = 6;

    private static final List<String> MULTI_CONNECT_WORDS = Arrays.asList("同时", "以及", "并且", "分别", "各自", "和", "、");

    private static final List<String> ANALYSIS_WORDS = Arrays.asList("分析", "统计", "汇总", "对比", "比较", "趋势", "占比", "分布", "排名", "排行", "明细", "报表", "表格", "excel", "Excel");

    private static final List<String> DOMAIN_WORDS = Arrays.asList("项目", "实施方案", "方案", "取样", "样品", "种子", "植株", "转化", "耗材", "库存", "工单");

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
        int connectCount = hitCount(question, MULTI_CONNECT_WORDS);
        int analysisCount = hitCount(question, ANALYSIS_WORDS);
        int domainCount = hitCount(question, DOMAIN_WORDS);
        int score = connectCount * 2 + analysisCount * 2 + Math.max(0, domainCount - 1);

        // 明确要求“分别/各自/同时”的分析型问题，优先按多步骤报表处理。
        if ((question.contains("分别") || question.contains("各自") || question.contains("同时")) && analysisCount > 0) {
            return true;
        }
        // 多个业务对象叠加统计/明细/报表，一般不是单接口或单SQL能稳定表达的。
        return score >= 5 || (domainCount >= 3 && analysisCount > 0);
    }

    @Override
    public AiAnalysisRspDTO analysis(AiAnalysisReqDTO reqDTO) {
        long startTime = System.currentTimeMillis();
        AiReportPlanDTO reportPlan = aiReportPlanService.generate(reqDTO.getQuestion());
        validateReportPlan(reportPlan);

        AiAnalysisRspDTO rspDTO = new AiAnalysisRspDTO();
        Map<String, AiTableDTO> stepTables = new LinkedHashMap<>();
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
            stepTables.put(step.getStepCode(), table);
            rspDTO.getTables().add(table);
            rspDTO.getCharts().addAll(stepResult.getCharts());
        }

        appendAggregations(reportPlan, rspDTO, stepTables);
        rspDTO.setAnswer(buildAnswer(reportPlan, rspDTO));
        rspDTO.setNextQuestion("可以继续让我按项目、时间、负责人或状态进一步筛选。");
        log.info("AI多步骤分析完成，cost={}ms，stepCount={}，tableCount={}",
                System.currentTimeMillis() - startTime, reportPlan.getSteps().size(), rspDTO.getTables().size());
        return rspDTO;
    }

    private void validateReportPlan(AiReportPlanDTO reportPlan) {
        if (reportPlan == null || CollectionUtil.isEmpty(reportPlan.getSteps())) {
            throw new BusinessException("AI未能生成多步骤查询计划");
        }
        if (reportPlan.getSteps().size() > MAX_STEP_COUNT) {
            throw new BusinessException("本次分析拆分步骤过多，请缩小查询范围后重试");
        }
        Map<String, Boolean> codeMap = new HashMap<>();
        for (int i = 0; i < reportPlan.getSteps().size(); i++) {
            AiReportStepDTO step = reportPlan.getSteps().get(i);
            if (step == null || step.getQueryPlan() == null) {
                throw new BusinessException("AI生成的多步骤查询计划不完整");
            }
            if (StrUtil.isBlank(step.getStepCode())) {
                step.setStepCode("step_" + (i + 1));
            }
            if (codeMap.containsKey(step.getStepCode())) {
                step.setStepCode(step.getStepCode() + "_" + (i + 1));
            }
            codeMap.put(step.getStepCode(), Boolean.TRUE);
        }
    }

    private void appendAggregations(AiReportPlanDTO reportPlan, AiAnalysisRspDTO rspDTO, Map<String, AiTableDTO> stepTables) {
        if (reportPlan == null || CollectionUtil.isEmpty(reportPlan.getAggregations())) {
            return;
        }
        for (AiReportAggregationDTO aggregation : reportPlan.getAggregations()) {
            if (aggregation == null || !"leftJoin".equalsIgnoreCase(aggregation.getType())) {
                continue;
            }
            AiTableDTO table = leftJoin(aggregation, stepTables);
            if (table != null) {
                rspDTO.getTables().add(table);
            }
        }
    }

    private AiTableDTO leftJoin(AiReportAggregationDTO aggregation, Map<String, AiTableDTO> stepTables) {
        AiTableDTO baseTable = stepTables.get(aggregation.getBaseStepCode());
        AiTableDTO joinTable = stepTables.get(aggregation.getJoinStepCode());
        if (baseTable == null || joinTable == null || StrUtil.isBlank(aggregation.getBaseKey()) || StrUtil.isBlank(aggregation.getJoinKey())) {
            return null;
        }

        List<String> baseFields = chooseFields(aggregation.getBaseFields(), baseTable);
        List<String> joinFields = chooseFields(aggregation.getJoinFields(), joinTable);
        joinFields.remove(aggregation.getJoinKey());

        Map<Object, Map<String, Object>> joinIndex = new LinkedHashMap<>();
        for (Map<String, Object> row : defaultRows(joinTable)) {
            joinIndex.put(row.get(aggregation.getJoinKey()), row);
        }

        AiTableDTO result = new AiTableDTO();
        result.setTitle(StrUtil.blankToDefault(aggregation.getSheetName(), "合并结果"));
        result.setColumns(buildJoinColumns(baseFields, joinFields, baseTable, joinTable, aggregation.getJoinStepCode()));
        for (Map<String, Object> baseRow : defaultRows(baseTable)) {
            Map<String, Object> joinedRow = new LinkedHashMap<>();
            for (String field : baseFields) {
                joinedRow.put(field, baseRow.get(field));
            }
            Map<String, Object> rightRow = joinIndex.get(baseRow.get(aggregation.getBaseKey()));
            for (String field : joinFields) {
                joinedRow.put(joinFieldName(field, aggregation.getJoinStepCode(), joinedRow), rightRow == null ? null : rightRow.get(field));
            }
            result.getData().add(joinedRow);
        }
        return result;
    }

    private List<AiTableColumnDTO> buildJoinColumns(List<String> baseFields, List<String> joinFields, AiTableDTO baseTable, AiTableDTO joinTable, String joinStepCode) {
        List<AiTableColumnDTO> columns = new ArrayList<>();
        Map<String, String> used = new HashMap<>();
        for (String field : baseFields) {
            columns.add(column(titleOf(baseTable, field), field));
            used.put(field, field);
        }
        for (String field : joinFields) {
            String dataIndex = used.containsKey(field) ? joinStepCode + "_" + field : field;
            columns.add(column(titleOf(joinTable, field), dataIndex));
            used.put(dataIndex, dataIndex);
        }
        return columns;
    }

    private String joinFieldName(String field, String joinStepCode, Map<String, Object> row) {
        return row.containsKey(field) ? joinStepCode + "_" + field : field;
    }

    private AiTableColumnDTO column(String title, String dataIndex) {
        AiTableColumnDTO column = new AiTableColumnDTO();
        column.setTitle(StrUtil.blankToDefault(title, dataIndex));
        column.setDataIndex(dataIndex);
        return column;
    }

    private String titleOf(AiTableDTO table, String field) {
        if (table == null || CollectionUtil.isEmpty(table.getColumns())) {
            return field;
        }
        for (AiTableColumnDTO column : table.getColumns()) {
            if (column != null && field.equals(column.getDataIndex())) {
                return column.getTitle();
            }
        }
        return field;
    }

    private List<String> chooseFields(List<String> preferredFields, AiTableDTO table) {
        if (CollectionUtil.isNotEmpty(preferredFields)) {
            return new ArrayList<>(preferredFields);
        }
        if (table == null || CollectionUtil.isEmpty(table.getColumns())) {
            return new ArrayList<>();
        }
        List<String> fields = new ArrayList<>();
        for (AiTableColumnDTO column : table.getColumns()) {
            if (column != null && StrUtil.isNotBlank(column.getDataIndex())) {
                fields.add(column.getDataIndex());
            }
        }
        return fields;
    }

    private List<Map<String, Object>> defaultRows(AiTableDTO table) {
        if (table == null || table.getData() == null) {
            return Collections.emptyList();
        }
        return table.getData();
    }

    private String buildAnswer(AiReportPlanDTO reportPlan, AiAnalysisRspDTO rspDTO) {
        String reportName = StrUtil.blankToDefault(reportPlan.getReportName(), "查询结果");
        int tableCount = rspDTO.getTables().size();
        if (tableCount == 0) {
            return reportName + "没有查到符合条件的数据。";
        }
        List<String> summaries = new ArrayList<>();
        for (AiTableDTO table : rspDTO.getTables()) {
            summaries.add(StrUtil.blankToDefault(table.getTitle(), "结果") + "(" + table.getData().size() + "行)");
        }
        return reportName + "已完成，共生成" + tableCount + "张表：" + String.join("、", summaries) + "。";
    }

    private int hitCount(String question, List<String> words) {
        int count = 0;
        for (String word : words) {
            if (question.contains(word)) {
                count++;
            }
        }
        return count;
    }
}
