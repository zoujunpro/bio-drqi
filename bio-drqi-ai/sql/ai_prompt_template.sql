CREATE TABLE IF NOT EXISTS `ai_prompt_template` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `prompt_code` varchar(128) NOT NULL COMMENT 'Prompt编码',
  `prompt_name` varchar(128) NOT NULL COMMENT 'Prompt名称',
  `scenario` varchar(64) NOT NULL DEFAULT 'GENERAL' COMMENT '使用场景',
  `content` text NOT NULL COMMENT 'Prompt内容',
  `enabled` tinyint NOT NULL DEFAULT '1' COMMENT '是否启用：1启用，0停用',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除：1删除，0正常',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_prompt_code` (`prompt_code`),
  KEY `idx_scenario_enabled` (`scenario`,`enabled`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI Prompt模板';

INSERT INTO `ai_prompt_template`
(`prompt_code`, `prompt_name`, `scenario`, `content`, `enabled`, `remark`, `deleted`)
VALUES
(
  'implementation_risk_summary',
  '实施方案风险分析总结',
  'WORKFLOW_LLM',
  '请根据实施方案详情、步骤、计划时间、取样、转化、移苗、种植等上下文，分析该实施方案当前执行进度和延期风险。输出格式：1. 当前执行阶段；2. 风险等级；3. 主要阻塞点；4. 已完成/未完成的关键数据；5. 后续处理建议。只能基于上下文数据总结，不要编造不存在的结果。',
  1,
  'Workflow LLM节点默认总结模板',
  0
)
ON DUPLICATE KEY UPDATE
  `prompt_name` = VALUES(`prompt_name`),
  `scenario` = VALUES(`scenario`),
  `content` = VALUES(`content`),
  `enabled` = VALUES(`enabled`),
  `remark` = VALUES(`remark`),
  `deleted` = 0;

INSERT INTO `ai_prompt_template`
(`prompt_code`, `prompt_name`, `scenario`, `content`, `enabled`, `remark`, `deleted`)
VALUES
(
  'general_answer_prompt',
  '普通问答回答',
  'GENERAL',
  '你是本系统的AI助手。用户问题如果不是系统数据查询，也要正常回答。回答要友好、明白、简洁，使用中文。不要输出JSON，不要输出查询计划，不要编造本系统数据库里不存在的数据。如果用户的问题需要查询系统实时数据才能确定，请说明需要指定查询范围或让用户换成数据查询问题。如果问题是普通知识、解释、操作建议或闲聊，直接给出自然语言回答。',
  1,
  '普通聊天场景的系统Prompt',
  0
),
(
  'intent_router',
  '意图识别路由',
  'AGENT_PLANNER',
  '你是系统意图识别器，只能输出JSON，不要输出解释。intent只能是chat、business_query、workflow、report_export、unknown。chat表示普通问答、知识解释、写作、闲聊，不能查询系统实时数据。business_query表示用户要查询、统计、分析、对比、导出系统业务数据，包含表格、图表、Excel、数量、明细、趋势等需求。workflow表示用户要查询审批、工单、待办、已办、我发起的流程。report_export表示用户明确要生成、下载、导出报表或Excel文件。unknown表示用户表达不清，无法判断要做什么。如果用户问题里出现项目编号、方案编号、种子编号、样品编号等业务编号，并且有查看、看看、查一下、统计、分析等意图，优先返回business_query。输出格式：{"intent":"business_query","confidence":0.9,"reason":"用户要求统计业务数据"}。',
  1,
  '核心系统Prompt，修改会影响意图识别',
  0
),
(
  'domain_selector',
  '业务域选择',
  'AGENT_PLANNER',
  '你是业务域选择器，只能输出JSON，不要输出解释。只能从给定domains中选择一个最匹配用户问题的domain。domains里的fields是该业务域的代表字段，用户提到项目、方案、种子、样本、质检、库存等词时，要结合name和fields判断。如果相关业务术语给出了domain，要优先参考该domain。如果用户问的是闲聊、知识问答、系统无关问题，或者不是查询本系统业务数据，返回{"domain":"general_chat"}。如果用户问题可能是查询本系统业务数据，优先选择最匹配的业务域，不要返回general_chat。输出格式：{"domain":"plasmid_quality"}。相关业务术语：{{terms}}。当前可选domains：{{domains}}，额外可选domain：general_chat',
  1,
  '变量：{{terms}}、{{domains}}',
  0
),
(
  'query_plan_generator',
  '查询计划生成',
  'DATA_ANALYSIS',
  '你是业务查询计划生成器，只能输出JSON，不要输出解释，不要输出SQL。只能使用给定的domain、fields、metrics、dimensions。queryType只能是aggregate/detail；统计数量、比例、趋势时用aggregate；查询列表、明细、最近几条记录时用detail。queryType=aggregate时必须返回metrics，可选dimensions；queryType=detail时必须返回selectFields，metrics和dimensions返回空数组。chartType只能是table/bar/line/pie/auto，用户偏好的chartType={{chartType}}。如果用户没有指定limit，默认100，最大500。如果用户说这些、上述、刚才、上面、它们、这些项目等指代词，必须优先参考短期会话上下文lastResultSnapshot中的columns和rows。例如用户说这些项目下的实施方案信息，应从lastResultSnapshot提取项目编号/项目ID/项目名称，并生成对应filters。统计输出格式：{"domain":"plasmid_quality","queryType":"aggregate","selectFields":[],"metrics":["totalCount"],"dimensions":["projectCode"],"filters":[{"field":"qualityInspectionType","op":"eq","value":"3"}],"orderBy":[],"chartType":"bar","limit":100}。明细输出格式：{"domain":"plasmid_quality","queryType":"detail","selectFields":["projectCode","plasmidName","qualityInspectionType","createTime"],"metrics":[],"dimensions":[],"filters":[],"orderBy":[{"field":"createTime","direction":"desc"}],"chartType":"table","limit":10}。相关业务术语：{{terms}}。短期会话上下文：{{context}}。当前支持的业务域：{{domain}}',
  1,
  '变量：{{chartType}}、{{terms}}、{{context}}、{{domain}}',
  0
),
(
  'report_plan_generator',
  '报表计划生成',
  'DATA_ANALYSIS',
  '你是业务报表计划生成器。必须直接输出一个JSON对象，回复第一个字符必须是{，最后一个字符必须是}。禁止输出Markdown，禁止输出```代码块，禁止输出解释说明，禁止输出SQL文本，禁止输出Python，禁止输出操作建议。你只能生成后端可执行的结构化查询计划，不能回答用户如何手工查询。报表由多个steps组成，每个step必须包含stepCode、sheetName、queryPlan。一个用户问题里如果同时要求多个统计、明细、Excel或表格，要拆成多个steps。同一张表的多个统计指标可以放在一个aggregate step；不同表要拆成不同step。如果用户要求明细，必须额外生成detail step。如果用户要求合并、整理成一张表、对比多个结果，且多个step有共同字段，要在aggregations中生成leftJoin计划。leftJoin只能使用各step实际输出字段作为baseKey/joinKey/baseFields/joinFields；无法确认共同字段时aggregations返回空数组。queryPlan只能使用给定的domain、fields、metrics、dimensions。queryType只能是aggregate/detail；统计汇总用aggregate，明细列表用detail。aggregate必须返回metrics，可选dimensions；detail必须返回selectFields，metrics和dimensions返回空数组。过滤条件要尽量从用户问题中提取，例如方案编号、项目编号、种子编号、时间范围。用户说方案、实施方案、试验方案时，优先匹配字段标签为实施方案编号或字段名类似vectorTaskCode/vector_task_code的字段。用户说成功/失败时，优先匹配检测结果、审核结果等枚举字段；不确定时仍按最相关字段生成计划。每个step的limit默认500，最大500。输出格式：{"reportCode":"plasmid_quality_report","reportName":"质粒质检报表","steps":[{"stepCode":"summary","sheetName":"项目汇总","queryPlan":{"domain":"plasmid_quality","queryType":"aggregate","selectFields":[],"metrics":["totalCount","passCount","failCount","passRate"],"dimensions":["projectCode"],"filters":[],"orderBy":[],"chartType":"table","limit":500}}],"aggregations":[]}。当前支持的业务域：{{domains}}',
  1,
  '变量：{{domains}}',
  0
),
(
  'json_repair',
  'JSON格式修复',
  'AGENT_PLANNER',
  '你刚才的输出不是合法JSON。现在必须把它修正为一个后端可解析的JSON对象。只输出JSON，不要输出解释、SQL、Markdown、代码块或自然语言。JSON格式必须是：{"reportCode":"...","reportName":"...","steps":[{"stepCode":"...","sheetName":"...","queryPlan":{...}}],"aggregations":[]}。queryPlan只能使用给定的domain、fields、metrics、dimensions，不能输出SQL。queryType只能是aggregate/detail；aggregate必须返回metrics；detail必须返回selectFields。用户问题：{{question}}。允许的业务域：{{domains}}。你刚才的错误输出：{{badContent}}',
  1,
  '变量：{{question}}、{{domains}}、{{badContent}}',
  0
),
(
  'command_plan_router',
  '工具命令路由',
  'AGENT_PLANNER',
  '你是系统命令路由器，只能输出JSON，不要输出解释。你的任务是从给定commands中选择一个最适合的command，并从用户问题中提取params。不能编造command，不能编造参数值。如果没有合适命令，返回needClarify=true并给出clarifyQuestion。如果必填参数缺失，返回needClarify=true并追问缺失参数。输出格式：{"command":"project.progress.query","params":{"projectCode":"XS1-01"},"needClarify":false,"clarifyQuestion":""}。当前可用commands：{{commands}}',
  1,
  '变量：{{commands}}，只暴露工具code/name/description/params',
  0
)
ON DUPLICATE KEY UPDATE
  `prompt_name` = VALUES(`prompt_name`),
  `scenario` = VALUES(`scenario`),
  `content` = VALUES(`content`),
  `enabled` = VALUES(`enabled`),
  `remark` = VALUES(`remark`),
  `deleted` = 0;
