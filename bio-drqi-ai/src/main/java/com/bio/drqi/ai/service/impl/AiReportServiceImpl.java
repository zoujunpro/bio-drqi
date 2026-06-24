package com.bio.drqi.ai.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.dto.plan.AiQueryPlanDTO;
import com.bio.drqi.ai.dto.plan.AiReportAggregationDTO;
import com.bio.drqi.ai.dto.plan.AiReportPlanDTO;
import com.bio.drqi.ai.dto.plan.AiReportStepDTO;
import com.bio.drqi.ai.dto.req.AiReportReqDTO;
import com.bio.drqi.ai.dto.rsp.AiAnalysisRspDTO;
import com.bio.drqi.ai.dto.rsp.AiTableColumnDTO;
import com.bio.drqi.ai.dto.rsp.AiTableDTO;
import com.bio.drqi.ai.registry.AiDomainRegistry;
import com.bio.drqi.ai.schema.AiDomainSchema;
import com.bio.drqi.ai.service.AiAuditLogService;
import com.bio.drqi.ai.service.AiQueryExecutorService;
import com.bio.drqi.ai.service.AiQueryPlanValidator;
import com.bio.drqi.ai.service.AiReportPlanService;
import com.bio.drqi.ai.service.AiReportService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class AiReportServiceImpl implements AiReportService {

    @Resource
    private AiReportPlanService aiReportPlanService;

    @Resource
    private AiDomainRegistry aiDomainRegistry;

    @Resource
    private AiQueryPlanValidator aiQueryPlanValidator;

    @Resource
    private AiQueryExecutorService aiQueryExecutorService;

    @Resource
    private AiAuditLogService aiAuditLogService;

    @Override
    public void export(AiReportReqDTO reqDTO, HttpServletResponse response) {
        long startTime = System.currentTimeMillis();
        AiReportPlanDTO reportPlan = aiReportPlanService.generate(reqDTO.getQuestion());
        validateReportPlan(reportPlan);

        SXSSFWorkbook workbook = new SXSSFWorkbook(200);
        try {
            CellStyle headStyle = buildHeadStyle(workbook);
            Map<String, AiTableDTO> stepTableMap = new LinkedHashMap<>();
            int totalRows = 0;
            for (AiReportStepDTO step : reportPlan.getSteps()) {
                AiQueryPlanDTO queryPlan = step.getQueryPlan();
                AiDomainSchema schema = aiDomainRegistry.getRequired(queryPlan.getDomain());
                aiQueryPlanValidator.validate(queryPlan, schema);
                AiAnalysisRspDTO queryResult = aiQueryExecutorService.execute(queryPlan, schema);
                AiTableDTO table = queryResult.getTables().isEmpty() ? new AiTableDTO() : queryResult.getTables().get(0);
                totalRows += table.getData().size();
                stepTableMap.put(step.getStepCode(), table);
                writeSheet(workbook, headStyle, step, table);
            }
            writeAggregationSheets(workbook, headStyle, reportPlan, stepTableMap);
            aiAuditLogService.log("REPORT", reqDTO.getQuestion(), JSONUtil.toJsonStr(reportPlan), totalRows, System.currentTimeMillis() - startTime);
            writeWorkbook(response, resolveFileName(reqDTO, reportPlan), workbook);
        } finally {
            workbook.dispose();
        }
    }

    private void validateReportPlan(AiReportPlanDTO reportPlan) {
        if (reportPlan == null || CollectionUtil.isEmpty(reportPlan.getSteps())) {
            throw new BusinessException("AI报表计划缺少查询步骤");
        }
        Set<String> stepCodes = new HashSet<>();
        for (AiReportStepDTO step : reportPlan.getSteps()) {
            if (step == null || StrUtil.isBlank(step.getStepCode())) {
                throw new BusinessException("AI报表步骤编码不能为空");
            }
            if (!stepCodes.add(step.getStepCode())) {
                throw new BusinessException("AI报表步骤编码重复：" + step.getStepCode());
            }
            if (step.getQueryPlan() == null) {
                throw new BusinessException("AI报表步骤缺少查询计划：" + step.getStepCode());
            }
        }
    }

    private void writeAggregationSheets(SXSSFWorkbook workbook, CellStyle headStyle, AiReportPlanDTO reportPlan, Map<String, AiTableDTO> stepTableMap) {
        if (CollectionUtil.isEmpty(reportPlan.getAggregations())) {
            return;
        }
        for (AiReportAggregationDTO aggregation : reportPlan.getAggregations()) {
            AiTableDTO table = aggregate(aggregation, stepTableMap);
            AiReportStepDTO step = new AiReportStepDTO();
            step.setStepCode("aggregation");
            step.setSheetName(aggregation.getSheetName());
            writeSheet(workbook, headStyle, step, table);
        }
    }

    private AiTableDTO aggregate(AiReportAggregationDTO aggregation, Map<String, AiTableDTO> stepTableMap) {
        validateAggregation(aggregation, stepTableMap);
        if (!"leftJoin".equals(aggregation.getType())) {
            throw new BusinessException("不支持的AI报表二次聚合类型：" + aggregation.getType());
        }
        return leftJoin(aggregation, stepTableMap.get(aggregation.getBaseStepCode()), stepTableMap.get(aggregation.getJoinStepCode()));
    }

    private void validateAggregation(AiReportAggregationDTO aggregation, Map<String, AiTableDTO> stepTableMap) {
        if (aggregation == null || StrUtil.isBlank(aggregation.getType())) {
            throw new BusinessException("AI报表二次聚合类型不能为空");
        }
        if (!stepTableMap.containsKey(aggregation.getBaseStepCode())) {
            throw new BusinessException("AI报表二次聚合主步骤不存在：" + aggregation.getBaseStepCode());
        }
        if (!stepTableMap.containsKey(aggregation.getJoinStepCode())) {
            throw new BusinessException("AI报表二次聚合关联步骤不存在：" + aggregation.getJoinStepCode());
        }
        if (StrUtil.isBlank(aggregation.getBaseKey()) || StrUtil.isBlank(aggregation.getJoinKey())) {
            throw new BusinessException("AI报表二次聚合关联字段不能为空");
        }
    }

    private AiTableDTO leftJoin(AiReportAggregationDTO aggregation, AiTableDTO baseTable, AiTableDTO joinTable) {
        List<String> baseFields = resolveFields(aggregation.getBaseFields(), baseTable);
        List<String> joinFields = resolveFields(aggregation.getJoinFields(), joinTable);
        Map<String, Map<String, Object>> joinRowMap = buildJoinRowMap(joinTable, aggregation.getJoinKey());

        AiTableDTO result = new AiTableDTO();
        result.setTitle(StrUtil.blankToDefault(aggregation.getSheetName(), "组合结果"));
        appendColumns(result, baseTable, baseFields, null);
        appendColumns(result, joinTable, joinFields, aggregation.getBaseFields().contains(aggregation.getJoinKey()) ? "join_" : null);

        for (Map<String, Object> baseRow : baseTable.getData()) {
            Map<String, Object> resultRow = new LinkedHashMap<>();
            for (String field : baseFields) {
                resultRow.put(field, baseRow.get(field));
            }
            Map<String, Object> joinRow = joinRowMap.get(String.valueOf(baseRow.get(aggregation.getBaseKey())));
            for (String field : joinFields) {
                String resultField = resultRow.containsKey(field) ? "join_" + field : field;
                resultRow.put(resultField, joinRow == null ? null : joinRow.get(field));
            }
            result.getData().add(resultRow);
        }
        return result;
    }

    private Map<String, Map<String, Object>> buildJoinRowMap(AiTableDTO joinTable, String joinKey) {
        Map<String, Map<String, Object>> map = new LinkedHashMap<>();
        for (Map<String, Object> row : joinTable.getData()) {
            Object key = row.get(joinKey);
            if (key != null) {
                map.put(String.valueOf(key), row);
            }
        }
        return map;
    }

    private List<String> resolveFields(List<String> fields, AiTableDTO table) {
        if (CollectionUtil.isNotEmpty(fields)) {
            return fields;
        }
        List<String> result = new ArrayList<>();
        for (AiTableColumnDTO column : table.getColumns()) {
            result.add(column.getDataIndex());
        }
        return result;
    }

    private void appendColumns(AiTableDTO result, AiTableDTO source, List<String> fields, String prefix) {
        Map<String, AiTableColumnDTO> columnMap = new LinkedHashMap<>();
        for (AiTableColumnDTO column : source.getColumns()) {
            columnMap.put(column.getDataIndex(), column);
        }
        for (String field : fields) {
            AiTableColumnDTO sourceColumn = columnMap.get(field);
            AiTableColumnDTO resultColumn = new AiTableColumnDTO();
            resultColumn.setTitle(sourceColumn == null ? field : sourceColumn.getTitle());
            resultColumn.setDataIndex(StrUtil.blankToDefault(prefix, "") + field);
            result.getColumns().add(resultColumn);
        }
    }

    private void writeSheet(SXSSFWorkbook workbook, CellStyle headStyle, AiReportStepDTO step, AiTableDTO table) {
        String sheetName = safeSheetName(StrUtil.blankToDefault(step.getSheetName(), step.getStepCode()));
        SXSSFSheet sheet = workbook.createSheet(sheetName);
        List<AiTableColumnDTO> columns = table.getColumns();

        Row headRow = sheet.createRow(0);
        for (int i = 0; i < columns.size(); i++) {
            Cell cell = headRow.createCell(i);
            cell.setCellValue(columns.get(i).getTitle());
            cell.setCellStyle(headStyle);
            sheet.setColumnWidth(i, 20 * 256);
        }

        List<Map<String, Object>> data = table.getData();
        for (int rowIndex = 0; rowIndex < data.size(); rowIndex++) {
            Row row = sheet.createRow(rowIndex + 1);
            Map<String, Object> rowData = data.get(rowIndex);
            for (int colIndex = 0; colIndex < columns.size(); colIndex++) {
                AiTableColumnDTO column = columns.get(colIndex);
                Object value = rowData.get(column.getDataIndex());
                row.createCell(colIndex).setCellValue(value == null ? "" : String.valueOf(value));
            }
        }
    }

    private CellStyle buildHeadStyle(SXSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private void writeWorkbook(HttpServletResponse response, String fileName, SXSSFWorkbook workbook) {
        try {
            String encodeFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename*=UTF-8''" + encodeFileName + ".xlsx");
            workbook.write(response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            throw new BusinessException("AI报表导出失败");
        }
    }

    private String resolveFileName(AiReportReqDTO reqDTO, AiReportPlanDTO reportPlan) {
        String fileName = StrUtil.blankToDefault(reqDTO.getFileName(), reportPlan.getReportName());
        return StrUtil.blankToDefault(fileName, "AI智能报表");
    }

    private String safeSheetName(String sheetName) {
        String safeName = sheetName.replaceAll("[\\\\/?*\\[\\]:]", "_");
        if (safeName.length() > 31) {
            safeName = safeName.substring(0, 31);
        }
        return StrUtil.blankToDefault(safeName, "sheet");
    }
}
