package com.bio.drqi.ai.registry;

import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.schema.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AiDomainRegistry {

    /**
     * AI 可查询业务域注册表。
     * key 是给模型使用的 domain，value 是后端真正用于校验和执行的 schema。
     */
    private final Map<String, AiDomainSchema> domainMap = new LinkedHashMap<>();

    @PostConstruct
    public void init() {
        // 每新增一个智能查询业务，在这里注册一个 schema。
        register(plasmidQuality());
    }

    public AiDomainSchema getRequired(String domain) {
        AiDomainSchema schema = domainMap.get(domain);
        if (schema == null) {
            throw new BusinessException("不支持的智能查询业务域：" + domain);
        }
        return schema;
    }

    public List<AiDomainPromptDTO> listForPrompt() {
        // 给模型的配置只保留业务字段、枚举、指标名称，不暴露 SQL 表达式。
        return domainMap.values().stream().map(this::toPromptDTO).collect(Collectors.toList());
    }

    public AiDomainPromptDTO getForPrompt(String domain) {
        return toPromptDTO(getRequired(domain));
    }

    public List<AiDomainSummaryDTO> listSummaryForPrompt(int maxSize) {
        return domainMap.values().stream().limit(maxSize).map(this::toSummaryDTO).collect(Collectors.toList());
    }

    public void register(AiDomainSchema schema) {
        domainMap.put(schema.getDomain(), schema);
    }

    public boolean contains(String domain) {
        return domainMap.containsKey(domain);
    }

    private AiDomainSchema plasmidQuality() {
        // 质粒质检业务域示例。
        // tableName/column/expression 只给后端执行器使用，模型只能看到 field/label/type/enum/metric。
        AiDomainSchema schema = new AiDomainSchema();
        schema.setDomain("plasmid_quality");
        schema.setName("质粒质检");
        schema.setTableName("cer_plasmid_quality_tb");

        Map<String, AiFieldSchema> fields = new LinkedHashMap<>();
        fields.put("projectCode", field("projectCode", "项目编号", "project_code", "string"));
        fields.put("subProjectCode", field("subProjectCode", "子项目编号", "sub_project_code", "string"));
        fields.put("vectorTaskCode", field("vectorTaskCode", "实施方案编号", "vector_task_code", "string"));
        fields.put("plasmidName", field("plasmidName", "质粒名称", "plasmid_name", "string"));
        fields.put("qualityInspectionType", enumField("qualityInspectionType", "下一步安排", "quality_inspection_type",
                enumMap("1", "质粒制备", "2", "农杆菌转化", "3", "gRNA合成")));
        fields.put("qualityInspectionResult", enumField("qualityInspectionResult", "质检结果", "quality_inspection_result",
                enumMap("pass", "合格", "refuse", "不合格")));
        fields.put("grnaSequence", field("grnaSequence", "gRNA序列", "grna_sequence", "string"));
        fields.put("createTime", field("createTime", "创建时间", "create_time", "date"));
        schema.setFields(fields);

        Map<String, AiFieldSchema> dimensions = new LinkedHashMap<>();
        dimensions.put("projectCode", fields.get("projectCode"));
        dimensions.put("subProjectCode", fields.get("subProjectCode"));
        dimensions.put("qualityInspectionType", fields.get("qualityInspectionType"));
        dimensions.put("qualityInspectionResult", fields.get("qualityInspectionResult"));
        dimensions.put("month", field("month", "月份", "date_format(create_time, '%Y-%m')", "date"));
        dimensions.put("day", field("day", "日期", "date_format(create_time, '%Y-%m-%d')", "date"));
        schema.setDimensions(dimensions);

        Map<String, AiMetricSchema> metrics = new LinkedHashMap<>();
        metrics.put("totalCount", metric("totalCount", "总数量", "sql", "count(*)"));
        metrics.put("passCount", metric("passCount", "合格数量", "sql",
                "sum(case when quality_inspection_result = 'pass' then 1 else 0 end)"));
        metrics.put("failCount", metric("failCount", "不合格数量", "sql",
                "sum(case when quality_inspection_result = 'refuse' then 1 else 0 end)"));
        metrics.put("passRate", metric("passRate", "通过率", "sql",
                "round(ifnull(sum(case when quality_inspection_result = 'pass' then 1 else 0 end) / nullif(count(*), 0), 0), 4)"));
        schema.setMetrics(metrics);
        return schema;
    }

    private AiDomainPromptDTO toPromptDTO(AiDomainSchema schema) {
        AiDomainPromptDTO dto = new AiDomainPromptDTO();
        dto.setDomain(schema.getDomain());
        dto.setName(schema.getName());
        dto.setFields(schema.getFields().values().stream().map(this::toFieldPromptDTO).collect(Collectors.toList()));
        dto.setDimensions(schema.getDimensions().values().stream().map(this::toFieldPromptDTO).collect(Collectors.toList()));
        dto.setMetrics(schema.getMetrics().values().stream().map(this::toMetricPromptDTO).collect(Collectors.toList()));
        return dto;
    }

    private AiDomainSummaryDTO toSummaryDTO(AiDomainSchema schema) {
        AiDomainSummaryDTO dto = new AiDomainSummaryDTO();
        dto.setDomain(schema.getDomain());
        dto.setName(schema.getName());
        return dto;
    }

    private AiFieldPromptDTO toFieldPromptDTO(AiFieldSchema schema) {
        AiFieldPromptDTO dto = new AiFieldPromptDTO();
        dto.setField(schema.getField());
        dto.setLabel(schema.getLabel());
        dto.setType(schema.getType());
        dto.setEnumValues(schema.getEnumValues());
        return dto;
    }

    private AiMetricPromptDTO toMetricPromptDTO(AiMetricSchema schema) {
        AiMetricPromptDTO dto = new AiMetricPromptDTO();
        dto.setMetric(schema.getMetric());
        dto.setLabel(schema.getLabel());
        return dto;
    }

    private AiFieldSchema field(String field, String label, String column, String type) {
        AiFieldSchema schema = new AiFieldSchema();
        schema.setField(field);
        schema.setLabel(label);
        schema.setColumn(column);
        schema.setType(type);
        return schema;
    }

    private AiFieldSchema field(String field, String label, String column, String type, String... requiredJoinAliases) {
        AiFieldSchema schema = field(field, label, column, type);
        if (requiredJoinAliases != null) {
            for (String alias : requiredJoinAliases) {
                schema.getRequiredJoinAliases().add(alias);
            }
        }
        return schema;
    }

    private AiFieldSchema enumField(String field, String label, String column, Map<String, String> enumValues) {
        AiFieldSchema schema = field(field, label, column, "enum");
        schema.setEnumValues(enumValues);
        return schema;
    }

    private AiMetricSchema metric(String metric, String label, String type, String expression) {
        AiMetricSchema schema = new AiMetricSchema();
        schema.setMetric(metric);
        schema.setLabel(label);
        schema.setType(type);
        schema.setExpression(expression);
        return schema;
    }

    private AiJoinSchema join(String tableName, String alias, String joinType, String onExpression) {
        AiJoinSchema schema = new AiJoinSchema();
        schema.setTableName(tableName);
        schema.setAlias(alias);
        schema.setJoinType(joinType);
        schema.setOnExpression(onExpression);
        return schema;
    }

    private Map<String, String> enumMap(String... values) {
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 0; i + 1 < values.length; i += 2) {
            map.put(values[i], values[i + 1]);
        }
        return map;
    }
}
