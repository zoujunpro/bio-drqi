# bio-drqi-ai

## 模块定位

`bio-drqi-ai` 是智能查询模块，最终被 `bio-drqi-admin` 启动包引入。模块不让大模型直接执行 SQL，而是让模型先把用户问题转换成结构化查询计划，再由后端按白名单校验和执行。

## 当前调用链路

1. 前端调用 `POST /ai/analysis`，传入用户问题和可选图表类型。
2. `AiAnalysisController` 接收请求，转给 `AiAnalysisService`。
3. `AiQueryPlanService` 读取 `AiDomainRegistry` 中配置的业务域，拼接 prompt，调用大模型。
4. 大模型只允许返回 JSON 查询计划，不允许返回 SQL。
5. `AiQueryPlanValidator` 校验 domain、metrics、dimensions、filters、orderBy、chartType、limit。
6. 校验通过后，`AiQueryExecutorService` 按 schema 生成白名单 SQL 并执行。
7. 查询结果转换成 `AiTableDTO`，如果 `chartType` 不是 `table`，同时生成基础 `AiChartDTO`。

查询计划支持两种类型：

- `aggregate`：统计查询，需要 `metrics`，可选 `dimensions`；
- `detail`：明细查询，需要 `selectFields`，不需要 `metrics`。

## 智能报表导出

复杂报表走独立接口：

```text
POST /ai/report/export
```

请求示例：

```json
{
  "question": "导出最近一个月质粒质检项目汇总和gRNA明细",
  "fileName": "质粒质检报表"
}
```

处理流程：

1. `AiReportPlanService` 让模型生成 `AiReportPlanDTO`。
2. 报表计划包含多个 `steps`，每个 step 都是一条 `AiQueryPlanDTO`。
3. 后端逐个校验 step 的查询计划。
4. `AiQueryExecutorService` 逐个执行查询。
5. 如果存在 `aggregations`，`AiReportService` 会按规则把多个 step 的结果二次合并成新的结果集。
6. `AiReportService` 把原始 step 结果和二次合并结果写入同一个 Excel，不同结果对应不同 sheet。

模型仍然不能输出 SQL，也不直接计算真实数据，只能输出报表步骤、查询计划和二次聚合规则。

二次聚合示例：

```json
{
  "type": "leftJoin",
  "sheetName": "组合结果",
  "baseStepCode": "summary",
  "joinStepCode": "detail",
  "baseKey": "projectCode",
  "joinKey": "projectCode",
  "baseFields": ["projectCode", "totalCount", "passRate"],
  "joinFields": ["plasmidName", "grnaSequence"]
}
```

## 为什么要这样做

大模型输出不稳定，也不能直接信任。系统必须由后端控制：

- 哪些业务域可以查；
- 哪些字段可以过滤、分组、排序；
- 哪些指标可以统计；
- 查询最大返回数量；
- SQL 表名、列名、表达式如何映射。

所以模型只负责“理解用户问题并生成计划”，真正的查询权限和执行逻辑都在后端。

## 业务域配置

业务域集中维护在 `AiDomainRegistry`。

新增业务域时，一般需要配置：

- `domain`：模型返回的业务域编码；
- `name`：业务中文名称；
- `tableName`：后端执行器使用的主表；
- `fields`：可过滤、排序字段；
- `dimensions`：可分组维度；
- `metrics`：可统计指标。

`listForPrompt()` 只把字段名、中文名、枚举、指标名暴露给模型，不暴露 SQL 表达式。

## 模型配置

启动包配置示例：

```yaml
bio:
  ai:
    llm:
      base-url: http://172.16.14.9:11434/v1
      model: qwen3:8b
      temperature: 0.1
      timeout: 120000
    max-domain-prompt-size: 80
    include-tables: []
    exclude-tables:
      - system_*
      - "*_log"
      - "*_ref"
      - bio_request_log
    exclude-fields:
      - password
      - token
      - "*secret*"
      - "*key*"
      - phone
      - mobile
      - id_card
      - identity_card
    dict-fields:
      - table-name: cer_plasmid_quality_tb
        field-name: quality_inspection_type
        dict-source: bio_dict
        dict-type: QUALITY_INSPECTION_TYPE
```

接口按 OpenAI 兼容协议调用 `/chat/completions`，Ollama、vLLM 等都可以适配。

## 安全优化

当前已加三类保护：

- 自动表注册支持 `include-tables`、`exclude-tables`、`exclude-fields`，默认排除系统表、日志表、关联表和常见敏感字段。
- 查询计划生成改成两阶段：先让模型从业务域摘要里选择 domain，再只发送单个 domain 的字段和指标，避免 prompt 过长。
- 查询和报表导出会写 AI 审计日志，记录问题、计划、行数、耗时；如果 `bio_request_log` 可写，会同步写入请求日志表。

## 当前执行能力

`AiQueryExecutorService` 当前支持：

- 统计查询和明细查询；
- 单业务域主表查询；
- 固定 join 白名单查询；
- 指标聚合；
- 维度分组；
- 明细字段选择；
- `eq/in/like/between/gte/lte/last_days/last_months` 过滤；
- 按已选择维度或指标排序；
- `table/bar/line/pie/auto` 基础图表返回；
- 枚举维度值自动翻译。

## 后续建议

多表查询通过 `AiDomainSchema` 的 join 白名单配置：

1. 主表和别名；
2. 可 join 的表；
3. join 类型；
4. 固定 on 条件；
5. 字段依赖哪个 join。

执行器仍然只按后端 schema 拼 SQL，不让模型直接输出 join SQL。

示例：

```java
schema.setTableName("cer_plasmid_quality_tb");
schema.setTableAlias("q");

schema.getJoins().put("p", join("cer_project_tb", "p", "left join", "p.project_code = q.project_code"));

fields.put("projectName", field("projectName", "项目名称", "p.project_name", "string", "p"));
```

当查询计划使用 `projectName` 时，执行器会自动追加 `p` 这个 join；没用到时不会 join。
